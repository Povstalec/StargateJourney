package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.AutoDialerItem;
import net.povstalec.sgjourney.common.misc.ButtonTooltip;
import net.povstalec.sgjourney.common.misc.ParsingResult;
import net.povstalec.sgjourney.common.packets.ServerboundAutoDialerUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.Address;
import org.jetbrains.annotations.NotNull;

public class AutoDialerScreen extends Screen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/auto_dialer_gui.png");
	
	public static final int EDIT_BOX_WIDTH = 176;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	public static final int BUTTON_WIDTH = 76;
	public static final int BUTTON_HEIGHT = 20;
	
	protected int imageWidth = 186;
	protected int imageHeight = 100;
	protected int titleLabelX;
	protected int titleLabelY;
	
	protected int leftPos;
	protected int topPos;
	
	protected EditBox editBox;
	protected Address.Mutable address = new Address.Mutable();
	
	
	protected boolean doKawoosh = true;
	
	protected Button doneButton;
	protected final ButtonTooltip doneButtonTooltip = new ButtonTooltip()
	{
		@Override
		public void onTooltip(Button button, PoseStack stack, int mouseX, int mouseY)
		{
			renderTooltip(stack, getComponent(), mouseX, mouseY);
		}
	};
	
	protected final InteractionHand interactionHand;
	
	public AutoDialerScreen(InteractionHand interactionHand)
	{
		super(Component.translatable("screen.sgjourney.auto_dialer"));
		
		this.interactionHand = interactionHand;
		
		this.titleLabelX = 8;
		this.titleLabelY = 6;
	}
	
	@Override
	protected void init()
	{
		leftPos = (width - imageWidth) / 2;
		topPos = (height - imageHeight) / 2;
		
		ItemStack stack = Minecraft.getInstance().player.getItemInHand(interactionHand);
		address.fromAddress(AutoDialerItem.getAddress(stack));
		doKawoosh = AutoDialerItem.doKawoosh(stack);
		
		this.doneButton = new Button(leftPos - BUTTON_WIDTH / 2 + 46, topPos + 60, BUTTON_WIDTH, BUTTON_HEIGHT, CommonComponents.GUI_DONE,
				button -> save());
		
		this.addRenderableWidget(this.doneButton);
		
		this.addRenderableWidget(CycleButton.booleanBuilder(CommonComponents.OPTION_ON, CommonComponents.OPTION_OFF)
				.withTooltip(value -> Minecraft.getInstance().font.split(value ? Component.translatable("tooltip.sgjourney.auto_dialer.kawoosh_on") : Component.translatable("tooltip.sgjourney.auto_dialer.kawoosh_off"), 200))
				.withInitialValue(doKawoosh)
			.create(leftPos + imageWidth - BUTTON_WIDTH / 2 - 46, topPos + 60, BUTTON_WIDTH, BUTTON_HEIGHT, Component.translatable("tooltip.sgjourney.auto_dialer.kawoosh"),
			(button, value) -> doKawoosh = value));
		
		this.editBox = new EditBox(font, leftPos + (imageWidth - EDIT_BOX_WIDTH) / 2, topPos + 20, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.translatable("tooltip.sgjourney.address"));
		this.editBox.setFilter(Address::canBeTransformedToAddress);
		
		this.editBox.setMaxLength(28);
		this.editBox.setValue(address.toString());
		this.editBox.setResponder(text ->
		{
			int[] addressArray = Address.addressStringToIntArray(text);
			ParsingResult parsingResult = Address.intArrayParsingResult(addressArray);
			if(parsingResult.isSuccess())
			{
				address.fromString(text);
				doneButton.active = true;
				doneButtonTooltip.setTooltip(Component.empty());
			}
			else
			{
				address.reset();
				doneButton.active = false;
				doneButtonTooltip.setTooltip(parsingResult.getMessage());
			}
		});
		
		this.addRenderableWidget(this.editBox);
	}
	
	public void save()
	{
		PacketHandlerInit.INSTANCE.sendToServer(new ServerboundAutoDialerUpdatePacket(interactionHand, address, doKawoosh));
		onClose();
	}
	
	@Override
	public boolean isPauseScreen() 
	{
		return false;
	}

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
		renderBackground(poseStack);
		
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
		
		this.blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		RenderSystem.disableDepthTest();
		super.render(poseStack, mouseX, mouseY, delta);
		
		PoseStack posestack = RenderSystem.getModelViewStack();
		posestack.pushPose();
		posestack.translate((float) leftPos, (float) topPos, 0.0F);
		RenderSystem.applyModelViewMatrix();
		
		renderLabels(poseStack, mouseX, mouseY);
		
		posestack.popPose();
		RenderSystem.applyModelViewMatrix();
		RenderSystem.enableDepthTest();
    }
	
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
	{
		this.font.draw(poseStack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
	}
}
