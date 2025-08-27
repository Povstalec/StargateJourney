package net.povstalec.sgjourney.client.models.entity;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.povstalec.sgjourney.common.entities.Goauld;

import java.util.Arrays;

public class GoauldModel<T extends Goauld> extends HierarchicalModel<T>
{
	private static final int BODY_COUNT = 7;
	private final ModelPart root;
	private final ModelPart[] bodyParts = new ModelPart[7];
	//private final ModelPart[] bodyLayers = new ModelPart[3];
	private static final int[][] BODY_SIZES = new int[][]{{2, 2, 3}, {2, 2, 3}, {2, 2, 3}, {2, 2, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
	private static final int[][] BODY_TEXS = new int[][]{{0, 0}, {0, 5}, {0, 10}, {0, 15}, {0, 20}, {10, 0}, {10, 3}};
	
	public GoauldModel(ModelPart part)
	{
		this.root = part;
		Arrays.setAll(this.bodyParts, (id) -> part.getChild(getSegmentName(id)));
		//Arrays.setAll(this.bodyLayers, (id) -> part.getChild(getLayerName(id)));
	}
	
	/*private static String getLayerName(int id)
	{
		return "layer" + id;
	}*/
	
	private static String getSegmentName(int id)
	{
		return "segment" + id;
	}
	
	public static LayerDefinition createBodyLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		float[] afloat = new float[BODY_COUNT];
		float f = -3.5F;
		
		for(int i = 0; i < BODY_COUNT; ++i)
		{
			partdefinition.addOrReplaceChild(getSegmentName(i), CubeListBuilder.create().texOffs(BODY_TEXS[i][0], BODY_TEXS[i][1]).addBox(
					(float)BODY_SIZES[i][0] * -0.5F, 0.0F, (float)BODY_SIZES[i][2] * -0.5F, (float)BODY_SIZES[i][0], (float)BODY_SIZES[i][1], (float)BODY_SIZES[i][2]),
					PartPose.offset(0.0F, (float)(24 - BODY_SIZES[i][1]), f));
			afloat[i] = f;
			
			if(i < 6)
				f += (BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5F;
		}
		
		/*partdefinition.addOrReplaceChild(getLayerName(0), CubeListBuilder.create().texOffs(20, 0).addBox(-5.0F, 0.0F, (float)BODY_SIZES[2][2] * -0.5F, 2.0F, 2.0F, (float)BODY_SIZES[2][2]), PartPose.offset(0.0F, 16.0F, afloat[2]));
		partdefinition.addOrReplaceChild(getLayerName(1), CubeListBuilder.create().texOffs(20, 11).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 2.0F, 2.0F, (float)BODY_SIZES[4][2]), PartPose.offset(0.0F, 20.0F, afloat[4]));
		partdefinition.addOrReplaceChild(getLayerName(2), CubeListBuilder.create().texOffs(20, 18).addBox(-3.0F, 0.0F, (float)BODY_SIZES[4][2] * -0.5F, 2.0F, 2.0F, (float)BODY_SIZES[1][2]), PartPose.offset(0.0F, 19.0F, afloat[1]));*/
		
		return LayerDefinition.create(meshdefinition, 64, 32);
	}
	
	@Override
	public ModelPart root()
	{
		return this.root;
	}
	
	@Override
	public void setupAnim(T goauld, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		for(int i = 0; i < this.bodyParts.length; ++i)
		{
			this.bodyParts[i].yRot = Mth.cos(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.05F * (float)(1 + Math.abs(i - 2));
			this.bodyParts[i].x = Mth.sin(ageInTicks * 0.9F + (float)i * 0.15F * (float)Math.PI) * (float)Math.PI * 0.2F * (float)Math.abs(i - 2);
		}
		
		/*this.bodyLayers[0].yRot = this.bodyParts[2].yRot;
		this.bodyLayers[1].yRot = this.bodyParts[4].yRot;
		this.bodyLayers[1].x = this.bodyParts[4].x;
		this.bodyLayers[2].yRot = this.bodyParts[1].yRot;
		this.bodyLayers[2].x = this.bodyParts[1].x;*/
	}
}
