package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;

public class CrystalConfiguratorItem extends Item
{
	public CrystalConfiguratorItem(Properties properties)
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
		
		if(offHandItem.getItem() instanceof CrystalConfiguratorItem && mainHandItem.getItem() instanceof MemoryCrystalItem memoryCrystal)
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof AbstractTransporterEntity transporter)
			{
				if(transporter.getID() != null)
				{
					memoryCrystal.saveUUID(mainHandItem, transporter.getID());
					player.displayClientMessage(Component.translatable("message.sgjourney.memory_crystal.saved_id").withStyle(ChatFormatting.BLUE), true);
					return InteractionResult.SUCCESS;
				}
			}
		}
		
		return InteractionResult.PASS;
	}
}
