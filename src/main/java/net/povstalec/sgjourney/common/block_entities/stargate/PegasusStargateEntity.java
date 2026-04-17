package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.info.DHDInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.PegasusBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.PegasusStargate;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;

public class PegasusStargateEntity extends IrisStargateEntity<PegasusBlockEntityStargate>
{
	public static final String CAN_ENGAGE = "can_engage";
	public static final String ADDRESS_BUFFER = "AddressBuffer";
	public static final String SYMBOL_BUFFER = "SymbolBuffer";
	public static final String CURRENT_SYMBOL = "CurrentSymbol";
	
	public static final String DYNAMC_SYMBOLS = "DynamicSymbols";
	
	public static final int TOTAL_SYMBOLS = 48;
	
	private final ResourceLocation backVariant = new ResourceLocation(StargateJourney.MODID, "pegasus_back_chevron");
	
	protected int currentSymbol = 0;
	public Address.Mutable addressBuffer = new Address.Mutable();
	protected boolean canEngage = false;
	public int symbolBuffer = 0;
	protected boolean passedOver = false;
	
	protected boolean dynamicSymbols = true;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), StargateInit.PEGASUS.get(), new ResourceLocation(StargateJourney.MODID, "pegasus"), pos, state, TOTAL_SYMBOLS, 3);
		this.setOpenSoundLead(13);
		
		this.dhdInfo = new DHDInfo(this)
		{
			@Override
			public void updateDHD()
			{
				if(hasDHD())
					this.dhd.updateDHD(!stargate.isConnected() || (stargate.isConnected() && stargate.isDialingOut()) ?
							addressBuffer : new Address.Mutable(), canEngage || isConnected());
			}
		};
	}
	
	@Override
    public void onLoad()
	{
        super.onLoad();

        if(this.level.isClientSide())
        	return;
		
		if(generationStep == Step.GENERATED)
			setLocalSymbols();
    }
	
	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		tag.putBoolean(DYNAMC_SYMBOLS, dynamicSymbols);
		
		if(!dynamicSymbols)
			symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		dynamicSymbols = tag.getBoolean(DYNAMC_SYMBOLS);
		
		if(!dynamicSymbols)
			symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	@Override
    public void load(CompoundTag tag)
	{
        super.load(tag);
		
		canEngage = tag.getBoolean(CAN_ENGAGE);
		addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
        symbolBuffer = tag.getInt(SYMBOL_BUFFER);
        currentSymbol = tag.getInt(CURRENT_SYMBOL);
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putBoolean(CAN_ENGAGE, canEngage);
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putInt(CURRENT_SYMBOL, currentSymbol);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = super.getUpdateTag();
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		tag.putBoolean(CAN_ENGAGE, canEngage);
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
			canEngage = tag.getBoolean(CAN_ENGAGE);
			addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
			symbolBuffer = tag.getInt(SYMBOL_BUFFER);
			currentSymbol = tag.getInt(CURRENT_SYMBOL);
		}
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public ResourceLocation defaultVariant()
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ? backVariant : super.defaultVariant();
	}
	
	public void dynamicSymbols(boolean dynamicSymbols)
	{
		this.dynamicSymbols = dynamicSymbols;
		this.setChanged();
	}
	
	public boolean useDynamicSymbols()
	{
		return this.dynamicSymbols;
	}
	
	@Override
	public StargateInfo.Feedback indirectEngageSymbol(int symbol, boolean canEngageStargate)
	{
		if(level.isClientSide())
			return StargateInfo.Feedback.NONE;
		
		canEngage = canEngageStargate;
		
		if(isSymbolOutOfBounds(symbol))
			return StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS;
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED);
		}
		
		if(addressBuffer.containsSymbol(symbol))
			return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS);
		
		if(addressBuffer.getLength() == getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		addressBuffer.addSymbol(symbol);
		
		updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STARTED, spinClockwise());
		
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED);
	}
	
	@Override
	public StargateInfo.Feedback directEngageSymbol(int symbol, boolean canEngageStargate)
	{
		if(!addressBuffer.containsSymbol(symbol))
			addressBuffer.addSymbol(symbol);
		
		return super.directEngageSymbol(symbol, canEngageStargate);
	}
	
	@Override
	protected StargateInfo.Feedback encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		passedOver = false;
		
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		StargateInfo.Feedback feedback = super.encodeChevron(symbol, incoming, encode);
		
		if(addressBuffer.getLength() > getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		return setRecentFeedback(feedback);
	}
	
	@Override
	public StargateInfo.Feedback dhdEngageStargate()
	{
		// Engages the Stargate if all chevrons are encoded, or informs it that it can engage automatically once the last chevron is encoded
		if(address.getLength() < addressBuffer.getLength())
		{
			if(canEngage) // Interrupt Stargate rotation
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
			else
			{
				canEngage = true;
				return StargateInfo.Feedback.NONE;
			}
		}
		else
			return super.dhdEngageStargate();
	}
	
	public int getChevronPosition(int chevron)
	{
		if(chevron < 0 || chevron > 8)
			return 0;
		
		return 4 * getEngagedChevrons()[chevron - 1];
	}
	
	public int getCurrentSymbol()
	{
		return this.currentSymbol;
	}
	
	public boolean isSymbolSpinning()
	{
		return !isConnected() && addressBuffer.getLength() > symbolBuffer;
	}
	
	private void animateSpin()
	{
		if(isSymbolSpinning())
		{
			int symbol = addressBuffer.symbolAt(symbolBuffer);
			if(symbol == 0)
			{
				if(currentSymbol == getChevronPosition(9))
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					directEngageSymbol(symbol, canEngage);
				}
				else
					symbolWork();
			}
			else if(currentSymbol == getChevronPosition(symbolBuffer + 1))
			{
				if(symbolBuffer % 2 != 0 && !passedOver)
				{
					passedOver = true;
					symbolWork();
				}
				else
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					directEngageSymbol(symbol, canEngage);
				}
			}
			else
				symbolWork();
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PegasusStargateEntity stargate)
	{
		AbstractStargateEntity.tick(level, pos, state, stargate);
		
		if(level.isClientSide())
			return;
		
		stargate.animateSpin();
		stargate.updateClient();
	}
	
	private boolean spinClockwise()
	{
		return symbolBuffer % 2 != 0;
	}
	
	private void symbolWork()
	{
		if(spinClockwise())
			currentSymbol++;
		else
			currentSymbol--;

		if(currentSymbol > 35)
			currentSymbol = 0;
		else if(currentSymbol < 0)
			currentSymbol = 35;
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
		canEngage = false;
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

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? PegasusStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.registerPegasusStargateMethods(wrapper);
	}
	
	@Override
	public void doWhileDialed(Address connectedAddress, int kawooshStartTicks, boolean doKawoosh, int connectionTime)
	{
		super.doWhileDialed(connectedAddress, kawooshStartTicks, doKawoosh, connectionTime);
		
		if(this.level.isClientSide())
			return;
		
		if(this.currentSymbol >= 36)
			return;
		
		StargateInfo.ChevronLockSpeed chevronLockSpeed = getChevronLockSpeed(doKawoosh);
		this.currentSymbol = connectionTime / chevronLockSpeed.getMultiplier();
		this.updateClient();
	}
	
	public void setLocalSymbols()
	{
		if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		
		if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
	}
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		if(generationStep == StructureGenEntity.Step.SETUP)
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(null);
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(null);
		}
		else
			setLocalSymbols();
	}
}