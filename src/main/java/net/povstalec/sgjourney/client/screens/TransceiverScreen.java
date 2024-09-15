package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.TransceiverButton;
import net.povstalec.sgjourney.client.widgets.TransceiverLargeButton;
import net.povstalec.sgjourney.common.menu.TransceiverMenu;

public class TransceiverScreen extends AbstractContainerScreen<TransceiverMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/transceiver/transceiver_gui.png");

	public TransceiverScreen(TransceiverMenu menu, Inventory playerInventory, Component title)
    {
        super(menu, playerInventory, title);
        
        this.imageWidth = 176;
        this.imageHeight = 88;
    }
	
	@Override
    public void init()
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		super.init();
		
		this.addRenderableWidget(new TransceiverButton(x + 43, y + 21, Component.literal("7"), (button) -> addToCode(7)));
		this.addRenderableWidget(new TransceiverButton(x + 61, y + 21, Component.literal("8"), (button) -> addToCode(8)));
		this.addRenderableWidget(new TransceiverButton(x + 79, y + 21, Component.literal("9"), (button) -> addToCode(9)));

		this.addRenderableWidget(new TransceiverButton(x + 43, y + 33, Component.literal("4"), (button) -> addToCode(4)));
		this.addRenderableWidget(new TransceiverButton(x + 61, y + 33, Component.literal("5"), (button) -> addToCode(5)));
		this.addRenderableWidget(new TransceiverButton(x + 79, y + 33, Component.literal("6"), (button) -> addToCode(6)));

		this.addRenderableWidget(new TransceiverButton(x + 43, y + 45, Component.literal("1"), (button) -> addToCode(1)));
		this.addRenderableWidget(new TransceiverButton(x + 61, y + 45, Component.literal("2"), (button) -> addToCode(2)));
		this.addRenderableWidget(new TransceiverButton(x + 79, y + 45, Component.literal("3"), (button) -> addToCode(3)));
		
		this.addRenderableWidget(new TransceiverButton(x + 43, y + 57, Component.translatable("screen.sgjourney.transceiver.symbol.delete"), Component.translatable("screen.sgjourney.transceiver.delete"), (button) -> removeFromCode()));
		this.addRenderableWidget(new TransceiverButton(x + 61, y + 57, Component.literal("0"), (button) -> addToCode(0)));
		this.addRenderableWidget(new TransceiverButton(x + 79, y + 57, Component.translatable("screen.sgjourney.transceiver.symbol.toggle_frequency"), Component.translatable("screen.sgjourney.transceiver.toggle_frequency"),
				(button) -> toggleFrequency()));

		TransceiverLargeButton transceiverLargeButton = new TransceiverLargeButton(x + 14, y + 34, Component.empty(), Component.translatable("screen.sgjourney.transceiver.send_transmission"), (button) -> sendTransmission());
		this.addRenderableWidget(transceiverLargeButton);
	}

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
		int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta)
    {
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
        
        PoseStack stack = graphics.pose();
        stack.pushPose();
        stack.scale(0.5F, 0.5F, 0.5F);
        stack.translate((float)x, (float)y, 0.0F);
        
    	renderLabels(graphics, mouseX, mouseY, x, y);
		
    	stack.popPose();
    }
    
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) 
	{
    	graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x191e2a, false);
    }
    
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY, int x, int y) 
	{
    	graphics.drawString(this.font, Component.literal(menu.getCurrentCode()), x + 218, y + 70, 0x009393, false);
    	graphics.drawString(this.font, Component.translatable("screen.sgjourney.gdo.frequency").append(Component.literal(editingFrequency() ? ": #" : ":")), x + 218, y + 86, 0x009393, false);
    	graphics.drawString(this.font, Component.literal(String.valueOf(menu.getFrequency())), x + 218, y + 98, 0x009393, false);
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
    		
    	}
    	return super.keyPressed(p_96552_, p_96553_, p_96554_);
	}
    
    private boolean editingFrequency()
    {
    	return menu.editingFrequency();
    }
    
    private void toggleFrequency()
    {
    	menu.toggleFrequency();
    }
    
    private void sendTransmission()
    {
    	menu.sendTransmission();
    }
    
    private void addToCode(int number)
    {
    	menu.addToCode(false, number);
    }
    
    private void removeFromCode()
    {
    	menu.removeFromCode(false);
    }
}
