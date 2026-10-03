package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.BlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.StargateType;
import org.jetbrains.annotations.NotNull;

public abstract class StopMotionStargateEntity<SG extends BlockEntityStargate<?>> extends IrisStargateEntity<SG>
{
	public static final String CAN_ENGAGE = "can_engage";
	public static final String ADDRESS_BUFFER = "AddressBuffer";
	public static final String SYMBOL_BUFFER = "SymbolBuffer";
	public static final String CURRENT_SYMBOL = "CurrentSymbol";
	
	public enum CanEngage
	{
		NO,
		READY,
		YES
	}
	
	protected int currentSymbol = 0;
	
	protected Address.Mutable addressBuffer = new Address.Mutable();
	protected int symbolBuffer = 0;
	
	protected CanEngage canEngage = CanEngage.NO;
	
	protected boolean dynamicSymbols = true;
	
	protected final int maxSymbolIndex;
	
	public StopMotionStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
									int totalSymbols, int defaultNetwork, float verticalCenterHeight, float horizontalCenterHeight, int maxSymbolIndex)
	{
		super(blockEntityType, stargateType, defaultVariant, pos, state, totalSymbols, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.maxSymbolIndex = maxSymbolIndex;
	}
	
	public StopMotionStargateEntity(BlockEntityType<?> blockEntityType, StargateType<SG> stargateType, ResourceLocation defaultVariant, BlockPos pos, BlockState state,
									int totalSymbols, int defaultNetwork, int maxSymbolIndex)
	{
		super(blockEntityType, stargateType, defaultVariant, pos, state, totalSymbols, defaultNetwork);
		
		this.maxSymbolIndex = maxSymbolIndex;
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		
		canEngage = CanEngage.values()[tag.getByte(CAN_ENGAGE)];
		addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
		symbolBuffer = tag.getInt(SYMBOL_BUFFER);
		currentSymbol = tag.getInt(CURRENT_SYMBOL);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putByte(CAN_ENGAGE, (byte) canEngage.ordinal());
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putInt(CURRENT_SYMBOL, currentSymbol);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		tag.putByte(CAN_ENGAGE, (byte) canEngage.ordinal());
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putInt(CURRENT_SYMBOL, currentSymbol);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
			canEngage = CanEngage.values()[tag.getByte(CAN_ENGAGE)];
			addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
			symbolBuffer = tag.getInt(SYMBOL_BUFFER);
			currentSymbol = tag.getInt(CURRENT_SYMBOL);
		}
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	public Address getAddressBuffer()
	{
		return addressBuffer;
	}
	
	public int getSymbolBuffer()
	{
		return symbolBuffer;
	}
	
	@Override
	public void updateDHD(AbstractDHDEntity dhd)
	{
		dhd.updateDHD(!isConnected() || (isConnected() && isDialingOut()) ? addressBuffer : new Address.Mutable(), canEngage != CanEngage.NO || isConnected());
	}
	
	@Override
	public StargateInfo.FeedbackMessage indirectEngageSymbol(int symbol, boolean canEngageStargate)
	{
		if(level.isClientSide())
			return StargateInfo.Feedback.NONE.withInfo();
		
		// Special case where only the Point of Origin is encoded (attempting to encode any symbols after it should reset the Stargate)
		if(addressBuffer.getLength() == 1 && addressBuffer.hasPointOfOrigin())
			return disconnectStargate(incompleteAddress());
		
		canEngage = canEngageStargate ? CanEngage.READY : CanEngage.NO;
		
		if(isSymbolOutOfBounds(symbol))
			return StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS.withInfo(symbol);
		
		if(isConnected())
		{
			if(symbol == 0) // Can't map over Point of Origin, so this check is fine
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.withInfo());
		}
		
		int mappedSymbol = symbolMap.getMappedSymbol(symbol);
		
		if(addressBuffer.containsSymbol(mappedSymbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS.withInfo(mappedSymbol));
		
		if(addressBuffer.getLength() == getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		encodedSymbols.addSymbol(symbol); // Keep track of what symbols have physically been encoded on the gate, ignoring any remapping
		addressBuffer.addSymbol(mappedSymbol);
		
		updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STARTED, spinDirection());
		
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED.withInfo(mappedSymbol));
	}
	
	@Override
	public StargateInfo.FeedbackMessage directEngageSymbol(int symbol, boolean canEngageStargate)
	{
		int mappedSymbol = symbolMap.getMappedSymbol(symbol);
		if(!addressBuffer.containsSymbol(mappedSymbol))
			addressBuffer.addSymbol(mappedSymbol);
		
		return super.directEngageSymbol(symbol, canEngageStargate);
	}
	
	@Override
	protected StargateInfo.FeedbackMessage encodeChevron(int symbol, StargateInfo.Direction direction, StargateInfo.ChevronSound sound)
	{
		symbolBuffer++;
		
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		StargateInfo.FeedbackMessage feedback = super.encodeChevron(symbol, direction, sound);
		
		if(addressBuffer.getLength() > getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		return setRecentFeedback(feedback);
	}
	
	@Override
	public StargateInfo.FeedbackMessage dhdEngageStargate(AbstractDHDEntity dhd)
	{
		// Special case where no symbols are encoded
		if(addressBuffer.isEmpty())
			return disconnectStargate(incompleteAddress());
		
		// Special case where only the Point of Origin is encoded (attempting to encode any symbols after it should reset the Stargate)
		if(addressBuffer.getLength() == 1 && addressBuffer.hasPointOfOrigin())
			return disconnectStargate(incompleteAddress());
		
		if(!addressBuffer.canBeDialed())
		{
			if(addressBuffer.getLength() > getAddress().getLength())
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS.withInfo(Component.translatable("message.sgjourney.stargate.error.incomplete_address.dialing_aborted")));
			else
				return disconnectStargate(incompleteAddress());
		}
		
		// Engages the Stargate if all chevrons are encoded, or informs it that it can engage automatically once the last chevron is encoded
		if(address.getLength() < addressBuffer.getLength())
		{
			if(canEngage != CanEngage.NO) // Interrupt Stargate rotation
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS.withInfo(Component.translatable("message.sgjourney.stargate.error.incomplete_address.dialing_aborted")));
			else
			{
				canEngage = CanEngage.READY;
				return StargateInfo.Feedback.NONE.withInfo();
			}
		}
		else
			return super.dhdEngageStargate(dhd);
	}
	
	public int getCurrentSymbol()
	{
		return this.currentSymbol;
	}
	
	public boolean isSymbolSpinning()
	{
		return !isConnected() && addressBuffer.getLength() > symbolBuffer;
	}
	
	protected abstract void animateSpin();
	
	public static void tick(Level level, BlockPos pos, BlockState state, StopMotionStargateEntity<?> stargate)
	{
		IrisStargateEntity.tick(level, pos, state, stargate);
		
		if(level.isClientSide())
			return;
		
		stargate.animateSpin();
	}
	
	protected abstract RotationDirection spinDirection();
	
	protected void moveSymbol()
	{
		switch(spinDirection())
		{
			case CLOCKWISE -> ++currentSymbol;
			case ANTICLOCKWISE -> --currentSymbol;
		}
		
		if(currentSymbol > maxSymbolIndex)
			currentSymbol = 0;
		else if(currentSymbol < 0)
			currentSymbol = maxSymbolIndex;
	}
	
	public int getLastSymbol()
	{
		if(isConnected() && !isDialingOut())
			return 0;
		
		return addressBuffer.lastSymbol();
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		return getLastSymbol() % 12 + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return (getLastSymbol() / (totalSymbols / SEGMENTS) + 1) * 5;
	}
	
	@Override
	protected void resetAddress()
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer.reset();
		canEngage = CanEngage.NO;
		super.resetAddress();
	}
	
	@Override
	public void playRotationSound()
	{
		this.stopRotationSound();
		this.spinSound.playSound();
	}
	
	@Override
	public void stopRotationSound()
	{
		this.spinSound.stopSound();
	}
	
	public void setLocalSymbols()
	{
		if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
	}
}
