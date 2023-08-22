package net.povstalec.sgjourney.common.misc;

public class CoordinateHelper
{
	public static float polarToCartesianX(float r, float phi)
	{
		return r * (float) Math.cos(Math.toRadians(phi));
	}
	
	public static float polarToCartesianY(float r, float phi)
	{
		return r * (float) Math.sin(Math.toRadians(phi));
	}
}
