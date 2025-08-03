package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundTransceiverUpdatePacket;

public class TransceiverMenu extends AbstractContainerMenu
{
	private final TransceiverEntity transceiverEntity;
	private final Level level;
	
	public TransceiverMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
	{
		this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
	}
	
	public TransceiverMenu(int containerId, Inventory inventory, BlockEntity blockEntity)
	{
		super(MenuInit.TRANSCEIVER.get(), containerId);
		
		transceiverEntity = ((TransceiverEntity) blockEntity);
		this.level = inventory.player.level();
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, transceiverEntity.getBlockPos()),
				player, BlockInit.TRANSCEIVER.get());
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) 
	{
		return null;
	}
	
	public boolean editingFrequency()
	{
		return transceiverEntity.editingFrequency();
	}
	
	public int getFrequency()
	{
		return transceiverEntity.getFrequency();
	}
	
	public String getCurrentCode()
	{
		return transceiverEntity.getCurrentCode();
	}
    
    public void toggleFrequency()
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundTransceiverUpdatePacket(transceiverEntity.getBlockPos(), false, true, 0, false));
    }
    
    public void sendTransmission()
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundTransceiverUpdatePacket(transceiverEntity.getBlockPos(), false, false, 0, true));
    }
    
    public void addToCode(boolean toggledFrequency, int number)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundTransceiverUpdatePacket(transceiverEntity.getBlockPos(), false, false, number, false));
    }
    
    public void removeFromCode(boolean toggledFrequency)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundTransceiverUpdatePacket(transceiverEntity.getBlockPos(), true, false, 0, false));
    }
}
