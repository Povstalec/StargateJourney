package net.povstalec.sgjourney.common.blocks.tech_interface;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;
import net.povstalec.sgjourney.common.misc.ComponentHelper;

public class AdvancedCrystalInterfaceBlock extends AbstractInterfaceBlock
{
	public AdvancedCrystalInterfaceBlock(Properties properties)
	{
		super(properties);
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
	
	@Override
	public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace)
	{
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof AdvancedCrystalInterfaceEntity interfaceEntity)
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
					return new InterfaceMenu.AdvancedCrystal(windowId, playerInventory, interfaceEntity);
				}
			};
			NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
		}
		else
			throw new IllegalStateException("Our named container provider is missing!");
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ADVANCED_CRYSTAL_INTERFACE.get(), AdvancedCrystalInterfaceEntity::tick);
    }
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
		
		tooltipComponents.add(ComponentHelper.description("block.sgjourney.advanced_crystal_interface.description"));
		tooltipComponents.add(ComponentHelper.usage("block.sgjourney.advanced_crystal_interface.description.mode"));
    }
}
