package net.povstalec.sgjourney.common.entities.ai;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.schedule.Activity;
import net.povstalec.sgjourney.common.entities.Jaffa;

public class JaffaAi
{
	protected static Brain<?> makeBrain(Jaffa jaffa, Brain<Jaffa> brain)
	{
		//initCoreActivity(brain);
		//initIdleActivity(brain);
		//initAdmireItemActivity(brain);
		//initFightActivity(jaffa, brain);
		//initCelebrateActivity(brain);
		//initRetreatActivity(brain);
		//initRideHoglinActivity(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.useDefaultActivity();
		
		return brain;
	}
}
