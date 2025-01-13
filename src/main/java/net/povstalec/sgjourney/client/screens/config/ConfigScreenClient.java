package net.povstalec.sgjourney.client.screens.config;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

public class ConfigScreenClient extends Screen
{
	private final Screen parentScreen;

    private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;
	
	protected ConfigScreenClient(Screen parentScreen)
	{
		super(Component.translatable("gui.sgjourney.config_client"));
		this.parentScreen = parentScreen;
	}
	
	@Override
    public void init()
    {
		int l = this.height / 4 + 48;
		
		super.init();
		this.addRenderableWidget(Button.builder(Component.translatable("gui.sgjourney.config_stargate"), 
				(button) -> this.minecraft.setScreen(new ConfigScreenClientStargate(this))).bounds(this.width / 2 - 100, l, 200, 20).build());

		this.addRenderableWidget(Button.builder(Component.translatable("gui.sgjourney.config_sky"), 
				(button) -> this.minecraft.setScreen(new ConfigScreenClientSky(this))).bounds(this.width / 2 - 100, l + 24, 200, 20).build());
		
		this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("gui.sgjourney.true").withStyle(ChatFormatting.GREEN),
				Component.translatable("gui.sgjourney.false").withStyle(ChatFormatting.RED)).withInitialValue(StargateJourneyConfig.disable_smooth_animations.get()).create(this.width / 2 - 100, l + 48, 200, 20, Component.translatable("gui.sgjourney.disable_smooth_animations"), 
						(cycleButton, isTrue)->StargateJourneyConfig.disable_smooth_animations.set(isTrue)));

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, 
				(button) -> this.minecraft.setScreen(this.parentScreen))
				.bounds((this.width - BACK_BUTTON_WIDTH) / 2, this.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build());
    }
	
	@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
		super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(this.font, this.title, this.width / 2, 8, 16777215);
    }
	
}
