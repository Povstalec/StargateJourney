package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

public class ConfigScreen extends Screen
{
	private final Screen parentScreen;

    private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;
	
	public ConfigScreen(Screen parentScreen)
	{
		super(Component.translatable("gui.sgjourney.config"));
		this.parentScreen = parentScreen;
	}
	
	@Override
    public void init()
    {
		int l = this.height / 4 + 48;
		
		super.init();
		this.addRenderableWidget(Button.builder(Component.translatable("gui.sgjourney.client"), 
				(button) -> this.minecraft.setScreen(new ConfigScreenClient(this))).bounds(this.width / 2 - 100, l, 200, 20).build());
		
		this.addRenderableWidget(Button.builder(Component.translatable("gui.sgjourney.common"),
				(button) -> this.minecraft.setScreen(new ConfigScreenCommon(this))).bounds(this.width / 2 - 100, l + 24, 200, 20).build());

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, 
				(button) -> this.minecraft.setScreen(this.parentScreen))
				.bounds((this.width - BACK_BUTTON_WIDTH) / 2, this.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build());
    }
	
	@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
}
