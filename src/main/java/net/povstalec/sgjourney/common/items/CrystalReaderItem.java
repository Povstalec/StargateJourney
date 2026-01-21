package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.sgjourney.MemoryEntry;

public class CrystalReaderItem extends Item
{
	public CrystalReaderItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		Player player = context.getPlayer();
		
		ItemStack offHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
		ItemStack mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(offHandItem.getItem() instanceof CrystalReaderItem && mainHandItem.getItem() instanceof MemoryCrystalItem memoryCrystal)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof AbstractTransporterEntity transporter)
			{
				if(transporter.getID() != null)
				{
					memoryCrystal.saveMemoryEntry(mainHandItem, new MemoryEntry.TransporterID("", level.getGameTime(), MemoryEntry.Type.TRANSPORTER_ID, transporter.getID()), false);
					player.displayClientMessage(Component.translatable("message.sgjourney.memory_crystal.saved.transporter_id").withStyle(ChatFormatting.BLUE), true);
					return InteractionResult.SUCCESS;
				}
			}
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(!level.isClientSide())
			return super.use(level, player, usedHand);
		
		ItemStack offHandStack = player.getItemInHand(InteractionHand.OFF_HAND);
		ItemStack mainHandStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		
		if(offHandStack.getItem() instanceof CrystalReaderItem && mainHandStack.getItem() instanceof MemoryCrystalItem memoryCrystal)
		{
			ListTag list = memoryCrystal.getMemoryList(mainHandStack);
			
			for(int i = 0; i < list.size(); i++)
			{
				//TODO player.sendSystemMessage(Component.literal(memoryCrystal.memoryStringAt(list, i)));
			}
		}
		
		return super.use(level, player, usedHand);
	}
}
