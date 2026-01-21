package net.povstalec.sgjourney.common.blocks.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3d;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;

public class NaquadahGeneratorMarkIBlock extends NaquadahGeneratorBlock
{
	private static final ArrayList<Tuple<Vector3d, Vector3d>> MIN_MAX = new ArrayList<Tuple<Vector3d, Vector3d>>(Arrays.asList(
			new Tuple<Vector3d, Vector3d>(new Vector3d(5.5D, 5.5D, 14.0D), new Vector3d(10.5D, 10.5D, 16.0D)), // Base 1
			new Tuple<Vector3d, Vector3d>(new Vector3d(6.5D, 6.5D, 13.0D), new Vector3d(9.5D, 9.5D, 14.0D)), // Base 2
			new Tuple<Vector3d, Vector3d>(new Vector3d(4.0D, 6.5D, 10.0D), new Vector3d(12.0D, 9.5D, 13.0D)), // Tube
			new Tuple<Vector3d, Vector3d>(new Vector3d(12.0D, 6.0D, 9.5D), new Vector3d(16.0D, 10.0D, 13.5D)), // Left Ball
			new Tuple<Vector3d, Vector3d>(new Vector3d(0.0D, 6.0D, 9.5D), new Vector3d(4.0D, 10.0D, 13.5D)), // Right Ball
			new Tuple<Vector3d, Vector3d>(new Vector3d(6.5D, 6.5D, 9.0D), new Vector3d(9.5D, 9.5D, 10.0D)), // Top 1
			new Tuple<Vector3d, Vector3d>(new Vector3d(5.5D, 5.5D, 8.0D), new Vector3d(10.5D, 10.5D, 9.0D)), // Top 2
			new Tuple<Vector3d, Vector3d>(new Vector3d(6.5D, 6.5D, 7.0D), new Vector3d(9.5D, 9.5D, 8.0D)) // Top 3
			));
	
	private static final VoxelShape SHAPE_NORTH_UP = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.NORTH_UP);
	private static final VoxelShape SHAPE_EAST_UP = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.EAST_UP);
	private static final VoxelShape SHAPE_SOUTH_UP = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.SOUTH_UP);
	private static final VoxelShape SHAPE_WEST_UP = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.WEST_UP);

	private static final VoxelShape SHAPE_UP_NORTH = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.UP_NORTH);
	private static final VoxelShape SHAPE_UP_EAST = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.UP_EAST);
	private static final VoxelShape SHAPE_UP_SOUTH = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.UP_SOUTH);
	private static final VoxelShape SHAPE_UP_WEST = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.UP_WEST);

	private static final VoxelShape SHAPE_DOWN_NORTH = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.DOWN_NORTH);
	private static final VoxelShape SHAPE_DOWN_EAST = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.DOWN_EAST);
	private static final VoxelShape SHAPE_DOWN_SOUTH = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.DOWN_SOUTH);
	private static final VoxelShape SHAPE_DOWN_WEST = VoxelShapeProvider.getOrientedShapes(MIN_MAX, FrontAndTop.DOWN_WEST);
	
	public NaquadahGeneratorMarkIBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new NaquadahGeneratorEntity.MarkI(pos, state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) 
	{
		return switch(state.getValue(ORIENTATION))
		{
		case NORTH_UP -> SHAPE_NORTH_UP;
		case EAST_UP -> SHAPE_EAST_UP;
		case SOUTH_UP -> SHAPE_SOUTH_UP;
		case WEST_UP -> SHAPE_WEST_UP;

		case UP_NORTH -> SHAPE_UP_NORTH;
		case UP_EAST -> SHAPE_UP_EAST;
		case UP_SOUTH -> SHAPE_UP_SOUTH;
		case UP_WEST -> SHAPE_UP_WEST;

		case DOWN_NORTH -> SHAPE_DOWN_NORTH;
		case DOWN_EAST -> SHAPE_DOWN_EAST;
		case DOWN_SOUTH -> SHAPE_DOWN_SOUTH;
		case DOWN_WEST -> SHAPE_DOWN_WEST;
		
		default -> SHAPE_NORTH_UP;
		};
	}
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof NaquadahGeneratorEntity)
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
	public long energyPerTick()
	{
		return CommonNaquadahGeneratorConfig.naquadah_generator_mark_i_energy_per_tick.get();
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    	tooltipComponents.add(Component.translatable("block.sgjourney.naquadah_generator_mark_i.description.mode").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
