package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.stargate.universe.UniverseBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.universe.UniverseStargate;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;

public class UniverseStargateEntity extends RotatingStargateEntity<UniverseBlockEntityStargate>
{
	public static final String CAN_ENGAGE = "can_engage";
	public static final String ADDRESS_BUFFER = "AddressBuffer"; //TODO Rename to "address_buffer"
	public static final String SYMBOL_BUFFER = "SymbolBuffer"; //TODO Rename to "symbol_buffer"
	
	public static final boolean FAST_ROTATION = CommonStargateConfig.universe_fast_rotation.get();
	
	public static final int TOTAL_SYMBOLS = 36;
	public static final int MAX_ROTATION = 324; // 54 * (FAST_ROTATION ? 2 : 3); // 108 : 162
	
	public static final int ROTATION_THIRD = MAX_ROTATION / 3;
	public static final int RESET_DEGREES = ROTATION_THIRD * 2;
	
	public static final int MAX_WAIT_TICKS = 20;
	
	public int waitTicks = 1;
	
	public Address.Mutable addressBuffer = new Address.Mutable();
	protected boolean canEngage = false;
	public int symbolBuffer = 0;
	
	protected int angle;
	
	public UniverseStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.UNIVERSE_STARGATE.get(), StargateInit.UNIVERSE.get(), StargateJourney.sgjourneyLocation("universe"), pos, state, TOTAL_SYMBOLS, 1, MAX_ROTATION);
		this.setOpenSoundLead(8);
		
		this.angle = this.maxRotation / 54;
		
		symbolInfo.setPointOfOrigin(Conversion.locationToPointOfOrigin(PointOfOrigin.UNIVERSAL_LOCATION));
		symbolInfo.setSymbols(Conversion.locationToSymbols(Symbols.UNIVERSAL_LOCATION));
		
		this.oldRotation = RESET_DEGREES;
		this.rotation = RESET_DEGREES;
	}
	
	@Override
	public void load(CompoundTag tag)
	{
        super.load(tag);
		
		canEngage = tag.getBoolean(CAN_ENGAGE);
		addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
        symbolBuffer = tag.getInt(SYMBOL_BUFFER);
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putBoolean(CAN_ENGAGE, canEngage);
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		tag.putBoolean(CAN_ENGAGE, canEngage);
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		super.onDataPacket(net, packet);
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			canEngage = tag.getBoolean(CAN_ENGAGE);
			symbolBuffer = tag.getInt(ADDRESS_BUFFER);
			addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
		}
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public void updateDHD(AbstractDHDEntity dhd)
	{
		dhd.updateDHD(!isConnected() || (isConnected() && isDialingOut()) ? addressBuffer : new Address.Mutable(), canEngage || isConnected());
	}
	
	@Override
	public StargateInfo.FeedbackMessage indirectEngageSymbol(int symbol, boolean canEngageStargate)
	{
		canEngage = canEngageStargate;
		
		if(isSymbolOutOfBounds(symbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS.withInfo(symbol));
		
		if(addressBuffer.getLength() == 0 && address.getLength() > 0)
			resetStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED.withInfo());
		}
		else if(symbol == 0 && !isConnected() && addressBuffer.getLength() == 0)
			return setRecentFeedback(StargateInfo.Feedback.INCOMPLETE_ADDRESS.withInfo());
		
		if(addressBuffer.containsSymbol(symbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS.withInfo(symbol));
		
		if(addressBuffer.getLength() == 0 && address.getLength() == 0)
			startSound();
		
		addressBuffer.addSymbol(symbol);
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED.withInfo(symbol));
	}
	
	@Override
	public StargateInfo.FeedbackMessage directEngageSymbol(int symbol, boolean canEngageStargate)
	{
		if(!addressBuffer.containsSymbol(symbol))
			addressBuffer.addSymbol(symbol);
		
		return super.directEngageSymbol(symbol, canEngageStargate);
	}
	
	@Override
	protected StargateInfo.FeedbackMessage encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		waitTicks++;
		
		return super.encodeChevron(symbol, incoming, encode);
	}
	
	@Override
	public StargateInfo.FeedbackMessage dhdEngageStargate()
	{
		if(!addressBuffer.canBeDialed())
			return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
		
		// Engages the Stargate if all chevrons are encoded, or informs it that it can engage automatically once the last chevron is encoded
		if(address.getLength() < addressBuffer.getLength())
		{
			if(canEngage) // Interrupt Stargate rotation
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
			else
			{
				canEngage = true;
				return StargateInfo.Feedback.NONE.withInfo();
			}
		}
		else
			return super.dhdEngageStargate();
	}
	
	public void startSound()
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.UniverseStart(this.worldPosition));
	}
	
	public boolean dialFromBuffer()
	{
		return !addressBuffer.isEmpty();
	}
	
	public void bufferDialing()
	{
		if(waitTicks != 0)
		{
			if(waitTicks >= MAX_WAIT_TICKS)
				waitTicks = 0;
			else
				waitTicks++;
			return;
		}
		
		if(!isConnected() && addressBuffer.getLength() > symbolBuffer)
		{
			if(!isRotating())
				startRotation(addressBuffer.symbolAt(symbolBuffer), CommonStargateConfig.universe_best_direction.get() ?
						bestSymbolDirection(addressBuffer.symbolAt(symbolBuffer)) : alternatingDirection(address.getLength()));
			
			if(rotation == desiredRotation)
				directEngageSymbol(getCurrentSymbol(), canEngage);
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, UniverseStargateEntity stargate)
	{
		if(stargate.dialFromBuffer() && !level.isClientSide())
			stargate.bufferDialing();
		RotatingStargateEntity.tick(level, pos, state, stargate);
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		int currentSymbol = getCurrentSymbol();
		if(currentSymbol == -1)
			return 0;
		
		return currentSymbol % 12 + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return ((rotation / ROTATION_THIRD) + 1) * 5;
	}
	
	@Override
	public int getDesiredRotation(int desiredSymbol)
	{
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		return 3 * (this.angle / 2) + whole * this.maxRotation / 9 + (this.angle * leftover);
	}
	
	@Override
	public int getCurrentSymbol()
	{
		int segment6 = rotation / 6;
		if(segment6 % 6 == 0 || segment6 % 6 == 5)
			return -1;
		
		int emptySegments = 2 * (segment6 / 6) + 1;
		
		return segment6 - emptySegments;
	}
	
	@Override
	protected int rotationStep()
	{
		return FAST_ROTATION ? (this.rotationDirection.isRotating ? 3 : 2) : 2; // Only rotates fast during computer dialing or DHD dialing, not during redstone dialing
	}
	
	@Override
	public StargateInfo.FeedbackMessage resetStargate(StargateInfo.FeedbackMessage feedback)
	{
		super.resetStargate(feedback);
		
		if(this.rotation != RESET_DEGREES)
			rotateTo(RESET_DEGREES, bestRotationDirection(RESET_DEGREES));
		
		return feedback;
	}

	@Override
	protected void resetAddress()
	{
		waitTicks = 1;
		symbolBuffer = 0;
		addressBuffer.reset();
		canEngage = false;
		super.resetAddress();
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? UniverseStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.Stargate.registerUniverseStargateMethods(wrapper);
	}
	
	@Override
	public void doWhileDialed(Address connectedAddress, int kawooshStartTicks, boolean doKawoosh, int connectionTime)
	{
		super.doWhileDialed(connectedAddress, kawooshStartTicks, doKawoosh, connectionTime);
		
		if(this.level.isClientSide())
			return;
		
		if(connectionTime == 1)
		{
			startSound();
			startRotation(-1, RotationDirection.CLOCKWISE);
		}
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = getChevronLockSpeed(doKawoosh);
		if(connectionTime == chevronLockSpeed.getChevronWaitTicks() * 8)
			endRotation(false);
	}
}
