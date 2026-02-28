package net.povstalec.sgjourney.common.sgjourney.factions;

public class PlayerFactionStanding
{
	protected int renown; // How well the Player is known
	protected int reputation; // Reputation the Player has
	
	public PlayerFactionStanding(int renown, int reputation)
	{
		this.renown = renown;
		this.reputation = reputation;
	}
	
	public PlayerFactionStanding()
	{
		this(0, 0);
	}
}
