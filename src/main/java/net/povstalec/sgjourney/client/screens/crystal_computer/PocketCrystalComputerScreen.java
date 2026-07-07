package net.povstalec.sgjourney.client.screens.crystal_computer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.crystal_computer.CrystalComputerButton;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.items.crystals.AbstractCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CommunicationCrystalItem;
import net.povstalec.sgjourney.common.items.crystals.CrystalCache;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.packets.ServerboundCrystalComputerUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Function;

public abstract class PocketCrystalComputerScreen extends Screen
{
	public static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/pocket_crystal_computer_gui.png");
	
	public static final int DARK_RED_COLOR = 11141120;
	
	public enum SelectedCrystal
	{
		NONE,
		CRYSTAL_IN_COMPUTER,
		CRYSTAL_IN_HAND
	}

	protected int imageWidth = 256;
	protected int imageHeight = 142;
	
	protected final InteractionHand interactionHand;
	
	protected CrystalComputerButton mainScreenButton;
	
	protected CrystalComputerButton crystalInComputerButton;
	protected CrystalComputerButton crystalInHandButton;
	
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
		mainScreenButton = CrystalComputerButton.mainScreenButton(this.width / 2 + 83, this.height / 2 - 13,
				Component.empty(), Component.translatable("screen.sgjourney.crystal_computer.main_screen"),
				button -> this.minecraft.setScreen(new PocketCrystalComputerMainScreen(interactionHand, selectedCrystal)));
		addRenderableWidget(mainScreenButton);
		
		ItemStack crystalInComputer = getCrystalInComputer();
		// Button to set Crystal in computer as the target
		crystalInComputerButton = CrystalComputerButton.switchTargetButton(this.width / 2 + 83, this.height / 2 - 40 - 7,
				Component.empty(), crystalInComputer.isEmpty() ?
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_computer.none").withStyle(ChatFormatting.DARK_RED) :
						Component.translatable("screen.sgjourney.crystal_computer.select_crystal_in_computer", crystalInComputer.getDisplayName()),
				button -> selectCrystal(SelectedCrystal.CRYSTAL_IN_COMPUTER));
		crystalInComputerButton.active = !getCrystalInComputer().isEmpty();
		addRenderableWidget(crystalInComputerButton);
		
		ItemStack crystalInHand = getCrystalInHand();
		// Button to set Crystal in hand as the target
		crystalInHandButton = CrystalComputerButton.switchTargetButton(this.width / 2 + 83, this.height / 2 + 40 - 7,
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
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float delta)
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
	
	@Nullable
	public CrystalCache.Type selectedCrystalType(SelectedCrystal selectedCrystal)
	{
		ItemStack stack = getCrystal(selectedCrystal);
		if(stack.getItem() instanceof AbstractCrystalItem crystal)
			return crystal.getType();
		
		return null;
	}
	
	protected void selectCrystal(SelectedCrystal selectedCrystal)
	{
		this.selectedCrystal = selectedCrystal;
		
		crystalInComputerButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_COMPUTER && !getCrystalInComputer().isEmpty();
		crystalInHandButton.active = selectedCrystal != SelectedCrystal.CRYSTAL_IN_HAND && !getCrystalInHand().isEmpty();
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
	
	public boolean memoryCrystalHasFreeSpace(SelectedCrystal selectedCrystal)
	{
		ItemStack stack = getCrystal(selectedCrystal);
		
		if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			return memoryCrystal.hasFreeSpace(stack);
		
		return false;
	}
	
	public void executeOnCrystal(SelectedCrystal selectedCrystal, Function<ItemStack, Boolean> function)
	{
		if(selectedCrystal == SelectedCrystal.CRYSTAL_IN_COMPUTER)
		{
			getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
			{
				ItemStack stack = itemHandler.extractItem(0, 1, false); // Extract item to work with it
				boolean shouldUpdate = function.apply(stack);
				itemHandler.insertItem(0, stack, false); // Insert item back so it doesn't disappear
				if(shouldUpdate)
					updateServer(selectedCrystal);
			});
		}
		else if(selectedCrystal == SelectedCrystal.CRYSTAL_IN_HAND)
		{
			ItemStack stack = getCrystalInHand();
			if(function.apply(stack))
				updateServer(selectedCrystal);
		}
	}
	
	public void saveToMemoryCrystal(MemoryEntry<?> memoryEntry)
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			{
				memoryCrystal.saveMemoryEntry(stack, memoryEntry, false);
				return true;
			}
			
			return false;
		});
	}
	
	public void overwriteMemoryEntry(int index, MemoryEntry<?> memoryEntry)
	{
		executeOnCrystal(selectedCrystal, stack ->
		{
			if(stack.getItem() instanceof MemoryCrystalItem)
			{
				MemoryCrystalItem.overwriteMemoryEntry(stack, memoryEntry, index);
				return true;
			}
			
			return false;
		});
	}
	
	public static CompoundTag listTagToCompoundTag(ListTag list)
	{
		CompoundTag tag = new CompoundTag();
		tag.put(MemoryCrystalItem.MEMORY_LIST, list);
		return tag;
	}
	
	public static CompoundTag frequencyToCompoundTag(int frequency)
	{
		CompoundTag tag = new CompoundTag();
		tag.putInt(CommunicationCrystalItem.FREQUENCY, frequency);
		return tag;
	}
	
	public static CompoundTag noFrequencyToCompoundTag()
	{
		CompoundTag tag = new CompoundTag();
		tag.putBoolean(CommunicationCrystalItem.FREQUENCY, true);
		return tag;
	}
	
	public CompoundTag getCrystalTag(SelectedCrystal selectedCrystal)
	{
		ItemStack stack = getCrystal(selectedCrystal);
		
		if(stack.hasTag() && stack.getItem() instanceof AbstractCrystalItem crystal)
		{
			if(crystal.getType() == CrystalCache.Type.MEMORY)
				return listTagToCompoundTag(MemoryCrystalItem.getMemoryList(stack));
			else if(crystal.getType() == CrystalCache.Type.COMMUNICATION)
			{
				if(CommunicationCrystalItem.hasFrequency(stack))
					return frequencyToCompoundTag(CommunicationCrystalItem.getFrequency(stack));
				else
					return noFrequencyToCompoundTag();
			}
		}
		
		return new CompoundTag();
	}
	
	public static void drawCenteredString(PoseStack poseStack, Font font, Component component, float x, float y, int color)
	{
		FormattedCharSequence formattedCharSequence = component.getVisualOrderText();
		font.drawShadow(poseStack, formattedCharSequence, x - font.width(formattedCharSequence) / 2F, y, color);
	}
	
	public void updateServer(SelectedCrystal selectedCrystal)
    {
		if(selectedCrystal != SelectedCrystal.NONE)
			PacketHandlerInit.INSTANCE.sendToServer(new ServerboundCrystalComputerUpdatePacket(selectedCrystal == SelectedCrystal.CRYSTAL_IN_COMPUTER ? interactionHand : otherHand(interactionHand), getCrystalTag(selectedCrystal)));
    }
}
