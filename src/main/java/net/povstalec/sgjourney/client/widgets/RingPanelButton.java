package net.povstalec.sgjourney.client.widgets;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.misc.TransporterControllerButton;
import org.jetbrains.annotations.NotNull;

public class RingPanelButton extends SGJourneyButton
{
	// Disabled button texture is just the enabled button texture but with HSV adjusted to -60 lightness
	
	protected final TransporterControllerButton<?> button;
	
	public RingPanelButton(int x, int y, OnPress press, TransporterControllerButton<?> button)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/ring_panel_widgets.png"), x, y, 32, 16, Component.empty(), Component.empty(), press);
		
		this.button = button;
		this.button.setUpdate(this::onButtonUpdate);
		onButtonUpdate();
	}
	
	
	
	public Component componentFromButton(TransporterControllerButton<?> button)
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
		return this.button.state().textureOffsetX;
	}
	
	@Override
	public void playDownSound(@NotNull SoundManager soundManager) {}
}
