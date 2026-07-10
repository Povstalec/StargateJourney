package net.povstalec.sgjourney.common.misc;

public enum Trinary
{
	DEFAULT((byte) 0),
	TRUE((byte) 1),
	FALSE((byte) -1);
	
	public final byte value;
	
	Trinary(byte value)
	{
		this.value = value;
	}
	
	public boolean isTrue()
	{
		return this == TRUE;
	}
	
	public boolean isFalse()
	{
		return this == FALSE;
	}
	
	public boolean isDefault()
	{
		return this == DEFAULT;
	}
	
	public boolean isNotTrue()
	{
		return this != TRUE;
	}
	
	public boolean isNotFalse()
	{
		return this != FALSE;
	}
	
	public boolean isNotDefault()
	{
		return this != DEFAULT;
	}
	
	public static Trinary fromInt(int value)
	{
		if(value > 0)
			return TRUE;
		if(value < 0)
			return FALSE;
		return DEFAULT;
	}
	
	public static Trinary fromBoolean(boolean value)
	{
		return value ? TRUE : FALSE;
	}
}
