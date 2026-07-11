package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.info.DHDInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.PegasusStargate;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;

public class PegasusStargateEntity extends IrisStargateEntity
{
	public static final String ADDRESS_BUFFER = "address_buffer";
	public static final String SYMBOL_BUFFER = "symbol_buffer";
	public static final String CURRENT_SYMBOL = "current_symbol";
	
	public static final String DYNAMC_SYMBOLS = "dynamic_symbols";
	
	public static final int TOTAL_SYMBOLS = 48;
	
	private final ResourceLocation backVariant = StargateJourney.sgjourneyLocation("pegasus/pegasus_back_chevron");
	
	public int currentSymbol = 0;
	public Address.Mutable addressBuffer = new Address.Mutable();
	public int symbolBuffer = 0;
	private boolean passedOver = false;
	
	protected boolean dynamicSymbols = true;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), StargateJourney.sgjourneyLocation("pegasus/pegasus"), pos, state,
				TOTAL_SYMBOLS, StargateInfo.Gen.GEN_3, 3);
		this.setOpenSoundLead(13);
		
		this.dhdInfo = new DHDInfo(this)
		{
			@Override
			public void updateDHD()
			{
				if(hasDHD())
					this.dhd.updateDHD(!stargate.isConnected() || (stargate.isConnected() && stargate.isDialingOut()) ?
							addressBuffer : new Address.Mutable(), addressBuffer.hasPointOfOrigin() || stargate.isConnected());
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
	public CompoundTag serializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.serializeStargateInfo(tag, registries);
		
		tag.putBoolean(DYNAMC_SYMBOLS, dynamicSymbols);
		
		if(!dynamicSymbols)
		{
			tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
			tag.putString(SYMBOLS, symbolInfo().symbols().toString());
		}
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgraded)
	{
		dynamicSymbols = tag.getBoolean(DYNAMC_SYMBOLS);
		
		if(!dynamicSymbols)
		{
			symbolInfo().setPointOfOrigin(ResourceLocation.tryParse(tag.getString(POINT_OF_ORIGIN)));
			symbolInfo().setSymbols(ResourceLocation.tryParse(tag.getString(SYMBOLS)));
		}
		
		super.deserializeStargateInfo(tag, registries, isUpgraded);
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		
		addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
        symbolBuffer = tag.getInt(SYMBOL_BUFFER);
        currentSymbol = tag.getInt(CURRENT_SYMBOL);
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putInt(CURRENT_SYMBOL, currentSymbol);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		CompoundTag tag = super.getUpdateTag(registries);
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
		
		tag.putIntArray(ADDRESS_BUFFER, addressBuffer.getArray());
		tag.putInt(SYMBOL_BUFFER, symbolBuffer);
		tag.putInt(CURRENT_SYMBOL, currentSymbol);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries)
	{
		super.onDataPacket(net, packet, registries);
		CompoundTag tag = packet.getTag();
		
		if (tag.contains(POINT_OF_ORIGIN))
			symbolInfo().setPointOfOrigin(ResourceLocation.tryParse(tag.getString(POINT_OF_ORIGIN)));
		
		if(tag.contains(SYMBOLS))
			symbolInfo().setSymbols(ResourceLocation.tryParse(tag.getString(SYMBOLS)));
		
		addressBuffer.fromArray(tag.getIntArray(ADDRESS_BUFFER));
		symbolBuffer = tag.getInt(SYMBOL_BUFFER);
		currentSymbol = tag.getInt(CURRENT_SYMBOL);
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
	public StargateInfo.Feedback dhdEngageSymbol(int symbol)
	{
		if(level.isClientSide())
			return StargateInfo.Feedback.NONE;
		
		if(isSymbolOutOfBounds(symbol))
			return StargateInfo.Feedback.SYMBOL_OUT_OF_BOUNDS;
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
			else
				return setRecentFeedback(StargateInfo.Feedback.ENCODE_WHEN_CONNECTED);
		}
		
		if(addressBuffer.containsSymbol(symbol))
		{
			if(symbol == 0)
				return resetStargate(StargateInfo.Feedback.INCOMPLETE_ADDRESS);
			else
				return setRecentFeedback(StargateInfo.Feedback.SYMBOL_IN_ADDRESS);
		}
		
		if(addressBuffer.getLength() == getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		addressBuffer.addSymbol(symbol);
		
		updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STARTED, spinClockwise());
		
		return setRecentFeedback(StargateInfo.Feedback.SYMBOL_ENCODED);
	}
	
	@Override
	public StargateInfo.Feedback engageSymbol(int symbol)
	{
		if(!addressBuffer.containsSymbol(symbol))
			addressBuffer.addSymbol(symbol);
		
		return super.engageSymbol(symbol);
	}
	
	@Override
	protected StargateInfo.Feedback lockPrimaryChevron()
	{
		if(!this.level.isClientSide())
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		return super.lockPrimaryChevron();
	}
	
	@Override
	public StargateInfo.Feedback encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		passedOver = false;
		
		if(!this.level.isClientSide())
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.StargateRotation(worldPosition, true));
		StargateInfo.Feedback feedback = super.encodeChevron(symbol, incoming, encode);
		
		if(addressBuffer.getLength() > getAddress().getLength())
		{
			if(!this.level.isClientSide())
				PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientBoundSoundPackets.StargateRotation(worldPosition, false));
		}
		
		return setRecentFeedback(feedback);
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
	
	private void animateSpin()
	{
		if(!isConnected() && addressBuffer.getLength() > symbolBuffer)
		{
			int symbol = addressBuffer.symbolAt(symbolBuffer);
			if(symbol == 0)
			{
				if(currentSymbol == getChevronPosition(9))
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					engageSymbol(symbol);
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
					engageSymbol(symbol);
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
	protected void resetAddress(boolean updateInterfaces)
	{
		currentSymbol = 0;
		symbolBuffer = 0;
		addressBuffer.reset();
		super.resetAddress(updateInterfaces);
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
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
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
		if(!PointOfOrigin.validLocation(level.getServer(), symbolInfo().pointOfOrigin()))
			symbolInfo().setPointOfOrigin(PointOfOrigin.fromDimension(level.getServer(), level.dimension()));
		
		if(!Symbols.validLocation(level.getServer(), symbolInfo().symbols()))
			symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
	}
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		if(generationStep == StructureGenEntity.Step.SETUP)
		{
			if(!PointOfOrigin.validLocation(level.getServer(), symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(StargateJourney.EMPTY_LOCATION);
			
			if(!Symbols.validLocation(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(StargateJourney.EMPTY_LOCATION);
		}
		else
			setLocalSymbols();
	}
}