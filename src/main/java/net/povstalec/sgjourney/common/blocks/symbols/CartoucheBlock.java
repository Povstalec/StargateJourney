package net.povstalec.sgjourney.common.blocks.symbols;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.symbols.CartoucheEntity;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class CartoucheBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	
	public CartoucheBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
	}
	
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING))).setValue(ORIENTATION, Orientation.REGULAR);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		Orientation orientation = Orientation.getOrientationFromXRot(context.getPlayer());
		
		if(orientation == Orientation.REGULAR && blockpos.getY() > level.getMaxBuildHeight() - 1)
			return null;
		
		if(!level.getBlockState(blockpos.relative(Orientation.getCenterDirection(Direction.UP, orientation))).canBeReplaced(context))
			return null;
		
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER);
	}
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
    	builder.add(FACING).add(HALF).add(ORIENTATION);
	}
    
    public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, LevelAccessor levelAccessor, BlockPos pos1, BlockPos pos2)
    {
    	DoubleBlockHalf doubleblockhalf = state1.getValue(HALF);
    	if(direction.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (direction == Direction.UP) || state2.is(this) && state2.getValue(HALF) != doubleblockhalf)
    	{
    		return doubleblockhalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state1.canSurvive(levelAccessor, pos1) ? 
    				Blocks.AIR.defaultBlockState() : super.updateShape(state1, direction, state2, levelAccessor, pos1, pos2);
    	}
    	else
    	{
    		return Blocks.AIR.defaultBlockState();
        }
	}
    
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if (!level.isClientSide()) 
        {
        	if(level.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER)
        		pos = pos.below();
        		
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof CartoucheEntity cartouche) 
        	{
        		MutableComponent symbols = Component.literal(cartouche.getAddress());
        		/*MutableComponent symbols = Component.literal(Symbols.addressUnicode(cartouche.getAddress()));
        		Style style = symbols.getStyle().withFont(new ResourceLocation("sgjourney", "milky_way"));
        		symbols = symbols.withStyle(style);*/
        		
        		MutableComponent text = Component.literal("Address: ").withStyle(ChatFormatting.YELLOW).append(symbols.withStyle(ChatFormatting.AQUA));
        		
        		player.sendSystemMessage(text);
        	}
        }
        return InteractionResult.SUCCESS;
    }
    
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	String dimension = "";
    	if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Dimension"))
    	{
    		dimension = stack.getTag().getCompound("BlockEntityTag").getString("Dimension");
    	}
    	
		tooltipComponents.add(Component.literal("Dimension: " + dimension).withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
