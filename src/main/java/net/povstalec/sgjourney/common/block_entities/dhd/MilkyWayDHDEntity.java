package net.povstalec.sgjourney.common.block_entities.dhd;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.config.CommonDHDConfig;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.items.energy_cores.FusionCoreItem;
import org.jetbrains.annotations.NotNull;

public class MilkyWayDHDEntity extends CrystalDHDEntity
{
	public MilkyWayDHDEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.MILKY_WAY_DHD.get(), pos, state);
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		addTransferCrystals(itemHandler);
		
		symbolInfo().setPointOfOrigin(new ResourceLocation(tag.getString(POINT_OF_ORIGIN)));
		symbolInfo().setSymbols(new ResourceLocation(tag.getString(SYMBOLS)));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putString(POINT_OF_ORIGIN, symbolInfo().pointOfOrigin().toString());
		tag.putString(SYMBOLS, symbolInfo().symbols().toString());
	}
	
	protected long buttonPressEnergyCost()
	{
		return CommonDHDConfig.milky_way_dhd_button_press_energy_cost.get();
	}
	
	@Override
	protected long capacity()
	{
		return CommonDHDConfig.milky_way_dhd_energy_buffer_capacity.get();
	}
	
	@Override
	protected long maxReceive()
	{
		return CommonDHDConfig.milky_way_dhd_max_energy_receive.get();
	}
	
	@Override
	public long maxEnergyDeplete()
	{
		return this.maxEnergyTransfer < 0 ? CommonDHDConfig.milky_way_dhd_max_energy_extract.get() : this.maxEnergyTransfer;
	}

	@Override
	protected SoundEvent getEnterSound()
	{
		return SoundInit.MILKY_WAY_DHD_ENTER.get();
	}

	@Override
	protected SoundEvent getPressSound()
	{
		return SoundInit.MILKY_WAY_DHD_PRESS.get();
	}

	// TODO Temporary function for replacing old Energy Crystals with new Transfer Crystals
	public static void addTransferCrystals(ItemStackHandler itemHandler)
	{
		int slots = itemHandler.getSlots();
		
		for(int i = 0; i < slots; i++)
		{
			ItemStack stack = itemHandler.getStackInSlot(i);
			
			if(stack.is(ItemInit.ENERGY_CRYSTAL.get()) && stack.hasTag())
			{
				if(stack.getTag().getString(CRYSTAL_MODE).equals(ENERGY_TRANSFER))
				{
					itemHandler.setStackInSlot(i, new ItemStack(ItemInit.TRANSFER_CRYSTAL.get()));
					StargateJourney.LOGGER.info("Replaced Transfer Crystal");
				}
			}
		}
	}
	
	@Override
	protected void generateEnergyCore()
	{
		super.generateEnergyCore();
		
		energyItemHandler.setStackInSlot(0, FusionCoreItem.randomFusionCore(CommonTechConfig.fusion_core_fuel_capacity.get() / 3, CommonTechConfig.fusion_core_fuel_capacity.get()));
	}
}
