package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorMarkIEntity;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class NaquadahGeneratorMarkIBlock extends NaquadahGeneratorBlock
{
	private static final VoxelShape BASE_1 = Block.box(5.5D, 0.0D, 5.5D, 10.5D, 2.0D, 10.5D);
	private static final VoxelShape BASE_2 = Block.box(6.5D, 2.0D, 6.5D, 9.5D, 3.0D, 9.5D);
	private static final VoxelShape TUBE_X = Block.box(6.5D, 3.0D, 4.0D, 9.5D, 6.0D, 12.0D);
	private static final VoxelShape TUBE_Z = Block.box(4.0D, 3.0D, 6.5D, 12.0D, 6.0D, 9.5D);
	private static final VoxelShape LEFT_BALL_X = Block.box(6.0D, 2.5D, 12.0D, 10.0D, 6.5D, 16.0D);
	private static final VoxelShape LEFT_BALL_Z = Block.box(12.0D, 2.5D, 6.0D, 16.0D, 6.5D, 10.0D);
	private static final VoxelShape RIGHT_BALL_X = Block.box(6.0D, 2.5D, 0.0D, 10.0D, 6.5D, 4.0D);
	private static final VoxelShape RIGHT_BALL_Z = Block.box(0.0D, 2.5D, 6.0D, 4.0D, 6.5D, 10.0D);
	private static final VoxelShape TOP_1 = Block.box(6.5D, 6.0D, 6.5D, 9.5D, 7.0D, 9.5D);
	private static final VoxelShape TOP_2 = Block.box(5.5D, 7.0D, 5.5D, 10.5D, 8.0D, 10.5D);
	private static final VoxelShape TOP_3 = Block.box(6.5D, 8.0D, 6.5D, 9.5D, 9.0D, 9.5D);

	private static final VoxelShape SHAPE_X = Shapes.or(BASE_1, BASE_2, TUBE_X, LEFT_BALL_X, RIGHT_BALL_X, TOP_1, TOP_2, TOP_3);
	private static final VoxelShape SHAPE_Z = Shapes.or(BASE_1, BASE_2, TUBE_Z, LEFT_BALL_Z, RIGHT_BALL_Z, TOP_1, TOP_2, TOP_3);
	
	public NaquadahGeneratorMarkIBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new NaquadahGeneratorMarkIEntity(pos, state);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) 
	{
		Direction direction = state.getValue(FACING);
		return direction.getAxis() == Direction.Axis.X ? SHAPE_X : SHAPE_Z;
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof NaquadahGeneratorEntity)
		{
			if (!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.NAQUADAH_GENERATOR_MARK_I.get());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.NAQUADAH_GENERATOR_MARK_I.get(), NaquadahGeneratorEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	long capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_capacity.get();
    	
    	long energyPerTick = CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
    	
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy + "/" + capacity +" FE")).withStyle(ChatFormatting.DARK_RED));
        tooltipComponents.add(Component.literal(energyPerTick + " FE/Tick").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
