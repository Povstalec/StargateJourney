package net.povstalec.sgjourney.block_entities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.povstalec.sgjourney.capabilities.CCTweakedCapabilities;
import net.povstalec.sgjourney.cctweaked.peripherals.CrystalInterfacePeripheral;
import net.povstalec.sgjourney.data.Universe;
import net.povstalec.sgjourney.init.BlockEntityInit;

public class CrystalInterfaceEntity extends BasicInterfaceEntity
{
	public CrystalInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTAL_INTERFACE.get(), pos, state);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(ModList.get().isLoaded("computercraft") && cap == CCTweakedCapabilities.CAPABILITY_PERIPHERAL)
		{
			return LazyOptional.of(() -> new CrystalInterfacePeripheral(this)).cast();
		}
		return super.getCapability(cap, side);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================

	@Override
	public long capacity()
	{
		return 50000000;
	}

	@Override
	public long maxReceive()
	{
		return 1000000;
	}

	@Override
	public long maxExtract()
	{
		return 1000000;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	public String getLocalAddress()
	{
		String dimension = this.level.dimension().location().toString();
		String galaxy = Universe.get(this.level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys().iterator().next();
		//TODO What if the Dimension is not located inside a Galaxy
		return Universe.get(this.level).getAddressInGalaxyFromDimension(galaxy, dimension);
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, CrystalInterfaceEntity advancedInterface)
	{
		BasicInterfaceEntity.tick(level, pos, state, advancedInterface);
	}
}
