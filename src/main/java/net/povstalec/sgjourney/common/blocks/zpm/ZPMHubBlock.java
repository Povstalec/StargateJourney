package net.povstalec.sgjourney.common.blocks.zpm;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMHubEntity;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ZPMHubBlock extends AbstractZPMEnergyHolderBlock
{
	public static final MapCodec<ZPMHubBlock> CODEC = simpleCodec(ZPMHubBlock::new);
	
	public ZPMHubBlock(Properties properties)
	{
		super(properties);
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return CODEC;
	}
	
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new ZPMHubEntity(pos, state);
	}
	
	@Override
	public long getMaxEnergyTransfer()
	{
		return SyncedConfig.zpm_hub_max_transfer.get();
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
		tooltipComponents.add(ComponentHelper.description("block.sgjourney.zpm_hub.description"));
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ZPM_HUB.get(), ZPMHubEntity::tick);
    }
}
