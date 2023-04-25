package net.povstalec.sgjourney.common.misc;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class TreasureMapForEmeraldsTrade implements VillagerTrades.ItemListing {
    private final int emeraldCost;
    private final TagKey<Structure> destination;
    private final String displayName;
    private final MapDecoration.Type destinationType;
    private final int maxUses;
    private final int villagerXp;

    public TreasureMapForEmeraldsTrade(int p_207767_, TagKey<Structure> p_207768_, String p_207769_, MapDecoration.Type p_207770_, int p_207771_, int p_207772_) {
       this.emeraldCost = p_207767_;
       this.destination = p_207768_;
       this.displayName = p_207769_;
       this.destinationType = p_207770_;
       this.maxUses = p_207771_;
       this.villagerXp = p_207772_;
    }

    @Nullable
    public MerchantOffer getOffer(Entity p_219708_, RandomSource p_219709_) {
       if (!(p_219708_.level instanceof ServerLevel)) {
          return null;
       } else {
          ServerLevel serverlevel = (ServerLevel)p_219708_.level;
          BlockPos blockpos = serverlevel.findNearestMapStructure(this.destination, p_219708_.blockPosition(), 100, true);
          if (blockpos != null) {
             ItemStack itemstack = MapItem.create(serverlevel, blockpos.getX(), blockpos.getZ(), (byte)2, true, true);
             MapItem.renderBiomePreviewMap(serverlevel, itemstack);
             MapItemSavedData.addTargetDecoration(itemstack, blockpos, "+", this.destinationType);
             itemstack.setHoverName(Component.translatable(this.displayName));
             return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(Items.COMPASS), itemstack, this.maxUses, this.villagerXp, 0.2F);
          } else {
             return null;
          }
       }
    }
 }
