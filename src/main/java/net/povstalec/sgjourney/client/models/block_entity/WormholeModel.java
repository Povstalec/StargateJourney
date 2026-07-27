package net.povstalec.sgjourney.client.models.block_entity;

import java.util.Random;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.povstalec.sgjourney.client.render.SGJourneyRenderTypes;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel.WormholeTexture;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.ColorUtil;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

public class WormholeModel
{
	protected static final float DEFAULT_RADIUS = 3.5F;
	protected static final int DEFAULT_SIDES = 36;
	
	protected static final int MAX_LIGHT = 15728864;
	
	protected static final float SHIELDING_OFFSET = 1F / 16 / 2;
	
	protected static final int DEFAULT_FRAMES = 32;
	protected static final float DEFAULT_SCALE = 1F / DEFAULT_FRAMES;
	
	public static final int STRUDEL_TICKS = 20;
	
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
		return Math.min(wormholeDistortion / 100F, maxDefaultDistortion);
	}
	
	protected boolean isBlocked(int blockLayer, short blockProgress)
	{
		return switch(blockLayer)
		{
			case -1 -> false;
			case 0 -> blockProgress >= BLOCK_PROGRESS_1;
			case 1 -> blockProgress >= BLOCK_PROGRESS_2;
			case 2 -> blockProgress >= BLOCK_PROGRESS_3;
			case 3 -> blockProgress >= BLOCK_PROGRESS_4;
			default -> blockProgress >= BLOCK_PROGRESS_5;
		};
	}
	
	public void renderWormhole(AbstractStargateEntity<?> stargate, PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, int combinedLight, int combinedOverlay)
	{
		short irisProgress = stargate instanceof IrisStargateEntity<?> irisStargate ? irisStargate.irisInfo().getIrisProgress() : (short) 0;
		float wormholeDistortion = getMaxDistortion(wormhole.distortion());
		
		wormholeDistortion = getDistortion(wormholeDistortion, wormhole.disconnectTicks().disconnectEndTicks, stargate.getDisconnectTicks());
		
		int distortionTicks = getDistortionTicks(stargate);
		
		if(stargate.isConnected())
			this.renderKawoosh(stack, source, wormhole, wormholeDistortion, stargate.getAnimationTicks(), distortionTicks, stargate.getKawooshTickCount(), irisProgress);
		
		this.renderEventHorizon(stack, source, wormhole, stargate, wormholeDistortion, irisProgress, stargate.showUnstableWormhole());
		this.renderDisconnect(stack, source, wormhole, stargate, wormholeDistortion, wormhole.disconnectTicks().disconnectEndTicks, irisProgress);
		
		if(wormhole.hasStrudel())
			this.renderStrudel(stack, source, wormhole, wormholeDistortion, stargate.getAnimationTicks(), distortionTicks, stargate.getKawooshTickCount(), irisProgress);
	}
	
	private static float getDistortion(float wormholeDistortion, int maxTicks, int disconnectTicks)
	{
		if(maxTicks > 0)
			return wormholeDistortion * (1F - ((float) disconnectTicks / maxTicks));
		
		return wormholeDistortion;
	}
	
	private static float disconnectOpacity(int ticks, ResourcepackModel.DisconnectTicks disconnectTicks)
	{
		if(ticks < disconnectTicks.waitTicks)
			return 0F;
		if(ticks < disconnectTicks.waitTicks + disconnectTicks.fadeInTicks)
			return (float) (ticks - disconnectTicks.waitTicks) / disconnectTicks.fadeInTicks;
		else if(ticks < disconnectTicks.waitTicks + disconnectTicks.fadeInTicks + disconnectTicks.stableTicks)
			return 1F;
		else
			return 1F - (float) (ticks - disconnectTicks.waitTicks - disconnectTicks.fadeInTicks - disconnectTicks.stableTicks) / disconnectTicks.fadeOutTicks;
	}
	
	private static boolean isDisconnecting(AbstractStargateEntity<?> stargate)
	{
		return stargate.getDisconnectTicks() > 0;
	}
	
	private static int getDistortionTicks(AbstractStargateEntity<?> stargate)
	{
		if(isDisconnecting(stargate))
			return stargate.getAnimationTicks() + stargate.getDisconnectTicks() * 8;
		else
			return stargate.getAnimationTicks();
	}
	
	protected void renderEventHorizon(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, AbstractStargateEntity<?> stargate, float wormholeDistortion, short irisProgress, boolean isUnstable)
	{
		if(stargate.getKawooshTickCount() > 0 || (stargate.getDisconnectTicks() > 0 && stargate.getDisconnectTicks() < wormhole.disconnectTicks().eventHorizonStopTicks))
		{
			WormholeTexture frontTexture = isUnstable ? wormhole.unstableEventHorizonTexture(ResourcepackModel.WormholeSide.FRONT) : wormhole.eventHorizonTexture(ResourcepackModel.WormholeSide.FRONT);
			ResourcepackModel.WormholeTexture backTexture = isUnstable ? wormhole.unstableEventHorizonTexture(ResourcepackModel.WormholeSide.BACK) : wormhole.eventHorizonTexture(ResourcepackModel.WormholeSide.BACK);
			
			renderPuddle(stack, source, frontTexture, backTexture, wormholeDistortion, stargate.getAnimationTicks(), getDistortionTicks(stargate), irisProgress, 1F);
		}
	}
	
	protected void renderDisconnect(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, AbstractStargateEntity<?> stargate, float wormholeDistortion, int maxDisconnectTicks, short irisProgress)
	{
		if(stargate.getDisconnectTicks() > 0)
		{
			if(stargate.getDisconnectTicks() >= maxDisconnectTicks || wormhole.disconnect() == null)
			{
				stargate.resetAnimationTicks();
				stargate.resetDisconnectTicks();
			}
			else
			{
				int textureTicks = stargate.getDisconnectTicks() < wormhole.disconnectTicks().eventHorizonStopTicks ? 0 : stargate.getDisconnectTicks() - wormhole.disconnectTicks().eventHorizonStopTicks;
				
				renderPuddle(stack, source, wormhole.disconnectTexture(ResourcepackModel.WormholeSide.FRONT), wormhole.disconnectTexture(ResourcepackModel.WormholeSide.BACK),
						wormholeDistortion, textureTicks, getDistortionTicks(stargate), irisProgress, disconnectOpacity(stargate.getDisconnectTicks(), wormhole.disconnectTicks()));
			}
		}
	}
	
	protected void renderPuddle(PoseStack stack, MultiBufferSource source, WormholeTexture frontTexture, WormholeTexture backTexture, float wormholeDistortion, int textureTicks, int distortionTicks, short irisProgress, float opacity)
	{
		if(opacity <= 0F)
			return;
		
		float yOffset = distortionTicks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		PoseStack.Pose pose = stack.last();
		
		int totalSides = coordinates[0].length;
		
		// Front
		int frontFrame = frontTexture.frame(textureTicks);
		float uFrontScale = frontTexture.uScale();
		float vFrontScale = frontTexture.vScale();
		
		float uFrontOffset = frontTexture.uOffset(frontFrame);
		float vFrontOffset = frontTexture.vOffset(frontFrame);
		
		ColorUtil.RGBA frontRGBA = frontTexture.rgba();
		float frontAlpha = frontRGBA.alpha() * opacity;
		
		VertexConsumer frontConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizon(frontTexture.texture()));

		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(frontConsumer, matrix4, pose,
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontAlpha, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, pose,
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0],
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontAlpha, uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
			
		// Back
		int backFrame = backTexture.frame(textureTicks);
		float uBackScale = backTexture.uScale();
		float vBackScale = backTexture.vScale();
		
		float uBackOffset = backTexture.uOffset(backFrame);
		float vBackOffset = backTexture.vOffset(backFrame);
		
		ColorUtil.RGBA backRGBA = backTexture.rgba();
		float backAlpha = backRGBA.alpha() * opacity;
		
		VertexConsumer backConsumer = source.getBuffer(SGJourneyRenderTypes.eventHorizon(backTexture.texture()));
		
		for(int i = 0; i < 5; i++)
		{
			boolean isBlocked = isBlocked(i, irisProgress);
			boolean isBlockedOld = isBlocked(i - 1, irisProgress);
			
			for(int j = 0; j < totalSides; j++)
			{
				createTriangle(backConsumer, matrix4, pose,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, 0), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backAlpha, uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, pose,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, 0),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, 0),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, 0), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backAlpha, uBackScale, vBackScale, uBackOffset, vBackOffset);
			}
		}
	}
	
	protected void renderKawoosh(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int textureTicks, int distortionTicks, int kawooshProgress, short irisProgress)
	{
		if(kawooshProgress <= 0 || kawooshProgress >= StargateConnection.KAWOOSH_DURATION)
			return;
		
		float yOffset = distortionTicks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		PoseStack.Pose pose = stack.last();
		
		int totalSides = coordinates[0].length;
		
		// Front
		WormholeTexture frontTexture = wormhole.kawooshTexture(ResourcepackModel.WormholeSide.FRONT);
		int frontFrame = frontTexture.frame(textureTicks);
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
				createTriangle(frontConsumer, matrix4, pose,
						bubbleX(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontRGBA.alpha(), uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, pose,
						bubbleX(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),

						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),

						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontRGBA.alpha(), uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
		
		// Back
		ResourcepackModel.WormholeTexture backTexture = wormhole.kawooshTexture(ResourcepackModel.WormholeSide.BACK);
		int backFrame = backTexture.frame(textureTicks);
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
				createTriangle(backConsumer, matrix4, pose,
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][j % coordinates[i].length][0], coordinates[i][j % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, kawooshProgress), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backRGBA.alpha(), uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, pose,
						bubbleX(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][j % coordinates[i + 1].length][0], coordinates[i + 1][j % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress),
						
						bubbleX(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						bubbleY(coordinates[i][(j + 1) % coordinates[i].length][0], coordinates[i][(j + 1) % coordinates[i].length][1], i, kawooshProgress),
						distortionMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, kawooshProgress),
						
						bubbleX(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						bubbleY(coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], i + 1, kawooshProgress),
						distortionMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, kawooshProgress), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backRGBA.alpha(), uBackScale, vBackScale, uBackOffset, vBackOffset);
			}
		}
	}
	
	protected void renderStrudel(PoseStack stack, MultiBufferSource source, ResourcepackModel.Wormhole wormhole, float wormholeDistortion, int textureTicks, int distortionTicks, int kawooshProgress, short irisProgress)
	{
		if(kawooshProgress <= StargateConnection.KAWOOSH_DURATION)
			return;
		
		int strudelProgress = Math.min(kawooshProgress,  StargateConnection.KAWOOSH_DURATION + STRUDEL_TICKS);
		
		float yOffset = distortionTicks * DEFAULT_SCALE;
		
		Matrix4f matrix4 = stack.last().pose();
		PoseStack.Pose pose = stack.last();
		
		int totalSides = coordinates[0].length;
		
		// Front
		WormholeTexture frontTexture = wormhole.strudelTexture(ResourcepackModel.WormholeSide.FRONT);
		int frontFrame = frontTexture.frame(textureTicks);
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
				createTriangle(frontConsumer, matrix4, pose,
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, strudelProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, strudelProgress), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontRGBA.alpha(), uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
				
				createTriangle(frontConsumer, matrix4, pose,
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0],
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, strudelProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress), frontRGBA.red(), frontRGBA.green(), frontRGBA.blue(), frontRGBA.alpha(), uFrontScale, vFrontScale, uFrontOffset, vFrontOffset);
			}
		}
		
		// Back
		WormholeTexture backTexture = wormhole.strudelTexture(ResourcepackModel.WormholeSide.BACK);
		int backFrame = backTexture.frame(textureTicks);
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
				createTriangle(backConsumer, matrix4, pose,
						coordinates[i][(j + 1) % coordinates[i].length][0], 
						coordinates[i][(j + 1) % coordinates[i].length][1], 
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, strudelProgress),
						
						coordinates[i + 1][j % coordinates[i + 1].length][0],
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress),
						
						coordinates[i][j % coordinates[i].length][0],
						coordinates[i][j % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][j % coordinates[i].length][2], yOffset, i, strudelProgress), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backRGBA.alpha(), uBackScale, vBackScale, uBackOffset, vBackOffset);
				
				createTriangle(backConsumer, matrix4, pose,
						coordinates[i + 1][j % coordinates[i + 1].length][0], 
						coordinates[i + 1][j % coordinates[i + 1].length][1],
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][j % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress),
						
						coordinates[i][(j + 1) % coordinates[i].length][0],
						coordinates[i][(j + 1) % coordinates[i].length][1],
						vortexMaker(isBlockedOld, wormholeDistortion, coordinates[i][(j + 1) % coordinates[i].length][2], yOffset, i, strudelProgress),
						
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][0], 
						coordinates[i + 1][(j + 1) % coordinates[i + 1].length][1], 
						vortexMaker(isBlocked, wormholeDistortion, coordinates[i + 1][(j + 1) % coordinates[i + 1].length][2], yOffset, i + 1, strudelProgress), backRGBA.red(), backRGBA.green(), backRGBA.blue(), backRGBA.alpha(), uBackScale, vBackScale, uBackOffset, vBackOffset);
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
	public static float[][] coordinates(int sides, float distanceFromCenter, float offset, int seed)
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
	
	public static float bubbleX(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1F + 0.4F * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianX(r, phi);
	}
	
	public static float bubbleY(float x, float y, int multiplier, int progress)
	{
		float r = CoordinateHelper.CoordinateSystems.cartesianToPolarR(x, y);
		float phi = CoordinateHelper.CoordinateSystems.cartesianToPolarPhi(x, y);
		
		r *= (1F + 0.4F * (float) (Math.pow(Math.sin((double) multiplier / 3), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		return CoordinateHelper.CoordinateSystems.polarToCartesianY(r, phi);
	}
	
	public static float defaultDistortion(float maxDefaultDistortion, float defaultOffset, float distortionOffset)
	{
		return (float) Math.sin(defaultOffset * distortionOffset * 2F) * maxDefaultDistortion;
	}
	
	public static float distortionMaker(boolean shielded, float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		float defaultDistortion = defaultDistortion(maxDefaultDistortion, defaultOffset, distortionOffset);
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 4), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = defaultDistortion + kawooshDistortion;
		
		if(shielded && totalDistortion > SHIELDING_OFFSET - 0.05F) //TODO
			totalDistortion = SHIELDING_OFFSET - 0.05F;
		
		return Math.max(totalDistortion, -maxDefaultDistortion);
	}
	
	public static float vortexMaker(boolean shielded, float maxDefaultDistortion, float defaultOffset, float distortionOffset, int multiplier, int progress)
	{
		float defaultDistortion = defaultDistortion(maxDefaultDistortion, defaultOffset, distortionOffset);
		float actualDistortion = defaultDistortion * (1 + (float)( Math.pow(Math.sin((double) multiplier / 5), 4) * AbstractStargateEntity.kawooshFunction(progress)));
		
		float gradualKawoosh = (float) Math.pow(Math.sin((double) multiplier / 5), 2);
		float kawooshDistortion = (float) (1.2 * AbstractStargateEntity.kawooshFunction(progress)) * gradualKawoosh;
		
		float totalDistortion = actualDistortion + kawooshDistortion;
		
		if(shielded && totalDistortion > SHIELDING_OFFSET - 0.05F) //TODO
			totalDistortion = SHIELDING_OFFSET - 0.05F;
		
		return totalDistortion;
	}
	
	protected void createTriangle(VertexConsumer consumer, Matrix4f matrix4, PoseStack.Pose pose,
			float x1, float y1, float z1,
			float x2, float y2, float z2,
			float x3, float y3, float z3,
			float red, float green, float blue, float alpha,
			float uScale, float vScale,
			float uOffset, float vOffset)
	{
		float uHalfOffset = 0.5F * uScale;
		float vHalfOffset = 0.5F * vScale;
		
		if(StargateJourney.shouldRenderAMD())
		{
			consumer.addVertex(matrix4, x1, y1, z1).setColor(red, green, blue, alpha).setUv(x1 * uScale / 5 + uHalfOffset + uOffset, y1 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 0, 0, 0);
			
			consumer.addVertex(matrix4, x2, y2, z2).setColor(red, green, blue, alpha).setUv(x2 * uScale / 5 + uHalfOffset + uOffset, y2 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 0, 0, 0);
			
			consumer.addVertex(matrix4, x3, y3, z3).setColor(red, green, blue, alpha).setUv(x3 * uScale / 5 + uHalfOffset + uOffset, y3 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 0, 0, 0);
		}
		else
		{
			consumer.addVertex(matrix4, x1, y1, z1).setColor(red, green, blue, alpha).setUv(x1 * uScale / 5 + uHalfOffset + uOffset, y1 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 1, 1, 1);
			
			consumer.addVertex(matrix4, x2, y2, z2).setColor(red, green, blue, alpha).setUv(x2 * uScale / 5 + uHalfOffset + uOffset, y2 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 1, 1, 1);
			
			consumer.addVertex(matrix4, x3, y3, z3).setColor(red, green, blue, alpha).setUv(x3 * uScale / 5 + uHalfOffset + uOffset, y3 * vScale / 5 + vHalfOffset + vOffset)
					.setOverlay(OverlayTexture.NO_OVERLAY).setUv2(MAX_LIGHT, MAX_LIGHT >> 16).setNormal(pose, 1, 1, 1);
		}
	}
}
