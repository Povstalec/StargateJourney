package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class CrystalInterfaceEntity extends AbstractInterfaceEntity
{
	public CrystalInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTAL_INTERFACE.get(), pos, state, InterfaceType.CRYSTAL);
	}
	
	protected CrystalInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, InterfaceType.CRYSTAL);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public long capacity()
	{
		return CommonInterfaceConfig.crystal_interface_capacity.get();
	}

	@Override
	public long maxReceive()
	{
		return CommonInterfaceConfig.crystal_interface_max_transfer.get();
	}

	@Override
	public long maxExtract()
	{
		return CommonInterfaceConfig.crystal_interface_max_transfer.get();
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	/*public String getLocalAddress()
	{
		String dimension = this.level.dimension().location().toString();
		String galaxy = Universe.get(this.level).getGalaxiesFromDimension(dimension).getCompound(0).getAllKeys().iterator().next();
		//TODO What if the Dimension is not located inside a Galaxy
		return Universe.get(this.level).getAddressInGalaxyFromDimension(galaxy, dimension);
	}*/
}
