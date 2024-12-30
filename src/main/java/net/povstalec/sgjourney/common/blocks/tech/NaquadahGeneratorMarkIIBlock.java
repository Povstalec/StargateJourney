package net.povstalec.sgjourney.common.blocks.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.BaseEntityBlock;
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
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorMarkIIEntity;
import net.povstalec.sgjourney.common.config.CommonNaquadahGeneratorConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;

public class NaquadahGeneratorMarkIIBlock extends NaquadahGeneratorBlock
{
	private static final ArrayList<Tuple<Vector3d, Vector3d>> MIN_MAX = new ArrayList<Tuple<Vector3d, Vector3d>>(Arrays.asList(
			new Tuple<Vector3d, Vector3d>(new Vector3d(1.0D, 5.0D, 15.0D), new Vector3d(15.0D, 11.0D, 16.0D)), // Bottom
			new Tuple<Vector3d, Vector3d>(new Vector3d(0.0D, 5.0D, 11.0D), new Vector3d(16.0D, 11.0D, 15.0D)), // Base
			new Tuple<Vector3d, Vector3d>(new Vector3d(1.0D, 5.0D, 10.0D), new Vector3d(4.0D, 11.0D, 11.0D)), // Top Left
			new Tuple<Vector3d, Vector3d>(new Vector3d(11.0D, 5.0D, 10.0D), new Vector3d(15.0D, 11.0D, 11.0D)) // Top Right
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

	public static final MapCodec<NaquadahGeneratorMarkIIBlock> CODEC = simpleCodec(NaquadahGeneratorMarkIIBlock::new);

	public NaquadahGeneratorMarkIIBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected MapCodec<NaquadahGeneratorMarkIIBlock> codec()
	{
		return CODEC;
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new NaquadahGeneratorMarkIIEntity(pos, state);
	}
	
	@Override
	public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if (blockentity instanceof NaquadahGeneratorEntity)
		{
			if (!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
				
				blockentity.saveToItem(itemstack, level.registryAccess());

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		return super.playerWillDestroy(level, pos, state, player);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.NAQUADAH_GENERATOR_MARK_II.get(), NaquadahGeneratorEntity::tick);
    }
	
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	long capacity = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_capacity.get();
    	
    	long energyPerTick = CommonNaquadahGeneratorConfig.naquadah_generator_mark_ii_energy_per_tick.get();
    	
    	int energy = 0;
		
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag tag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			if(tag.contains("Energy"))
				energy = tag.getInt("Energy");
		}

        tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy + "/" + capacity +" FE")).withStyle(ChatFormatting.DARK_RED));
        tooltipComponents.add(Component.literal(energyPerTick + " FE/Tick").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
