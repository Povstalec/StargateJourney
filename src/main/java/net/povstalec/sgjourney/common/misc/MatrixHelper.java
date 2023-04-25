package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.phys.Vec3;

public class MatrixHelper
{
	public static Vec3 rotateVector(Vec3 initialVector, Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation)
	{
		//System.out.println("Before " + initialVector.toString());
		Vec3 vector = initialVector;
    	int initialHorizontal = initialDirection.get2DDataValue();
    	int destinationHorizontal = destinationDirection.get2DDataValue();
		Axis initialRotationAxis = initialDirection.getClockWise().getAxis();
		Axis destinationRotationAxis = destinationDirection.getClockWise().getAxis();
    	int initialVertical = initialOrientation.get2DDataValue();
    	int destinationVertical = destinationOrientation.get2DDataValue();
    	int regular = Orientation.REGULAR.get2DDataValue();
    	
    	// Rotate to default position
    	if(initialVertical > regular)
    		regular += 4;
    	
		int xzRotation1 = regular - initialVertical;
		boolean negativeAxis1 = initialDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		
		if(initialRotationAxis == Direction.Axis.X)
			vector = xRotateClockwise(vector, xzRotation1, negativeAxis1);
		else
			vector = zRotateClockwise(vector, xzRotation1, negativeAxis1);
    	
		// Rotate horizontally
    	if(destinationHorizontal - 2 < initialHorizontal)
    		destinationHorizontal += 4;

		int yRotation = destinationHorizontal - initialHorizontal - 2;
		vector = yRotateClockwise(vector, yRotation);
		
		
    	//Rotate to Stargate position
    	if(destinationVertical < Orientation.REGULAR.get2DDataValue())
    		destinationVertical += 4;
    	
		int xzRotation2 = destinationVertical - Orientation.REGULAR.get2DDataValue();
		boolean negativeAxis2 = destinationDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
		
		if(destinationRotationAxis == Direction.Axis.X)
			vector = xRotateClockwise(vector, xzRotation2, negativeAxis2);
		else
			vector = zRotateClockwise(vector, xzRotation2, negativeAxis2);
		
		return vector;
	}
	
	public static Vec3 yRotateClockwise(Vec3 vector, int numberOfRotations)
	{
		double x = vector.x();
		double z = vector.z();
		
		for(int i = 0; i < numberOfRotations; i++)
		{
			double helper = x;
			x = -z;
			z = helper;
		}
		
		return new Vec3(x, vector.y, z);
	}
	
	public static Vec3 xRotateClockwise(Vec3 vector, int numberOfRotations, boolean clockwise)
	{
		double y = vector.y();
		double z = vector.z();
		
		int multiplier = clockwise ? 1 : -1;
		
		for(int i = 0; i < numberOfRotations; i++)
		{
			double helper = y;
			y = -z * multiplier;
			z = helper * multiplier;
		}
		
		return new Vec3(vector.x, y, z);
	}
	
	public static Vec3 zRotateClockwise(Vec3 vector, int numberOfRotations, boolean clockwise)
	{
		double x = vector.x();
		double y = vector.y();
		
		int multiplier = clockwise ? 1 : -1;
		
		for(int i = 0; i < numberOfRotations; i++)
		{
			double helper = y;
			y = -x * multiplier;
			x = helper * multiplier;
		}
		
		return new Vec3(x, y, vector.z);
	}
}
