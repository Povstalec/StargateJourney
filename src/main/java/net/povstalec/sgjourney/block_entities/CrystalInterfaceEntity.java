package net.povstalec.sgjourney.block_entities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.peripherals.CrystalInterfacePeripheral;

public class CrystalInterfaceEntity extends BasicInterfaceEntity
{
	public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new CrystalInterfacePeripheral(this));
	
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
		if(cap == Capabilities.CAPABILITY_PERIPHERAL)
			return peripheral.cast();
		
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
	
	public void inputSymbol(int symbol)
	{
		stargate.inputSymbol(symbol);
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, CrystalInterfaceEntity advancedInterface)
	{
		BasicInterfaceEntity.tick(level, pos, state, advancedInterface);
	}
}
