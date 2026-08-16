package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.items.SchrodingersMapItem;

import javax.annotation.Nullable;
import java.util.Optional;

public class TreasureMapForEmeraldsTrade implements VillagerTrades.ItemListing
{
	protected final int emeraldCost;
	protected final TagKey<Structure> destination;
	protected final String displayName;
	protected final Holder<MapDecorationType> destinationType;
	protected final int maxUses;
	protected final int villagerXp;
	
	public TreasureMapForEmeraldsTrade(int emeraldCost, TagKey<Structure> destination, String displayName, Holder<MapDecorationType> destinationType, int maxUses, int villagerXp)
	{
		this.emeraldCost = emeraldCost;
		this.destination = destination;
		this.displayName = displayName;
		this.destinationType = destinationType;
		this.maxUses = maxUses;
		this.villagerXp = villagerXp;
	}
	
	@Nullable
	public MerchantOffer getOffer(Entity entity, RandomSource source)
	{
		ItemStack mapStack = SchrodingersMapItem.withDestination(Component.translatable(this.displayName), this.destination, this.destinationType, entity.level().dimension(), true);
		
		return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), mapStack, this.maxUses, this.villagerXp, 0.2F);
	}
	
	public static class StargateMapTrade extends TreasureMapForEmeraldsTrade
	{
		public StargateMapTrade(int emeraldCost, String displayName, int villagerXp)
		{
			super(emeraldCost, TagInit.Structures.STARGATE_MAP, displayName, MapDecorationTypes.RED_X, 1, villagerXp);
		}
		
		@Nullable
		public MerchantOffer getOffer(Entity entity, RandomSource source)
		{
			ItemStack mapStack = SchrodingersMapItem.withDestination(Component.translatable(this.displayName), this.destination, this.destinationType, entity.level().dimension(), false);
			
			return new MerchantOffer(new ItemCost(Items.EMERALD, this.emeraldCost), Optional.of(new ItemCost(Items.COMPASS)), mapStack, this.maxUses, this.villagerXp, 0.2F);
		}
	}
 }
