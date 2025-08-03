package net.povstalec.sgjourney.common.blockstates;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.event.ForgeEventFactory;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.sgjourney.StargateBlockCover;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;

public class StargateBlockState extends BlockState
{
	public StargateBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> properties,
							  MapCodec<BlockState> states)
	{
		super(block, properties, states);
	}
	
	@Override
	public float getDestroySpeed(BlockGetter reader, BlockPos pos)
	{
		// Null checks here because ProjectMMO passes a null values in here https://github.com/Caltinor/Project-MMO-2.0/issues/706
		if(reader != null && pos != null && this.getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			AbstractStargateEntity stargate = stargateBlock.getStargate(reader, pos, reader.getBlockState(pos));
			if(stargate != null && !CommonStargateConfig.can_break_connected_stargate.get())
			{
				StargateConnection.State state = stargate.getConnectionState();
				if(state.isConnected())
					return -1.0F;
			}
			
			Optional<StargateBlockCover> blockCover = stargateBlock.getBlockCover(reader, this, pos);
			
			if(blockCover.isPresent())
			{
				StargatePart part = this.getValue(AbstractStargateBlock.PART);
				Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
				
				if(coverState.isPresent()) // Destroy speed for the cover block
					return coverState.get().getDestroySpeed(reader, pos);
			}
		}
		
		return super.getDestroySpeed(reader, pos);
	}
	
	@Override
	public float getDestroyProgress(Player player, BlockGetter reader, BlockPos pos)
	{
		float destroySpeed = getDestroySpeed(reader, pos);
		if(destroySpeed == -1.0F)
			return 0.0F;
		
		if(this.getBlock() instanceof AbstractStargateBlock stargate)
		{
			Optional<StargateBlockCover> blockCover = stargate.getBlockCover(reader, this, pos);
			
			if(blockCover.isPresent())
			{
				StargatePart part = this.getValue(AbstractStargateBlock.PART);
				Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
				
				if(coverState.isPresent())
				{
					float multiplier = ForgeHooks.isCorrectToolForDrops(coverState.get(), player) ? 30F : 100F;
					
					return player.getDigSpeed(coverState.get(), pos) / destroySpeed / multiplier;
				}
			}
		}
		// Adding this here because I now have trust issues with IForgeBlockState and whatever mixins can do to it
		return this.getBlock().getDestroyProgress(asState(), player, reader, pos);
	}
	
	@Override
	public SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity)
	{
		BlockState state = level.getBlockState(pos);
		
		if(state.getBlock() instanceof AbstractStargateBlock stargate)
		{
			Optional<StargateBlockCover> blockCover = stargate.getBlockCover(level, state, pos);
			
			if(blockCover.isPresent())
			{
				StargatePart part = state.getValue(AbstractStargateBlock.PART);
				Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
				
				if(coverState.isPresent()) // Destroy speed for the cover block
					return coverState.get().getSoundType(level, pos, entity);
			}
		}
		// Adding this here because I now have trust issues with IForgeBlockState and whatever mixins can do to it
		return this.self().getBlock().getSoundType(self(), level, pos, entity);
		
	}
	
	// Adding this here because I now have trust issues with IForgeBlockState and whatever mixins can do to it
	@Override
	protected BlockState asState()
	{
		return this;
	}
	
	private BlockState self()
	{
		return this;
	}
	
	@Override
	public float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity)
	{
		return this.self().getBlock().getFriction(this.self(), level, pos, entity);
	}
	
	@Override
	public int getLightEmission(BlockGetter level, BlockPos pos)
	{
		return this.self().getBlock().getLightEmission(this.self(), level, pos);
	}
	
	@Override
	public boolean isLadder(LevelReader level, BlockPos pos, LivingEntity entity)
	{
		return this.self().getBlock().isLadder(this.self(), level, pos, entity);
	}
	
	@Override
	public boolean canHarvestBlock(BlockGetter level, BlockPos pos, Player player)
	{
		return this.self().getBlock().canHarvestBlock(this.self(), level, pos, player);
	}
	
	@Override
	public boolean onDestroyedByPlayer(Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
	{
		return this.self().getBlock().onDestroyedByPlayer(this.self(), level, pos, player, willHarvest, fluid);
	}
	
	@Override
	public boolean isBed(BlockGetter level, BlockPos pos, @Nullable LivingEntity sleeper)
	{
		return this.self().getBlock().isBed(this.self(), level, pos, sleeper);
	}
	
	@Override
	public boolean isValidSpawn(LevelReader level, BlockPos pos, SpawnPlacements.Type type, EntityType<?> entityType)
	{
		return this.self().getBlock().isValidSpawn(this.self(), level, pos, type, entityType);
	}
	
	@Override
	public Optional<Vec3> getRespawnPosition(EntityType<?> type, LevelReader level, BlockPos pos, float orientation, @Nullable LivingEntity entity)
	{
		return this.self().getBlock().getRespawnPosition(this.self(), type, level, pos, orientation, entity);
	}
	
	@Override
	public void setBedOccupied(Level level, BlockPos pos, LivingEntity sleeper, boolean occupied)
	{
		this.self().getBlock().setBedOccupied(this.self(), level, pos, sleeper, occupied);
	}
	
	@Override
	public Direction getBedDirection(LevelReader level, BlockPos pos)
	{
		return this.self().getBlock().getBedDirection(this.self(), level, pos);
	}
	
	@Override
	public float getExplosionResistance(BlockGetter level, BlockPos pos, Explosion explosion)
	{
		return this.self().getBlock().getExplosionResistance(this.self(), level, pos, explosion);
	}
	
	@Override
	public ItemStack getCloneItemStack(HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		return this.self().getBlock().getCloneItemStack(this.self(), target, level, pos, player);
	}
	
	@Override
	public boolean addLandingEffects(ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles)
	{
		return this.self().getBlock().addLandingEffects(this.self(), level, pos, state2, entity, numberOfParticles);
	}
	
	@Override
	public boolean addRunningEffects(Level level, BlockPos pos, Entity entity)
	{
		return this.self().getBlock().addRunningEffects(this.self(), level, pos, entity);
	}
	
	@Override
	public boolean canSustainPlant(BlockGetter level, BlockPos pos, Direction facing, IPlantable plantable)
	{
		return this.self().getBlock().canSustainPlant(this.self(), level, pos, facing, plantable);
	}
	
	@Override
	public boolean onTreeGrow(LevelReader level, BiConsumer<BlockPos, BlockState> placeFunction, RandomSource randomSource, BlockPos pos, TreeConfiguration config)
	{
		return this.self().getBlock().onTreeGrow(this.self(), level, placeFunction, randomSource, pos, config);
	}
	
	@Override
	public boolean isFertile(BlockGetter level, BlockPos pos)
	{
		return this.self().getBlock().isFertile(this.self(), level, pos);
	}
	
	@Override
	public boolean isConduitFrame(LevelReader level, BlockPos pos, BlockPos conduit)
	{
		return this.self().getBlock().isConduitFrame(this.self(), level, pos, conduit);
	}
	
	@Override
	public boolean isPortalFrame(BlockGetter level, BlockPos pos)
	{
		return this.self().getBlock().isPortalFrame(this.self(), level, pos);
	}
	
	@Override
	public int getExpDrop(LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel)
	{
		return this.self().getBlock().getExpDrop(this.self(), level, randomSource, pos, fortuneLevel, silkTouchLevel);
	}
	
	@Override
	public BlockState rotate(LevelAccessor level, BlockPos pos, Rotation direction)
	{
		return this.self().getBlock().rotate(this.self(), level, pos, direction);
	}
	
	@Override
	public float getEnchantPowerBonus(LevelReader level, BlockPos pos)
	{
		return this.self().getBlock().getEnchantPowerBonus(this.self(), level, pos);
	}
	
	@Override
	public void onNeighborChange(LevelReader level, BlockPos pos, BlockPos neighbor)
	{
		this.self().getBlock().onNeighborChange(this.self(), level, pos, neighbor);
	}
	
	@Override
	public boolean shouldCheckWeakPower(SignalGetter level, BlockPos pos, Direction side)
	{
		return this.self().getBlock().shouldCheckWeakPower(this.self(), level, pos, side);
	}
	
	@Override
	public boolean getWeakChanges(LevelReader level, BlockPos pos)
	{
		return this.self().getBlock().getWeakChanges(this.self(), level, pos);
	}
	
	@Override
	public @Nullable float[] getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beacon)
	{
		return this.self().getBlock().getBeaconColorMultiplier(this.self(), level, pos, beacon);
	}
	
	@Override
	public BlockState getStateAtViewpoint(BlockGetter level, BlockPos pos, Vec3 viewpoint)
	{
		return this.self().getBlock().getStateAtViewpoint(this.self(), level, pos, viewpoint);
	}
	
	@Override
	public boolean isSlimeBlock()
	{
		return this.self().getBlock().isSlimeBlock(this.self());
	}
	
	@Override
	public boolean isStickyBlock()
	{
		return this.self().getBlock().isStickyBlock(this.self());
	}
	
	@Override
	public boolean canStickTo(@NotNull BlockState other)
	{
		return this.self().getBlock().canStickTo(this.self(), other);
	}
	
	@Override
	public int getFlammability(BlockGetter level, BlockPos pos, Direction face)
	{
		return this.self().getBlock().getFlammability(this.self(), level, pos, face);
	}
	
	@Override
	public boolean isFlammable(BlockGetter level, BlockPos pos, Direction face)
	{
		return this.self().getBlock().isFlammable(this.self(), level, pos, face);
	}
	
	@Override
	public void onCaughtFire(Level level, BlockPos pos, @Nullable Direction face, @Nullable LivingEntity igniter)
	{
		this.self().getBlock().onCaughtFire(this.self(), level, pos, face, igniter);
	}
	
	@Override
	public int getFireSpreadSpeed(BlockGetter level, BlockPos pos, Direction face)
	{
		return this.self().getBlock().getFireSpreadSpeed(this.self(), level, pos, face);
	}
	
	@Override
	public boolean isFireSource(LevelReader level, BlockPos pos, Direction side)
	{
		return this.self().getBlock().isFireSource(this.self(), level, pos, side);
	}
	
	@Override
	public boolean canEntityDestroy(BlockGetter level, BlockPos pos, Entity entity)
	{
		return this.self().getBlock().canEntityDestroy(this.self(), level, pos, entity);
	}
	
	@Override
	public boolean isBurning(BlockGetter level, BlockPos pos)
	{
		return this.self().getBlock().isBurning(this.self(), level, pos);
	}
	
	@Override
	public @Nullable BlockPathTypes getBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob)
	{
		return this.self().getBlock().getBlockPathType(this.self(), level, pos, mob);
	}
	
	@Override
	public @Nullable BlockPathTypes getAdjacentBlockPathType(BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType)
	{
		return this.self().getBlock().getAdjacentBlockPathType(this.self(), level, pos, mob, originalType);
	}
	
	@Override
	public boolean canDropFromExplosion(BlockGetter level, BlockPos pos, Explosion explosion)
	{
		return this.self().getBlock().canDropFromExplosion(this.self(), level, pos, explosion);
	}
	
	@Override
	public void onBlockExploded(Level level, BlockPos pos, Explosion explosion)
	{
		this.self().getBlock().onBlockExploded(this.self(), level, pos, explosion);
	}
	
	@Override
	public boolean collisionExtendsVertically(BlockGetter level, BlockPos pos, Entity collidingEntity)
	{
		return this.self().getBlock().collisionExtendsVertically(this.self(), level, pos, collidingEntity);
	}
	
	@Override
	public boolean shouldDisplayFluidOverlay(BlockAndTintGetter level, BlockPos pos, FluidState fluidState)
	{
		return this.self().getBlock().shouldDisplayFluidOverlay(this.self(), level, pos, fluidState);
	}
	
	@Override
	public @Nullable BlockState getToolModifiedState(UseOnContext context, ToolAction toolAction, boolean simulate)
	{
		BlockState eventState = ForgeEventFactory.onToolUse(this.self(), context, toolAction, simulate);
		return eventState != this.self() ? eventState : this.self().getBlock().getToolModifiedState(this.self(), context, toolAction, simulate);
	}
	
	@Override
	public boolean isScaffolding(LivingEntity entity)
	{
		return this.self().getBlock().isScaffolding(this.self(), entity.level(), entity.blockPosition(), entity);
	}
	
	@Override
	public boolean canRedstoneConnectTo(BlockGetter level, BlockPos pos, @Nullable Direction direction)
	{
		return this.self().getBlock().canConnectRedstone(this.self(), level, pos, direction);
	}
	
	@Override
	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir)
	{
		return this.self().getBlock().hidesNeighborFace(level, pos, this.self(), neighborState, dir);
	}
	
	@Override
	public boolean supportsExternalFaceHiding()
	{
		return this.self().getBlock().supportsExternalFaceHiding(this.self());
	}
	
	@Override
	public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState)
	{
		this.self().getBlock().onBlockStateChange(level, pos, oldState, this.self());
	}
	
	@Override
	public boolean canBeHydrated(BlockGetter getter, BlockPos pos, FluidState fluid, BlockPos fluidPos)
	{
		return this.self().getBlock().canBeHydrated(this.self(), getter, pos, fluid, fluidPos);
	}
	
	@Override
	public BlockState getAppearance(BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos)
	{
		return this.self().getBlock().getAppearance(this.self(), level, pos, side, queryState, queryPos);
	}
}
