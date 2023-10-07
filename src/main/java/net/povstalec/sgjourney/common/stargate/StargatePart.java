package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;

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
	
	RIGHT("right", -1, 0),


	/** SPECIFIC FOR VERTICALLY PLACED TOLLAN GATE **/
	LEFT2_ABOVE4("left2_above4", 2, 4),
	LEFT_ABOVE5("left_above5", 1, 5),
	ABOVE5("above5", 0, 5),
	RIGHT_ABOVE5("right_above5", -1, 5),
	RIGHT2_ABOVE4("right2_above4", -2, 4);
	
	public static final ArrayList<StargatePart> DEFAULT_PARTS = getParts(StargateType.DEFAULT);
	public static final ArrayList<StargatePart> TOLLAN_PARTS = getParts(StargateType.TOLLAN);
	
	private final String name;
	private final int width;
	private final int height;
	
	private StargatePart(String name, int width, int height)
	{
		this.name = name;
		this.width = width;
		this.height = height;
	}
	
	private static ArrayList<StargatePart> getParts(StargateType type)
	{
		ArrayList<StargatePart> parts = new ArrayList<>();
		
		parts.add(BASE);
		parts.add(LEFT);
		parts.add(LEFT2);
		parts.add(LEFT2_ABOVE);
		parts.add(LEFT3_ABOVE);
		parts.add(LEFT3_ABOVE2);
		parts.add(LEFT3_ABOVE3);
		parts.add(LEFT3_ABOVE4);
		if(type == StargateType.TOLLAN)
		{
			parts.add(LEFT2_ABOVE4);
			parts.add(LEFT2_ABOVE5);
			parts.add(LEFT_ABOVE5);
			parts.add(ABOVE5);
			parts.add(RIGHT_ABOVE5);
			parts.add(RIGHT2_ABOVE5);
			parts.add(RIGHT2_ABOVE4);
		}
		else
		{
			parts.add(LEFT3_ABOVE5);
			parts.add(LEFT2_ABOVE5);
			parts.add(LEFT2_ABOVE6);
			parts.add(LEFT_ABOVE6);
			parts.add(ABOVE6);
			parts.add(RIGHT_ABOVE6);
			parts.add(RIGHT2_ABOVE6);
			parts.add(RIGHT2_ABOVE5);
			parts.add(RIGHT3_ABOVE5);
		}
		parts.add(RIGHT3_ABOVE4);
		parts.add(RIGHT3_ABOVE3);
		parts.add(RIGHT3_ABOVE2);
		parts.add(RIGHT3_ABOVE);
		parts.add(RIGHT2_ABOVE);
		parts.add(RIGHT2);
		parts.add(RIGHT);
		

		System.out.println(parts.toString());

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
