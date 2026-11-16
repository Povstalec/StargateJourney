package net.povstalec.sgjourney.common.items;

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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.capabilities.ItemInventoryProvider;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.packets.ClientboundCrystalComputerOpenMainScreenPacket;
import net.povstalec.sgjourney.common.packets.ClientboundCrystalComputerOpenSaveScreenPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	
	@Override
	public final ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag tag)
	{
		return new ItemInventoryProvider(stack)
		{
			@Override
			public int getNumberOfSlots()
			{
				return 1;
			}
			
			@Override
			public boolean isValid(int slot, @NotNull ItemStack stack)
			{
				return stack.isEmpty() || stack.getItem() instanceof AbstractCrystalItem crystal && isCorrectCrystalType(crystal.getType());
			}
		};
	}
	
	public boolean isCorrectCrystalType(CrystalCache.Type type)
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
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
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
		});
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context)
	{
		Level level = context.getLevel();
		Player player = context.getPlayer();
		
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundCrystalComputerOpenSaveScreenPacket(context.getHand(), context.getClickedPos()));
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new ClientboundCrystalComputerOpenMainScreenPacket(usedHand));
		
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
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
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
