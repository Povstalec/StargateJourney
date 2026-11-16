package net.povstalec.sgjourney.common.sgjourney;

public enum RotationDirection
{
	NONE(false, (byte) 0),
	CLOCKWISE(true, (byte) -1),
	ANTICLOCKWISE(true, (byte) 1);
	
	public final boolean isRotating;
	public final byte value;
	
	RotationDirection(boolean isRotating, byte value)
	{
		this.isRotating = isRotating;
		this.value = value;
	}
	
	public static RotationDirection fromByte(byte value)
	{
		return switch(value)
		{
			case -1 -> CLOCKWISE;
			case 1 -> ANTICLOCKWISE;
			default -> NONE;
		};
	}
	
	public RotationDirection opposite()
	{
		return switch(this)
		{
			case CLOCKWISE -> ANTICLOCKWISE;
			case ANTICLOCKWISE -> CLOCKWISE;
			default -> NONE;
		};
	}
}
