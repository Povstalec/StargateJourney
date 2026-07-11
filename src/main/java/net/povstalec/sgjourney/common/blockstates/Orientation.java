package net.povstalec.sgjourney.common.blockstates;

import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public enum Orientation implements StringRepresentable
{
	REGULAR("regular", 1, 0),
	UPWARD("upward", 0, 1),
	DOWNWARD("downward", 2, -1);
	
	private static final Vec3[] VECTORS = new Vec3[]
	{
		new  Vec3(0, -1, 0), new  Vec3(0, 1, 0), new  Vec3(0, 0, -1), new  Vec3(0, 0, 1), new  Vec3(-1, 0, 0), new  Vec3(1, 0, 0)
	};
	
	private String name;
	private int data2d;
	private int index;
	
	Orientation(String name, int data2d, int index)
	{
		this.name = name;
		this.data2d = data2d;
		this.index = index;
	}
	
	@Override
	public String getSerializedName()
	{
		return this.name;
	}

	public int get2DDataValue()
	{
		return this.data2d;
	}

	public int getIndex()
	{
		return this.index;
	}
	
	public static Orientation getOrientationFromXRot(Player player)
	{
		if(player == null)
			return REGULAR;
		
		float rotation = player.getXRot();
		
		if(rotation > 65.0F)
			return UPWARD;
		else if(rotation < -65.0F)
			return DOWNWARD;
		
		return REGULAR;
	}
	
	public static Direction getCenterDirection(Direction facingDirection, Orientation orientation)
	{
		if(orientation != null && facingDirection != null)
		{
			switch(orientation)
			{
			case UPWARD:
				return facingDirection.getOpposite();
			case DOWNWARD:
				return facingDirection;
			default:
				break;
			}
		}
		
		return Direction.UP;
	}
	
	public static Direction getForwardDirection(Direction facingDirection, Orientation orientation)
	{
		if(orientation != null)
		{
			switch(orientation)
			{
			case UPWARD:
				return Direction.UP;
			case DOWNWARD:
				return Direction.DOWN;
			default:
				break;
			}
		}
		
		return facingDirection;
	}
	
	public static Direction getMultiDirection(Direction facingDirection, Direction direction, Orientation orientation)
	{
		if(orientation == REGULAR)
			return direction;
		
		else if(direction != null)
		{
			switch(direction)
			{
			case UP:
				return orientation == UPWARD ? facingDirection.getOpposite() : facingDirection;
			case DOWN:
				return orientation == UPWARD ? facingDirection : facingDirection.getOpposite();
			default:
				return getForwardDirection(facingDirection, orientation);
			}
		}
		
		return facingDirection;
	}
	
	public static Vec3 getForwardVector(Direction facingDirection, Orientation orientation)
	{
		if(orientation != null)
		{
			switch(orientation)
			{
			case UPWARD:
				return VECTORS[1];
			case DOWNWARD:
				return VECTORS[0];
			default:
				break;
			}
		}
		
		return VECTORS[facingDirection.get3DDataValue()];
	}
	
	public static Vec3 getUpVector(Direction facingDirection, Orientation orientation)
	{
		if(orientation != null && facingDirection != null)
		{
			switch(orientation)
			{
				case UPWARD:
					return VECTORS[facingDirection.getOpposite().get3DDataValue()];
				case DOWNWARD:
					return VECTORS[facingDirection.get3DDataValue()];
				default:
					break;
			}
		}
		
		return VECTORS[1];
	}
}
