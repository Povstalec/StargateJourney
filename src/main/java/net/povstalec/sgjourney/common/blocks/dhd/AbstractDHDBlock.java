package net.povstalec.sgjourney.common.blocks.dhd;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

import java.util.List;


public abstract class AbstractDHDBlock extends HorizontalDirectionalBlock implements EntityBlock, ProtectedBlock
{
	public AbstractDHDBlock(Properties properties) 
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(level.isClientSide())
			return;
		
		if(oldState.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof AbstractDHDEntity dhd)
				dhd.stargateCache.clearTwoWays();
		}
		
		super.onRemove(oldState, level, pos, newState, isMoving);
	}
    
    public abstract Block getDHD();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractDHDEntity)
		{
			if(!level.isClientSide && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(getDHD());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>) ticker : null;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.dhd.description"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.dhd.dialing_menu"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.dhd.crystal_menu"));
		
		String pointOfOriginString = "";
		String symbolsString = "";
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		long energy = 0;
		
		if(blockEntityTag != null)
		{
			if(blockEntityTag.contains(AbstractDHDEntity.POINT_OF_ORIGIN))
				pointOfOriginString = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(AbstractDHDEntity.POINT_OF_ORIGIN))), "Error");
			if(blockEntityTag.contains(AbstractDHDEntity.SYMBOLS))
				symbolsString = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(AbstractDHDEntity.SYMBOLS))), "Error");
			
			if(blockEntityTag.contains(AbstractDHDEntity.ENERGY))
				energy = blockEntityTag.getLong(AbstractDHDEntity.ENERGY);
		}
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOriginString)).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbolsString)).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		tooltipComponents.add(ComponentHelper.energy("tooltip.sgjourney.energy_buffer", energy));
		
		if(blockEntityTag != null && blockEntityTag.contains(AbstractDHDEntity.GENERATION_STEP, CompoundTag.TAG_BYTE) && StructureGenEntity.Step.GENERATED != StructureGenEntity.Step.fromByte(blockEntityTag.getByte(AbstractDHDEntity.GENERATION_STEP)))
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.generates_inside_structure").withStyle(ChatFormatting.YELLOW));
		
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
	}
	
	@Nullable
	public ProtectedBlockEntity getProtectedBlockEntity(BlockGetter reader, BlockPos pos, BlockState state)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof AbstractDHDEntity dhd)
			return dhd;
		
		return null;
	}
	
	@Override
	public boolean hasPermissions(BlockGetter reader, BlockPos pos, BlockState state, Player player, boolean sendMessage)
	{
		BlockEntity blockEntity = reader.getBlockEntity(pos);
		
		if(blockEntity instanceof AbstractDHDEntity dhd)
			return dhd.hasPermissions(player, sendMessage);
		
		return true;
	}
}
