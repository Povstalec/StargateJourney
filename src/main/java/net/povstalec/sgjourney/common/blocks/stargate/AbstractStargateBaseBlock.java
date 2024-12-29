package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.items.StargateVariantItem;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateConnection;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public abstract class AbstractStargateBaseBlock extends AbstractStargateBlock implements EntityBlock
{
	public static final String EMPTY = StargateJourney.EMPTY;
	public static final String LOCAL_POINT_OF_ORIGIN = "LocalPointOfOrigin";
	
	public AbstractStargateBaseBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties, width, horizontalOffset);
	}
	
	public abstract AbstractStargateRingBlock getRing();
	
	public abstract AbstractShieldingBlock getIris();
	
	public boolean setVariant(Level level, BlockPos pos, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		Item item = stack.getItem();
		
		if(item instanceof StargateVariantItem)
		{
			if(!stack.has(DataComponentInit.STARGATE_VARIANT))
			{
				BlockEntity blockEntity = level.getBlockEntity(pos);
				if(blockEntity instanceof AbstractStargateEntity stargate)
				{
					if(stargate.getVariant().equals(StargateJourney.EMPTY))
					{
						player.displayClientMessage(Component.translatable("block.sgjourney.stargate.same_variant"), true);
						return true;
					}
					
					stargate.setVariant(StargateJourney.EMPTY_LOCATION);
					
					if(!player.isCreative())
						stack.shrink(1);
				}
				
				return true;
			}
			
			ResourceLocation variant = StargateVariantItem.getStargateVariant(stack);
			
			if(variant != null)
			{
				if(level.isClientSide())
					return true;
				
				BlockEntity blockEntity = level.getBlockEntity(pos);
				
				if(blockEntity instanceof AbstractStargateEntity stargate)
				{
					if(variant.toString().equals(stargate.getVariant()))
					{
						player.displayClientMessage(Component.translatable("block.sgjourney.stargate.same_variant"), true);
						return true;
					}
					
					RegistryAccess registries = level.getServer().registryAccess();
			        Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
			        
			        Optional<StargateVariant> stargateVariant = Optional.ofNullable(variantRegistry.get(variant));
					
					if(stargateVariant.isPresent() && !stargateVariant.get().getBaseStargate().equals(BlockEntityType.getKey(stargate.getType())))
					{
						player.displayClientMessage(Component.translatable("block.sgjourney.stargate.incorrect_stargate_type"), true);
						return true;
					}
					
					stargate.setVariant(variant);
					
					if(!player.isCreative())
						stack.shrink(1);
					
					return true;
				}
			}
			else
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.invalid_variant"), true);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(stack.is(ItemInit.STARGATE_VARIANT_CRYSTAL.get()))
			return setVariant(level, pos, player, hand) ? ItemInteractionResult.SUCCESS : ItemInteractionResult.FAIL;
		
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		Player player = context.getPlayer();
		Orientation orientation = Orientation.getOrientationFromXRot(player);
		
		return this.defaultBlockState()
				.setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER))
				.setValue(ORIENTATION, orientation);
	}
	 
	@Nullable
	@Override
	public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState state);
	
	public abstract BlockState ringState();
	
	@Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, placer, stack);
		
		for(StargatePart part : getParts())
		{
			if(!part.equals(StargatePart.BASE))
			{
				level.setBlock(part.getRingPos(pos,  state.getValue(FACING), state.getValue(ORIENTATION)), 
						ringState()
						.setValue(AbstractStargateRingBlock.PART, part)
						.setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING))
						.setValue(AbstractStargateRingBlock.ORIENTATION, level.getBlockState(pos).getValue(ORIENTATION))
						.setValue(WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getRingPos(pos, state.getValue(FACING), state.getValue(ORIENTATION))).getType() == Fluids.WATER)), 3);
			}
		}
		
		AbstractStargateEntity stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			updateIris(level, pos, state, stargate.getShieldingState());
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(oldState.getBlock() != newState.getBlock())
        {
    		BlockEntity blockentity = level.getBlockEntity(pos);
    		if(blockentity instanceof AbstractStargateEntity stargate)
    		{
    			stargate.bypassDisconnectStargate(Stargate.Feedback.STARGATE_DESTROYED, false);
    			stargate.unsetDHD(true);
    			stargate.removeStargateFromNetwork();
    		}
    		
    		destroyStargate(level, pos, getParts(), getShieldingParts(), oldState.getValue(FACING), oldState.getValue(ORIENTATION), oldState.getValue(PART));
    		
            super.onRemove(oldState, level, pos, newState, isMoving);
        }
    }
	
	public void updateStargate(Level level, BlockPos pos, BlockState state, StargateConnection.State connectionState, int chevronsActive, ShieldingState shieldingState)
	{
		if(!(state.getBlock() instanceof AbstractStargateBlock))
			return;
		
		level.setBlock(pos, state.setValue(AbstractStargateBaseBlock.CONNECTION_STATE, connectionState)
				.setValue(AbstractStargateBaseBlock.CHEVRONS_ACTIVE, chevronsActive), 2);
		
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);
		
		for(StargatePart part : getParts(shieldingState != ShieldingState.OPEN))
		{
			if(!part.equals(StargatePart.BASE))
			{
				BlockPos ringPos = part.getRingPos(pos,  direction, orientation);
				if(level.getBlockState(ringPos).getBlock() instanceof AbstractStargateBlock)
				{
					level.setBlock(part.getRingPos(pos,  direction, orientation), 
							ringState()
							.setValue(AbstractStargateRingBlock.PART, part)
							.setValue(AbstractStargateRingBlock.CONNECTION_STATE, level.getBlockState(pos).getValue(CONNECTION_STATE))
							.setValue(AbstractStargateRingBlock.CHEVRONS_ACTIVE, level.getBlockState(pos).getValue(CHEVRONS_ACTIVE))
							.setValue(AbstractStargateRingBlock.FACING, level.getBlockState(pos).getValue(FACING))
							.setValue(AbstractStargateRingBlock.ORIENTATION, level.getBlockState(pos).getValue(ORIENTATION))
							.setValue(AbstractStargateRingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getRingPos(pos, state.getValue(FACING), state.getValue(ORIENTATION))).getType() == Fluids.WATER)), 3);
				}
			}
		}
	}
	
	public void unsetIris(BlockState state, Level level, BlockPos pos)
	{
		AbstractStargateEntity stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			stargate.removeIris();
		
		updateStargate(level, pos, state, state.getValue(AbstractStargateBlock.CONNECTION_STATE), state.getValue(AbstractStargateBlock.CHEVRONS_ACTIVE), ShieldingState.OPEN);
	}
	
	public void updateIris(Level level, BlockPos pos, BlockState state, ShieldingState shieldingState)
	{
		if(getIris() == null)
			return;
			
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);
		
		AbstractShieldingBlock.setIrisState(getIris(), level, pos, getShieldingParts(), direction, orientation, shieldingState);
	}
	
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	long energy = 0;
        String id = "";
		boolean hasData = stack.has(DataComponents.BLOCK_ENTITY_DATA);
		CompoundTag blockEntityTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
    	
        if(hasData)
        {
            if(blockEntityTag.contains(AbstractStargateEntity.VARIANT))
            {
            	String variant = blockEntityTag.getString(AbstractStargateEntity.VARIANT);
            	
            	if(!variant.equals(EMPTY))
            		tooltipComponents.add(Component.translatable("tooltip.sgjourney.variant").append(Component.literal(": " + variant)).withStyle(ChatFormatting.GREEN));
            }
            
            if(blockEntityTag.contains(AbstractStargateEntity.ENERGY))
            	energy = blockEntityTag.getLong(AbstractStargateEntity.ENERGY);
        }
        
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy + " FE")).withStyle(ChatFormatting.DARK_RED));
		
        
        if(hasData)
        {
        	if((blockEntityTag.contains(AbstractStargateEntity.DISPLAY_ID) && blockEntityTag.getBoolean(AbstractStargateEntity.DISPLAY_ID)) || CommonStargateConfig.always_display_stargate_id.get())
        	{
        		if(blockEntityTag.contains(AbstractStargateEntity.ID))
        		{
        			id = blockEntityTag.getString(AbstractStargateEntity.ID);
                	tooltipComponents.add(Component.translatable("tooltip.sgjourney.9_chevron_address").append(Component.literal(": " + id)).withStyle(ChatFormatting.AQUA));
        		}
        		else if(blockEntityTag.contains(AbstractStargateEntity.ID_9_CHEVRON_ADDRESS))
        		{

        			id = new Address(blockEntityTag.getIntArray(AbstractStargateEntity.ID_9_CHEVRON_ADDRESS)).toString();
                	tooltipComponents.add(Component.translatable("tooltip.sgjourney.9_chevron_address").append(Component.literal(": " + id)).withStyle(ChatFormatting.AQUA));
        		}
            	
        	}
        	
        	if((blockEntityTag.contains(AbstractStargateEntity.UPGRADED) && blockEntityTag.getBoolean(AbstractStargateEntity.UPGRADED)))
            	tooltipComponents.add(Component.translatable("tooltip.sgjourney.upgraded").withStyle(ChatFormatting.DARK_GREEN));
        	
        	if((blockEntityTag.contains(LOCAL_POINT_OF_ORIGIN)))
            	tooltipComponents.add(Component.translatable("tooltip.sgjourney.local_point_of_origin").withStyle(ChatFormatting.GREEN));
        }
        
        if(hasData && blockEntityTag.contains(AbstractStargateEntity.ADD_TO_NETWORK) && !blockEntityTag.getBoolean(AbstractStargateEntity.ADD_TO_NETWORK))
            tooltipComponents.add(Component.translatable("tooltip.sgjourney.not_added_to_network").withStyle(ChatFormatting.YELLOW));
        
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	public static ItemStack excludeFromNetwork(ItemStack stack)
	{
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean(AbstractStargateEntity.ADD_TO_NETWORK, false);
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundtag));
		
		return stack;
	}
	
	public static ItemStack localPointOfOrigin(ItemStack stack)
	{
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putBoolean(LOCAL_POINT_OF_ORIGIN, true);
		stack.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(compoundtag));
		
		return stack;
	}
	
	@Override
	public AbstractStargateEntity getStargate(BlockGetter reader, BlockPos pos, BlockState state)
	{
		BlockEntity blockentity = reader.getBlockEntity(pos);
		
		if(blockentity instanceof AbstractStargateEntity stargate)
			return stargate;
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}
}
