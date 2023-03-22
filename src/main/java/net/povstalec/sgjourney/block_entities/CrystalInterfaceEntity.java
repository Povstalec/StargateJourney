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
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.capabilities.CCTweakedCapabilities;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.peripherals.CrystalStargatePeripheral;

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
			if(energyBlockEntity instanceof AbstractStargateEntity stargate)
				return LazyOptional.of(() -> new CrystalStargatePeripheral(this, stargate)).cast();
			
			//TODO
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
	
	/*public void inputSymbol(int symbol)
	{
		stargate.engageSymbol(symbol);
	}*/
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, CrystalInterfaceEntity advancedInterface)
	{
		BasicInterfaceEntity.tick(level, pos, state, advancedInterface);
	}
}
