package net.povstalec.sgjourney.common.blocks.tech;

import java.util.List;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.tech.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class BasicInterfaceBlock extends AbstractInterfaceBlock
{
	public static final MapCodec<BasicInterfaceBlock> CODEC = simpleCodec(BasicInterfaceBlock::new);

	public BasicInterfaceBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	protected MapCodec<BasicInterfaceBlock> codec()
	{
		return CODEC;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new BasicInterfaceEntity(pos, state);
	}
	
	public Block getDroppedBlock()
	{
		return BlockInit.BASIC_INTERFACE.get();
	}
	
	public long getCapacity()
	{
		return CommonInterfaceConfig.basic_interface_capacity.get();
	}
	
	@Override
	public void updateInterface(BlockState state, Level level, BlockPos pos) //TODO Remove these eventually
	{
		if(state.getValue(MODE) == InterfaceMode.SHIELDING)
			level.setBlock(pos, state.setValue(AbstractInterfaceBlock.UPDATE, true).setValue(BasicInterfaceBlock.MODE, InterfaceMode.IRIS), 3);
		else
			level.setBlock(pos, state.setValue(AbstractInterfaceBlock.UPDATE, true), 3);
		level.scheduleTick(pos, this, 2);
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.BASIC_INTERFACE.get(), BasicInterfaceEntity::tick);
    }
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		tooltipComponents.add(Component.translatable("block.sgjourney.basic_interface.description").withStyle(ChatFormatting.DARK_GRAY));
		tooltipComponents.add(Component.translatable("block.sgjourney.basic_interface.description.mode").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
    }
}
