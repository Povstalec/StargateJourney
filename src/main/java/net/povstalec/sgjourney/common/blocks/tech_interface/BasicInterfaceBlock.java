package net.povstalec.sgjourney.common.blocks.tech_interface;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import javax.annotation.Nullable;
import java.util.List;

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
	public void openMenu(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult trace)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof BasicInterfaceEntity interfaceEntity)
		{
			MenuProvider containerProvider = new MenuProvider()
			{
				@Override
				public Component getDisplayName()
				{
					return Component.translatable("screen.sgjourney." + interfaceEntity.getInterfaceType().getName());
				}
				
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
				{
					return new InterfaceMenu.Basic(windowId, playerInventory, interfaceEntity);
				}
			};
			NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
		}
		else
			throw new IllegalStateException("Our named container provider is missing!");
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
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
		
		tooltipComponents.add(ComponentHelper.description("block.sgjourney.basic_interface.description"));
		tooltipComponents.add(ComponentHelper.usage("block.sgjourney.basic_interface.description.mode"));
    }
}
