package net.povstalec.sgjourney.client.models;

import java.util.Random;

import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel.WormholeTexture;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.stargate.StargateConnection;

public class WormholeModel
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	
	protected static final int MAX_LIGHT = 15728864;
	
	protected static final float SHIELDING_OFFSET = 1F / 16 / 2;
	
	protected static final int DEFAULT_FRAMES = 32;
	protected static final float DEFAULT_SCALE = 1F / DEFAULT_FRAMES;
	
	protected float maxDefaultDistortion;
	
	protected float[][] outerCircle = coordinates(DEFAULT_SIDES, 2.5F, 5, 0);
	protected float[][] circle1 = coordinates(DEFAULT_SIDES, 2.0F, 0, 98);
	protected float[][] circle2 = coordinates(DEFAULT_SIDES, 1.5F, -5, 67);
	protected float[][] circle3 = coordinates(DEFAULT_SIDES, 1.0F, -10, 567);
	protected float[][] circle4 = coordinates(DEFAULT_SIDES, 0.5F, -15, 257);
	protected float[][] circle5 = coordinates(DEFAULT_SIDES, 0.0F, 0, -2);
	
	protected float[][][] coordinates = new float[][][] {outerCircle, circle1, circle2, circle3, circle4, circle5};
	
	protected static final short BLOCK_PROGRESS_1 = 7;
	protected static final short BLOCK_PROGRESS_2 = 24;
	protected static final short BLOCK_PROGRESS_3 = 36;
	protected static final short BLOCK_PROGRESS_4 = 44;
	protected static final short BLOCK_PROGRESS_5 = 53;
	
	public WormholeModel(float maxDefaultDistortion)
	{
		this.maxDefaultDistortion = maxDefaultDistortion;
	}
	
	protected float getMaxDistortion(int wormholeDistortion)
	{
		float distortion = wormholeDistortion / 100F;
		return distortion > maxDefaultDistortion ? maxDefaultDistortion : distortion;
	}
	
	protected boolean isBlocked(int blockLayer, short blockProgress)
	{
		switch(blockLayer)
		{
		case -1:
			return false;
		case 0:
			return blockProgress >= BLOCK_PROGRESS_1;
		case 1:
			return blockProgress >= BLOCK_PROGRESS_2;
		case 2:
			return blockProgress >= BLOCK_PROGRESS_3;
		case 3:
			return blockProgress >= BLOCK_PROGRESS_4;
		default:
			return blockProgress >= BLOCK_PROGRESS_5;
		}
	}
	
	public void renderWormhole(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, int combinedLight, int combinedOverlay)
	{
		short irisProgress = stargate instanceof IrisStargateEntity irisStargate ? irisStargate.irisInfo().getIrisProgress() : (short) 0;
		float wormholeDistortion = getMaxDistortion(wormhole.distortion());
		
		this.renderKawoosh(stack, source, wormhole, wormholeDistortion, stargate.getTickCount(), stargate.getKawooshTickCount(), irisProgress);
		
		this.renderEventHorizon(stack, source, wormhole, wormholeDistortion, stargate.getTickCount(), stargate.getKawooshTickCount(), irisProgress);
		
		//TODO this.renderDisconnect(stack, source, Optional.of(new ResourceLocation(StargateJourney.MODID, "textures/entity/stargate/shield/shield.png")), stargate.getTickCount(), stargate.getKawooshTickCount(), isBlocked);
		
		if(wormhole.hasStrudel())
			this.renderStrudel(stack, source, wormhole, wormholeDistortion, stargate.getTickCount(), stargate.getKawooshTickCount(), irisProgress);
	}
	
	protected void renderEventHorizon(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int ticks, int kawooshProgress, short irisProgress)
	{
		renderPuddle(stack, source, wormhole, wormholeDistortion, ticks, kawooshProgress, irisProgress);
	}
	
	protected void renderDisconnect(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int ticks, int kawooshProgress, short irisProgress)
	{
		renderPuddle(stack, source, wormhole, wormholeDistortion, ticks, kawooshProgress, irisProgress);
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int ticks, int kawooshProgress, short irisProgress)
	{
		if(kawooshProgress <= 0)
			return;
		
		float yOffset = ticks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		int totalSides = coordinates[0].length;
		
		// Front
		WormholeTexture frontTexture = wormhole.eventHorizonTexture(true);
		int frontFrame = frontTexture.frame(ticks);
		float uFrontScale = frontTexture.uScale();
		float vFrontScale = frontTexture.vScale();
		
		float uFrontOffset = frontTexture.uOffset(frontFrame);
		float vFrontOffset = frontTexture.vOffset(frontFrame);
		
		ColorUtil.RGBA frontRGBA = frontTexture.rgba();
		
		VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizon(frontTexture.texture()));

		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0],
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
			
		// Back
		ResourcepackModel.WormholeTexture backTexture = wormhole.eventHorizonTexture(false);
		int backFrame = backTexture.frame(ticks);
		float uBackScale = backTexture.uScale();
		float vBackScale = backTexture.vScale();
		
		float uBackOffset = backTexture.uOffset(backFrame);
		float vBackOffset = backTexture.vOffset(backFrame);
		
		ColorUtil.RGBA backRGBA = backTexture.rgba();
		
		VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizon(backTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, 0), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, matrix3,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
			}
		}
	}
	
	protected void renderKawoosh(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int ticks, int kawooshProgress, short irisProgress)
	{
		if(kawooshProgress <= 0 || kawooshProgress >= StargateConnection.KAWOOSH_TICKS)
			return;
		
		float yOffset = ticks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		int totalSides = coordinates[0].length;
		
		// Front
		WormholeTexture frontTexture = wormhole.kawooshTexture(true);
		int frontFrame = frontTexture.frame(ticks);
		float uFrontScale = frontTexture.uScale();
		float vFrontScale = frontTexture.vScale();
		
		float uFrontOffset = frontTexture.uOffset(frontFrame);
		float vFrontOffset = frontTexture.vOffset(frontFrame);
		
		ColorUtil.RGBA frontRGBA = frontTexture.rgba();
		
		VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(frontTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3,
						bubbleX(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, matrix3,
						bubbleX(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),

						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),

						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
		
		// Back
		ResourcepackModel.WormholeTexture backTexture = wormhole.kawooshTexture(false);
		int backFrame = backTexture.frame(ticks);
		float uBackScale = backTexture.uScale();
		float vBackScale = backTexture.vScale();
		
		float uBackOffset = backTexture.uOffset(backFrame);
		float vBackOffset = backTexture.vOffset(backFrame);
		
		ColorUtil.RGBA backRGBA = backTexture.rgba();
		
		VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(backTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3,
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, matrix3,
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
			}
		}
	}
	
	protected void renderStrudel(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int ticks, int kawooshProgress, short irisProgress)
	{
		if(kawooshProgress <= StargateConnection.KAWOOSH_TICKS)
			return;
		
		float yOffset = ticks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		Matrix3f matrix3 = stack.last().normal();
		
		int totalSides = coordinates[0].length;
		
		// Front
		WormholeTexture frontTexture = wormhole.kawooshTexture(true);
		int frontFrame = frontTexture.frame(ticks);
		float uFrontScale = frontTexture.uScale();
		float vFrontScale = frontTexture.vScale();
		
		float uFrontOffset = frontTexture.uOffset(frontFrame);
		float vFrontOffset = frontTexture.vOffset(frontFrame);
		
		ColorUtil.RGBA frontRGBA = frontTexture.rgba();
		
		VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(frontTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, matrix3,
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0],
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), frontRGBA, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
		
		// Back
		WormholeTexture backTexture = wormhole.strudelTexture(false);
		int backFrame = backTexture.frame(ticks);
		float uBackScale = backTexture.uScale();
		float vBackScale = backTexture.vScale();
		
		float uBackOffset = backTexture.uOffset(backFrame);
		float vBackOffset = backTexture.vOffset(backFrame);
		
		ColorUtil.RGBA backRGBA = backTexture.rgba();
		
		VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.vortex(backTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, matrix3,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, matrix3,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), backRGBA, uBackScale, vBackScale, uBackOffset, vBackOffset);
			}
		}
	}
	
	//============================================================================================
	//**********************************Coordinates and Vertexes**********************************
	//============================================================================================
	
	/**
	 * Ïf the seed is above 0, a random offset will be chosen.
	 * If the seed is 0, the offset will be 0.
	 * If the seed is below 0, a random offset will be chosen that is same for all the coordinates.
	 * @param sides
	 * @param distanceFromCenter
	 * @param offset
	 * @param seed
	 * @return
	 */
	protected static float[][] coordinates(int sides, float distanceFromCenter, float offset, int seed)
	{
		Random random = new Random(seed);
		float angle = (float) 360 / sides;
		float[][] coordinates = new float[sides][3];
		float baseWidth = 9.8F / 16;
		float ratio = distanceFromCenter / DEFAULT_RADIUS;
		float usedWidth = baseWidth * ratio;
		
		float circumcircleRadius = SGJourneyModel.circumcircleRadius(angle, usedWidth);
		float defaultOffset = seed == 0 ? 0 : random.nextFloat() - 0.5F;
		
		if(distanceFromCenter >= 0)
		{
			for(int i = 0; i < sides; i++)
			{
				coordinates[i][0] = CoordinateHelper.CoordinateSystems.polarToCartesianY(circumcircleRadius, angle * i - offset);
				coordinates[i][1] = CoordinateHelper.CoordinateSystems.polarToCartesianX(circumcircleRadius, angle * i - offset);
				coordinates[i][2] = seed <= 0 ? defaultOffset : 2 * random.nextFloat() - 1;
			}
		}
		else
		{
			coordinates[0][0] = 0;
			coordinates[0][1] = 0;
			coordinates[0][2] = defaultOffset;
		}
		
		return coordinates;
	}
	
	protected static float bubbleX(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1 + 0.4 * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianX(r, phi);
	}
	
	protected static float bubbleY(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1 + 0.4 * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianY(r, phi);
	}
	
	protected static float distortionMaker(boolean shielded, float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		defaultOffset *= maxDefaultDistortion;
		float defaultDistortion = (float) Math.sin(defaultOffset * distortionOffset * 8) * maxDefaultDistortion;
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 4), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = defaultDistortion + kawooshDistortion;
		
		if(shielded && totalDistortion > SHIELDING_OFFSET - 0.05F) //TODO
			totalDistortion = SHIELDING_OFFSET - 0.05F;
		
		return totalDistortion < -maxDefaultDistortion ? -maxDefaultDistortion : totalDistortion;
	}
	
	protected static float vortexMaker(boolean shielded, float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		defaultOffset *= maxDefaultDistortion;
		float defaultDistortion = (float) Math.sin(defaultOffset * distortionOffset * 8) * maxDefaultDistortion;
		float actualDistortion = defaultDistortion * (1 + (float)( Math.pow(Math.sin((double) multiplier / 5), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 5), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = actualDistortion + kawooshDistortion;
		
		if(shielded && totalDistortion > SHIELDING_OFFSET - 0.05F) //TODO
			totalDistortion = SHIELDING_OFFSET - 0.05F;
		
		return totalDistortion;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, Matrix3f matrix3,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			ColorUtil.RGBA rgba,
			float uScale, float vScale,
			float uOffset, float vOffset)
	{
		float uHalfOffset = 0.5F * uScale;
		float vHalfOffset = 0.5F * vScale;
		
		consumer.vertex(matrix4, x1, y1, z1).color(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha()).uv(x1 * uScale / 5 + uHalfOffset + uOffset, y1 * vScale / 5 + vHalfOffset + vOffset)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1, 1, 1).endVertex();
		
		consumer.vertex(matrix4, x2, y2, z2).color(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha()).uv(x2 * uScale / 5 + uHalfOffset + uOffset, y2 * vScale / 5 + vHalfOffset + vOffset)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1, 1, 1).endVertex();
		
		consumer.vertex(matrix4, x3, y3, z3).color(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha()).uv(x3 * uScale / 5 + uHalfOffset + uOffset, y3 * vScale / 5 + vHalfOffset + vOffset)
		.overlayCoords(OverlayTexture.NO_OVERLAY).uv2(MAX_LIGHT).normal(matrix3, 1, 1, 1).endVertex();
	}
}
