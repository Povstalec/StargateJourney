package net.povstalec.sgjourney.client.screens.crystal_computer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.items.crystals.MemoryCrystalItem;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.CoordinateEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.TransporterIDEntry;

public class PocketCrystalComputerSaveScreen extends PocketCrystalComputerScreen
{
	public static final int EDIT_BOX_WIDTH = 160;
	public static final int EDIT_BOX_HEIGHT = 20;
	
	private static final int BACK_BUTTON_WIDTH = 162;
	private static final int BACK_BUTTON_HEIGHT = 20;
	
	private static final int CHOICE_BUTTON_WIDTH = 162;
	private static final int CHOICE_BUTTON_HEIGHT = 20;
	
	protected EditBox editBox;
	protected BlockPos clickedPos;
	
	public PocketCrystalComputerSaveScreen(boolean mainHand, BlockPos clickedPos)
	{
		super(mainHand);
		
		this.clickedPos = clickedPos;
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		
		this.editBox = new EditBox(this.font, this.width / 2 - EDIT_BOX_WIDTH / 2, y + 8, EDIT_BOX_WIDTH, EDIT_BOX_HEIGHT, Component.translatable("tooltip.sgjourney.energy_target"));
		addRenderableWidget(this.editBox);
		
		addRenderableWidget(Button.builder(ComponentHelper.coordinate(clickedPos),
						button -> {saveToMemoryCrystal(new CoordinateEntry(editBox.getValue(), this.minecraft.level.getGameTime(), clickedPos)); this.minecraft.screen.onClose();})
				.bounds((this.width - CHOICE_BUTTON_WIDTH) / 2, y + 30, CHOICE_BUTTON_WIDTH, CHOICE_BUTTON_HEIGHT).build());
		
		if(this.minecraft.level.getBlockEntity(clickedPos) instanceof AbstractTransporterEntity<?> transporter) // Choice between saving position and Transporter ID
		{
			addRenderableWidget(Button.builder(transporter.getID().toComponent(false, ChatFormatting.WHITE),
							button -> {saveToMemoryCrystal(new TransporterIDEntry(editBox.getValue(), this.minecraft.level.getGameTime(), transporter.getID())); this.minecraft.screen.onClose();})
					.bounds((this.width - CHOICE_BUTTON_WIDTH) / 2, y + 52, CHOICE_BUTTON_WIDTH, CHOICE_BUTTON_HEIGHT).build());
		}
		
		
		
		addRenderableWidget(Button.builder(CommonComponents.GUI_BACK,
						button -> this.minecraft.screen.onClose())
				.bounds((this.width - BACK_BUTTON_WIDTH) / 2, y + 146, BACK_BUTTON_WIDTH, BACK_BUTTON_HEIGHT).build());
	}
	
	protected void saveToMemoryCrystal(MemoryEntry<?> memoryEntry)
	{
		getItemInHand(interactionHand).getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler ->
		{
			ItemStack stack = itemHandler.extractItem(0, 1, false);
			if(stack.getItem() instanceof MemoryCrystalItem memoryCrystal)
			{
				memoryCrystal.saveMemoryEntry(stack, memoryEntry, true);
				itemHandler.insertItem(0, stack, false);
				updateServer();
			}
		});
	}
}
