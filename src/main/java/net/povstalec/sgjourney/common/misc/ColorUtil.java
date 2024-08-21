package net.povstalec.sgjourney.common.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ColorUtil
{
	public static final int MAX_INT_VALUE = 255;
	public static final int MIN_INT_VALUE = 0;
	
	public static final float MAX_FLOAT_VALUE = 1F;
	public static final float MIN_FLOAT_VALUE = 0F;
	
	public static final String RED = "red";
	public static final String GREEN = "green";
	public static final String BLUE = "blue";
	public static final String ALPHA = "alpha";
	
	public static class RGBA
	{
		protected float red;
		protected float green;
		protected float blue;
	    protected float alpha;
		
		public static final Codec<RGBA> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.floatRange(MIN_FLOAT_VALUE, MAX_FLOAT_VALUE).fieldOf(RED).forGetter(RGBA::red),
				Codec.floatRange(MIN_FLOAT_VALUE, MAX_FLOAT_VALUE).fieldOf(GREEN).forGetter(RGBA::green),
				Codec.floatRange(MIN_FLOAT_VALUE, MAX_FLOAT_VALUE).fieldOf(BLUE).forGetter(RGBA::blue),
				Codec.floatRange(MIN_FLOAT_VALUE, MAX_FLOAT_VALUE).fieldOf(ALPHA).forGetter(RGBA::alpha)
				).apply(instance, RGBA::new));
		
		public RGBA(float red, float green, float blue, float alpha)
		{
			if(red > MAX_FLOAT_VALUE || green > MAX_FLOAT_VALUE || blue > MAX_FLOAT_VALUE || alpha > MAX_FLOAT_VALUE)
				throw(new IllegalArgumentException("No value may be higher than 1.0"));
			else if(red < MIN_FLOAT_VALUE || green < MIN_FLOAT_VALUE || blue < MIN_FLOAT_VALUE || alpha < MIN_FLOAT_VALUE)
				throw(new IllegalArgumentException("No value may be lower than 0.0"));
			
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}
		
		public RGBA(int red, int green, int blue, int alpha)
		{
			if(red > MAX_INT_VALUE || green > MAX_INT_VALUE || blue > MAX_INT_VALUE || alpha > MAX_INT_VALUE)
				throw(new IllegalArgumentException("No value may be higher than 255"));
			else if(red < MIN_INT_VALUE || green < MIN_INT_VALUE || blue < MIN_INT_VALUE || alpha < MIN_INT_VALUE)
				throw(new IllegalArgumentException("No value may be lower than 0"));
			
			this.red = red / 255F;
			this.green = green / 255F;
			this.blue = blue / 255F;
			this.alpha = alpha / 255F;
		}
		
		public float red()
		{
			return red;
		}
		
		public float green()
		{
			return green;
		}
		
		public float blue()
		{
			return blue;
		}
		
		public float alpha()
		{
			return alpha;
		}
	}
}
