package net.povstalec.sgjourney.common.misc;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.items.SchrodingersMapItem;

import javax.annotation.Nullable;
import java.util.Random;

public class TreasureMapForEmeraldsTrade implements VillagerTrades.ItemListing
{
	protected final int emeraldCost;
	protected final TagKey<ConfiguredStructureFeature<?, ?>> destination;
	protected final String displayName;
	protected final MapDecoration.Type destinationType;
	protected final int maxUses;
	protected final int villagerXp;
	
	public TreasureMapForEmeraldsTrade(int emeraldCost, TagKey<ConfiguredStructureFeature<?, ?>> destination, String displayName, MapDecoration.Type destinationType, int maxUses, int villagerXp)
	{
		this.emeraldCost = emeraldCost;
		this.destination = destination;
		this.displayName = displayName;
		this.destinationType = destinationType;
		this.maxUses = maxUses;
		this.villagerXp = villagerXp;
	}
	
	@Nullable
	public MerchantOffer getOffer(Entity entity, Random source)
	{
		ItemStack mapStack = SchrodingersMapItem.withDestination(new TranslatableComponent(this.displayName), this.destination, this.destinationType, entity.level.dimension(), true);
		
		return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), mapStack, this.maxUses, this.villagerXp, 0.2F);
	}
	
	public static class StargateMapTrade extends TreasureMapForEmeraldsTrade
	{
		public StargateMapTrade(int emeraldCost, String displayName, int villagerXp)
		{
			super(emeraldCost, TagInit.Structures.STARGATE_MAP, displayName, MapDecoration.Type.RED_X, 1, villagerXp);
		}
		
		@Nullable
		public MerchantOffer getOffer(Entity entity, Random source)
		{
			ItemStack mapStack = SchrodingersMapItem.withDestination(new TranslatableComponent(this.displayName), this.destination, this.destinationType, entity.level.dimension(), false);
			
			return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), mapStack, this.maxUses, this.villagerXp, 0.2F);
		}
	}
 }
