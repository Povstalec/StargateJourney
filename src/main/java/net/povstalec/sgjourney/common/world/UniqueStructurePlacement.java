package net.povstalec.sgjourney.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.*;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.init.StructurePlacementInit;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class UniqueStructurePlacement extends RandomSpreadStructurePlacement
{
	public static final int MAX_CHUNKS = 512;
	public static final int BOUND = 64;
	
	public static final Codec<UniqueStructurePlacement> CODEC = RecordCodecBuilder.<UniqueStructurePlacement>mapCodec(instance ->
			instance.group(
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(uniquePlacement -> uniquePlacement.salt()),
					Codec.intRange(-MAX_CHUNKS, MAX_CHUNKS).optionalFieldOf("x_chunk_offset").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkOffsetX)),
					Codec.intRange(-MAX_CHUNKS, MAX_CHUNKS).optionalFieldOf("z_chunk_offset").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkOffsetZ)),
					Codec.intRange(0, BOUND).optionalFieldOf("x_bound").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundX)),
					Codec.intRange(0, BOUND).optionalFieldOf("z_bound").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundZ)),
					Codec.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("x").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkX)),
					Codec.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("z").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkZ))
			).apply(instance, UniqueStructurePlacement::new)).codec();
	
	@Nullable
	protected Integer chunkOffsetX;
	@Nullable
	protected Integer chunkOffsetZ;
	
	@Nullable
	protected Integer chunkBoundX;
	@Nullable
	protected Integer chunkBoundZ;
	
	@Nullable
	protected Integer chunkX;
	@Nullable
	protected Integer chunkZ;
	
	protected UniqueStructurePlacement(int salt, Optional<Integer> chunkX, Optional<Integer> chunkZ, Optional<Integer> chunkOffsetX, Optional<Integer> chunkOffsetZ, Optional<Integer> chunkBoundX, Optional<Integer> chunkBoundZ)
	{
		super(1, 0, RandomSpreadType.LINEAR, salt);
		
		this.chunkX = chunkX.orElse(null);
		this.chunkZ = chunkZ.orElse(null);
		this.chunkOffsetX = chunkOffsetX.orElse(null);
		this.chunkOffsetZ = chunkOffsetZ.orElse(null);
		this.chunkBoundX = chunkBoundX.orElse(null);
		this.chunkBoundZ = chunkBoundZ.orElse(null);
	}
	
	public int getChunkOffsetX()
	{
		return this.chunkOffsetX == null ? 0 : this.chunkOffsetX;
	}
	
	public int getChunkOffsetZ()
	{
		return this.chunkOffsetZ == null ? 0 : this.chunkOffsetZ;
	}
	
	public int getChunkBoundX()
	{
		return this.chunkBoundX == null ? 0 : this.chunkBoundX;
	}
	
	public int getChunkBoundZ()
	{
		return this.chunkBoundZ == null ? 0 : this.chunkBoundZ;
	}
	
	public int getChunkX(long levelSeed)
	{
		if(this.chunkX == null)
		{
			Random random = new Random(levelSeed + 2 + salt());
			int xOffset = getChunkOffsetX();
			int xBound = getChunkBoundX();
			
			this.chunkX = xBound <= 0 ? xOffset : xOffset + random.nextInt(-xBound, xBound + 1);
		}
		
		return this.chunkX;
	}
	
	public int getChunkZ(long levelSeed)
	{
		if(this.chunkZ == null)
		{
			Random random = new Random(levelSeed + 3 + salt());
			int zOffset = getChunkOffsetZ();
			int zBound = getChunkBoundZ();
			
			this.chunkZ = zBound <= 0 ? zOffset : zOffset + random.nextInt(-zBound, zBound + 1);
		}
		
		return this.chunkZ;
	}
	
	@Override
	public ChunkPos getPotentialStructureChunk(long levelSeed, int chunkX, int chunkZ)
	{
		return new ChunkPos(getChunkX(levelSeed), getChunkZ(levelSeed));
	}
	
	@Override
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int chunkX, int chunkZ)
	{
		return chunkX == getChunkX(state.getLevelSeed()) && chunkZ == getChunkZ(state.getLevelSeed());
	}
	
	@Override
	public StructurePlacementType<?> type()
	{
		return StructurePlacementInit.UNIQUE_PLACEMENT.get();
	}
	
	
	
	public static class Stargate extends UniqueStructurePlacement
	{
		public static final Codec<UniqueStructurePlacement.Stargate> CODEC = RecordCodecBuilder.<UniqueStructurePlacement.Stargate>mapCodec(instance ->
				instance.group(
						ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(uniquePlacement -> uniquePlacement.salt())
				).apply(instance, UniqueStructurePlacement.Stargate::new)).codec();
		
		protected Stargate(int salt)
		{
			super(salt, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
		}
		
		@Override
		public int getChunkOffsetX()
		{
			return CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
		}
		
		@Override
		public int getChunkOffsetZ()
		{
			return CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
		}
		
		@Override
		public int getChunkBoundX()
		{
			return CommonGenerationConfig.stargate_generation_x_bound.get();
		}
		
		@Override
		public int getChunkBoundZ()
		{
			return CommonGenerationConfig.stargate_generation_z_bound.get();
		}
		
		@Override
		public StructurePlacementType<?> type()
		{
			return StructurePlacementInit.STARGATE_PLACEMENT.get();
		}
	}
	
	
	
	public static class BuriedStargate extends Stargate
	{
		public static final Codec<UniqueStructurePlacement.BuriedStargate> CODEC = RecordCodecBuilder.<UniqueStructurePlacement.BuriedStargate>mapCodec(instance ->
				instance.group(
						ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(uniquePlacement -> uniquePlacement.salt())
				).apply(instance, UniqueStructurePlacement.BuriedStargate::new)).codec();
		
		protected BuriedStargate(int salt)
		{
			super(salt);
		}
		
		@Override
		public int getChunkBoundX()
		{
			return CommonGenerationConfig.buried_stargate_generation_x_bound.get();
		}
		
		@Override
		public int getChunkBoundZ()
		{
			return CommonGenerationConfig.buried_stargate_generation_z_bound.get();
		}
		
		@Override
		public StructurePlacementType<?> type()
		{
			return StructurePlacementInit.BURIED_STARGATE_PLACEMENT.get();
		}
	}
}
