package net.povstalec.sgjourney.common.items;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.packets.ClientboundCrystalComputerOpenMainScreenPacket;
import net.povstalec.sgjourney.common.packets.ClientboundCrystalComputerOpenSaveScreenPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrystalComputerItem extends HolderItem
{
	public CrystalComputerItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
	public boolean isBarVisible(@NotNull ItemStack stack)
	{
		return MemoryCrystalItem.getMemoryListSize(getHeldItem(stack)) > 0;
	}
	
	@Override
	public int getBarWidth(@NotNull ItemStack stack)
	{
		ItemStack heldStack = getHeldItem(stack);
		if(heldStack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			return (int) Math.floor(13.0F * (float) MemoryCrystalItem.getMemoryListSize(heldStack) / memoryCrystal.getMemoryCapacity());
		
		return 0;
	}
	
	@Override
	public int getBarColor(@NotNull ItemStack stack)
	{
		return MemoryCrystalItem.BAR_COLOR_RGB;
	}
	
	public static boolean isCorrectCrystalType(CrystalCache.Type type)
	{
		return switch(type)
		{
			case /*CONTROL, */MEMORY, COMMUNICATION -> true;
			default -> false;
		};
	}
	
	@Override
	public void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack) {}
	
	public void updateFromCompoundTag(ItemStack stack, CompoundTag tag)
	{
		IItemHandler itemHandler = stack.getCapability(Capabilities.ItemHandler.ITEM);
		if(itemHandler != null)
		{
			ItemStack heldItem = itemHandler.extractItem(0, 1, false);
			
			if(heldItem.getItem() instanceof AbstractCrystalItem crystal)
			{
				if(crystal.getType() == CrystalCache.Type.MEMORY)
				{
					if(MemoryCrystalItem.containsMemoryListTag(tag))
						MemoryCrystalItem.setMemoryList(heldItem, tag.getList(MemoryCrystalItem.MEMORY_LIST, Tag.TAG_COMPOUND));
				}
				else if(crystal.getType() == CrystalCache.Type.COMMUNICATION)
				{
					if(CommunicationCrystalItem.containsFrequency(tag))
						CommunicationCrystalItem.setFrequency(heldItem, tag.getInt(CommunicationCrystalItem.FREQUENCY));
					else if(tag.contains(CommunicationCrystalItem.FREQUENCY, Tag.TAG_BYTE))
						CommunicationCrystalItem.unsetFrequency(heldItem);
				}
			}
			
			itemHandler.insertItem(0, heldItem, false);
		}
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		Player player = context.getPlayer();
		
		if(!level.isClientSide())
			PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundCrystalComputerOpenSaveScreenPacket(context.getHand(), context.getClickedPos()));
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);
		
		PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundCrystalComputerOpenMainScreenPacket(usedHand));
		
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		ItemStack heldItem = getHeldItem(stack);
		
		MutableComponent itemComponent = Component.translatable("tooltip.sgjourney.holding").append(Component.literal(": "));
		if(heldItem.isEmpty())
			itemComponent.append("[-]");
		else
			itemComponent.append(heldItem.getDisplayName());
		tooltipComponents.add(itemComponent);
		
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.pocket_crystal_computer.description"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.pocket_crystal_computer.usage"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.pocket_crystal_computer.usage.crystal"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.pocket_crystal_computer.usage.block"));
		
		super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
	}
	
	
	
	public static class ItemHandler extends ComponentItemHandler
	{
		public ItemHandler(MutableDataComponentHolder parent, DataComponentType<ItemContainerContents> component)
		{
			super(parent, component, 1);
		}
		
		@Override
		public boolean isItemValid(int slot, @NotNull ItemStack stack)
		{
			return stack.isEmpty() || stack.getItem() instanceof AbstractCrystalItem crystal && CrystalComputerItem.isCorrectCrystalType(crystal.getType());
		}
	}
}
