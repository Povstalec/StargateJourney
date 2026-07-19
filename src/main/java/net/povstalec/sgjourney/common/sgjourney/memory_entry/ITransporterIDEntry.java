package net.povstalec.sgjourney.common.sgjourney.memory_entry;

import net.povstalec.sgjourney.common.sgjourney.TransporterID;

public interface ITransporterIDEntry<T extends TransporterID>
{
	T getTransporterIDEntry();
}
