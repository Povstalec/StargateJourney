package net.povstalec.sgjourney.client.models;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.povstalec.sgjourney.client.Layers;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FalconArmorModel extends HumanoidModel<LivingEntity>
{
	public static FalconArmorModel INSTANCE;
	
	public FalconArmorModel(ModelPart root) 
	{
		super(root);
	}
	
	public static LayerDefinition createBodyLayer() 
	{
		MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition head = partdefinition.getChild("head");
        
        head.addOrReplaceChild("falcon_helmet", CubeListBuilder.create()
        		.texOffs(0, 0)
        		.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 
        				new CubeDeformation(1.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        head.addOrReplaceChild("falcon_neck", CubeListBuilder.create()
        		.texOffs(0, 18)
        		.addBox(-3.0F, -10.0F, -3.0F, 6.0F, 1.0F, 6.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        head.addOrReplaceChild("falcon_back", CubeListBuilder.create()
        		.texOffs(0, 16)
        		.addBox(-4.0F, 1.0F, 4.0F, 8.0F, 1.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        PartDefinition falconHead = head.addOrReplaceChild("falcon_head", CubeListBuilder.create()
        		.texOffs(40, 20)
        		.addBox(0.0F, 0.0F, 0.0F, 6.0F, 6.0F, 6.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3.0F, -8.0F, -9.25F, 0.75F, 0.0F, 0.0F));
        
        falconHead.addOrReplaceChild("falcon_beak", CubeListBuilder.create()
        		.texOffs(0, 27)
        		.addBox(1.5F, 1.5F, -2.0F, 3.0F, 3.0F, 2.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        falconHead.addOrReplaceChild("falcon_beak_1", CubeListBuilder.create()
        		.texOffs(10, 29)
        		.addBox(2.0F, 2.5F, -3.0F, 2.0F, 2.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        
        falconHead.addOrReplaceChild("falcon_beak_2", CubeListBuilder.create()
        		.texOffs(16, 30)
        		.addBox(2.5F, 4.5F, -3.0F, 1.0F, 1.0F, 1.0F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

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
        INSTANCE = new FalconArmorModel(entityModelSet.bakeLayer(Layers.FALCON_HEAD));
    }

}
