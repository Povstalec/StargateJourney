package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AndromedaStargateEntity;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.graver.StargateEngravingMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.NetworkUtils;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AndromedaStargateBlock extends RotatingStargateBaseBlock implements SpecialEngravableBlock
{
	public AndromedaStargateBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		 return new AndromedaStargateEntity(pos, state);
	}
	
	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.ANDROMEDA_RING.get();
	}
	
	@Override
	public AbstractShieldingBlock getIris()
	{
		return BlockInit.ANDROMEDA_SHIELDING.get();
	}

	@Override
	public BlockState ringState()
	{
		return getRing().defaultBlockState();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ANDROMEDA_STARGATE.get(), AndromedaStargateEntity::tick);
    }
	
    @Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		ResourceKey<PointOfOrigin> pointOfOrigin = null;
		ResourceKey<Symbols> symbols = null;
		
		if(blockEntityTag != null)
		{
			if(blockEntityTag.contains(AbstractStargateEntity.POINT_OF_ORIGIN))
				pointOfOrigin = Conversion.stringToPointOfOrigin(blockEntityTag.getString(AbstractStargateEntity.POINT_OF_ORIGIN));
			
			if(blockEntityTag.contains(AbstractStargateEntity.SYMBOLS))
				symbols = Conversion.stringToSymbols(blockEntityTag.getString(AbstractStargateEntity.SYMBOLS));
		}
		
		tooltipComponents.add(ClientPointOfOrigin.translationComponent(pointOfOrigin, ComponentHelper.ERROR).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipComponents.add(ClientSymbols.translationComponent(symbols, ComponentHelper.ERROR).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
	
	protected void openStargateGravingMenu(Level level, BlockPos pos, BlockState state, @Nullable Player player)
	{
		if(getStargate(level, pos, state) instanceof AndromedaStargateEntity andromedaStargate)
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
					return new StargateEngravingMenu.Andromeda(windowId, playerInventory, andromedaStargate, ContainerLevelAccess.create(level, pos));
				}
			};
			NetworkUtils.openMenu((ServerPlayer) player, containerProvider, andromedaStargate.getBlockPos());
		}
	}
	
	@Override
	public InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack)
	{
		if(!level.isClientSide())
			openStargateGravingMenu(level, pos, level.getBlockState(pos), player);
		
		return InteractionResult.PASS;
	}
	
	@Override
	public void setPointOfOrigin(Level level, BlockPos pos, BlockState state, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		if(stargate != null)
		{
			stargate.symbolInfo().setPointOfOrigin(pointOfOrigin);
			stargate.setChanged();
			stargate.updateClient();
		}
	}
	
	@Override
	public @Nullable ResourceKey<PointOfOrigin> getPointOfOrigin(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.symbolInfo().pointOfOrigin();
		
		return null;
	}
	
	@Override
	public void setSymbols(Level level, BlockPos pos, BlockState state, ResourceKey<Symbols> symbols)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		if(stargate != null)
		{
			stargate.symbolInfo().setSymbols(symbols);
			stargate.setChanged();
			stargate.updateClient();
		}
	}
	
	@Override
	public @Nullable ResourceKey<Symbols> getSymbols(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.symbolInfo().symbols();
		
		return null;
	}
}
