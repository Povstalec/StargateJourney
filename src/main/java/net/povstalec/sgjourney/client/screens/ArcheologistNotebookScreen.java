package net.povstalec.sgjourney.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class ArcheologistNotebookScreen extends Screen
{
	public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
	
	private static final int IMAGE_WIDTH = 192;
	private static final int IMAGE_HEIGHT = 192;
	
	public ArcheologistNotebookScreen(UUID playerId, boolean mainHand, CompoundTag tag)
	{
		super(Component.empty());
	}
	
	/*public void render(PoseStack stack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(stack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, BOOK_LOCATION);
		int x = (this.width - IMAGE_WIDTH) / 2;
		int y = 2;
		this.blit(stack, x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
		
		super.render(stack, mouseX, mouseY, delta);
	}*/
}
