package net.povstalec.sgjourney.common.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.*;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.init.StructurePlacementInit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class UniqueStructurePlacement extends RandomSpreadStructurePlacement
{
	public static final int MAX_CHUNKS = 512;
	public static final int MAX_BOUND = 64;
	
	public static final Codec<UniqueStructurePlacement> CODEC = RecordCodecBuilder.<UniqueStructurePlacement>mapCodec(instance ->
			instance.group(
					ExtraCodecs.NON_NEGATIVE_INT.fieldOf("salt").forGetter(uniquePlacement -> uniquePlacement.salt()),
					Codec.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("x").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkX)),
					Codec.intRange(Integer.MIN_VALUE, Integer.MAX_VALUE).optionalFieldOf("z").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkZ)),
					Codec.intRange(-MAX_CHUNKS, MAX_CHUNKS).optionalFieldOf("x_chunk_offset").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkOffsetX)),
					Codec.intRange(-MAX_CHUNKS, MAX_CHUNKS).optionalFieldOf("z_chunk_offset").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkOffsetZ)),
					Codec.intRange(0, MAX_BOUND).optionalFieldOf("x_bound_min").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundMinX)),
					Codec.intRange(0, MAX_BOUND).optionalFieldOf("z_bound_min").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundMinZ)),
					Codec.intRange(0, MAX_BOUND).optionalFieldOf("x_bound_max").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundMaxX)),
					Codec.intRange(0, MAX_BOUND).optionalFieldOf("z_bound_max").forGetter(uniquePlacement -> Optional.ofNullable(uniquePlacement.chunkBoundMaxZ))
			).apply(instance, UniqueStructurePlacement::new)).codec();
	
	@Nullable
	protected Integer chunkOffsetX;
	@Nullable
	protected Integer chunkOffsetZ;
	
	@Nullable
	protected Integer chunkBoundMinX;
	@Nullable
	protected Integer chunkBoundMinZ;
	@Nullable
	protected Integer chunkBoundMaxX;
	@Nullable
	protected Integer chunkBoundMaxZ;
	
	@Nullable
	protected Integer chunkX;
	@Nullable
	protected Integer chunkZ;
	
	protected UniqueStructurePlacement(int salt, Optional<Integer> chunkX, Optional<Integer> chunkZ, Optional<Integer> chunkOffsetX, Optional<Integer> chunkOffsetZ,
									   Optional<Integer> chunkBoundMinX, Optional<Integer> chunkBoundMinZ, Optional<Integer> chunkBoundMaxX, Optional<Integer> chunkBoundMaxZ)
	{
		super(1, 0, RandomSpreadType.LINEAR, salt);
		
		this.chunkX = chunkX.orElse(null);
		this.chunkZ = chunkZ.orElse(null);
		this.chunkOffsetX = chunkOffsetX.orElse(null);
		this.chunkOffsetZ = chunkOffsetZ.orElse(null);
		this.chunkBoundMinX = chunkBoundMinX.orElse(null);
		this.chunkBoundMinZ = chunkBoundMinZ.orElse(null);
		this.chunkBoundMaxX = chunkBoundMaxX.orElse(null);
		this.chunkBoundMaxZ = chunkBoundMaxZ.orElse(null);
		
		if(this.chunkBoundMinX != null && this.chunkBoundMaxX != null && this.chunkBoundMinX > this.chunkBoundMaxX)
			throw new IllegalArgumentException("x_bound_min must be less than x_bound_max");
		
		if(this.chunkBoundMinZ != null && this.chunkBoundMaxZ != null && this.chunkBoundMinZ > this.chunkBoundMaxZ)
			throw new IllegalArgumentException("z_bound_min must be less than z_bound_max");
	}
	
	public int getChunkOffsetX()
	{
		return this.chunkOffsetX == null ? 0 : this.chunkOffsetX;
	}
	
	public int getChunkOffsetZ()
	{
		return this.chunkOffsetZ == null ? 0 : this.chunkOffsetZ;
	}
	
	public int getChunkBoundMinX()
	{
		return this.chunkBoundMinX == null ? 0 : this.chunkBoundMinX;
	}
	
	public int getChunkBoundMinZ()
	{
		return this.chunkBoundMinZ == null ? 0 : this.chunkBoundMinZ;
	}
	
	public int getChunkBoundMaxX()
	{
		return this.chunkBoundMaxX == null ? MAX_BOUND : this.chunkBoundMaxX;
	}
	
	public int getChunkBoundMaxZ()
	{
		return this.chunkBoundMaxZ == null ? MAX_BOUND : this.chunkBoundMaxZ;
	}
	
	public int getChunkX(long levelSeed)
	{
		if(this.chunkX == null)
		{
			int xOffset = getChunkOffsetX();
			int xBoundMax = getChunkBoundMaxX();
			
			if(xBoundMax == 0)
				return xOffset;
			
			Random random = new Random(levelSeed + 2 + salt());
			int xBoundMin = getChunkBoundMinX();
			int xBound = random.nextBoolean() ? random.nextInt(-xBoundMax, -xBoundMin + 1) : random.nextInt(xBoundMin, xBoundMax + 1);
			
			this.chunkX = xBoundMax <= 0 ? xOffset : xOffset + xBound;
		}
		
		return this.chunkX;
	}
	
	public int getChunkZ(long levelSeed)
	{
		if(this.chunkZ == null)
		{
			int zOffset = getChunkOffsetZ();
			int zBoundMax = getChunkBoundMaxZ();
			
			if(zBoundMax == 0)
				return zOffset;
			
			Random random = new Random(levelSeed + 3 + salt());
			int zBoundMin = getChunkBoundMinZ();
			int zBound = random.nextBoolean() ? random.nextInt(-zBoundMax, -zBoundMin + 1) : random.nextInt(zBoundMin, zBoundMax + 1);
			
			this.chunkZ = zBoundMax <= 0 ? zOffset : zOffset + zBound;
		}
		
		return this.chunkZ;
	}
	
	@Override
	public @NotNull ChunkPos getPotentialStructureChunk(long levelSeed, int chunkX, int chunkZ)
	{
		return new ChunkPos(getChunkX(levelSeed), getChunkZ(levelSeed));
	}
	
	@Override
	protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int chunkX, int chunkZ)
	{
		return chunkX == getChunkX(state.getLevelSeed()) && chunkZ == getChunkZ(state.getLevelSeed());
	}
	
	@Override
	public @NotNull StructurePlacementType<?> type()
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
			super(salt, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
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
		public int getChunkBoundMinX()
		{
			return 0;
		}
		
		@Override
		public int getChunkBoundMinZ()
		{
			return 0;
		}
		
		@Override
		public int getChunkBoundMaxX()
		{
			return CommonGenerationConfig.stargate_generation_x_bound.get();
		}
		
		@Override
		public int getChunkBoundMaxZ()
		{
			return CommonGenerationConfig.stargate_generation_z_bound.get();
		}
		
		@Override
		public @NotNull StructurePlacementType<?> type()
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
		public int getChunkBoundMaxX()
		{
			return CommonGenerationConfig.buried_stargate_generation_x_bound.get();
		}
		
		@Override
		public int getChunkBoundMaxZ()
		{
			return CommonGenerationConfig.buried_stargate_generation_z_bound.get();
		}
		
		@Override
		public @NotNull StructurePlacementType<?> type()
		{
			return StructurePlacementInit.BURIED_STARGATE_PLACEMENT.get();
		}
	}
}
