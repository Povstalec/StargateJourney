package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.packets.ClientboundArcheologistNotebookOpenScreenPacket;

public class ArcheologistNotebook extends Item
{
	public ArcheologistNotebook(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(!level.isClientSide())
		{
			ItemStack stack = player.getItemInHand(usedHand);
			CompoundTag compoundtag = /*stack.hasTag() ? stack.getTag() :*/ new CompoundTag(); //TODO Create a new Data Component for the book
			
			PacketDistributor.sendToPlayer((ServerPlayer) player,
					new ClientboundArcheologistNotebookOpenScreenPacket(player.getUUID(), usedHand == InteractionHand.MAIN_HAND, compoundtag));
		}
		
		return super.use(level, player, usedHand);
	}
}
