package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.client.screens.config.ConfigList.BooleanConfigEntry;
import net.povstalec.sgjourney.client.screens.config.ConfigList.SliderConfigEntry;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

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
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.stargate_variants"), this.width, ClientStargateConfig.stargate_variants));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.unique_symbols"), this.width, ClientStargateConfig.unique_symbols));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.shiny_event_horizons"), this.width, ClientStargateConfig.shiny_event_horizons));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.enable_vortex"), this.width, ClientStargateConfig.enable_vortex));
		this.configList.add(new SliderConfigEntry(Component.translatable("gui.sgjourney.event_horizon_distortion"), Component.empty(), this.width, ClientStargateConfig.event_horizon_distortion));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.use_movie_stargate_model"), this.width, ClientStargateConfig.use_movie_stargate_model));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.universe_front_rotates"), this.width, ClientStargateConfig.universe_front_rotates));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.movie_primary_chevron_opens"), this.width, ClientStargateConfig.movie_primary_chevron_opens));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.alternate_movie_chevron_locking"), this.width, ClientStargateConfig.alternate_movie_chevron_locking));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.milky_way_stargate_back_lights_up"), this.width, ClientStargateConfig.milky_way_stargate_back_lights_up));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.pegasus_stargate_back_lights_up"), this.width, ClientStargateConfig.pegasus_stargate_back_lights_up));
		this.configList.add(new SliderConfigEntry(Component.translatable("gui.sgjourney.stargate_full_sound_distance"), Component.empty(), this.width, ClientStargateConfig.stargate_full_sound_distance));
		this.configList.add(new SliderConfigEntry(Component.translatable("gui.sgjourney.stargate_max_sound_distance"), Component.empty(), this.width, ClientStargateConfig.stargate_max_sound_distance));
		
		this.addWidget(this.configList);

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
