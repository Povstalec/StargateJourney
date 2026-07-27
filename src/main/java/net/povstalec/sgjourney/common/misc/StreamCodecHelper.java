package net.povstalec.sgjourney.common.misc;

import net.minecraft.network.codec.StreamCodec;

import java.util.function.Supplier;

public class StreamCodecHelper
{
	public static <B, C> StreamCodec<B, C> ofNothing(final Supplier<C> supplier)
	{
		return new StreamCodec<>()
		{
			public C decode(B b)
			{
				return supplier.get();
			}
			
			public void encode(B b, C c) {}
		};
	}
}
