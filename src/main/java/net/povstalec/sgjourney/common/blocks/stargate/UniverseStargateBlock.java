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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.graver.StargateEngravingMenu;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class UniverseStargateBlock extends RotatingStargateBaseBlock implements SpecialEngravableBlock
{
	public UniverseStargateBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new UniverseStargateEntity(pos, state);
	}

	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.UNIVERSE_RING.get();
	}
	
	@Override
	public AbstractShieldingBlock getIris()
	{
		return BlockInit.UNIVERSE_SHIELDING.get();
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
		return createTickerHelper(type, BlockEntityInit.UNIVERSE_STARGATE.get(), UniverseStargateEntity::tick);
    }
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		CompoundTag blockEntityTag = InventoryUtil.getBlockEntityTag(stack);
		
		String pointOfOrigin = "";
		String symbols = "";
		
		if(blockEntityTag != null)
		{
			if(blockEntityTag.contains(AbstractStargateEntity.POINT_OF_ORIGIN))
				pointOfOrigin = ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(Conversion.stringToPointOfOrigin(blockEntityTag.getString(AbstractStargateEntity.POINT_OF_ORIGIN))), "tooltip.sgjourney.error");
			
			if(blockEntityTag.contains(AbstractStargateEntity.SYMBOLS))
				symbols = ClientSymbols.translationName(ClientSymbols.getSymbols(Conversion.stringToSymbols(blockEntityTag.getString(AbstractStargateEntity.SYMBOLS))), "tooltip.sgjourney.error");
		}
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(Component.literal(": ")).append(Component.translatable(pointOfOrigin)).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(Component.literal(": ")).append(Component.translatable(symbols)).withStyle(ChatFormatting.LIGHT_PURPLE));
		
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
	}
	
	protected void openStargateGravingMenu(Level level, BlockPos pos, BlockState state, @Nullable Player player)
	{
		if(getStargate(level, pos, state) instanceof UniverseStargateEntity universeStargate)
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
					return new StargateEngravingMenu.Universe(windowId, playerInventory, universeStargate, ContainerLevelAccess.create(level, pos));
				}
			};
			NetworkHooks.openScreen((ServerPlayer) player, containerProvider, universeStargate.getBlockPos());
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
	public @Nullable ResourceKey<PointOfOrigin> getPointOfOrigin(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.symbolInfo().pointOfOrigin();
		
		return null;
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
