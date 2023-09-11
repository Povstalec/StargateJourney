package net.povstalec.sgjourney.common.misc;

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
	
	public static Vec3 getEffectiveVector(Direction facingDirection, Orientation orientation)
	{
		if(orientation != null)
		{
			switch(orientation)
			{
			case UPWARD:
				return new Vec3(0, 1, 0);
			case DOWNWARD:
				return new Vec3(0, -1, 0);
			default:
				break;
			}
		}
		
		Vec3i facingNormal = facingDirection.getNormal();
		
		return new Vec3(facingNormal.getX(), facingNormal.getY(), facingNormal.getZ());
	}
}
