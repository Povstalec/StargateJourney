package net.povstalec.sgjourney.common.blocks.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import net.povstalec.sgjourney.common.misc.NetworkUtils;

import javax.annotation.Nullable;
import java.util.List;

public abstract class CrystalDHDBlock extends AbstractDHDBlock
{
	public CrystalDHDBlock(Properties properties)
	{
		super(properties);
	}
	
	protected void openCrystalMenu(Player player, CrystalDHDEntity dhd)
	{
		MenuProvider containerProvider = new MenuProvider()
		{
			@Override
			public Component getDisplayName()
			{
				return Component.translatable("screen.sgjourney.dhd");
			}
			
			@Override
			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity)
			{
				return new DHDCrystalMenu(windowId, playerInventory, dhd);
			}
		};
		NetworkUtils.openMenu((ServerPlayer) player, containerProvider, dhd.getBlockPos());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		if(stack.has(DataComponents.BLOCK_ENTITY_DATA))
		{
			CompoundTag blockEntityTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).getUnsafe();
			ListTag tagList = blockEntityTag.getCompound(CrystalDHDEntity.CRYSTAL_INVENTORY).getList("Items", Tag.TAG_COMPOUND);
			
			if(tagList.size() > 0)
			{
				CompoundTag list1 = tagList.getCompound(0);
				
				if(list1.contains("id", Tag.TAG_STRING) && list1.getString("id").equals(InventoryUtil.itemName(ItemInit.LARGE_CONTROL_CRYSTAL.get())) && list1.contains("count", Tag.TAG_INT) && list1.getInt("count") > 0)
					tooltipComponents.add(Component.translatable("tooltip.sgjourney.dhd.has_control_crystal").withStyle(ChatFormatting.DARK_RED));
			}
		}
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
}
