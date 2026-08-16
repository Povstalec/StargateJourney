package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class NaquadahGeneratorMenu extends EnergyBlockMenu<NaquadahGeneratorEntity>
{
	public NaquadahGeneratorMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(containerId, inventory, (NaquadahGeneratorEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
	}
	
	public NaquadahGeneratorMenu(int containerId, Inventory inventory, NaquadahGeneratorEntity blockEntity)
	{
		super(MenuInit.NAQUADAH_GENERATOR.get(), containerId, inventory, blockEntity);
		checkContainerSize(inventory, 1);
	
		addPlayerInventory(inventory, 8, 86);
		addPlayerHotbar(inventory, 8, 144);
		
		this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
			this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 62, 35));
		});
	}
	
	public int getReactionProgress()
	{
		return this.blockEntity.getReactionProgress();
	}
	
	public long getReactionTime()
	{
		return this.blockEntity.getReactionTime();
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.NAQUADAH_REACTOR.get()) ||
				stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.NAQUADAH_GENERATOR_MARK_I.get()) ||
				stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.NAQUADAH_GENERATOR_MARK_II.get());
	}
}
