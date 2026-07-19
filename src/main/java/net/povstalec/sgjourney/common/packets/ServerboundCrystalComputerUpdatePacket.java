package net.povstalec.sgjourney.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.CrystalComputerItem;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

public record ServerboundCrystalComputerUpdatePacket(InteractionHand hand, CompoundTag crystalTag) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundCrystalComputerUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_crystal_computer_update"));
	
	public static final StreamCodec<FriendlyByteBuf, ServerboundCrystalComputerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ServerboundCrystalComputerUpdatePacket::hand,
			ByteBufCodecs.COMPOUND_TAG, ServerboundCrystalComputerUpdatePacket::crystalTag,
			ServerboundCrystalComputerUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	private static ListTag getMemoryList(CompoundTag tag)
	{
		if(tag != null && tag.contains(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_LIST))
			return tag.getList(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_COMPOUND);
		
		return new ListTag();
	}
	
	private static void updateItemInHand(Player player, InteractionHand hand, CompoundTag crystalTag)
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
	
	public static void handle(ServerboundCrystalComputerUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			if(!packet.crystalTag.isEmpty())
				updateItemInHand(ctx.player(), packet.hand, packet.crystalTag);
		});
	}
}


