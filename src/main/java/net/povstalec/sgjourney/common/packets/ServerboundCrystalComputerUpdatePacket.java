package net.povstalec.sgjourney.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.items.CrystalComputerItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

import java.util.function.Supplier;

public class ServerboundCrystalComputerUpdatePacket
{
	public final CompoundTag mainHandCrystalTag;
	public final CompoundTag offHandCrystalTag;

	public ServerboundCrystalComputerUpdatePacket(CompoundTag mainHandCrystalTag, CompoundTag offHandCrystalTag)
	{
		this.mainHandCrystalTag = mainHandCrystalTag;
		this.offHandCrystalTag = offHandCrystalTag;
	}

	public ServerboundCrystalComputerUpdatePacket(FriendlyByteBuf buffer)
	{
		this(buffer.readNbt(), buffer.readNbt());
	}

	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeNbt(mainHandCrystalTag);
		buffer.writeNbt(offHandCrystalTag);
	}
	
	private static ListTag getMemoryList(CompoundTag tag)
	{
		if(tag != null && tag.contains(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_LIST))
			return tag.getList(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_COMPOUND);
		
		return new ListTag();
	}
	
	private static void updateItemInHand(ServerPlayer player, InteractionHand hand, ListTag list)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		if(stack.getItem() instanceof CrystalComputerItem crystalComputer)
			crystalComputer.updateFromList(stack, list);
		else if(stack.getItem() instanceof MemoryCrystalItem)
			MemoryCrystalItem.setMemoryList(stack, list);
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			final ServerPlayer player = ctx.get().getSender();
			
			updateItemInHand(player, InteractionHand.MAIN_HAND, getMemoryList(mainHandCrystalTag));
			updateItemInHand(player, InteractionHand.OFF_HAND, getMemoryList(offHandCrystalTag));
		});
		return true;
	}
}


