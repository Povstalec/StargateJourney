package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.CrystalComputerItem;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.packets.ServerboundCrystalComputerUpdatePacket;

public abstract class PocketCrystalComputerScreen extends Screen
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/pocket_crystal_computer_gui.png");

	protected int imageWidth = 176;
	protected int imageHeight = 176;
	
	protected final InteractionHand interactionHand;
	
	public PocketCrystalComputerScreen(boolean mainHand)
	{
		super(Component.empty());
		
		this.interactionHand = mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
	}
	
	protected ItemStack getItemInHand(InteractionHand hand)
	{
		return Minecraft.getInstance().player.getItemInHand(hand);
	}
	
	protected ItemStack getItemInComputer()
	{
		return getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> itemHandler.getStackInSlot(0)).orElse(ItemStack.EMPTY);
	}
	
	protected CompoundTag listTagToCompoundTag(ListTag list)
	{
		CompoundTag tag = new CompoundTag();
		tag.put(MemoryCrystalItem.MEMORY_LIST, list);
		return tag;
	}
	
	protected CompoundTag getMemoryCrystalTagInHand(InteractionHand hand)
	{
		ItemStack stack = getItemInHand(hand);
		
		if(stack.getItem() instanceof CrystalComputerItem)
		{
			ItemStack heldStack = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler -> itemHandler.getStackInSlot(0)).orElse(ItemStack.EMPTY);
			
			if(heldStack.hasTag())
				return listTagToCompoundTag(MemoryCrystalItem.getMemoryList(heldStack));
		}
		else if(stack.getItem() instanceof MemoryCrystalItem && stack.hasTag())
			return listTagToCompoundTag(MemoryCrystalItem.getMemoryList(stack));
		
		return new CompoundTag();
	}
	
	@Override
	public boolean isPauseScreen() 
	{
		return false;
	}

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta)
    {
    	this.renderBackground(poseStack);
    	int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
		
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(poseStack, x, y, 0, 0, imageWidth, imageHeight);

    	super.render(poseStack, mouseX, mouseY, delta);
    	
    	poseStack.pushPose();
    	poseStack.scale(0.5F, 0.5F, 0.5F);
    	poseStack.translate((float)x, (float)y, 0.0F);
        
    	renderLabels(poseStack, mouseX, mouseY, x, y);
		
		poseStack.popPose();
    }
    
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y) {}
    
    protected void updateServer()
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundCrystalComputerUpdatePacket(getMemoryCrystalTagInHand(InteractionHand.MAIN_HAND), getMemoryCrystalTagInHand(InteractionHand.OFF_HAND)));
    }
}
