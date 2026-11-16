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
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

import java.util.function.Supplier;

public class ServerboundCrystalComputerUpdatePacket
{
	public final InteractionHand hand;
	public final CompoundTag crystalTag;

	public ServerboundCrystalComputerUpdatePacket(InteractionHand hand, CompoundTag crystalTag)
	{
		this.hand = hand;
		this.crystalTag = crystalTag;
	}

	public ServerboundCrystalComputerUpdatePacket(FriendlyByteBuf buffer)
	{
		this(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buffer.readNbt());
	}

	public void encode(FriendlyByteBuf buffer)
	{
		buffer.writeBoolean(hand == InteractionHand.MAIN_HAND);
		buffer.writeNbt(crystalTag);
	}
	
	private static ListTag getMemoryList(CompoundTag tag)
	{
		if(tag != null && tag.contains(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_LIST))
			return tag.getList(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_COMPOUND);
		
		return new ListTag();
	}
	
	private static void updateItemInHand(ServerPlayer player, InteractionHand hand, CompoundTag crystalTag)
	{
		ItemStack stack = player.getItemInHand(hand);
		
		if(stack.getItem() instanceof CrystalComputerItem crystalComputer)
			crystalComputer.updateFromCompoundTag(stack, crystalTag);
		else if(stack.getItem() instanceof AbstractCrystalItem crystal)
		{
			if(crystal.getType() == CrystalCache.Type.MEMORY)
				MemoryCrystalItem.setMemoryList(stack, getMemoryList(crystalTag));
			else if(crystal.getType() == CrystalCache.Type.COMMUNICATION)
			{
				if(CommunicationCrystalItem.containsFrequency(crystalTag))
					CommunicationCrystalItem.setFrequency(stack, crystalTag.getInt(CommunicationCrystalItem.FREQUENCY));
				else if(crystalTag.contains(CommunicationCrystalItem.FREQUENCY, Tag.TAG_BYTE))
					CommunicationCrystalItem.unsetFrequency(stack);
			}
		}
	}

	public boolean handle(Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			final ServerPlayer player = ctx.get().getSender();
			
			if(!crystalTag.isEmpty())
				updateItemInHand(player, hand, crystalTag);
		});
		return true;
	}
}


