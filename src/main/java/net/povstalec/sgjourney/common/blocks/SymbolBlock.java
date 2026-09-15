package net.povstalec.sgjourney.common.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.CartoucheBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.SymbolBlockGravingMenu;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SymbolBlock extends DirectionalBlock implements EntityBlock, SpecialGravableBlock
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
					
					if(symbolBlock.getSymbolTable() != null)
						player.sendSystemMessage(Component.translatable("info.sgjourney.symbol_table").append(Component.literal(": " + symbolBlock.getSymbolTable().location())).withStyle(ChatFormatting.YELLOW));
					
					if(symbolBlock.getSymbolTable() != null)
						player.sendSystemMessage(Component.translatable("info.sgjourney.point_of_origin_table").append(Component.literal(": " + symbolBlock.getSymbolTable().location())).withStyle(ChatFormatting.GOLD));
					
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
				symbolString = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(SymbolBlockEntity.SYMBOL))), "tooltip.sgjourney.error");

        	if(symbolNumber != 0 && blockEntityTag.contains(SymbolBlockEntity.SYMBOLS))
				symbolsString = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(SymbolBlockEntity.SYMBOLS))), "tooltip.sgjourney.error");
    	}
		
		if(symbolNumber == 0)
			tooltipComponents.add(Component.translatable("info.sgjourney.symbol").append(Component.literal(": ").append(Component.translatable(symbolString))).withStyle(ChatFormatting.DARK_PURPLE));
		else
		{
			tooltipComponents.add(Component.translatable("info.sgjourney.symbol_number").append(Component.literal(": ").append("" + symbolNumber)).withStyle(ChatFormatting.YELLOW));
			tooltipComponents.add(Component.translatable("info.sgjourney.symbols").append(Component.literal(": ").append(Component.translatable(symbolsString))).withStyle(ChatFormatting.LIGHT_PURPLE));
		}
		
		if(blockEntityTag != null)
		{
			if(blockEntityTag.contains(SymbolBlockEntity.SYMBOL_TABLE))
				tooltipComponents.add(Component.translatable("info.sgjourney.symbol_table").append(Component.literal(": " + blockEntityTag.getString(SymbolBlockEntity.SYMBOL_TABLE))).withStyle(ChatFormatting.YELLOW));
			
			if(blockEntityTag.contains(SymbolBlockEntity.POINT_OF_ORIGIN_TABLE))
				tooltipComponents.add(Component.translatable("info.sgjourney.point_of_origin_table").append(Component.literal(": " + blockEntityTag.getString(SymbolBlockEntity.POINT_OF_ORIGIN_TABLE))).withStyle(ChatFormatting.GOLD));
			
			if(blockEntityTag.contains(CartoucheBlockEntity.GENERATION_STEP, CompoundTag.TAG_BYTE)
				&& StructureGenEntity.Step.SETUP == StructureGenEntity.Step.fromByte(blockEntityTag.getByte(CartoucheBlockEntity.GENERATION_STEP)))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.generates_inside_structure").withStyle(ChatFormatting.YELLOW));
			
			if(blockEntityTag.contains(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.local_point_of_origin").withStyle(ChatFormatting.GREEN));
			
			if(blockEntityTag.contains(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.random_point_of_origin").withStyle(ChatFormatting.DARK_GREEN));
		}
    }
	
	protected abstract void openSymbolBlockGravingMenu(Level level, BlockPos pos, @Nullable Player player);
	
	@Override
	public InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack)
	{
		if(!level.isClientSide())
			openSymbolBlockGravingMenu(level, pos, player);
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPointOfOrigin(Level level, BlockPos pos, BlockState state, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(level.getBlockEntity(pos) instanceof SymbolBlockEntity symbolBlock)
			symbolBlock.setPointOfOrigin(pointOfOrigin);
	}
	
	@Override
	public void setSymbols(Level level, BlockPos pos, BlockState state, ResourceKey<Symbols> symbols)
	{
		if(level.getBlockEntity(pos) instanceof SymbolBlockEntity symbolBlock)
			symbolBlock.setSymbols(symbols);
	}
	
	@Override
	public void setAddress(Level level, BlockPos pos, BlockState state, Address address)
	{
		// Using Address of length 1 to decide the symbol
		if(level.getBlockEntity(pos) instanceof SymbolBlockEntity symbolBlock)
		{
			if(address.getLength() == 0)
				symbolBlock.setSymbolNumber(-1);
			else
				symbolBlock.setSymbolNumber(address.symbolAt(0));
		}
	}
	
	
	
	public static ItemStack localPointOfOrigin(ItemLike item)
	{
		ItemStack stack = new ItemStack(item);
		CompoundTag blockEntityTag = new CompoundTag();
		blockEntityTag.putBoolean(SymbolBlockEntity.LOCAL_POINT_OF_ORIGIN, true);
		stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
		
		return stack;
	}
	
	public static ItemStack randomPointOfOrigin(ItemLike item)
	{
		ItemStack stack = new ItemStack(item);
		CompoundTag blockEntityTag = new CompoundTag();
		blockEntityTag.putBoolean(SymbolBlockEntity.RANDOM_POINT_OF_ORIGIN, true);
		stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
		
		return stack;
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
		
		@Override
		protected void openSymbolBlockGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof SymbolBlockEntity.Stone symbolBlockEntity)
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName()
					{
						return Component.empty();
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity)
					{
						return new SymbolBlockGravingMenu.Stone(windowId, playerInventory, symbolBlockEntity, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, symbolBlockEntity.getBlockPos());
			}
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
		
		@Override
		protected void openSymbolBlockGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof SymbolBlockEntity.Sandstone symbolBlockEntity)
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName()
					{
						return Component.empty();
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity)
					{
						return new SymbolBlockGravingMenu.Sandstone(windowId, playerInventory, symbolBlockEntity, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, symbolBlockEntity.getBlockPos());
			}
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
		
		@Override
		protected void openSymbolBlockGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof SymbolBlockEntity.RedSandstone symbolBlockEntity)
			{
				MenuProvider containerProvider = new MenuProvider()
				{
					@Override
					public @NotNull Component getDisplayName()
					{
						return Component.empty();
					}
					
					@Override
					public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player playerEntity)
					{
						return new SymbolBlockGravingMenu.RedSandstone(windowId, playerInventory, symbolBlockEntity, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, symbolBlockEntity.getBlockPos());
			}
		}
		
	}
}
