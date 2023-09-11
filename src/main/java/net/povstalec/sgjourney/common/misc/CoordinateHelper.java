package net.povstalec.sgjourney.common.misc;

public class CoordinateHelper
{
	public static float cartesianToPolarR(float x, float y)
	{
		return (float) Math.sqrt(x * x + y * y);
	}
	
	public static float cartesianToPolarPhi(float x, float y)
	{
		return (float) Math.toDegrees(Math.atan2(y, x));
	}
	
	public static float polarToCartesianX(float r, float phi)
	{
		return r * (float) Math.cos(Math.toRadians(phi));
	}
	
	public static float polarToCartesianY(float r, float phi)
	{
		return r * (float) Math.sin(Math.toRadians(phi));
	}
}
