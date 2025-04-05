package net.povstalec.sgjourney.common.blocks.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.dhd.CrystalDHDEntity;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.menu.DHDCrystalMenu;
import net.povstalec.sgjourney.common.misc.InventoryUtil;

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
		if(!dhd.hasPermissions(player, true))
			return;
		
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
		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, dhd.getBlockPos());
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
	{
		if(stack.hasTag())
		{
			CompoundTag blockEntityTag = BlockItem.getBlockEntityData(stack);
			ListTag tagList = blockEntityTag.getCompound("Inventory").getList("Items", Tag.TAG_COMPOUND);
			
			if(tagList.size() > 0)
			{
				CompoundTag list1 = tagList.getCompound(0);
				
				if(list1.contains("id", Tag.TAG_STRING) && list1.getString("id").equals(InventoryUtil.itemName(ItemInit.LARGE_CONTROL_CRYSTAL.get())) && list1.contains("Count", Tag.TAG_BYTE) && list1.getByte("Count") > 0)
					tooltipComponents.add(Component.translatable("tooltip.sgjourney.dhd.has_control_crystal").withStyle(ChatFormatting.DARK_RED));
			}
		}
		
		super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
	}
}
