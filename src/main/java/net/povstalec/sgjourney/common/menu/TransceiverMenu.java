package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.packets.ServerboundTransceiverUpdatePacket;

public class TransceiverMenu extends SGJourneyMenu<TransceiverEntity>
{
	public TransceiverMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(containerId, inventory, (TransceiverEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
	}
	
	public TransceiverMenu(int containerId, Inventory inventory, TransceiverEntity blockEntity)
	{
		super(MenuInit.TRANSCEIVER.get(), containerId, inventory, blockEntity);
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.TRANSCEIVER.get());
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) 
	{
		return ItemStack.EMPTY;
	}
	
	public boolean editingFrequency()
	{
		return blockEntity.editingFrequency();
	}
	
	public int getFrequency()
	{
		return blockEntity.getFrequency();
	}
	
	public String getCurrentCode()
	{
		return blockEntity.getCurrentCode();
	}
    
    public void toggleFrequency()
    {
		PacketDistributor.sendToServer(new ServerboundTransceiverUpdatePacket(blockEntity.getBlockPos(), false, true, 0, false));
    }
    
    public void sendTransmission()
    {
		PacketDistributor.sendToServer(new ServerboundTransceiverUpdatePacket(blockEntity.getBlockPos(), false, false, 0, true));
    }
    
    public void addToCode(boolean toggledFrequency, int number)
    {
		PacketDistributor.sendToServer(new ServerboundTransceiverUpdatePacket(blockEntity.getBlockPos(), false, false, number, false));
    }
    
    public void removeFromCode(boolean toggledFrequency)
    {
		PacketDistributor.sendToServer(new ServerboundTransceiverUpdatePacket(blockEntity.getBlockPos(), true, false, 0, false));
    }
}
