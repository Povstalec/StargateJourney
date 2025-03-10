package net.povstalec.sgjourney.common.blocks.tech;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.tech.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class AdvancedCrystalInterfaceBlock extends AbstractInterfaceBlock
{
	public static final MapCodec<AdvancedCrystalInterfaceBlock> CODEC = simpleCodec(AdvancedCrystalInterfaceBlock::new);

	public AdvancedCrystalInterfaceBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected MapCodec<AdvancedCrystalInterfaceBlock> codec()
	{
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new AdvancedCrystalInterfaceEntity(pos, state);
	}
	
	public Block getDroppedBlock()
	{
		return BlockInit.ADVANCED_CRYSTAL_INTERFACE.get();
	}
	
	public long getCapacity()
	{
		return CommonInterfaceConfig.advanced_crystal_interface_capacity.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), CrystalInterfaceEntity::tick);
    }
	
	@Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
		tooltipComponents.add(Component.translatable("block.sgjourney.advanced_crystal_interface.description").withStyle(ChatFormatting.DARK_GRAY));
		tooltipComponents.add(Component.translatable("block.sgjourney.advanced_crystal_interface.description.mode").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
