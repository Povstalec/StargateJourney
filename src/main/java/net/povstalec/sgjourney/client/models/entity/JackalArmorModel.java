package net.povstalec.sgjourney.client.models.entity;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.povstalec.sgjourney.client.Layers;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class JackalArmorModel extends HumanoidModel<LivingEntity>
{
	public static JackalArmorModel INSTANCE;

	public JackalArmorModel(ModelPart part) 
	{
		super(part);
	}
	
	public static LayerDefinition createBodyLayer() 
	{
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition head = partdefinition.getChild("head");
        
        head.addOrReplaceChild("jackal_helmet", CubeListBuilder.create()
        		.texOffs(0, 0)
        		.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 
        				new CubeDeformation(1.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        head.addOrReplaceChild("jackal_neck", CubeListBuilder.create()
        		.texOffs(0, 18)
        		.addBox(-3.0F, -10.0F, -3.0F, 6.0F, 1.0F, 6.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        head.addOrReplaceChild("jackal_back", CubeListBuilder.create()
        		.texOffs(0, 16)
        		.addBox(-4.0F, 1.0F, 4.0F, 8.0F, 1.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        PartDefinition jackalHead = head.addOrReplaceChild("jackal_head", CubeListBuilder.create()
        		.texOffs(40, 20)
        		.addBox(0.0F, 0.0F, 0.0F, 6.0F, 6.0F, 6.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3.0F, -8.0F, -9.25F, 0.75F, 0.0F, 0.0F));
        
        jackalHead.addOrReplaceChild("jackal_nose", CubeListBuilder.create()
        		.texOffs(0, 25)
        		.addBox(1.5F, 3.0F, -4.0F, 3.0F, 3.0F, 4.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        jackalHead.addOrReplaceChild("jackal_ear_right", CubeListBuilder.create()
        		.texOffs(14, 26)
        		.addBox(-1.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(1.0F, 0.0F, 3.0F, 0.0F, 0.0F, -0.25F));
        
        jackalHead.addOrReplaceChild("jackal_ear_left", CubeListBuilder.create()
        		.texOffs(14, 26)
        		.addBox(0.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(5.0F, 0.0F, 3.0F, 0.0F, 0.0F, 0.25F));
		
		jackalHead.addOrReplaceChild("jackal_flap_right", CubeListBuilder.create()
						.texOffs(25, 20)
						.addBox(-5.0F, -3.0F, 0.0F, 5.0F, 6.0F, 0.0F,
								new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, 1.5F, 5.0F, 0.0F, 1.25F, 0.0F));
		
		jackalHead.addOrReplaceChild("jackal_flap_left", CubeListBuilder.create()
						.texOffs(25, 26)
						.addBox(0.0F, -3.0F, 0.0F, 5.0F, 6.0F, 0.0F,
								new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(6.0F, 1.5F, 5.0F, 0.0F, -1.25F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
	
    @Override
    protected Iterable<ModelPart> bodyParts()
    {
    	return ImmutableList.of();
    }

    @SubscribeEvent
    public static void bakeModelLayers(EntityRenderersEvent.AddLayers event)
    {
        EntityModelSet entityModelSet = event.getEntityModels();
        INSTANCE = new JackalArmorModel(entityModelSet.bakeLayer(Layers.JACKAL_HEAD));
    }
}
