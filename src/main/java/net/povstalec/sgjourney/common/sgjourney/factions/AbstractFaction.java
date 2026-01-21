package net.povstalec.sgjourney.common.sgjourney.factions;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractFaction
{
	protected Map<UUID, Integer> playerStandings = new HashMap<>();
	
	public AbstractFaction()
	{
		//TODO
	}
	
	public int getPlayerStanding(@NotNull Player player)
	{
		UUID playerUUID = player.getUUID();
		//TODO Base the standing on the head the player is wearing, allow disabling it in the config
		// if(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof PlayerHeadItem playerHead)
		
		return playerStandings.computeIfAbsent(playerUUID, uuid -> 0);
	}
}
