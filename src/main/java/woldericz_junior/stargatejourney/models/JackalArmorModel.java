package woldericz_junior.stargatejourney.models;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;

public class JackalArmorModel extends BipedModel
{

		ModelRenderer jackalHead;
		ModelRenderer jackalNeck;
		ModelRenderer jackalNose;
		ModelRenderer jackalREar;
		ModelRenderer jackalLEar;
		ModelRenderer jackalBack;

	  public JackalArmorModel(float expand)
	  {
	   super(expand, 0, 64, 32);     
	   jackalHead = new ModelRenderer(this, 40, 20);
	   jackalHead.addBox(0F, 0F, 0F, 6, 6, 6, 0);
	   jackalHead.setRotationPoint(-3F, -8F, -9.25F);
	   jackalHead.setTextureSize(64, 32);
	   jackalHead.mirror = true;
	   setRotation(jackalHead, 0.75F, 0F, 0F);
	        
	   jackalNeck = new ModelRenderer(this, 0, 18);
	   jackalNeck.addBox(-3F, -10F, -3F, 6, 1, 6, 0);
	   jackalNeck.setRotationPoint(0F, 0F, 0F);
	   jackalNeck.setTextureSize(64, 32);
	   jackalNeck.mirror = true;
	   setRotation(jackalNeck, 0F, 0F, 0F);
	   
	   jackalNose = new ModelRenderer(this, 0, 25);
	   jackalNose.addBox(1.5F, 3F, -4F, 3, 3, 4, 0);
	   jackalNose.setRotationPoint(-3F, -8F, -9.25F);
	   jackalNose.setTextureSize(64, 32);
	   jackalNose.mirror = true;
	   setRotation(jackalNose, 0.75F, 0F, 0F);
	   
	   jackalREar = new ModelRenderer(this, 14, 26);
	   jackalREar.addBox(0F, -6F, -3F, 1, 5, 1, 0);
	   jackalREar.setRotationPoint(-3F, -12F, -3.5F);
	   jackalREar.setTextureSize(64, 32);
	   jackalREar.mirror = true;
	   setRotation(jackalREar, 0.75F, 0F, 0F);
	   
	   jackalLEar = new ModelRenderer(this, 14, 26);
	   jackalLEar.addBox(5F, -6F, -3F, 1, 5, 1, 0);
	   jackalLEar.setRotationPoint(-3F, -12F, -3.5F);
	   jackalLEar.setTextureSize(64, 32);
	   jackalLEar.mirror = true;
	   setRotation(jackalLEar, 0.75F, 0F, 0F);
	   
	   jackalBack = new ModelRenderer(this, 0, 16);
	   jackalBack.addBox(-4F, 1F, 4F, 8, 1, 1, 0);
	   jackalBack.setRotationPoint(0F, 0F, 0F);
	   jackalBack.setTextureSize(64, 32);
	   jackalBack.mirror = true;
	   setRotation(jackalBack, 0F, 0F, 0F);

	    this.bipedHead.addChild(jackalHead);
	    this.bipedHead.addChild(jackalNeck);
	    this.bipedHead.addChild(jackalNose);
	    this.bipedHead.addChild(jackalREar);
	    this.bipedHead.addChild(jackalLEar);
	    this.bipedHead.addChild(jackalBack);

	 } 

		 private void setRotation(ModelRenderer model, float x, float y, float z)
		  {
		    model.rotateAngleX = x;
		    model.rotateAngleY = y;
		    model.rotateAngleZ = z;
		  } 

}
