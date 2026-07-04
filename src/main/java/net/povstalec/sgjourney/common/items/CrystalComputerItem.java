package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
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
				return stack.isEmpty() || stack.getItem() instanceof MemoryCrystalItem;
			}
		};
	}
	
	@Override
	public void onSwapped(ItemStack holderStack, ItemStack insertedStack, ItemStack removedStack)
	{
		//TODO
	}
	
	public void updateFromList(ItemStack stack, ListTag list)
	{
		stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			ItemStack heldItem = itemHandler.extractItem(0, 1, false);
			MemoryCrystalItem.setMemoryList(heldItem, list);
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
		
		super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
	}
}
