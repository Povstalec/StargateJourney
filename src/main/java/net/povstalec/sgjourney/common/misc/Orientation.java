package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;

public enum Orientation implements StringRepresentable
{
	REGULAR("regular", 1),
	UPWARD("upward", 0),
	DOWNWARD("downward", 2);
	
	private String name;
	private int data2d;
	
	Orientation(String name, int data2d)
	{
		this.name = name;
		this.data2d = data2d;
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
	
	public static Orientation getOrientationFromXRot(Player player)
	{
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
	
	public static Direction getEffectiveDirection(Direction facingDirection, Orientation orientation)
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
}
