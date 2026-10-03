package net.povstalec.sgjourney.common.misc;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.Objects;

public class ColorCodec<RGBA extends ColorUtil.RGBA> implements Codec<RGBA>
{
	private final Codec<RGBA> first;
	private final Codec<RGBA> second;
	
	public ColorCodec(final Codec<RGBA> first, final Codec<RGBA> second)
	{
		this.first = first;
		this.second = second;
	}
	
	@Override
	public <T> DataResult<Pair<RGBA, T>> decode(final DynamicOps<T> ops, final T input)
	{
		final DataResult<Pair<RGBA, T>> firstRead = first.decode(ops, input);
		if(firstRead.result().isPresent())
			return firstRead;
		
		return second.decode(ops, input);
	}
	
	@Override
	public <T> DataResult<T> encode(final RGBA input, final DynamicOps<T> ops, final T prefix)
	{
		return second.encode(input, ops, prefix);
	}
	
	@Override
	public boolean equals(final Object o)
	{
		if(this == o)
			return true;
		
		if(o == null || getClass() != o.getClass())
			return false;
		
		final ColorCodec<?> eitherCodec = ((ColorCodec<?>) o);
		return Objects.equals(first, eitherCodec.first) && Objects.equals(second, eitherCodec.second);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(first, second);
	}
	
	@Override
	public String toString()
	{
		return "ColorCodec[" + first + ", " + second + ']';
	}
}
