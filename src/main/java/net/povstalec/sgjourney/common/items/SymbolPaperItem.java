package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.packets.ClientboundSymbolPaperOpenScreenPacket;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SymbolPaperItem extends Item
{
	public static final String POINT_OF_ORIGIN = "point_of_origin";
	public static final String SYMBOLS = "symbols";
	
	public SymbolPaperItem(Properties properties)
	{
		super(properties);
	}
	
	public static void setPointOfOrigin(ItemStack stack, @Nullable ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		if(pointOfOrigin != null)
			stack.set(DataComponentInit.POINT_OF_ORIGIN, pointOfOrigin);
		else if(stack.has(DataComponentInit.POINT_OF_ORIGIN))
			stack.remove(DataComponentInit.POINT_OF_ORIGIN);
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> getPointOfOrigin(ItemStack stack)
	{
		if(stack.has(DataComponentInit.POINT_OF_ORIGIN))
			return stack.get(DataComponentInit.POINT_OF_ORIGIN);
		
		return null;
	}
	
	public static void setSymbols(ItemStack stack, @Nullable ResourceKey<Symbols> symbols)
	{
		if(symbols != null)
			stack.set(DataComponentInit.SYMBOLS, symbols);
		else if(stack.has(DataComponentInit.SYMBOLS))
			stack.remove(DataComponentInit.SYMBOLS);
	}
	
	@Nullable
	public static ResourceKey<Symbols> getSymbols(ItemStack stack)
	{
		if(stack.has(DataComponentInit.SYMBOLS))
			return stack.get(DataComponentInit.SYMBOLS);
		
		return null;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(!level.isClientSide())
			PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundSymbolPaperOpenScreenPacket(usedHand));
		
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		ResourceKey<PointOfOrigin> pointOfOrigin = getPointOfOrigin(stack);
		if(pointOfOrigin != null)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(": ").append(Component.translatable(ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(pointOfOrigin), "tooltip.sgjourney.error"))).withStyle(ChatFormatting.DARK_PURPLE));
		
		ResourceKey<Symbols> symbols = getSymbols(stack);
		if(symbols != null)
			tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(": ").append(Component.translatable(ClientSymbols.translationName(ClientSymbols.getSymbols(symbols), "tooltip.sgjourney.error"))).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}
