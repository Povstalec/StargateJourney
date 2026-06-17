package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.NaquadahFuelRodItem;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

public class ClassicDHDEntity extends CrystalDHDEntity
{
	public ClassicDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CLASSIC_DHD.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(level.isClientSide())
			return;
		
		if(stargateCache.isPresent()) // Copy from connected Stargate
			setSymbolsFromStargate();
		else // Generate from Dimension
			setLocalSymbols();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		
		symbolInfo().loadFromCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		symbolInfo().saveToCompoundTag(tag, POINT_OF_ORIGIN, SYMBOLS);
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.classic_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long getCapacity()
	{
		return CommonDHDConfig.classic_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonDHDConfig.classic_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyTransfer()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.classic_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.CLASSIC_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.CLASSIC_DHD_PRESS.get();
	}
	
	@Override
	protected void generateEnergyCore()
	{
		energyItemHandler.setStackInSlot(0, new ItemStack(ItemInit.NAQUADAH_GENERATOR_CORE.get()));
		energyItemHandler.setStackInSlot(1, NaquadahFuelRodItem.randomFuelRod(CommonNaquadahGeneratorConfig.naquadah_rod_max_fuel.get() / 2, CommonNaquadahGeneratorConfig.naquadah_rod_max_fuel.get()));
	}
	
	//============================================================================================
	//*****************************************Generation*****************************************
	//============================================================================================
	
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
		else if(stargateCache.isPresent()) // Copy from connected Stargate
			setSymbolsFromStargate();
		else // Generate from Dimension
			setLocalSymbols();
		
		recalculateCrystals();
	}
	
	@Override
	protected void generateCrystals()
	{
		crystalHandler.setStackInSlot(0, new ItemStack(ItemInit.LARGE_CONTROL_CRYSTAL.get()));
		crystalHandler.setStackInSlot(1, new ItemStack(ItemInit.ENERGY_CRYSTAL.get()));
		crystalHandler.setStackInSlot(2, new ItemStack(ItemInit.COMMUNICATION_CRYSTAL.get()));
		crystalHandler.setStackInSlot(3, new ItemStack(ItemInit.ENERGY_CRYSTAL.get()));
		crystalHandler.setStackInSlot(5, new ItemStack(ItemInit.ENERGY_CRYSTAL.get()));
		crystalHandler.setStackInSlot(7, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get()));
	}
}
