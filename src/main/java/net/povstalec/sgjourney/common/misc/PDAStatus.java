package net.povstalec.sgjourney.common.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public interface PDAStatus
{
	/**
	 * A way to get additional information from something through the PDA
	 * @return List of Components to send to the PDA
	 */
	List<Component> getStatus();
}
