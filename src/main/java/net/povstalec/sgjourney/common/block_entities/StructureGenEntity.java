package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;

public interface StructureGenEntity
{
	String GENERATION_STEP = "generation_step";
	
	void generateInStructure(WorldGenLevel level, RandomSource randomSource);
	
	enum Step
	{
		GENERATED, // Already generated
		READY, // Can generate as soon as the Block Entity is loaded
		SETUP; // Prepared for generation, won't generate unless manually triggered
		
		public byte byteValue()
		{
			return (byte) ordinal();
		}
		
		public static Step fromByte(byte value)
		{
			return switch(value)
			{
			case 1 -> READY;
			case 2 -> SETUP;
			default -> GENERATED;
			};
		}
	}
}
