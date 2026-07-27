package net.povstalec.sgjourney.common.entities;

import net.povstalec.sgjourney.common.sgjourney.factions.AbstractFaction;

import javax.annotation.Nullable;

public interface FactionMember
{
	void setFaction(AbstractFaction faction);
	
	@Nullable
	AbstractFaction getFaction();
}
