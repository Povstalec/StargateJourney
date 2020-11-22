package woldericz_junior.stargatejourney.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class HorusArmorModel extends BipedModel
{

		ModelRenderer horusHead;
		ModelRenderer horusNeck;
		ModelRenderer horusBeak;
		ModelRenderer horusBeak1;
		ModelRenderer horusBeak2;
		ModelRenderer horusBack;

	  public HorusArmorModel(float expand)
	  {
	   super(expand, 0, 64, 32);     
	   horusHead = new ModelRenderer(this, 40, 20);
	   horusHead.addBox(0F, 0F, 0F, 6, 6, 6, 0);
	   horusHead.setRotationPoint(-3F, -8F, -9.25F);
	   horusHead.setTextureSize(64, 32);
	   horusHead.mirror = true;
	   setRotation(horusHead, 0.75F, 0F, 0F);
	        
	   horusNeck = new ModelRenderer(this, 0, 18);
	   horusNeck.addBox(-3F, -10F, -3F, 6, 1, 6, 0);
	   horusNeck.setRotationPoint(0F, 0F, 0F);
	   horusNeck.setTextureSize(64, 32);
	   horusNeck.mirror = true;
	   setRotation(horusNeck, 0F, 0F, 0F);
	   
	   horusBeak = new ModelRenderer(this, 0, 25);
	   horusBeak.addBox(1.5F, 3F, -2F, 3, 2, 2, 0);
	   horusBeak.setRotationPoint(-3F, -8F, -9.25F);
	   horusBeak.setTextureSize(64, 32);
	   horusBeak.mirror = true;
	   setRotation(horusBeak, 0.75F, 0F, 0F);
	   
	   horusBeak1 = new ModelRenderer(this, 0, 25);
	   horusBeak1.addBox(2F, 2.5F, -3F, 2, 2, 3, 0);
	   horusBeak1.setRotationPoint(-3F, -8F, -9.25F);
	   horusBeak1.setTextureSize(64, 32);
	   horusBeak1.mirror = true;
	   setRotation(horusBeak1, 0.75F, 0F, 0F);
	   
	   horusBeak2 = new ModelRenderer(this, 0, 25);
	   horusBeak2.addBox(2.5F, 4.5F, -3F, 1, 1, 1, 0);
	   horusBeak2.setRotationPoint(-3F, -8F, -9.25F);
	   horusBeak2.setTextureSize(64, 32);
	   horusBeak2.mirror = true;
	   setRotation(horusBeak2, 0.75F, 0F, 0F);
	   
	   horusBack = new ModelRenderer(this, 0, 16);
	   horusBack.addBox(-4F, 1F, 4F, 8, 1, 1, 0);
	   horusBack.setRotationPoint(0F, 0F, 0F);
	   horusBack.setTextureSize(64, 32);
	   horusBack.mirror = true;
	   setRotation(horusBack, 0F, 0F, 0F);

	    this.bipedHead.addChild(horusHead);
	    this.bipedHead.addChild(horusNeck);
	    this.bipedHead.addChild(horusBeak);
	    this.bipedHead.addChild(horusBeak1);
	    this.bipedHead.addChild(horusBeak2);
	    this.bipedHead.addChild(horusBack);

	 } 

		 private void setRotation(ModelRenderer model, float x, float y, float z)
		  {
		    model.rotateAngleX = x;
		    model.rotateAngleY = y;
		    model.rotateAngleZ = z;
		  } 

}
