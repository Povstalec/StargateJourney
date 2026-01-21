package net.povstalec.sgjourney.client.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.transporter.RingPanelEntity;

public class RingPanelButton extends SGJourneyButton
{
	private RingPanelEntity.Button button;
	
	public RingPanelButton(int x, int y, OnPress press, RingPanelEntity.Button button)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/ring_panel_widgets.png"), x, y, 32, 16, Component.empty(), Component.empty(), press);
		
		this.button = button;
		this.button.setUpdate(this::onButtonUpdate);
	}
	
	
	
	public Component componentFromButton(RingPanelEntity.Button button)
	{
		MutableComponent component = Component.empty();
		
		if(button.tooltip() != null)
			component.append(button.tooltip());
		
		if(button.coords() != null)
			component.append(Component.literal(" [" + button.coords().x() + " " + button.coords().y() + " " + button.coords().z() + "] ").withStyle(ChatFormatting.DARK_GREEN));
		
		return component;
	}
	
	public void onButtonUpdate()
	{
		this.active = this.button.enabled();
		this.setTooltip(Tooltip.create(componentFromButton(this.button)));
	}
	
	@Override
	protected int getXImage()
	{
		return this.button.state().ordinal();
	}
	
	@Override
	public void playDownSound(SoundManager soundManager) {}
}
