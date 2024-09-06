package net.povstalec.sgjourney.common.blockstates;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.Vec3;

public enum ShieldingPart implements StringRepresentable
{
	LEFT_ABOVE5("left_above5", 1, 5, ShieldingState.MOVING_1),
	ABOVE5("above5", 0, 5, ShieldingState.MOVING_1),
	RIGHT_ABOVE5("right_above5", -1, 5, ShieldingState.MOVING_1),
	
	LEFT2_ABOVE4("left2_above4", 2, 4, ShieldingState.MOVING_1),
	
	LEFT_ABOVE4("left_above4", 1, 4, ShieldingState.MOVING_2),
	ABOVE4("above4", 0, 4, ShieldingState.MOVING_3),
	RIGHT_ABOVE4("right_above4", -1, 4, ShieldingState.MOVING_2),
	
	RIGHT2_ABOVE4("right2_above4", -2, 4, ShieldingState.MOVING_1),
	
	LEFT2_ABOVE3("left2_above3", 2, 3, ShieldingState.MOVING_1),
	
	LEFT_ABOVE3("left_above3", 1, 3, ShieldingState.MOVING_3),
	ABOVE3("above3", 0, 3, ShieldingState.CLOSED),
	RIGHT_ABOVE3("right_above3", -1, 3, ShieldingState.MOVING_3),
	
	RIGHT2_ABOVE3("right2_above3", -2, 3, ShieldingState.MOVING_1),
	
	LEFT2_ABOVE2("left2_above2", 2, 2, ShieldingState.MOVING_1),
	
	LEFT_ABOVE2("left_above2", 1, 2, ShieldingState.MOVING_2),
	ABOVE2("above2", 0, 2, ShieldingState.MOVING_3),
	RIGHT_ABOVE2("right_above2", -1, 2, ShieldingState.MOVING_2),
	
	RIGHT2_ABOVE2("right2_above2", -2, 2, ShieldingState.MOVING_1),
	
	LEFT_ABOVE("left_above", 1, 1, ShieldingState.MOVING_1),
	ABOVE("above", 0, 1, ShieldingState.MOVING_1),
	RIGHT_ABOVE("right_above", -1, 1, ShieldingState.MOVING_1);
	
	public static final ArrayList<ShieldingPart> DEFAULT_PARTS = getParts();
	
	private final String name;
	private final int width;
	private final int height;
	private final ShieldingState requiredShieldingState;
	
	private ShieldingPart(String name, int width, int height, ShieldingState requiredShieldingState) // Minimal shielding state required for the block to physically appear
	{
		this.name = name;
		this.width = width;
		this.height = height;
		this.requiredShieldingState = requiredShieldingState;
	}
	
	public boolean canExist(ShieldingState shieldingState)
	{
		return shieldingState == this.requiredShieldingState || shieldingState.isAfter(this.requiredShieldingState);
	}
	
	public ShieldingState shieldingState()
	{
		return this.requiredShieldingState;
	}
	
	private static ArrayList<ShieldingPart> getParts()
	{
		ArrayList<ShieldingPart> parts = new ArrayList<>();
		
		// Outer
		parts.add(LEFT_ABOVE5);
		parts.add(ABOVE5);
		parts.add(RIGHT_ABOVE5);

		parts.add(LEFT2_ABOVE4);
		parts.add(LEFT2_ABOVE3);
		parts.add(LEFT2_ABOVE2);

		parts.add(LEFT_ABOVE);
		parts.add(ABOVE);
		parts.add(RIGHT_ABOVE);

		parts.add(RIGHT2_ABOVE2);
		parts.add(RIGHT2_ABOVE3);
		parts.add(RIGHT2_ABOVE4);
		
		// Inner
		parts.add(LEFT_ABOVE4);
		parts.add(ABOVE4);
		parts.add(RIGHT_ABOVE4);

		parts.add(LEFT_ABOVE3);
		parts.add(RIGHT_ABOVE3);

		parts.add(LEFT_ABOVE2);
		parts.add(ABOVE2);
		parts.add(RIGHT_ABOVE2);
		
		// Center
		parts.add(ABOVE3);

		return parts;
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
	
	public BlockPos getShieldingPos(BlockPos pos, Direction direction, Orientation orientation)
	{
		Direction newDirection = direction.getClockWise();
		Direction centerDirection = Orientation.getCenterDirection(direction, orientation);
		
		return pos.relative(newDirection, this.width).relative(centerDirection, this.height);
	}
	
	public Vec3 getRelativeRingPos(BlockPos pos, Direction direction, Orientation orientation)
	{
		BlockPos ringPos = getShieldingPos(pos, direction, orientation);
		
		return new Vec3(ringPos.getX() - pos.getX(), ringPos.getY() - pos.getY(), ringPos.getZ() - pos.getZ());
	}

}
