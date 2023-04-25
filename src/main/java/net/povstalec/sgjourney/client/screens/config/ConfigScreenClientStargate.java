package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;

public class ConfigScreenClientStargate extends Screen
{
	private final Screen parentScreen;
	private ConfigList configList;

    private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;
    
    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	
	protected ConfigScreenClientStargate(Screen parentScreen)
	{
		super(Component.translatable("gui.sgjourney.config_stargate"));
		this.parentScreen = parentScreen;
	}

	
	@Override
    public void init()
    {
		super.init();
		
		this.configList = new ConfigList(minecraft, this.width, this.height, 
				OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, OPTIONS_LIST_ITEM_HEIGHT);
		this.configList.add();
		
		this.addWidget(this.configList);

		/*int l = this.height / 4 + 48;
		this.addRenderableWidget(CycleButton.booleanBuilder(Component.translatable("gui.sgjourney.true"),
				Component.translatable("gui.sgjourney.false")).withInitialValue(ClientStargateConfig.use_movie_stargate_model.get()).create(this.width / 2 - 100, l, 200, 20, 
				Component.translatable("gui.sgjourney.use_movie_stargate_model"), 
				(cycleButton, isTrue)->
				{
					ClientStargateConfig.use_movie_stargate_model.set(isTrue);
					ClientStargateConfig.use_movie_stargate_model.save();
				}));*/

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, 
				(button) -> this.minecraft.setScreen(this.parentScreen))
				.bounds((this.width - BACK_BUTTON_WIDTH) / 2, this.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build());
    }
	
	@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        this.configList.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
	
}
