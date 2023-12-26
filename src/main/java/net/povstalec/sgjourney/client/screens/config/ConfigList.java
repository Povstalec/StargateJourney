package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.povstalec.sgjourney.common.config.SGJourneyConfigValue;

public class ConfigList extends ObjectSelectionList<ConfigList.ConfigEntry>
{
	public ConfigList(Minecraft minecraft, int screenWidth, int screenHeight, int yStart, int yEnd, int itemHeight)
	{
		super(minecraft, screenWidth, screenHeight, yStart, yEnd, itemHeight);
	}
	
	public void add(ConfigEntry entry)
	{
		this.addEntry(entry);
	}

	public int getRowWidth()
	{
		return super.getRowWidth() + 45;
	}
	
	@Override
	protected int getScrollbarPosition()
	{
		return super.getScrollbarPosition() + 35;
	}

	public static abstract class ConfigEntry extends ObjectSelectionList.Entry<ConfigList.ConfigEntry>
	{
	    protected final AbstractWidget resetToDefault;
	    protected final Component reset = Component.literal("Reset");
	    
	    public ConfigEntry()
	    {
	    	this.resetToDefault = new Button(0, 0, 50, 20, reset, (button) -> reset());
	    }
	    
	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int key)
	    {
	    	if(this.resetToDefault.isMouseOver(mouseX, mouseY))
	    		((AbstractButton) this.resetToDefault).onPress();
	    	
			return super.mouseClicked(mouseX, mouseY, key);
	    }
	    
	    protected void reset()
	    {
			this.resetToDefault.playDownSound(Minecraft.getInstance().getSoundManager());
	    }

		@Override
		public Component getNarration()
		{
			return reset;
		}

		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.resetToDefault.x = k + 210;
	        this.resetToDefault.y = j;
	        this.resetToDefault.render(stack, n, o, partialTick);
		}
	}
	
	public static class BooleanConfigEntry extends ConfigEntry
	{
		protected AbstractWidget cycleButton;
		SGJourneyConfigValue.BooleanValue value;
		
		public BooleanConfigEntry(Component component, int screenWidth, SGJourneyConfigValue.BooleanValue value)
		{
			this.value = value;
			this.cycleButton = CycleButton.booleanBuilder(
					Component.translatable("gui.sgjourney.true").withStyle(ChatFormatting.GREEN),
					Component.translatable("gui.sgjourney.false").withStyle(ChatFormatting.RED))
					.withInitialValue(value.get())
					.create(0, 0, 200, 20, component, (cycleButton, isTrue) -> 
					{
						value.set(isTrue);
						this.cycleButton.playDownSound(Minecraft.getInstance().getSoundManager());
					});
		}
		
		@SuppressWarnings("unchecked")
		protected void reset()
		{
			this.value.set(this.value.getDefault());
			((CycleButton<Boolean>) this.cycleButton).setValue(this.value.get());
			super.reset();
		}
	    
	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int key)
	    {
	    	if(this.cycleButton.isMouseOver((double) mouseX, (double) mouseY))
	    		((AbstractButton) this.cycleButton).onPress();
	    	
			return super.mouseClicked(mouseX, mouseY, key);
	    }
		
		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.cycleButton.x = k;
	        this.cycleButton.y = j;
	        this.cycleButton.render(stack, n, o, partialTick);
			super.render(stack, i, j, k, l, m, n, o, bl, partialTick);
		}
		
	}
	
	public static class SliderConfigEntry extends ConfigEntry
	{
		protected AbstractWidget sliderButton;
		protected SGJourneyConfigValue.IntValue value;
		
		public SliderConfigEntry(Component component1, Component component2, int screenWidth, SGJourneyConfigValue.IntValue value)
		{
			this.value = value;
			this.sliderButton = new ForgeSlider(0, 0, 200, 20, 
					component1, component2,
					value.getMin(), value.getMax(), value.get(), 1.0, 1, true);
		}
		
		protected void reset()
		{
			this.value.set(this.value.getDefault());
			((ForgeSlider) this.sliderButton).setValue((double) this.value.get());
			super.reset();
		}
		
		protected void onChanged()
		{
	    	value.set((int) ((ForgeSlider) this.sliderButton).getValue());
		}
	    
	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int key)
	    {
	    	if(this.sliderButton.isMouseOver(mouseX, mouseY))
	    		((ForgeSlider) this.sliderButton).mouseClicked(mouseX, mouseY, key);
	    	onChanged();
	    	
			return super.mouseClicked(mouseX, mouseY, key);
	    }
	    
	    @Override
	    public boolean mouseDragged(double mouseX, double mouseY, int key, double dragX, double dragY)
	    {
	    	if(this.sliderButton.isMouseOver(mouseX, mouseY))
	    		((ForgeSlider) this.sliderButton).mouseDragged(mouseX, mouseY, key, dragX, dragY);
	    	
			return super.mouseDragged(mouseX, mouseY, key, dragX, dragY);
	    }
	    
	    @Override
	    public boolean mouseReleased(double mouseX, double mouseY, int key)
	    {
	    	if(this.sliderButton.isMouseOver(mouseX, mouseY))
	    		((ForgeSlider) this.sliderButton).mouseReleased(mouseX, mouseY, key);
	    	onChanged();
	    	
			return super.mouseReleased(mouseX, mouseY, key);
	    }
	    
	    @Override
	    public void mouseMoved(double mouseX, double mouseY)
	    {
	    	if(this.sliderButton.isMouseOver(mouseX, mouseY))
	    		((ForgeSlider) this.sliderButton).mouseMoved(mouseX, mouseY);
	    	
			super.mouseMoved(mouseX, mouseY);
	    }
		
		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.sliderButton.setX(k);
	        this.sliderButton.setY(j);
	        this.sliderButton.render(stack, n, o, partialTick);
			super.render(stack, i, j, k, l, m, n, o, bl, partialTick);
		}
		
	}
	
	/*public static class EnumConfigEntry<T extends Enum<T>> extends ConfigEntry
	{
		protected AbstractWidget cycleButton;
		SGJourneyConfigValue.EnumValue<T> value;
		
		@SuppressWarnings("unchecked")
		public EnumConfigEntry(Component component, int screenWidth, SGJourneyConfigValue.EnumValue<T> value)
		{
			this.value = value;
			this.cycleButton = CycleButton.builder((enumValue) -> getComponent((T) enumValue))
					.withInitialValue(value.get())
					.create(0, 0, 200, 20, component, (cycleButton, enumValue) -> 
					{
						value.set((T) enumValue);
						this.cycleButton.playDownSound(Minecraft.getInstance().getSoundManager());
					});
		}
		
		private Component getComponent(T enumValue)
		{
			return Component.translatable("");
		}
		
		protected void reset()
		{
			this.value.set(this.value.getDefault());
			super.reset();
		}
	    
	    @Override
	    public boolean mouseClicked(double mouseX, double mouseY, int key)
	    {
	    	System.out.println("Detected click");
	    	if(this.cycleButton.isMouseOver(mouseX, mouseY))
		    		System.out.println("Mouse is over Cycle Button");
	    	
			return super.mouseClicked(mouseX, mouseY, key);
	    }
		
		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.cycleButton.setX(k);
	        this.cycleButton.setY(j);
	        this.cycleButton.render(stack, n, o, partialTick);
			super.render(stack, i, j, k, l, m, n, o, bl, partialTick);
		}
		
	}*/
}
