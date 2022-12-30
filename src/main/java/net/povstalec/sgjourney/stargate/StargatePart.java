package net.povstalec.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum StargatePart implements StringRepresentable
{
	LEFT("left"),
	
	LEFT2("left2"),
	LEFT2_ABOVE("left2_above"),
	LEFT3_ABOVE("left3_above"),
	
	LEFT3_ABOVE2("left3_above2"),
	LEFT3_ABOVE3("left3_above3"),
	LEFT3_ABOVE4("left3_above4"),
	
	LEFT3_ABOVE5("left3_above5"),
	LEFT2_ABOVE5("left2_above5"),
	LEFT2_ABOVE6("left2_above6"),
	
	LEFT_ABOVE6("left_above6"),
	ABOVE6("above6"),
	RIGHT_ABOVE6("right_above6"),
	
	RIGHT2_ABOVE6("right2_above6"),
	RIGHT2_ABOVE5("right2_above5"),
	RIGHT3_ABOVE5("right3_above5"),
	
	RIGHT3_ABOVE4("right3_above4"),
	RIGHT3_ABOVE3("right3_above3"),
	RIGHT3_ABOVE2("right3_above2"),
	
	RIGHT3_ABOVE("right3_above"),
	RIGHT2_ABOVE("right2_above"),
	RIGHT2("right2"),
	
	RIGHT("right");
	
	private final String name;
	
	private StargatePart(String name)
	{
		this.name = name;
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
	
	public static BlockPos getMainBlockPos(BlockPos pos, Direction direction, StargatePart part)
	{
		Direction newDirection = direction.getCounterClockWise();
		BlockPos newPos;
		
		switch(part)
		{
		case LEFT:
			newPos = pos.relative(newDirection, 1);
			break;
		case LEFT2:
			newPos = pos.relative(newDirection, 2);
			break;
		case LEFT2_ABOVE:
			newPos = pos.relative(newDirection, 2).below();
			break;
		case LEFT3_ABOVE:
			newPos = pos.relative(newDirection, 3).below();
			break;
			
		case LEFT3_ABOVE2:
			newPos = pos.relative(newDirection, 3).below(2);
			break;
		case LEFT3_ABOVE3:
			newPos = pos.relative(newDirection, 3).below(3);
			break;
		case LEFT3_ABOVE4:
			newPos = pos.relative(newDirection, 3).below(4);
			break;
			
		case LEFT3_ABOVE5:
			newPos = pos.relative(newDirection, 3).below(5);
			break;
		case LEFT2_ABOVE5:
			newPos = pos.relative(newDirection, 2).below(5);
			break;
		case LEFT2_ABOVE6:
			newPos = pos.relative(newDirection, 2).below(6);
			break;
			
		case LEFT_ABOVE6:
			newPos = pos.relative(newDirection, 1).below(6);
			break;
		case ABOVE6:
			newPos = pos.below(6);
			break;
		case RIGHT_ABOVE6:
			newPos = pos.relative(newDirection, -1).below(6);
			break;

		case RIGHT2_ABOVE6:
			newPos = pos.relative(newDirection, -2).below(6);
			break;
		case RIGHT2_ABOVE5:
			newPos = pos.relative(newDirection, -2).below(5);
			break;
		case RIGHT3_ABOVE5:
			newPos = pos.relative(newDirection, -3).below(5);
			break;

		case RIGHT3_ABOVE4:
			newPos = pos.relative(newDirection, -3).below(4);
			break;
		case RIGHT3_ABOVE3:
			newPos = pos.relative(newDirection, -3).below(3);
			break;
		case RIGHT3_ABOVE2:
			newPos = pos.relative(newDirection, -3).below(2);
			break;

		case RIGHT3_ABOVE:
			newPos = pos.relative(newDirection, -3).below();
			break;
		case RIGHT2_ABOVE:
			newPos = pos.relative(newDirection, -2).below();
			break;
		case RIGHT2:
			newPos = pos.relative(newDirection, -2);
			break;
		case RIGHT:
			newPos = pos.relative(newDirection, -1);
			break;
		default:
			newPos = pos;	
		}
		
		return newPos;
	}
	
	public static BlockPos getRingPos(BlockPos pos, Direction direction, StargatePart part)
	{
		Direction newDirection = direction.getClockWise();
		BlockPos newPos;
		
		switch(part)
		{
		case LEFT:
			newPos = pos.relative(newDirection, 1);
			break;
		case LEFT2:
			newPos = pos.relative(newDirection, 2);
			break;
		case LEFT2_ABOVE:
			newPos = pos.relative(newDirection, 2).above();
			break;
		case LEFT3_ABOVE:
			newPos = pos.relative(newDirection, 3).above();
			break;
			
		case LEFT3_ABOVE2:
			newPos = pos.relative(newDirection, 3).above(2);
			break;
		case LEFT3_ABOVE3:
			newPos = pos.relative(newDirection, 3).above(3);
			break;
		case LEFT3_ABOVE4:
			newPos = pos.relative(newDirection, 3).above(4);
			break;
			
		case LEFT3_ABOVE5:
			newPos = pos.relative(newDirection, 3).above(5);
			break;
		case LEFT2_ABOVE5:
			newPos = pos.relative(newDirection, 2).above(5);
			break;
		case LEFT2_ABOVE6:
			newPos = pos.relative(newDirection, 2).above(6);
			break;
			
		case LEFT_ABOVE6:
			newPos = pos.relative(newDirection, 1).above(6);
			break;
		case ABOVE6:
			newPos = pos.above(6);
			break;
		case RIGHT_ABOVE6:
			newPos = pos.relative(newDirection, -1).above(6);
			break;

		case RIGHT2_ABOVE6:
			newPos = pos.relative(newDirection, -2).above(6);
			break;
		case RIGHT2_ABOVE5:
			newPos = pos.relative(newDirection, -2).above(5);
			break;
		case RIGHT3_ABOVE5:
			newPos = pos.relative(newDirection, -3).above(5);
			break;

		case RIGHT3_ABOVE4:
			newPos = pos.relative(newDirection, -3).above(4);
			break;
		case RIGHT3_ABOVE3:
			newPos = pos.relative(newDirection, -3).above(3);
			break;
		case RIGHT3_ABOVE2:
			newPos = pos.relative(newDirection, -3).above(2);
			break;

		case RIGHT3_ABOVE:
			newPos = pos.relative(newDirection, -3).above();
			break;
		case RIGHT2_ABOVE:
			newPos = pos.relative(newDirection, -2).above();
			break;
		case RIGHT2:
			newPos = pos.relative(newDirection, -2);
			break;
		case RIGHT:
			newPos = pos.relative(newDirection, -1);
			break;
		default:
			newPos = pos.above();
		}
		
		return newPos;
	}

}
