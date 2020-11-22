package init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import woldericz_junior.stargatejourney.StargateJourney;
import woldericz_junior.stargatejourney.entities.JaffaPlasma;

public class StargateEntities 
{
	public static final DeferredRegister<EntityType<?>> ENTITIES = new DeferredRegister<>(ForgeRegistries.ENTITIES, StargateJourney.MODID);
	
	public static final RegistryObject<EntityType<JaffaPlasma>> jaffa_plasma = ENTITIES.register("jaffa_plasma",
			() -> EntityType.Builder.<JaffaPlasma>create(JaffaPlasma::new, EntityClassification.MISC).size(0.25F, 0.25F).build("jaffa_plasma"));
}
