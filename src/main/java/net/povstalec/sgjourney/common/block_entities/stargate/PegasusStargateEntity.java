package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;
import net.povstalec.sgjourney.common.sgjourney.stargate.pegasus.PegasusBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.pegasus.PegasusStargate;

public class PegasusStargateEntity extends StopMotionStargateEntity<PegasusBlockEntityStargate>
{
	public static final String DYNAMC_SYMBOLS = "dynamic_symbols";
	
	public static final int TOTAL_SYMBOLS = 48;
	
	private final ResourceLocation backVariant = StargateJourney.sgjourneyLocation("pegasus_back_chevron");
	
	protected boolean passedOver = false;
	
	public PegasusStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.PEGASUS_STARGATE.get(), StargateInit.PEGASUS.get(), StargateJourney.sgjourneyLocation("pegasus"), pos, state, TOTAL_SYMBOLS, 3, 35);
		this.setOpenSoundLead(13);
	}
	
	@Override
    public void onLoad()
	{
        super.onLoad();

        if(this.level.isClientSide())
        	return;
		
		// Update symbols when loading
		if(generationStep == Step.GENERATED)
			setLocalSymbols();
    }
	
	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.serializeStargateInfo(tag, registries);
		
		tag.putBoolean(DYNAMC_SYMBOLS, dynamicSymbols);
		
		if(!dynamicSymbols)
			symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgraded)
	{
		dynamicSymbols = tag.getBoolean(DYNAMC_SYMBOLS);
		
		if(!dynamicSymbols)
			symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		super.deserializeStargateInfo(tag, registries, isUpgraded);
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public ResourceLocation defaultVariant()
	{
		return ClientStargateConfig.pegasus_stargate_back_lights_up.get() ? backVariant : super.defaultVariant();
	}
	
	public boolean overridePointOfOrigin(ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(!PointOfOrigin.isValid(level.getServer(), pointOfOrigin))
			return false;
		
		symbolInfo().setPointOfOrigin(pointOfOrigin);
		updateClient();
		setChanged();
		return true;
	}
	
	public boolean overrideSymbols(ResourceKey<Symbols> symbols)
	{
		if(!Symbols.isValid(level.getServer(), symbols))
			return false;
		
		symbolInfo().setSymbols(symbols);
		updateClient();
		setChanged();
		return true;
	}
	
	public boolean dynamicSymbols(boolean dynamicSymbols)
	{
		if(this.dynamicSymbols == dynamicSymbols)
			return false;
		
		this.dynamicSymbols = dynamicSymbols;
		updateClient();
		setChanged();
		return true;
	}
	
	public boolean useDynamicSymbols()
	{
		return this.dynamicSymbols;
	}
	
	@Override
	protected StargateInfo.FeedbackMessage encodeChevron(int symbol, StargateInfo.Direction direction, StargateInfo.ChevronSound sound)
	{
		passedOver = false;
		
		return super.encodeChevron(symbol, direction, sound);
	}
	
	public int getChevronPosition(int chevron)
	{
		if(chevron < 0 || chevron > 8)
			return 0;
		
		return 4 * getEngagedChevrons()[chevron - 1];
	}
	
	@Override
	protected void animateSpin()
	{
		// Delay the opening of the gate to the tick after all symbols are encoded
		if(canEngage == CanEngage.YES && !isConnected() && addressBuffer.equals(address) && (!addressBuffer.hasPointOfOrigin() || address.hasPointOfOrigin()))
			engageStargate();
		
		if(isSymbolSpinning())
		{
			int symbol = addressBuffer.symbolAt(symbolBuffer);
			if(symbol == 0)
			{
				if(currentSymbol == getChevronPosition(9))
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					if(!super.directEngageSymbol(symbol, false).feedback().isError() && getAddress().hasPointOfOriginOrMaxLength() && canEngage == CanEngage.READY)
						canEngage = CanEngage.YES; // Stargate is ready to engage
				}
				else
					moveSymbol();
			}
			else if(currentSymbol == getChevronPosition(symbolBuffer + 1))
			{
				if(symbolBuffer % 2 != 0 && !passedOver)
				{
					passedOver = true;
					moveSymbol();
				}
				else
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					if(!super.directEngageSymbol(symbolMap.getOriginalSymbol(symbol), false).feedback().isError() && getAddress().hasPointOfOriginOrMaxLength() && canEngage == CanEngage.READY)
						canEngage = CanEngage.YES; // Stargate is ready to engage
				}
			}
			else
				moveSymbol();
			
			updateClient();
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PegasusStargateEntity stargate)
	{
		StopMotionStargateEntity.tick(level, pos, state, stargate);
	}
	
	@Override
	protected RotationDirection spinDirection()
	{
		return symbolBuffer % 2 != 0 ? RotationDirection.CLOCKWISE : RotationDirection.ANTICLOCKWISE;
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? PegasusStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.Stargate.registerPegasusStargateMethods(wrapper);
	}
	
	@Override
	public void doWhileDialed(Address connectedAddress, int kawooshStartTicks, boolean doKawoosh, int connectionTime)
	{
		super.doWhileDialed(connectedAddress, kawooshStartTicks, doKawoosh, connectionTime);
		
		if(this.level.isClientSide())
			return;
		
		if(this.currentSymbol <= this.maxSymbolIndex)
		{
			StargateInfo.ChevronLockSpeed chevronLockSpeed = getChevronLockSpeed(doKawoosh);
			this.currentSymbol = connectionTime / chevronLockSpeed.getMultiplier();
			this.updateClient();
		}
	}
	
	public void clearSymbols()
	{
		symbolInfo().setPointOfOrigin(null);
		symbolInfo().setSymbols(null);
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
