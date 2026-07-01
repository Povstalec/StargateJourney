package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyButton;
import net.povstalec.sgjourney.common.sgjourney.memory_entry.MemoryEntry;

import javax.annotation.Nullable;

public class CrystalComputerEntryButton extends SGJourneyButton
{
	@Nullable
	protected MemoryEntry<?> memoryEntry;
	
	public CrystalComputerEntryButton(int x, int y, @Nullable MemoryEntry<?> memoryEntry, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/ring_panel_widgets.png"), x, y, 32, 16,
				memoryEntry != null ? Component.literal(memoryEntry.name()) : Component.empty(),
				memoryEntry != null ? Component.literal(memoryEntry.name()) : Component.empty(), press);
		
		this.memoryEntry = memoryEntry;
	}
}
