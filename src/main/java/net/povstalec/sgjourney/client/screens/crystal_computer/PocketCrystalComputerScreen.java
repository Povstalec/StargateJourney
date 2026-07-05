package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
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
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerPhysicalButton;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.packets.ServerboundCrystalComputerUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

public abstract class PocketCrystalComputerScreen extends Screen
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/pocket_crystal_computer_gui.png");
	
	public enum SelectedCrystal
	{
		NONE,
		CRYSTAL_IN_COMPUTER,
		CRYSTAL_IN_HAND
	}

	protected int imageWidth = 256;
	protected int imageHeight = 142;
	
	protected final InteractionHand interactionHand;
	
	protected CrystalComputerPhysicalButton mainScreenButton;
	
	protected CrystalComputerPhysicalButton crystalInComputerButton;
	protected CrystalComputerPhysicalButton crystalInHandButton;
	
	protected SelectedCrystal selectedCrystal;
	
	public PocketCrystalComputerScreen(InteractionHand interactionHand, SelectedCrystal selectedCrystal)
	{
		super(Component.empty());
		
		this.interactionHand = interactionHand;
		
		this.selectedCrystal = selectedCrystal;
	}
	
	@Override
	public void init()
	{
		// Button to take you to the main screen
		mainScreenButton = CrystalComputerPhysicalButton.mainScreenButton(this.width / 2 + 83, this.height / 2 - 13,
				Component.empty(), Component.translatable("screen.sgjourney.crystal_computer.main_screen"),
				button -> this.minecraft.setScreen(new PocketCrystalComputerMainScreen(interactionHand, selectedCrystal)));
		addRenderableWidget(mainScreenButton);
		
		ItemStack crystalInComputer = getCrystalInComputer();
		// Button to set Crystal in computer as the target
		crystalInComputerButton = CrystalComputerPhysicalButton.switchTargetButton(this.width / 2 + 83, this.height / 2 - 40 - 7,
				Component.empty(), crystalInComputer.isEmpty() ?
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_computer.none").withStyle(ChatFormatting.DARK_RED) :
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_computer", crystalInComputer.getDisplayName()),
				button -> selectCrystal(SelectedCrystal.CRYSTAL_IN_COMPUTER));
		crystalInComputerButton.active = !getCrystalInComputer().isEmpty();
		addRenderableWidget(crystalInComputerButton);
		
		ItemStack crystalInHand = getCrystalInHand();
		// Button to set Crystal in hand as the target
		crystalInHandButton = CrystalComputerPhysicalButton.switchTargetButton(this.width / 2 + 83, this.height / 2 + 40 - 7,
				Component.empty(), crystalInHand.isEmpty() ?
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_hand.none").withStyle(ChatFormatting.DARK_RED) :
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_hand", crystalInHand.getDisplayName()),
				button -> selectCrystal(SelectedCrystal.CRYSTAL_IN_HAND));
		crystalInHandButton.active = !getCrystalInHand().isEmpty();
		addRenderableWidget(crystalInHandButton);
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
        
    	renderLabels(poseStack, mouseX, mouseY, x, y);
    }
    
    protected abstract void renderLabels(PoseStack stack, int mouseX, int mouseY, float x, float y);
	
	// Crystal stuff
	
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		this.selectedCrystal = selectedCrystal;
	}
	
	public static InteractionHand otherHand(InteractionHand hand)
	{
		return hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
	}
	
	public ItemStack getItemInHand(InteractionHand hand)
	{
		return Minecraft.getInstance().player.getItemInHand(hand);
	}
	
	public boolean isCorrectCrystalType(CrystalCache.Type type)
	{
		return type == CrystalCache.Type.MEMORY || type == CrystalCache.Type.COMMUNICATION;
	}
	
	public ItemStack getCrystalInComputer()
	{
		return getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).map(itemHandler ->
		{
			ItemStack stack = itemHandler.getStackInSlot(0);
			if(stack.getItem() instanceof AbstractCrystalItem crystal && isCorrectCrystalType(crystal.getType()))
				return stack;
			
			return ItemStack.EMPTY;
		}).orElse(ItemStack.EMPTY);
	}
	
	public ItemStack getCrystalInHand()
	{
		ItemStack stack = getItemInHand(otherHand(interactionHand));
		
		if(stack.getItem() instanceof AbstractCrystalItem crystal && isCorrectCrystalType(crystal.getType()))
			return stack;
		
		return ItemStack.EMPTY;
	}
	
	public ItemStack getCrystal(SelectedCrystal selectedCrystal)
	{
		return switch(selectedCrystal)
		{
			case CRYSTAL_IN_COMPUTER -> getCrystalInComputer();
			case CRYSTAL_IN_HAND -> getCrystalInHand();
			default -> ItemStack.EMPTY;
		};
	}
	
	public void saveToMemoryCrystal(MemoryEntry<?> memoryEntry)
	{
		if(selectedCrystal == SelectedCrystal.CRYSTAL_IN_COMPUTER)
		{
			getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
			{
				ItemStack stack = itemHandler.extractItem(0, 1, false);
				if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
				{
					memoryCrystal.saveMemoryEntry(stack, memoryEntry, true);
					itemHandler.insertItem(0, stack, false);
					updateServer(selectedCrystal);
				}
			});
		}
		else if(selectedCrystal == SelectedCrystal.CRYSTAL_IN_HAND)
		{
			ItemStack stack = getCrystalInHand();
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			{
				memoryCrystal.saveMemoryEntry(stack, memoryEntry, true);
				updateServer(selectedCrystal);
			}
		}
	}
	
	public static CompoundTag listTagToCompoundTag(ListTag list)
	{
		CompoundTag tag = new CompoundTag();
		tag.put(MemoryCrystalItem.MEMORY_LIST, list);
		return tag;
	}
	
	public CompoundTag getMemoryCrystalTag(SelectedCrystal selectedCrystal)
	{
		ItemStack stack = getCrystal(selectedCrystal);
		
		if(stack.getItem() instanceof MemoryCrystalItem && stack.hasTag())
			return listTagToCompoundTag(MemoryCrystalItem.getMemoryList(stack));
		
		return new CompoundTag();
	}
	
	public void updateServer(SelectedCrystal selectedCrystal)
    {
		if(selectedCrystal != SelectedCrystal.NONE)
			PacketHandlerInit.INSTANCE.sendToServer(new ServerboundCrystalComputerUpdatePacket(selectedCrystal == SelectedCrystal.CRYSTAL_IN_COMPUTER ? interactionHand : otherHand(interactionHand), getMemoryCrystalTag(selectedCrystal)));
    }
}
