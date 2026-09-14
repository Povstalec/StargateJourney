package net.povstalec.sgjourney.common.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.CartoucheBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.CartoucheGravingMenu;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CartoucheBlock extends HorizontalDirectionalBlock implements EntityBlock, SpecialGravableBlock
{
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	
	public CartoucheBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
	}

    @Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
	      return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

    @Override
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		Orientation orientation = Orientation.getOrientationFromXRot(context.getPlayer());
		Direction direction = context.getHorizontalDirection().getOpposite();
		
		return this.defaultBlockState().setValue(FACING, direction).setValue(HALF, DoubleBlockHalf.LOWER).setValue(ORIENTATION, orientation);
	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
    	builder.add(FACING).add(HALF).add(ORIENTATION);
	}
    
    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
    	Direction direction = oldState.getValue(FACING);
    	Orientation orientation = oldState.getValue(ORIENTATION);
    	DoubleBlockHalf doubleblockhalf = oldState.getValue(HALF);
    	Direction relativeDirection = doubleblockhalf == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
    	
        if(oldState.getBlock() != newState.getBlock())
        {
    		BlockPos destroyPos = pos.relative(Orientation.getMultiDirection(direction, relativeDirection, orientation));
        	if(level.getBlockState(destroyPos).getBlock() instanceof CartoucheBlock)
        		level.setBlock(destroyPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        	
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }

    @Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
		if(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
		{
			if(!level.isClientSide())
			{
				Direction direction = state.getValue(FACING);
				Orientation orientation = state.getValue(ORIENTATION);
				
				if(level.getBlockState(pos).getValue(HALF) == DoubleBlockHalf.UPPER)
					pos = pos.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation));
				
				BlockEntity blockEntity = level.getBlockEntity(pos);
				
				if(blockEntity instanceof CartoucheBlockEntity cartouche)
				{
					Address address = cartouche.getUpToDateAddress();
					
					if(address instanceof Address.Dimension dimensionAddress)
						player.sendSystemMessage(Component.translatable("info.sgjourney.dimension").append(Component.literal(": ")).append(dimensionAddress.getDimension().location().toString()).withStyle(ChatFormatting.GREEN));
					
					BlockPos underPos = pos.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation));
					if(level.getBlockEntity(underPos) instanceof SymbolBlockEntity symbolBlockEntity && symbolBlockEntity.getSymbolNumber() == 0)
						address = Address.Immutable.extendWithPointOfOrigin(new Address.Immutable(address));
					player.sendSystemMessage(Component.translatable("info.sgjourney.address").append(Component.literal(": ")).withStyle(ChatFormatting.YELLOW).append(address.toComponent(true)));
					
					if(cartouche.getSymbols() != null)
						player.sendSystemMessage(Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + cartouche.getSymbols().location())).withStyle(ChatFormatting.LIGHT_PURPLE));
					
					if(cartouche.getAddressTable() != null)
						player.sendSystemMessage(Component.translatable("info.sgjourney.address_table").append(Component.literal(": " + cartouche.getAddressTable().location())).withStyle(ChatFormatting.YELLOW));
				}
			}
			return InteractionResult.SUCCESS;
		}
        else
			return InteractionResult.FAIL;
    }
	
	public abstract ItemLike getItem();
	
	public abstract Block getBlock();

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, @NotNull ItemStack stack)
    {
    	Orientation orientation = state.getValue(ORIENTATION);
    	Direction direction = state.getValue(FACING);
    	BlockPos blockpos = pos.relative(Orientation.getCenterDirection(direction, orientation));
    	
    	level.setBlock(blockpos, getBlock().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(ORIENTATION, orientation).setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}
    
    @Override
	public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, BlockState state, @NotNull Player player)
	{
    	Direction direction = state.getValue(FACING);
    	Orientation orientation = state.getValue(ORIENTATION);
    	DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
    	
    	if(doubleblockhalf == DoubleBlockHalf.UPPER)
    		pos = pos.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation));
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof CartoucheBlockEntity)
		{
			if(!level.isClientSide() && !player.isCreative() && player.hasCorrectToolForDrops(state))
			{
				ItemStack itemstack = new ItemStack(getItem());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter getter, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
    {
    	String dimensionString = null;
    	String symbolsString = "";
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
    	if(blockEntityTag != null)
    	{
			if(blockEntityTag.contains(CartoucheBlockEntity.ADDRESS, Tag.TAG_COMPOUND))
			{
				Address.Dimension address = Address.Dimension.loadFromCompoundTag(blockEntityTag, CartoucheBlockEntity.ADDRESS);
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.address").append(Component.literal(": ").append(address.toComponent(false))).withStyle(ChatFormatting.YELLOW));
				dimensionString = address.getDimension().location().toString();
			}
    		else if(blockEntityTag.contains(CartoucheBlockEntity.ADDRESS, Tag.TAG_INT_ARRAY))
    		{
    			int[] addressArray = blockEntityTag.getIntArray(CartoucheBlockEntity.ADDRESS);
    			Address address = new Address.Immutable(addressArray);
    			tooltipComponents.add(Component.translatable("tooltip.sgjourney.address").append(Component.literal(": ").append(address.toComponent(false))).withStyle(ChatFormatting.YELLOW));
    		}
    		
    		if(blockEntityTag.contains(CartoucheBlockEntity.SYMBOLS))
				symbolsString = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(CartoucheBlockEntity.SYMBOLS))), "tooltip.sgjourney.error");
    	}
    	
    	if(dimensionString != null)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.dimension").append(Component.literal(": " + dimensionString)).withStyle(ChatFormatting.GREEN));
		tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbolsString)).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		if(blockEntityTag != null)
		{
			if(blockEntityTag.contains(CartoucheBlockEntity.ADDRESS_TABLE))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.address_table").append(Component.literal(": " + blockEntityTag.getString(CartoucheBlockEntity.ADDRESS_TABLE))).withStyle(ChatFormatting.YELLOW));
			
			if(blockEntityTag.contains(CartoucheBlockEntity.GENERATION_STEP, CompoundTag.TAG_BYTE)
				&& StructureGenEntity.Step.SETUP == StructureGenEntity.Step.fromByte(blockEntityTag.getByte(CartoucheBlockEntity.GENERATION_STEP)))
				tooltipComponents.add(Component.translatable("tooltip.sgjourney.generates_inside_structure").withStyle(ChatFormatting.YELLOW));
			
			if(blockEntityTag.contains(CartoucheBlockEntity.LOCAL_ADDRESS))
				tooltipComponents.add(localAddressComponent(Address.Type.fromLength(blockEntityTag.getByte(CartoucheBlockEntity.LOCAL_ADDRESS))));
		}
    }
	
	public static Component localAddressComponent(Address.Type type)
	{
		MutableComponent component = switch(type)
		{
			case ADDRESS_7_CHEVRON -> Component.translatable("tooltip.sgjourney.local_7_chevron_address");
			case ADDRESS_8_CHEVRON -> Component.translatable("tooltip.sgjourney.local_8_chevron_address");
			case ADDRESS_9_CHEVRON -> Component.translatable("tooltip.sgjourney.local_9_chevron_address");
			default -> Component.empty();
		};
		
		return component.withStyle(type.getChatFormatting());
	}
	
	@Override
	public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state)
	{
		return PushReaction.BLOCK;
	}
	
	public static BlockPos getOtherCartoucheHalfPos(BlockPos pos, BlockState state)
	{
		DoubleBlockHalf half = state.getValue(CartoucheBlock.HALF);
		Direction direction = state.getValue(CartoucheBlock.FACING);
		Orientation orientation = state.getValue(CartoucheBlock.ORIENTATION);
		
		return pos.relative(Orientation.getMultiDirection(direction, half == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP, orientation));
	}
	
	protected abstract void openCartoucheGravingMenu(Level level, BlockPos pos, @Nullable Player player);
	
	@Override
	public InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack)
	{
		BlockState state = level.getBlockState(pos);
		if(state.hasProperty(CartoucheBlock.HALF))
		{
			if(state.getValue(CartoucheBlock.HALF) == DoubleBlockHalf.UPPER)
				return onGraverUsed(level, getOtherCartoucheHalfPos(pos, state), player, hand, graverStack);
			
			if(!level.isClientSide())
				openCartoucheGravingMenu(level, pos, player);
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setSymbols(Level level, BlockPos pos, BlockState state, ResourceKey<Symbols> symbols)
	{
		if(level.getBlockEntity(pos) instanceof CartoucheBlockEntity cartouche)
			cartouche.setSymbols(symbols);
	}
	
	@Override
	public void setAddress(Level level, BlockPos pos, BlockState state, Address address)
	{
		if(level.getBlockEntity(pos) instanceof CartoucheBlockEntity cartouche)
			cartouche.setAddress(address);
	}
	
	
	
	public static ItemStack localAddressSetup(ItemLike item, Address.Type type)
	{
		ItemStack stack = new ItemStack(item);
		CompoundTag blockEntityTag = new CompoundTag();
		blockEntityTag.putByte(CartoucheBlockEntity.LOCAL_ADDRESS, type.byteValue());
		stack.addTagElement(BlockItem.BLOCK_ENTITY_TAG, blockEntityTag);
		
		return stack;
	}
	
	
    
    public static class Stone extends CartoucheBlock
    {
		public Stone(Properties properties)
		{
			super(properties);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new CartoucheBlockEntity.Stone(pos, state);
		}

	    public Block getBlock()
	    {
	    	return BlockInit.STONE_CARTOUCHE.get();
	    }

		@Override
		public ItemLike getItem()
		{
			return BlockInit.STONE_CARTOUCHE.get();
		}
		
		@Override
		protected void openCartoucheGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof CartoucheBlockEntity.Stone cartouche)
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
						return new CartoucheGravingMenu.Stone(windowId, playerInventory, cartouche, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, cartouche.getBlockPos());
			}
		}
    }
    
    public static class Sandstone extends CartoucheBlock
    {
		public Sandstone(Properties properties)
		{
			super(properties);
		}

		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new CartoucheBlockEntity.Sandstone(pos, state);
		}

	    public Block getBlock()
	    {
	    	return BlockInit.SANDSTONE_CARTOUCHE.get();
	    }

		@Override
		public ItemLike getItem()
		{
			return BlockInit.SANDSTONE_CARTOUCHE.get();
		}
		
		@Override
		protected void openCartoucheGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof CartoucheBlockEntity.Sandstone cartouche)
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
						return new CartoucheGravingMenu.Sandstone(windowId, playerInventory, cartouche, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, cartouche.getBlockPos());
			}
		}
    }
	
	public static class RedSandstone extends CartoucheBlock
	{
		public RedSandstone(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new CartoucheBlockEntity.RedSandstone(pos, state);
		}
		
		public Block getBlock()
		{
			return BlockInit.RED_SANDSTONE_CARTOUCHE.get();
		}
		
		@Override
		public ItemLike getItem()
		{
			return BlockInit.RED_SANDSTONE_CARTOUCHE.get();
		}
		
		@Override
		protected void openCartoucheGravingMenu(Level level, BlockPos pos, @Nullable Player player)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof CartoucheBlockEntity.RedSandstone cartouche)
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
						return new CartoucheGravingMenu.RedSandstone(windowId, playerInventory, cartouche, ContainerLevelAccess.create(level, pos));
					}
				};
				NetworkHooks.openScreen((ServerPlayer) player, containerProvider, cartouche.getBlockPos());
			}
		}
	}
}
