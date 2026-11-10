package net.povstalec.sgjourney.common.sgjourney;

import net.povstalec.sgjourney.common.misc.Trinary;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSources;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrinaryTest
{
	private static Stream<Arguments> provideParameters()
	{
		return Stream.of(
			Arguments.of(Trinary.DEFAULT, Trinary.DEFAULT),
			Arguments.of(Trinary.DEFAULT, Trinary.TRUE),
			Arguments.of(Trinary.DEFAULT, Trinary.FALSE),
			
			Arguments.of(Trinary.TRUE, Trinary.TRUE),
			Arguments.of(Trinary.TRUE, Trinary.FALSE),
			
			Arguments.of(Trinary.FALSE, Trinary.FALSE)
		);
	}
	
	@ParameterizedTest
	@MethodSource("provideParameters")
	void testTrinary(Trinary a, Trinary b)
	{
		assertEquals(a.or(b), b.or(a));
	}
}
