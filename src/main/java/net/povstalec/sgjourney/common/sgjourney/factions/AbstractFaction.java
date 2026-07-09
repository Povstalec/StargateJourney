package net.povstalec.sgjourney.common.sgjourney.factions;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.povstalec.sgjourney.common.misc.InventoryUtil;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractFaction
{
	protected Map<UUID, PlayerFactionStanding> playerStandings = new HashMap<>();
	protected Map<AbstractFaction, Integer> factionStandings = new HashMap<>();
	
	public AbstractFaction()
	{
		//TODO
	}
	
	public UUID getPlayerUUID(@NotNull MinecraftServer server, @NotNull ServerPlayer player)
	{
		// Player's face is hidden by the Carved Pumpkin
		if(player.getItemBySlot(EquipmentSlot.HEAD).is(Items.CARVED_PUMPKIN)) //TODO Add a tag for items that hide player's identity (Maybe check other slots?)
			return null;
		
		// Player's face is invisible
		if(player.isInvisible())
			return null;
		
		// Player is wearing the Head of another Player
		ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
		if(headStack.getItem() instanceof PlayerHeadItem)
		{
			String playerName = InventoryUtil.getPlayerNameFromHead(headStack);
			if(playerName != null)
			{
				ServerPlayer otherPlayer = server.getPlayerList().getPlayerByName(playerName);
				if(otherPlayer != null)
					return otherPlayer.getUUID();
			}
		}
		
		return player.getUUID();
	}
	
	@Nullable
	public PlayerFactionStanding getPlayerFactionStanding(@NotNull MinecraftServer server, @NotNull ServerPlayer player)
	{
		UUID playerUUID = getPlayerUUID(server, player);
		if(playerUUID != null)
			return playerStandings.computeIfAbsent(playerUUID, uuid -> new PlayerFactionStanding());
		
		return null;
	}
	
	public void notifyDeath(Entity dyingEntity, DamageSource damageSource)
	{
		//TODO Do stuff when member dies
	}
	
	public abstract void tickFaction(MinecraftServer server, int ticks);
	
	/**
	 * @return Faction serialized into a CompoundTag
	 */
	public abstract CompoundTag serializeNBT();
	
	/**
	 * Deserializes the Faction
	 * @param tag CompoundTag containing information to be deserialized
	 */
	public abstract void deserializeNBT(CompoundTag tag);
}
