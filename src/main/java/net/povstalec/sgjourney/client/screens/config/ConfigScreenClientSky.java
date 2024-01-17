package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.client.screens.config.ConfigList.BooleanConfigEntry;
import net.povstalec.sgjourney.common.config.ClientSkyConfig;

public class ConfigScreenClientSky extends Screen
{
	private final Screen parentScreen;
	private ConfigList configList;

    private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;
    
    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	
	protected ConfigScreenClientSky(Screen parentScreen)
	{
		super(Component.translatable("gui.sgjourney.config_sky"));
		this.parentScreen = parentScreen;
	}

	
	@Override
    public void init()
    {
		super.init();
		
		this.configList = new ConfigList(minecraft, this.width, this.height, 
				OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, OPTIONS_LIST_ITEM_HEIGHT);
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.custom_abydos_sky"), this.width, ClientSkyConfig.custom_abydos_sky));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.custom_chulak_sky"), this.width, ClientSkyConfig.custom_chulak_sky));
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.custom_cavum_tenebrae_sky"), this.width, ClientSkyConfig.custom_cavum_tenebrae_sky));
		
		this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.custom_lantea_sky"), this.width, ClientSkyConfig.custom_lantea_sky));
		//this.configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.custom_athos_sky"), this.width, ClientSkyConfig.custom_athos_sky));
		
		this.addWidget(this.configList);

		this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, 
				(button) -> this.minecraft.setScreen(this.parentScreen))
				.bounds((this.width - BACK_BUTTON_WIDTH) / 2, this.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build());
    }
	
	@Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(graphics);
        this.configList.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawString(this.font, this.title, this.width / 2, 8, 16777215);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
	
}
