package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.blockstates.Orientation;

public class CoordinateHelper
{
	public static class CoordinateSystems
	{
		public static final float cartesianToPolarR(float x, float y)
		{
			return (float) Math.sqrt(x * x + y * y);
		}
		
		public static final float cartesianToPolarPhi(float x, float y)
		{
			return (float) Math.toDegrees(Math.atan2(y, x));
		}
		
		public static final float polarToCartesianX(float r, float phi)
		{
			return r * (float) Math.cos(Math.toRadians(phi));
		}
		
		public static final float polarToCartesianY(float r, float phi)
		{
			return r * (float) Math.sin(Math.toRadians(phi));
		}
	}
	
	public static class Relative
	{
		public static final Vec3 rotateVector(Vec3 initialVector, Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation)
		{
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
	    	if(destinationHorizontal - initialHorizontal - 2 < 0)
	    		destinationHorizontal += 4;
	    	
			int yRotation = destinationHorizontal - initialHorizontal - 2;
	    	if(yRotation < 0)
	    		yRotation += 4;
			
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
		
		public static final Vec3 yRotateClockwise(Vec3 vector, int numberOfRotations)
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
		
		public static final Vec3 xRotateClockwise(Vec3 vector, int numberOfRotations, boolean clockwise)
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
		
		public static final Vec3 zRotateClockwise(Vec3 vector, int numberOfRotations, boolean clockwise)
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
		
		public static final Vec3 preserveRelative(Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation, Vec3 initial)
	    {
	    	return rotateVector(initial, initialDirection, initialOrientation, destinationDirection, destinationOrientation);
	    }
		
		public static final float preserveYRot(Direction initialDirection, Direction destinationDirection, float yRot)
		{
			float initialStargateDirection = Mth.wrapDegrees(initialDirection.toYRot());
	    	float destinationStargateDirection = Mth.wrapDegrees(destinationDirection.toYRot());
	    	
	    	float relativeRot = destinationStargateDirection - initialStargateDirection;
	    	
	    	yRot = yRot + relativeRot + 180;
	    	
	    	return yRot;
		}
		

		
		public static final Vec3i yRotateClockwise(Vec3i vector, int numberOfRotations)
		{
			int x = vector.getX();
			int z = vector.getZ();
			
			for(int i = 0; i < numberOfRotations; i++)
			{
				int helper = x;
				x = -z;
				z = helper;
			}
			
			return new Vec3i(x, vector.getY(), z);
		}
		
		public static final Vec3i blockPosOffset(BlockPos initialPos, BlockPos otherPos)
		{
			return new Vec3i(otherPos.getX() - initialPos.getX(), otherPos.getY() - initialPos.getY(), otherPos.getZ() - initialPos.getZ());
		}
		
		public static final Vec3i getRelativeOffset(Direction initialDirection, BlockPos initialPos, BlockPos otherPos)
		{
			int initialRotation = initialDirection.get2DDataValue();
			
			int rotation = -initialRotation;
	    	if(rotation < 0)
	    		rotation += 4;
	    	
	    	Vec3i absoluteOffset = blockPosOffset(initialPos, otherPos);
	    	
	    	return yRotateClockwise(absoluteOffset, rotation);
		}
		
		public static final Vec3i getAbsoluteOffset(Direction initialDirection, Vec3i relativeOffset)
		{
			int destinationRotation = initialDirection.get2DDataValue();
			
			int rotation = destinationRotation;
	    	if(rotation < 0)
	    		rotation += 4;
	    	
	    	return yRotateClockwise(relativeOffset, rotation);
		}
		
		public static final BlockPos getOffsetPos(Direction initialDirection, BlockPos initialPos, Vec3i relativeOffset)
		{
			Vec3i absoluteOffset = getAbsoluteOffset(initialDirection, relativeOffset);
			
			return initialPos.offset(absoluteOffset);
		}
	}
}
