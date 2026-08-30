package net.povstalec.sgjourney.client.screens;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

public class SGJourneyMenuScreen<M extends AbstractContainerMenu> extends Screen implements MenuAccess<M>
{
	protected final M menu;
	
	protected int imageWidth = 176;
	protected int imageHeight = 166;
	
	protected int leftPos;
	protected int topPos;
	
	protected SGJourneyMenuScreen(M menu, Component title)
	{
		super(title);
		
		this.menu = menu;
	}
	
	@Override
	protected void init()
	{
		this.leftPos = (this.width - this.imageWidth) / 2;
		this.topPos = (this.height - this.imageHeight) / 2;
	}
	
	@Override
	public @NotNull M getMenu()
	{
		return menu;
	}
}
