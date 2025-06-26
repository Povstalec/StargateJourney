package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.world.entity.player.Player;

public interface ProtectedBlockEntity
{
	String PROTECTED = "protected";
	
	void setProtected(boolean isProtected);
	
	boolean isProtected();
	
	boolean hasPermissions(Player player, boolean sendMessage);
}
