package net.povstalec.sgjourney.common.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.misc.Orientation;

public enum StargatePart implements StringRepresentable
{
	BASE("base", 0, 0),
	
	LEFT("left", 1, 0),
	
	LEFT2("left2", 2, 0),
	LEFT2_ABOVE("left2_above", 2, 1),
	LEFT3_ABOVE("left3_above", 3, 1),
	
	LEFT3_ABOVE2("left3_above2", 3, 2),
	LEFT3_ABOVE3("left3_above3", 3, 3),
	LEFT3_ABOVE4("left3_above4", 3, 4),
	
	LEFT3_ABOVE5("left3_above5", 3, 5),
	LEFT2_ABOVE5("left2_above5", 2, 5),
	LEFT2_ABOVE6("left2_above6", 2, 6),
	
	LEFT_ABOVE6("left_above6", 1, 6),
	ABOVE6("above6", 0, 6),
	RIGHT_ABOVE6("right_above6", -1, 6),
	
	RIGHT2_ABOVE6("right2_above6", -2, 6),
	RIGHT2_ABOVE5("right2_above5", -2, 5),
	RIGHT3_ABOVE5("right3_above5", -3, 5),
	
	RIGHT3_ABOVE4("right3_above4", -3, 4),
	RIGHT3_ABOVE3("right3_above3", -3, 3),
	RIGHT3_ABOVE2("right3_above2", -3, 2),
	
	RIGHT3_ABOVE("right3_above", -3, 1),
	RIGHT2_ABOVE("right2_above", -2, 1),
	RIGHT2("right2", -2, 0),
	
	RIGHT("right", -1, 0);
	
	private final String name;
	private final int width;
	private final int height;
	
	private StargatePart(String name, int width, int height)
	{
		this.name = name;
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.name;
	}
	
	public BlockPos getBaseBlockPos(BlockPos pos, Direction direction, Orientation orientation)
	{
		Direction newDirection = direction.getCounterClockWise();
		Direction centerDirection = Orientation.getCenterDirection(direction, orientation);
		
		return pos.relative(newDirection, this.width).relative(centerDirection, -this.height);
	}
	
	public BlockPos getRingPos(BlockPos pos, Direction direction, Orientation orientation)
	{
		Direction newDirection = direction.getClockWise();
		Direction centerDirection = Orientation.getCenterDirection(direction, orientation);
		
		return pos.relative(newDirection, this.width).relative(centerDirection, this.height);
	}
	
	public Vec3 getRelativeRingPos(BlockPos pos, Direction direction, Orientation orientation)
	{
		BlockPos ringPos = getRingPos(pos, direction, orientation);
		
		return new Vec3(ringPos.getX() - pos.getX(), ringPos.getY() - pos.getY(), ringPos.getZ() - pos.getZ());
	}

}
