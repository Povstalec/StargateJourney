package net.povstalec.sgjourney.common.init;

import net.minecraft.advancements.CriteriaTriggers;
import net.povstalec.sgjourney.common.advancements.WormholeTravelCriterion;

public class AdvancementInit
{
	public static void register()
	{
		CriteriaTriggers.register(WormholeTravelCriterion.INSTANCE);
	}
}
