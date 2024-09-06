package net.povstalec.sgjourney.client.screens;

import java.util.UUID;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.GDOButton;
import net.povstalec.sgjourney.client.widgets.GDOLargeButton;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundGDOUpdatePacket;

public class GDOScreen extends Screen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_background.png");

	private int imageWidth = 176;
	private int imageHeight = 56;
	
	private final UUID playerId;
	private final boolean mainHand;
	private String idc;
	private int frequency;
	
	private boolean toggledFrequency = true;
	
	public GDOScreen(UUID playerId, boolean mainHand, String idc, int frequency)
	{
		super(Component.empty());
		
		this.playerId = playerId;
		this.idc = idc;
		this.frequency = frequency;
		this.mainHand = mainHand;
	}
    
    @Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new GDOButton(x + 43, y + 5, Component.literal("7"), (button) -> addToCode(7)));
		this.addRenderableWidget(new GDOButton(x + 61, y + 5, Component.literal("8"), (button) -> addToCode(8)));
		this.addRenderableWidget(new GDOButton(x + 79, y + 5, Component.literal("9"), (button) -> addToCode(9)));

		this.addRenderableWidget(new GDOButton(x + 43, y + 17, Component.literal("4"), (button) -> addToCode(4)));
		this.addRenderableWidget(new GDOButton(x + 61, y + 17, Component.literal("5"), (button) -> addToCode(5)));
		this.addRenderableWidget(new GDOButton(x + 79, y + 17, Component.literal("6"), (button) -> addToCode(6)));

		this.addRenderableWidget(new GDOButton(x + 43, y + 29, Component.literal("1"), (button) -> addToCode(1)));
		this.addRenderableWidget(new GDOButton(x + 61, y + 29, Component.literal("2"), (button) -> addToCode(2)));
		this.addRenderableWidget(new GDOButton(x + 79, y + 29, Component.literal("3"), (button) -> addToCode(3)));
		
		this.addRenderableWidget(new GDOButton(x + 43, y + 41, Component.literal("*"), (button) -> removeFromCode()));
		this.addRenderableWidget(new GDOButton(x + 61, y + 41, Component.literal("0"), (button) -> addToCode(0)));
		this.addRenderableWidget(new GDOButton(x + 79, y + 41, Component.literal("#"), (button) -> sendTransmission()));
		
		this.addRenderableWidget(new GDOLargeButton(x + 0, y + 14, Component.empty(), (button) -> toggleFrequency()));
	}
	
	@Override
	public boolean isPauseScreen() 
	{
		return false;
	}

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
    	RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
    	this.renderBackground(poseStack);
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

    	super.render(poseStack, mouseX, mouseY, delta);
    	
    	poseStack.pushPose();
    	poseStack.scale(0.5F, 0.5F, 0.5F);
    	poseStack.translate((float)x, (float)y, 0.0F);
        
    	renderLabels(poseStack, mouseX, mouseY, x, y);
		
		poseStack.popPose();
    }
    
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y) 
	{
    	this.font.draw(stack, Component.literal(idc), x + 202F, y + 40F, 0x2a2927);
		this.font.draw(stack, Component.translatable("screen.sgjourney.gdo.frequency").append(Component.literal(toggledFrequency ? ": #" : ":")), x + 202F, y + 56F, 0x2a2927); // TODO Translate
		this.font.draw(stack, Component.literal(String.valueOf(frequency)), x + 202F, y + 68F, 0x2a2927);
    }
    
    @Override
	public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_)
	{
    	switch(p_96552_)
    	{
    	case InputConstants.KEY_NUMPAD0, InputConstants.KEY_0:
    		addToCode(0);
    		break;
    	case InputConstants.KEY_NUMPAD1, InputConstants.KEY_1:
    		addToCode(1);
    		break;
    	case InputConstants.KEY_NUMPAD2, InputConstants.KEY_2:
    		addToCode(2);
    		break;
    	case InputConstants.KEY_NUMPAD3, InputConstants.KEY_3:
    		addToCode(3);
    		break;
    	case InputConstants.KEY_NUMPAD4, InputConstants.KEY_4:
    		addToCode(4);
    		break;
    	case InputConstants.KEY_NUMPAD5, InputConstants.KEY_5:
    		addToCode(5);
    		break;
    	case InputConstants.KEY_NUMPAD6, InputConstants.KEY_6:
    		addToCode(6);
    		break;
    	case InputConstants.KEY_NUMPAD7, InputConstants.KEY_7:
    		addToCode(7);
    		break;
    	case InputConstants.KEY_NUMPAD8, InputConstants.KEY_8:
    		addToCode(8);
    		break;
    	case InputConstants.KEY_NUMPAD9, InputConstants.KEY_9:
    		addToCode(9);
    		break;
    	case InputConstants.KEY_BACKSPACE, InputConstants.KEY_DELETE:
    		removeFromCode();
    		break;
    	case InputConstants.KEY_NUMPADENTER, InputConstants.KEY_RETURN:
    		sendTransmission();
    		break;
    	case InputConstants.KEY_LCONTROL, InputConstants.KEY_RCONTROL:
    		toggleFrequency();
    		break;
    		
    	}return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}
    
    private void sendTransmission()
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundGDOUpdatePacket(mainHand, frequency, idc, true));
    }
    
    private void updateServer()
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundGDOUpdatePacket(mainHand, frequency, idc, false));
    }
    
    private void toggleFrequency()
    {
    	toggledFrequency = !toggledFrequency;
    }
    
    private void addToCode(int number)
    {
    	if(!toggledFrequency)
    	{
    		if(idc.length() >= 16)
        		return;
        	
        	idc = idc + String.valueOf(number);
    	}
    	else
    	{
    		long tempFrequency = frequency;
    		tempFrequency = tempFrequency * 10 + number;
    		
    		if(tempFrequency > Integer.MAX_VALUE)
    			return;
    		
    		frequency = (int) tempFrequency;
    	}
    	
    	updateServer();
    }
    
    private void removeFromCode()
    {
    	if(!toggledFrequency)
    	{
        	if(idc.length() <= 0)
        		return;
        	
        	idc = idc.substring(0, idc.length() - 1);
    	}
    	else
    	{
    		if(frequency <= 0)
        		return;
        	
    		frequency = frequency / 10;
    	}
    	
    	updateServer();
    }
}
