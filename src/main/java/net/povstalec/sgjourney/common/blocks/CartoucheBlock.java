package net.povstalec.sgjourney.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Symbols;

public abstract class CartoucheBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	public static final String ADDRESS_TABLE = "AddressTable";
	public static final String DIMENSION = "Dimension";
	public static final String ADDRESS = "Address";
	
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
        		level.setBlock(destroyPos, Blocks.AIR.defaultBlockState(), 3);
        	
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
				
				if(blockEntity instanceof CartoucheEntity cartouche)
				{
					Address address = cartouche.getAddress();
					
					if(address.getDimension().isPresent())
						player.sendSystemMessage(Component.translatable("info.sgjourney.dimension").append(Component.literal(": ")).append(address.getDimension().get()).withStyle(ChatFormatting.GREEN));
					player.sendSystemMessage(Component.translatable("info.sgjourney.address").append(Component.literal(": ")).withStyle(ChatFormatting.YELLOW).append(address.toComponent(true)));
					
					if(cartouche.getSymbols() != null)
					{
						MutableComponent symbolsText = Component.translatable("info.sgjourney.symbols").append(Component.literal(": " + cartouche.getSymbols())).withStyle(ChatFormatting.LIGHT_PURPLE);
						
						player.sendSystemMessage(symbolsText);
					}
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
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack)
    {
    	Orientation orientation = state.getValue(ORIENTATION);
    	Direction direction = state.getValue(FACING);
    	BlockPos blockpos = pos.relative(Orientation.getCenterDirection(direction, orientation));
    	
    	level.setBlock(blockpos, getBlock().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(ORIENTATION, orientation).setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}
    
    @Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
    	Direction direction = state.getValue(FACING);
    	Orientation orientation = state.getValue(ORIENTATION);
    	DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
    	
    	if(doubleblockhalf == DoubleBlockHalf.UPPER)
    		pos = pos.relative(Orientation.getMultiDirection(direction, Direction.DOWN, orientation));
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof CartoucheEntity)
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
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
        if (!level.isClientSide())
        {
            return (localLevel, pos, blockState, entity) -> {
                if (entity instanceof CartoucheEntity cartouche) 
                {
                	cartouche.tick(localLevel, pos, blockState);
                }
            };
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	boolean hasAddress = false;
    	String dimension = "";
    	String symbols = "";
    	String addressTable = "";
    	
    	if(stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
    	{
    		CompoundTag tag = stack.getTag().getCompound("BlockEntityTag");
    		
    		if(tag.contains(CartoucheEntity.ADDRESS))
    		{
    			hasAddress = true;
    			
    			int[] addressArray = tag.getIntArray(CartoucheEntity.ADDRESS);
    			
    			Address address = new Address(addressArray);
    			tooltipComponents.add(Component.translatable("tooltip.sgjourney.address").append(Component.literal(": ").append(address.toComponent(false))).withStyle(ChatFormatting.YELLOW));
    		}
    		
    		if(tag.contains(CartoucheEntity.DIMENSION))
    			dimension = tag.getString(CartoucheEntity.DIMENSION);
    		
    		if(tag.contains(CartoucheEntity.SYMBOLS))
    		{
        		Minecraft minecraft = Minecraft.getInstance();
        		ClientPacketListener clientPacketListener = minecraft.getConnection();
        		RegistryAccess registries = clientPacketListener.registryAccess();
        		Registry<Symbols> symbolsRegistry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
        		
    			ResourceLocation location = new ResourceLocation(tag.getString(CartoucheEntity.SYMBOLS));
    			if(location.toString().equals("sgjourney:empty"))
    				symbols = "Empty";
    			else if(symbolsRegistry.containsKey(location))
    				symbols = symbolsRegistry.get(location).getTranslationName(!ClientStargateConfig.unique_symbols.get());
    			else
    				symbols = "Error";
    		}
        	
        	if(tag.contains(CartoucheEntity.ADDRESS_TABLE))
        	{
        		addressTable = tag.getString(CartoucheEntity.ADDRESS_TABLE);
        		if(!addressTable.equals("sgjourney:empty"))
        			tooltipComponents.add(Component.translatable("tooltip.sgjourney.address_table").append(Component.literal(": " + addressTable)).withStyle(ChatFormatting.YELLOW));
        	}
    	}
    	
    	if(!hasAddress)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.dimension").append(Component.literal(": " + dimension)).withStyle(ChatFormatting.GREEN));
		tooltipComponents.add(Component.translatable(Symbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
    	
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
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
			return new CartoucheEntity.Stone(pos, state);
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
			return new CartoucheEntity.Sandstone(pos, state);
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
			return new CartoucheEntity.RedSandstone(pos, state);
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
		
	}
}
