package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.Tag;
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
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.misc.Conversion;
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
	
	public static void setPointOfOrigin(ItemStack stack, ResourceKey<PointOfOrigin> pointOfOrigin)
	{
		stack.getOrCreateTag().putString(POINT_OF_ORIGIN, pointOfOrigin.location().toString());
	}
	
	@Nullable
	public static ResourceKey<PointOfOrigin> getPointOfOrigin(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains(POINT_OF_ORIGIN, Tag.TAG_STRING))
			return Conversion.stringToPointOfOrigin(stack.getTag().getString(POINT_OF_ORIGIN));
		
		return null;
	}
	
	public static void setSymbols(ItemStack stack, ResourceKey<Symbols> symbols)
	{
		stack.getOrCreateTag().putString(SYMBOLS, symbols.location().toString());
	}
	
	@Nullable
	public static ResourceKey<Symbols> getSymbols(ItemStack stack)
	{
		if(stack.hasTag() && stack.getTag().contains(SYMBOLS, Tag.TAG_STRING))
			return Conversion.stringToSymbols(stack.getTag().getString(SYMBOLS));
		
		return null;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(!level.isClientSide())
		{
			PacketHandlerInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
				new ClientboundSymbolPaperOpenScreenPacket(usedHand));
		}
		
		return super.use(level, player, usedHand);
	}
	
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
	{
		ResourceKey<PointOfOrigin> pointOfOrigin = getPointOfOrigin(stack);
		String pointOfOriginName = pointOfOrigin == null ? "" : ClientPointOfOrigin.translationName(ClientPointOfOrigin.getPointOfOrigin(pointOfOrigin), "tooltip.sgjourney.error");
		
		ResourceKey<Symbols> symbols = getSymbols(stack);
		String symbolsName = symbols == null ? "" : ClientSymbols.translationName(ClientSymbols.getSymbols(symbols), "tooltip.sgjourney.error");
		
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.point_of_origin").append(": ").append(Component.translatable(pointOfOriginName)).withStyle(ChatFormatting.DARK_PURPLE));
		tooltipComponents.add(Component.translatable(ClientSymbols.symbolsOrSet()).append(": ").append(Component.translatable(symbolsName)).withStyle(ChatFormatting.LIGHT_PURPLE));
	}
}
