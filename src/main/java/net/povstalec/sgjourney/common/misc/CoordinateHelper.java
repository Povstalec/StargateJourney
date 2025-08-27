package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

import javax.annotation.Nullable;

public class CoordinateHelper
{
	public static class CoordinateSystems
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
		
		public static float lookAngleY(Vec3 lookAngle)
		{
			return CoordinateSystems.cartesianToPolarPhi((float) lookAngle.x(), (float) lookAngle.z()) + 270;
		}
	}
	
	public static class Relative
	{
		/**
		 * Projects vector a onto b
		 * @param a Vector to be projected
		 * @param b Vector to be projected onto
		 * @return Scalar result of projecting vector a onto vector b
		 */
		public static double projectVectorToScalar(Vec3 a, Vec3 b)
		{
			return a.dot(b) / b.dot(b);
		}
		
		/**
		 * Projects vector a onto b
		 * @param a Vector to be projected
		 * @param b Vector to be projected onto
		 * @return Projection of vector a onto vector b
		 */
		public static Vec3 projectVector(Vec3 a, Vec3 b)
		{
			double scalar = projectVectorToScalar(a, b);
			return b.multiply(scalar, scalar, scalar);
		}
		
		/**
		 * Transforms the vector from a provided orthogonal basis to a vector in the canonical basis
		 * @param vector Vector to be transformed
		 * @param basisX 1st vector of the basis, must be a unit vector
		 * @param basisY 2nd vector of the basis, must be a unit vector
		 * @param basisZ 3rd vector of the basis, must be a unit vector
		 * @return A new vector with the coordinates of the original vector, but transformed to the canonical basis
		 */
		public static Vec3 fromOrthogonalBasis(Vec3 vector, Vec3 basisX, Vec3 basisY, Vec3 basisZ)
		{
			double xProj = projectVectorToScalar(vector, basisX);
			double yProj = projectVectorToScalar(vector, basisY);
			double zProj = projectVectorToScalar(vector, basisZ);
			
			return new Vec3(xProj, yProj, zProj);
		}
		
		/**
		 * Transforms the vector from the canonical basis to the provided orthogonal basis
		 * @param vector Vector to be transformed
		 * @param basisX 1st vector of the basis, must be a unit vector
		 * @param basisY 2nd vector of the basis, must be a unit vector
		 * @param basisZ 3rd vector of the basis, must be a unit vector
		 * @return A new vector with the coordinates of the original vector, but transformed to the provided basis
		 */
		public static Vec3 toOrthogonalBasis(Vec3 vector, Vec3 basisX, Vec3 basisY, Vec3 basisZ)
		{
			return new Vec3(vector.x() * basisX.x() + vector.y() * basisY.x() + vector.z() * basisZ.x(),
							vector.x() * basisX.y() + vector.y() * basisY.y() + vector.z() * basisZ.y(),
							vector.x() * basisX.z() + vector.y() * basisY.z() + vector.z() * basisZ.z());
		}
		
		public static Vec3 rotateVector(Vec3 initialVector, Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation)
		{
			Vec3 initialForward = Orientation.getForwardVector(initialDirection, initialOrientation);
			Vec3 initialUp = Orientation.getUpVector(initialDirection, initialOrientation);
			
			Vec3 destinationForward = Orientation.getForwardVector(destinationDirection, destinationOrientation);
			Vec3 destinationUp = Orientation.getUpVector(destinationDirection, destinationOrientation);
			
			return rotateVector(initialVector, initialForward, initialUp, destinationForward, destinationUp);
		}
		
		public static Vec3 rotateVector(Vec3 initialVector, Vec3 initialForward, Vec3 initialUp, Vec3 destinationForward, Vec3 destinationUp)
		{
			Vec3 initialRight = initialForward.cross(initialUp);
			Vec3 inbetweenVector = fromOrthogonalBasis(initialVector, initialForward, initialUp, initialRight);
			
			Vec3 destinationRight = destinationForward.cross(destinationUp);
			Vec3 destinationVector = toOrthogonalBasis(inbetweenVector, destinationForward.multiply(-1, -1, -1), destinationUp, destinationRight.multiply(-1, -1, -1));
			
			return destinationVector;
		}
		
		public static Vec3 preserveRelative(Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation, Vec3 initial)
	    {
	    	return rotateVector(initial, initialDirection, initialOrientation, destinationDirection, destinationOrientation);
	    }
		
		public static float preserveYRot(Direction initialDirection, Direction destinationDirection, float yRot)
		{
			float initialStargateDirection = Mth.wrapDegrees(initialDirection.toYRot());
	    	float destinationStargateDirection = Mth.wrapDegrees(destinationDirection.toYRot());
	    	
	    	float relativeRot = destinationStargateDirection - initialStargateDirection;
	    	
	    	yRot = yRot + relativeRot + 180;
	    	
	    	return yRot;
		}
		
		
		public static Vec3i yRotateClockwise(Vec3i vector, int numberOfRotations)
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
		
		public static long distanceSqr(Vec3i posA, Vec3i posB)
		{
			if(posA == null || posB == null)
				return Long.MAX_VALUE;
			
			long x = posB.getX() - posA.getX();
			long y = posB.getY() - posA.getY();
			long z = posB.getZ() - posA.getZ();
			
			return x*x + y*y + z*z;
		}
		
		public static Vec3i blockPosOffset(BlockPos initialPos, BlockPos otherPos)
		{
			return new Vec3i(otherPos.getX() - initialPos.getX(), otherPos.getY() - initialPos.getY(), otherPos.getZ() - initialPos.getZ());
		}
		
		public static Vec3i getRelativeOffset(Direction initialDirection, BlockPos initialPos, BlockPos otherPos)
		{
			int initialRotation = initialDirection.get2DDataValue();
			
			int rotation = -initialRotation;
	    	if(rotation < 0)
	    		rotation += 4;
	    	
	    	Vec3i absoluteOffset = blockPosOffset(initialPos, otherPos);
	    	
	    	return yRotateClockwise(absoluteOffset, rotation);
		}
		
		public static Vec3i getAbsoluteOffset(Direction initialDirection, Vec3i relativeOffset)
		{
			if(initialDirection == null)
				return null;
			
			int destinationRotation = initialDirection.get2DDataValue();
			
			int rotation = destinationRotation;
	    	if(rotation < 0)
	    		rotation += 4;
	    	
	    	return yRotateClockwise(relativeOffset, rotation);
		}
		
		public static BlockPos getOffsetPos(Direction initialDirection, BlockPos initialPos, Vec3i relativeOffset)
		{
			Vec3i absoluteOffset = getAbsoluteOffset(initialDirection, relativeOffset);
			
			if(absoluteOffset == null)
				return null;
			
			return initialPos.offset(absoluteOffset);
		}
	}
	
	
	
	public static class StargateCoords
	{
		/**
		 * @param forward Vector in the direction the Stargate is facing
		 * @param up Vector in the direction the relative up direction of the Stargate
		 * @return Vector facing to the right of the provided forward and up vector
		 */
		public static Vec3 getStargateRight(Vec3 forward, Vec3 up)
		{
			return forward.cross(up);
		}
		
		/**
		 * Transforms the vector from a Stargate's relative corodinate system, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction, with the Y and Z vectors being a percentage of the Stargate's radius
		 * to a vector in the absolute coordinate system
		 * @param vector Vector to be transformed
		 * @param forward Stargate's facing direction unit vector
		 * @param up Stargate's relative up direction unit vector
		 * @param right Stargate's relative right direction unit vector
		 * @param radius Stargate's inner radius
		 * @return A new vector with the coordinates of the original vector, but transformed to the canonical basis
		 */
		public static Vec3 fromStargateCoords(Vec3 vector, Vec3 forward, Vec3 up, Vec3 right, double radius)
		{
			Vec3 temp = Relative.fromOrthogonalBasis(vector, forward, up, right);
			return new Vec3(temp.x(), temp.y() / radius, temp.z() / radius);
		}
		
		/**
		 * Transforms the vector from a Stargate's relative corodinate system, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction,
		 * to a vector in the absolute coordinate system
		 * @param vector Vector to be transformed
		 * @param forward Stargate's facing direction unit vector
		 * @param up Stargate's relative up direction unit vector
		 * @param right Stargate's relative right direction unit vector
		 * @return A new vector with the coordinates of the original vector, but transformed to the canonical basis
		 */
		public static Vec3 fromStargateCoords(Vec3 vector, Vec3 forward, Vec3 up, Vec3 right)
		{
			return Relative.fromOrthogonalBasis(vector, forward, up, right);
		}
		
		/**
		 * Mirrors the Vector, used in cases like exiting the Stargate, where the traveler will want to exit the Stargate in the front
		 * @param vector Vector to mirror
		 * @return Mirrored vector
		 */
		public static Vec3 mirrorStargateCoords(Vec3 vector)
		{
			return vector.multiply(-1, -1, -1);
		}
		
		/**
		 * Transforms the vector from an absolute coordinate system to a coordinate system relative to Stargate, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction,
		 * with the Y and Z vectors being a percentage of the Stargate's radius
		 * @param vector Vector to be transformed
		 * @param forward Stargate's facing direction unit vector
		 * @param up Stargate's relative up direction unit vector
		 * @param right Stargate's relative right direction unit vector
		 * @param radius Stargate's inner radius
		 * @return A new vector with the coordinates of the original vector, but transformed to Stargate's relative coordinate system
		 */
		public static Vec3 toStargateCoords(Vec3 vector, Vec3 forward, Vec3 up, Vec3 right, double radius)
		{
			return Relative.toOrthogonalBasis(new Vec3(vector.x(), vector.y() * radius, vector.z() * radius), forward, up, right);
		}
		
		/**
		 * Transforms the vector from an absolute coordinate system to a coordinate system relative to Stargate, where X is the direction which the Stargate is facing, Y is Stargate's up direction and Z is Stargate's right direction
		 * @param vector Vector to be transformed
		 * @param forward Stargate's facing direction unit vector
		 * @param up Stargate's relative up direction unit vector
		 * @param right Stargate's relative right direction unit vector
		 * @return A new vector with the coordinates of the original vector, but transformed to Stargate's relative coordinate system
		 */
		public static Vec3 toStargateCoords(Vec3 vector, Vec3 forward, Vec3 up, Vec3 right)
		{
			return Relative.toOrthogonalBasis(vector, forward, up, right);
		}
		
		@Nullable
		public static BlockPos stargateBlockPos(Stargate stargate)
		{
			if(stargate == null)
				return null;
			
			Vec3 pos = stargate.getPosition();
			if(pos == null)
				return null;
			
			return new BlockPos(pos);
		}
	}
}
