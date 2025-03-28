package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FirePitBlock extends Block
{
	protected final ParticleOptions flameParticle;
	public static final BooleanProperty LIT = BooleanProperty.create("lit");
	
	public FirePitBlock(Properties properties, ParticleOptions particle)
	{
		super(properties);
		this.flameParticle = particle;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		if(!level.isClientSide() && state.getValue(LIT) == true)
			level.setBlock(pos, state.setValue(LIT, false), 3);

		return InteractionResult.SUCCESS;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(state.getValue(LIT) == false && player.getItemInHand(hand).getItem() == Items.FLINT_AND_STEEL)
		{
			level.playSound((Player)null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
			level.setBlock(pos, state.setValue(LIT, true), 3);
			player.getItemInHand(hand).hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));

			return ItemInteractionResult.SUCCESS;
		}
		else if(state.getValue(LIT) == false && player.getItemInHand(hand).getItem() == Items.FIRE_CHARGE)
		{
			level.playSound((Player)null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
			level.setBlock(pos, state.setValue(LIT, true), 3);
			if(!player.isCreative())
				player.getItemInHand(hand).shrink(1);

			return ItemInteractionResult.SUCCESS;
		}

		return ItemInteractionResult.FAIL;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(LIT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(LIT, false);
	}
	
	private static final VoxelShape BOTTOM = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 1.0D, 11.0D);
	private static final VoxelShape SIDE_1 = Block.box(4.0D, 1.0D, 5.0D, 5.0D, 2.0D, 11.0D);
	private static final VoxelShape SIDE_2 = Block.box(11.0D, 1.0D, 5.0D, 12.0D, 2.0D, 11.0D);
	private static final VoxelShape SIDE_3 = Block.box(5.0D, 1.0D, 4.0D, 11.0D, 2.0D, 5.0D);
	private static final VoxelShape SIDE_4 = Block.box(5.0D, 1.0D, 11.0D, 11.0D, 2.0D, 12.0D);
	
	private static final VoxelShape FIRE_PIT = Shapes.or(BOTTOM, SIDE_1, SIDE_2, SIDE_3, SIDE_4);

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) 
	{
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		return canSupportCenter(reader, pos.below(), Direction.UP);
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
	{
		double d0 = (double)pos.getX() + 0.5D;
		double d1 = (double)pos.getY() + 0.3D;
		double d2 = (double)pos.getZ() + 0.5D;
		if(state.getValue(LIT) == true)
	   	{
			level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
			level.addParticle(this.flameParticle, d0, d1, d2, 0.0D, 0.0D, 0.0D);
	   	}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision)
	{
		return FIRE_PIT;
	}
}
