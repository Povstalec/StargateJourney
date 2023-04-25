package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public class ConfigList extends ObjectSelectionList<ConfigList.ConfigEntry>
{
	public ConfigList(Minecraft minecraft, int screenWidth, int screenHeight, int yStart, int yEnd, int itemHeight)
	{
		super(minecraft, screenWidth, screenHeight, yStart, yEnd, itemHeight);
	}
	
	public void add()
	{
		this.addEntry(new BooleanConfigEntry(Component.translatable("gui.sgjourney.use_movie_stargate_model"), this.width, ClientStargateConfig.use_movie_stargate_model.get())
		{
			@Override
			protected void pressButton(boolean isTrue)
			{
				ClientStargateConfig.use_movie_stargate_model.set(isTrue);
				ClientStargateConfig.use_movie_stargate_model.save();
			}

			@Override
			protected void reset()
			{
				ClientStargateConfig.use_movie_stargate_model.set(ClientStargateConfig.use_movie_stargate_model.getDefault());
				ClientStargateConfig.use_movie_stargate_model.save();
			}
		});
		
		this.addEntry(new BooleanConfigEntry(Component.translatable("gui.sgjourney.milky_way_stargate_back_lights_up"), this.width, ClientStargateConfig.milky_way_stargate_back_lights_up.get())
		{
			@Override
			protected void pressButton(boolean isTrue)
			{
				ClientStargateConfig.milky_way_stargate_back_lights_up.set(isTrue);
				ClientStargateConfig.milky_way_stargate_back_lights_up.save();
			}

			@Override
			protected void reset()
			{
				ClientStargateConfig.milky_way_stargate_back_lights_up.set(ClientStargateConfig.milky_way_stargate_back_lights_up.getDefault());
				ClientStargateConfig.milky_way_stargate_back_lights_up.save();
			}
		});
	}
	
	@Override
	protected int getScrollbarPosition()
	{
		return super.getScrollbarPosition() + 35;
	}

	public abstract class ConfigEntry extends ObjectSelectionList.Entry<ConfigList.ConfigEntry>
	{
	    private final AbstractWidget resetToDefault;
	    private final Component reset = Component.literal("Reset");
	    
	    public ConfigEntry()
	    {
	    	this.resetToDefault = Button.builder(reset, (button) -> reset()).bounds(0, 0, 50, 20).build();
	    }
	    
	    protected abstract void reset();

		@Override
		public Component getNarration()
		{
			return reset;
		}

		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.resetToDefault.setX(k + 210);
	        this.resetToDefault.setY(j);
	        this.resetToDefault.render(stack, n, o, partialTick);
		}
	}
	
	public abstract class BooleanConfigEntry extends ConfigEntry
	{
		protected AbstractWidget cycleButton;
		
		public BooleanConfigEntry(Component component, int screenWidth, boolean defaultValue)
		{
			super();
			cycleButton = CycleButton.booleanBuilder(
					Component.translatable("gui.sgjourney.true"),
					Component.translatable("gui.sgjourney.false"))
					.withInitialValue(defaultValue)
					.create(0, 0, 200, 20, component, (cycleButton, isTrue)-> pressButton(isTrue));
		}
		
		protected abstract void pressButton(boolean isTrue);
		
		@Override
		public void render(PoseStack stack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float partialTick)
		{
			this.cycleButton.setX(k);
	        this.cycleButton.setY(j);
	        this.cycleButton.render(stack, n, o, partialTick);
			super.render(stack, i, j, k, l, m, n, o, bl, partialTick);
		}
		
	}
}
