package net.povstalec.sgjourney.common.misc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class ShuffleHelper
{
	public static <T> List<T> toShuffledList(Stream<T> stream, Random randomSource)
	{
		ObjectArrayList<T> objectarraylist = stream.collect(ObjectArrayList.toList());
		shuffle(objectarraylist, randomSource);
		return objectarraylist;
	}
	
	public static <T> void shuffle(ObjectArrayList<T> stream, Random randomSource)
	{
		int i = stream.size();
		
		for(int j = i; j > 1; --j)
		{
			int k = randomSource.nextInt(j);
			stream.set(j - 1, stream.set(k, stream.get(j - 1)));
		}
		
	}
}
