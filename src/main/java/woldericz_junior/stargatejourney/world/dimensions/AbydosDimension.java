package woldericz_junior.stargatejourney.world.dimensions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AbydosDimension extends Dimension
{

	public AbydosDimension(World world, DimensionType type) 
	{
		super(world, type, 0.0F);
	}

	@Override
	public ChunkGenerator<?> createChunkGenerator() {
		return new CustomChunkGenerator(world, new CustomBiomeProvider(), new CustomGenSettings());
	}

	@Override
	public BlockPos findSpawn(ChunkPos chunkPosIn, boolean checkValid) 
	{
		return null;
	}

	@Override
	public BlockPos findSpawn(int posX, int posZ, boolean checkValid) 
	{
		return null;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTicks) 
	{
		double d0 = MathHelper.frac((double)worldTime / 24000.0D - 0.25D);
		   double d1 = 0.5D - Math.cos(d0 * Math.PI) / 2.0D;
		   return (float)(d0 * 2.0D + d1) / 3.0F;
	}

	@Override
	public boolean isSurfaceWorld() 
	{
		return true;
	}

	@Override
	public Vec3d getFogColor(float celestialAngle, float partialTicks) 
	{
		return new Vec3d(0.843172549D, 1.0D, 1.0D);
	}

	@Override
	public boolean canRespawnHere() 
	{
		return true;
	}

	@Override
	public boolean doesXZShowFog(int x, int z) 
	{
		return false;
	}
	
	@Override
	public SleepResult canSleepAt(PlayerEntity player, BlockPos pos) 
	{
		return SleepResult.ALLOW;
	}
	
	@Override
	public int getActualHeight() 
	{
		return 256;
	}
	
	@OnlyIn(Dist.CLIENT)
	public float getStarBrightness(float brightness)
	{
		return 1.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public float getCloudHeight()
	{
		return 150F;
	}
	
	@Override
	public boolean hasSkyLight()
	{
		return true;
	}
}
