package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.energy_cores.FusionCoreItem;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class PegasusDHDEntity extends CrystalDHDEntity
{
	public PegasusDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.PEGASUS_DHD.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(this.level.isClientSide())
			return;
		
		// Update symbols when loading
		if(generationStep == Step.GENERATED)
		{
			if(stargateCache.isPresent()) // Copy from connected Stargate
				setSymbolsFromStargate();
			else // Generate from Dimension
				setLocalSymbols();
		}
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		addTransferCrystals(crystalHandler);
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.pegasus_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long getCapacity()
	{
		return CommonDHDConfig.pegasus_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonDHDConfig.pegasus_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyTransfer()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.pegasus_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.PEGASUS_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.PEGASUS_DHD_PRESS.get();
	}
	

	// TODO Temporary function for replacing old Energy Crystals with new Transfer Crystals
	public static void addTransferCrystals(ItemStackHandler itemHandler)
	{
		int slots = itemHandler.getSlots();
		
		for(int i = 0; i < slots; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(i);
			
			if(stack.is(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()) && stack.hasTag())
			{
				if(stack.getTag().getString(CRYSTAL_MODE).equals(ENERGY_TRANSFER))
				{
					itemHandler.setStackInSlot(i, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get()));
					StargateJourney.LOGGER.info("Replaced Transfer Crystal");
				}
			}
		}
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
	public void clearSymbols()
	{
		symbolInfo().setPointOfOrigin(null);
		symbolInfo().setSymbols(null);
	}
	
	@Override
	protected void generateEnergyCore()
	{
		energyItemHandler.setStackInSlot(0, FusionCoreItem.randomFusionCore(CommonTechConfig.fusion_core_fuel_capacity.get() / 2, CommonTechConfig.fusion_core_fuel_capacity.get()));
	}
	
	@Override
	public void generateAdditional(StructureGenEntity.Step generationStep)
	{
		if(generationStep == StructureGenEntity.Step.SETUP) // Set empty symbols before it's generated in a structure
		{
			if(!PointOfOrigin.isValid(level.getServer(), symbolInfo().pointOfOrigin()))
				symbolInfo().setPointOfOrigin(null);
			
			if(!Symbols.isValid(level.getServer(), symbolInfo().symbols()))
				symbolInfo().setSymbols(null);
		}
		else if(stargateCache.isPresent()) // Copy from connected Stargate
			setSymbolsFromStargate();
		else // Generate from Dimension
			setLocalSymbols();
		
		crystalCache.recalculateCrystals();
	}
	
	@Override
	protected void generateCrystals()
	{
		crystalHandler.setStackInSlot(0, new ItemStack(ItemInit.LARGE_CONTROL_CRYSTAL.get()));
		crystalHandler.setStackInSlot(1, new ItemStack(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()));
		crystalHandler.setStackInSlot(2, new ItemStack(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get()));
		crystalHandler.setStackInSlot(3, new ItemStack(ItemInit.ADVANCED_ENERGY_CRYSTAL.get()));
		crystalHandler.setStackInSlot(6, new ItemStack(ItemInit.ADVANCED_COMMUNICATION_CRYSTAL.get()));
		crystalHandler.setStackInSlot(7, new ItemStack(ItemInit.ADVANCED_TRANSFER_CRYSTAL.get()));
	}

}
