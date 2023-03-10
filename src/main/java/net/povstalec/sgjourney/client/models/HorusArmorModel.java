package net.povstalec.sgjourney.client.models;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.model.geom.builders.CubeDeformation;
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
public class HorusArmorModel extends HumanoidModel<LivingEntity>
{
	public static HorusArmorModel INSTANCE;
	
	public HorusArmorModel(ModelPart root) 
	{
		super(root);
	}
	
	public static LayerDefinition createBodyLayer() 
	{
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        
        head.addOrReplaceChild("horus_helmet", CubeListBuilder.create()
        		.texOffs(0, 0)
        		.addBox(-4F, -8F, -4F, 8F, 8F, 8F, 
        				new CubeDeformation(1.0F)), 
        		PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        
        head.addOrReplaceChild("horus_head", CubeListBuilder.create()
        		.texOffs(40, 20)
        		.addBox(0F, 0F, 0F, 6F, 6F, 6F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3F, -8F, -9.25F, 0.75F, 0F, 0F));
        
        head.addOrReplaceChild("horus_neck", CubeListBuilder.create()
        		.texOffs(0, 18)
        		.addBox(-3F, -10F, -3F, 6F, 1F, 6F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));
        
        head.addOrReplaceChild("horus_beak", CubeListBuilder.create()
        		.texOffs(0, 27)
        		.addBox(1.5F, 1.5F, -2F, 3F, 3F, 2F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3F, -8F, -9.25F, 0.75F, 0F, 0F));
        
        head.addOrReplaceChild("horus_beak_1", CubeListBuilder.create()
        		.texOffs(10, 29)
        		.addBox(2F, 2.5F, -3F, 2F, 2F, 1F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3F, -8F, -9.25F, 0.75F, 0F, 0F));
        
        head.addOrReplaceChild("horus_beak_2", CubeListBuilder.create()
        		.texOffs(16, 30)
        		.addBox(2.5F, 4.5F, -3F, 1F, 1F, 1F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(-3F, -8F, -9.25F, 0.75F, 0F, 0F));
        
        head.addOrReplaceChild("horus_back", CubeListBuilder.create()
        		.texOffs(0, 16)
        		.addBox(-4F, 1F, 4F, 8F, 1F, 1F, 
        				new CubeDeformation(0.0F)), 
        		PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0F));

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
        INSTANCE = new HorusArmorModel(entityModelSet.bakeLayer(Layers.HORUS_HEAD));
    }

}
