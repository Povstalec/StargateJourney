package net.povstalec.sgjourney.common.block_entities.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.StargatePeripheral;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.StargateInit;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.RotationDirection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.ChevronLockSpeed;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import net.povstalec.sgjourney.common.sgjourney.stargate.andromeda.AndromedaBlockEntityStargate;
import net.povstalec.sgjourney.common.sgjourney.stargate.andromeda.AndromedaStargate;

public class AndromedaStargateEntity extends StopMotionStargateEntity<AndromedaBlockEntityStargate>
{
	public static final int TOTAL_SYMBOLS = 42;
	
	private final ResourceLocation backVariant = StargateJourney.sgjourneyLocation("andromeda_back_chevron");
	
	public AndromedaStargateEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ANDROMEDA_STARGATE.get(), StargateInit.ANDROMEDA.get(), StargateJourney.sgjourneyLocation("andromeda"), pos, state, TOTAL_SYMBOLS, 3, 41);
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
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, HolderLookup.Provider registries, boolean isUpgraded)
	{
		symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
		
		super.deserializeStargateInfo(tag, registries, isUpgraded);
	}
	
	//============================================================================================
	//*******************************************Other********************************************
	//============================================================================================
	
	@Override
	public ResourceLocation defaultVariant()
	{
		return ClientStargateConfig.andromeda_stargate_back_lights_up.get() ? backVariant : super.defaultVariant();
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
				if(currentSymbol == symbol)
				{
					updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
					if(!super.directEngageSymbol(symbol, false).feedback().isError() && getAddress().hasPointOfOriginOrMaxLength() && canEngage == CanEngage.READY)
						canEngage = CanEngage.YES; // Stargate is ready to engage
				}
				else
					moveSymbol();
			}
			else if(currentSymbol == symbolMap.getMappedSymbol(symbol))
			{
				updateInterfaceBlocks(EVENT_STARGATE_ROTATION_STOPPED);
				if(!super.directEngageSymbol(symbolMap.getOriginalSymbol(symbol), false).feedback().isError() && getAddress().hasPointOfOriginOrMaxLength() && canEngage == CanEngage.READY)
					canEngage = CanEngage.YES; // Stargate is ready to engage
			}
			else
				moveSymbol();
			
			updateClient();
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AndromedaStargateEntity stargate)
	{
		StopMotionStargateEntity.tick(level, pos, state, stargate);
	}
	
	public RotationDirection bestRotationDirection(int desiredSymbol)
	{
		int start = getAddress().lastSymbol();
		
		if(desiredSymbol < start)
			desiredSymbol += maxSymbolIndex;
		
		int difference = desiredSymbol - start;
		
		if(difference >= maxSymbolIndex / 2)
			return RotationDirection.ANTICLOCKWISE;
		else
			return RotationDirection.CLOCKWISE;
	}
	
	@Override
	protected RotationDirection spinDirection()
	{
		return bestRotationDirection(symbolMap.getOriginalSymbol(addressBuffer.symbolAt(symbolBuffer))).opposite();
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed(boolean doKawoosh)
	{
		return doKawoosh ? AndromedaStargate.CHEVRON_LOCK_SPEED : ChevronLockSpeed.FAST;
	}

	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<StargatePeripheral> wrapper)
	{
		CCTweakedCompatibility.Stargate.registerAndromedaStargateMethods(wrapper);
	}
	
	@Override
	public void generateAdditional(Step generationStep)
	{
		if(generationStep == Step.SETUP)
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
