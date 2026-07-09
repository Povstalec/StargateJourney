package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public abstract class SymbolBlock extends DirectionalBlock implements EntityBlock
{
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	
	protected SymbolBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR));
	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
    	builder.add(FACING).add(ORIENTATION);
	}

    @Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

    @Override
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(ORIENTATION, Orientation.getOrientationFromXRot(context.getPlayer()));
	}
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}

    @Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
		if(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
		{
			if(!level.isClientSide())
			{
				BlockEntity blockEntity = level.getBlockEntity(pos);
				
				if(blockEntity instanceof SymbolBlockEntity symbolBlock)
				{
					int symbolNumber = symbolBlock.getSymbolNumber();
					MutableComponent text;
					
					player.sendSystemMessage(Component.translatable("info.sgjourney.symbol_number").append(Component.literal(": " + symbolNumber)).withStyle(ChatFormatting.YELLOW));
					
					if(symbolNumber == 0)
					{
						MutableComponent pointOfOrigin = PointOfOrigin.makeComponent(symbolBlock.getPointOfOrigin());
						text = Component.translatable("info.sgjourney.point_of_origin").append(Component.literal(": ")).append(pointOfOrigin).withStyle(ChatFormatting.DARK_PURPLE);
					}
					else
					{
						MutableComponent symbols = Symbols.makeComponent(symbolBlock.getSymbols());
						text = Component.translatable("info.sgjourney.symbols").append(Component.literal(": ")).append(symbols).withStyle(ChatFormatting.LIGHT_PURPLE);
					}
					
					player.sendSystemMessage(text);
				}
			}
			return InteractionResult.SUCCESS;
		}
        else
			return InteractionResult.FAIL;
    }
	
	public abstract ItemLike getItem();
    
    @Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(!level.isClientSide() && !player.isCreative() && player.hasCorrectToolForDrops(state))
		{
			ItemStack itemstack = new ItemStack(getItem());
			
			blockentity.saveToItem(itemstack);

			ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
			itementity.setDefaultPickUpDelay();
			level.addFreshEntity(itementity);
		}

		super.playerWillDestroy(level, pos, state, player);
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int symbolNumber = 0;
		String symbolString = "";
    	String symbolsString = "";
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		if(blockEntityTag != null)
    	{
        	if(blockEntityTag.contains(SymbolBlockEntity.SYMBOL_NUMBER))
            	symbolNumber = blockEntityTag.getInt(SymbolBlockEntity.SYMBOL_NUMBER);

        	if(symbolNumber == 0 && blockEntityTag.contains(SymbolBlockEntity.SYMBOL))
				symbolString = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(SymbolBlockEntity.SYMBOL))), "Error");

        	if(symbolNumber != 0 && blockEntityTag.contains(SymbolBlockEntity.SYMBOLS))
				symbolsString = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(SymbolBlockEntity.SYMBOLS))), "Error");
    	}
		
		if(symbolNumber == 0)
			tooltipComponents.add(Component.translatable("info.sgjourney.symbol").append(Component.literal(": ").append(Component.translatable(symbolString))).withStyle(ChatFormatting.DARK_PURPLE));
		else
		{
			tooltipComponents.add(Component.translatable("info.sgjourney.symbol_number").append(Component.literal(": ").append("" + symbolNumber)).withStyle(ChatFormatting.YELLOW));
			tooltipComponents.add(Component.translatable("info.sgjourney.symbols").append(Component.literal(": ").append(Component.translatable(symbolsString))).withStyle(ChatFormatting.LIGHT_PURPLE));
		}
    	
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
    
    public static class Stone extends SymbolBlock
    {
		public Stone(Properties properties)
		{
			super(properties);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new SymbolBlockEntity.Stone(pos, state);
		}

		@Override
		public ItemLike getItem()
		{
			return BlockInit.STONE_SYMBOL.get();
		}
    	
    }
    
    public static class Sandstone extends SymbolBlock
    {
		public Sandstone(Properties properties)
		{
			super(properties);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new SymbolBlockEntity.Sandstone(pos, state);
		}

		@Override
		public ItemLike getItem()
		{
			return BlockInit.SANDSTONE_SYMBOL.get();
		}
    	
    }
	
	public static class RedSandstone extends SymbolBlock
	{
		public RedSandstone(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new SymbolBlockEntity.RedSandstone(pos, state);
		}
		
		@Override
		public ItemLike getItem()
		{
			return BlockInit.RED_SANDSTONE_SYMBOL.get();
		}
		
	}
}
