package net.povstalec.sgjourney.client.render.level;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.stellarview.api.StellarViewSpecialEffects;
import net.povstalec.stellarview.api.celestial_objects.CelestialObject;
import net.povstalec.stellarview.api.celestial_objects.Moon;

public class StellarViewRendering
{
	private static final CelestialObject BLACK_HOLE = new CelestialObject(new ResourceLocation(StargateJourney.MODID, "textures/environment/black_hole.png"), 20F, 50F).halo(new ResourceLocation(StargateJourney.MODID, "textures/environment/black_hole_halo.png"), 50F);
	
	private static final StellarViewSpecialEffects ABYDOS_EFFECTS = new StellarViewSpecialEffects(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false){}
	.spiralGalaxy4Arms(10842L, 1500, 6, -10, 8, 0.0, 0.0, 0)
	.vanillaSun()
	.celestialObject(new Moon.DefaultMoon().initialTheta((float) Math.toRadians(-120)).initialPhi((float) Math.toRadians(170)))
	.celestialObject(new Moon.DefaultMoon().initialTheta((float) Math.toRadians(80)).initialPhi((float) Math.toRadians(152.5)))
	.celestialObject(new Moon.DefaultMoon().initialTheta((float) Math.toRadians(-65)).initialPhi((float) Math.toRadians(150)));
	
	private static final StellarViewSpecialEffects CHULAK_EFFECTS = new StellarViewSpecialEffects(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false){}
	.spiralGalaxy4Arms(10842L, 1500, 6, 0, 8, 0.0, 0.2 * Math.PI, 0.6 * Math.PI)
	.vanillaSun()
	.celestialObject(new Moon.DefaultMoon());
	
	private static final StellarViewSpecialEffects CAVUM_TENEBRAE_EFFECTS = new StellarViewSpecialEffects(192.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false){}
	.spiralGalaxy4Arms(10842L, 1500, 2, 1, 1, 0.3 * Math.PI, 0.1 * Math.PI, 0.8 * Math.PI)
	.celestialObject(BLACK_HOLE);

	private static final StellarViewSpecialEffects LANTEA_EFFECTS = new StellarViewSpecialEffects(386.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false){}
	.spiralGalaxy2Arms(17892L, 2250, 8, 0, 16, 0.35 * Math.PI, 0.35 * Math.PI, 0.15 * Math.PI)
	.vanillaSun()
	.celestialObject(new Moon.DefaultMoon());

	private static final StellarViewSpecialEffects ATHOS_EFFECTS = new StellarViewSpecialEffects(386.0F, true, DimensionSpecialEffects.SkyType.NORMAL, false, false){}
	.spiralGalaxy2Arms(17892L, 2250, 8, 0, 16, 0.35 * Math.PI, 0.35 * Math.PI, 0.15 * Math.PI)
	.vanillaSun()
	.celestialObject(new Moon.DefaultMoon());
	
	public static void registerStellarViewEffects(RegisterDimensionSpecialEffectsEvent event)
	{
		event.register(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, StellarViewRendering.ABYDOS_EFFECTS);
    	event.register(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, StellarViewRendering.CHULAK_EFFECTS);
    	event.register(SGJourneyDimensionSpecialEffects.CAVUM_TENEBRAE_EFFECTS, StellarViewRendering.CAVUM_TENEBRAE_EFFECTS);
    	event.register(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, StellarViewRendering.LANTEA_EFFECTS);
    	event.register(SGJourneyDimensionSpecialEffects.ATHOS_EFFECTS, StellarViewRendering.ATHOS_EFFECTS);
	}
}
