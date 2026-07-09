package net.povstalec.sgjourney.client.screens.config;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.client.screens.config.ConfigList.BooleanConfigEntry;
import net.povstalec.sgjourney.common.config.ClientDHDConfig;

public class ConfigScreenClientDHD extends Screen
{
	private final Screen parentScreen;
	
	private static final int BACK_BUTTON_WIDTH = 200;
    private static final int BACK_BUTTON_HEIGHT = 20;
    private static final int BACK_BUTTON_TOP_OFFSET = 26;
    
    private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
    private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
    private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;

	
	protected ConfigScreenClientDHD(Screen parentScreen)
	{
		super(Component.translatable("gui.sgjourney.config_stargate"));
		this.parentScreen = parentScreen;
	}

	
	@Override
    public void init()
    {
		super.init();
		
		ConfigList configList = new ConfigList(minecraft, this.width, this.height,
				OPTIONS_LIST_TOP_HEIGHT, this.height - OPTIONS_LIST_BOTTOM_OFFSET, OPTIONS_LIST_ITEM_HEIGHT);
		configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.dhd_symbols_numbers"), this.width, ClientDHDConfig.dhd_symbols_numbers,
				Component.translatable("gui.sgjourney.symbols").withStyle(ChatFormatting.AQUA), Component.translatable("gui.sgjourney.numbers").withStyle(ChatFormatting.GOLD)));
		configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.milky_way_dhd_button_layout"), this.width, ClientDHDConfig.milky_way_dhd_canon_button_layout,
				Component.translatable("gui.sgjourney.canon").withStyle(ChatFormatting.AQUA), Component.translatable("gui.sgjourney.ascending").withStyle(ChatFormatting.GOLD)));
		configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.pegasus_dhd_button_layout"), this.width, ClientDHDConfig.pegasus_dhd_canon_button_layout,
				Component.translatable("gui.sgjourney.canon").withStyle(ChatFormatting.AQUA), Component.translatable("gui.sgjourney.ascending").withStyle(ChatFormatting.GOLD)));
		configList.add(new BooleanConfigEntry(Component.translatable("gui.sgjourney.classic_dhd_button_layout"), this.width, ClientDHDConfig.classic_dhd_canon_button_layout,
				Component.translatable("gui.sgjourney.canon").withStyle(ChatFormatting.AQUA), Component.translatable("gui.sgjourney.ascending").withStyle(ChatFormatting.GOLD)));
		
		this.addRenderableWidget(configList);

		this.addRenderableWidget(new Button((this.width - BACK_BUTTON_WIDTH) / 2, this.height - BACK_BUTTON_TOP_OFFSET, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT, CommonComponents.GUI_BACK,
				(button) -> this.minecraft.setScreen(this.parentScreen)));
    }
	
	@Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 8, 16777215);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }
	
}
