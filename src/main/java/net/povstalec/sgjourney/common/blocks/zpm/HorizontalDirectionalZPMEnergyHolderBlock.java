package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;

public abstract class HorizontalDirectionalZPMEnergyHolderBlock extends AbstractZPMEnergyHolderBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public HorizontalDirectionalZPMEnergyHolderBlock(Properties properties)
	{
		super(properties);
	}
	
	public @NotNull BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}
	
	public @NotNull BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
