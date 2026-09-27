package net.povstalec.sgjourney.common.misc;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class ButtonTooltip implements Button.OnTooltip
{
	@Nullable
	private Component component;
	
	public ButtonTooltip(@Nullable Component component)
	{
		this.component = component;
	}
	
	public ButtonTooltip()
	{
		this(null);
	}
	
	public void setTooltip(@Nullable Component component)
	{
		this.component = component;
	}
	
	public Component getComponent()
	{
		return component != null ? component : Component.empty();
	}
	
	@Override
	public void narrateTooltip(Consumer<Component> consumer)
	{
		consumer.accept(getComponent());
	}
}
