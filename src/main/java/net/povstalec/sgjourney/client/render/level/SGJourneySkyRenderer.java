package net.povstalec.sgjourney.client.render.level;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.StargateJourney;

public abstract class SGJourneySkyRenderer
{
	protected static final ResourceLocation MOON_HALO_LOCATION = StargateJourney.sgjourneyLocation("textures/environment/blue_halo.png");
	protected static final ResourceLocation MOON_LOCATION = StargateJourney.sgjourneyLocation("textures/environment/moon_phases.png");
	protected static final ResourceLocation SUN_LOCATION = StargateJourney.location("textures/environment/sun.png");
	protected static final ResourceLocation BLACK_HOLE_HALO_LOCATION = StargateJourney.sgjourneyLocation("textures/environment/black_hole_halo.png");
	protected static final ResourceLocation BLACK_HOLE_LOCATION = StargateJourney.sgjourneyLocation("textures/environment/black_hole.png");
	
	protected Minecraft minecraft = Minecraft.getInstance();
	@Nullable
	protected VertexBuffer starBuffer;
	@Nullable
	protected VertexBuffer skyBuffer;
	@Nullable
	protected VertexBuffer darkBuffer;
	
	public SGJourneySkyRenderer(long seed, int numberOfStars)
	{
		this.createStars(seed, numberOfStars);
		this.createLightSky();
		this.createDarkSky();
	}
	
	protected boolean doesMobEffectBlockSky(Camera camera)
	{
		Entity entity = camera.getEntity();
		if (!(entity instanceof LivingEntity livingentity))
			return false;
		else
			return livingentity.hasEffect(MobEffects.BLINDNESS) || livingentity.hasEffect(MobEffects.DARKNESS);
	}
	
	protected boolean isFoggy(Camera camera)
	{
		Vec3 cameraPos = camera.getPosition();
		boolean isFoggy = this.minecraft.level.effects().isFoggyAt(Mth.floor(cameraPos.x()), Mth.floor(cameraPos.y())) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
		if(isFoggy)
			return true;
		
		FogType fogtype = camera.getFluidInCamera();
		return fogtype == FogType.POWDER_SNOW || fogtype == FogType.LAVA || this.doesMobEffectBlockSky(camera);
	}
	
	protected void createDarkSky()
	{
		if(darkBuffer != null)
			darkBuffer.close();
		
		darkBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
		
		MeshData mesh = buildSkyDisc(bufferbuilder, -16.0F);
		darkBuffer.bind();
		darkBuffer.upload(mesh);
		VertexBuffer.unbind();
	}

	protected void createLightSky()
	{
		if(skyBuffer != null)
			skyBuffer.close();
		
		skyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
		
		MeshData mesh = buildSkyDisc(bufferbuilder, 16.0F);
		skyBuffer.bind();
		skyBuffer.upload(mesh);
		VertexBuffer.unbind();
	}
	
	// Create the dark blue or black shading in the sky / the black circle below the horizon when in the void or below ground
	public static MeshData buildSkyDisc(BufferBuilder builder, float scale)
	{
		// invert the base radius based on the sign of scale to ensure the faces are facing the correct way.
		float baseRadius = 512.0F;
		float invertibleBaseRadius = Math.signum(scale) * baseRadius;
		RenderSystem.setShader(GameRenderer::getPositionShader);
		// Create a circle with it's vertex centered by the player
		// the circle is further above / below the horizon depending on the scale
		builder.addVertex(0.0F, scale, 0.0F);
		// Create the circle
		for (int i = -180; i <= 180; i += 45) {
			float radians = (float) Math.toRadians(i);
			
			builder.addVertex(invertibleBaseRadius * Mth.cos(radians),
					scale,
					baseRadius * Mth.sin(radians));
		}
		
		return builder.build();
	}
	
	protected void createStars(long seed, int numberOfStars)
	{
		Tesselator tesselator = Tesselator.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionShader);
		if(this.starBuffer != null)
			this.starBuffer.close();

		this.starBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
		MeshData mesh = this.drawStars(tesselator, seed, numberOfStars);
		this.starBuffer.bind();
		this.starBuffer.upload(mesh);
		VertexBuffer.unbind();
	}
	
	protected MeshData drawStars(Tesselator tesselator, long seed, int numberOfStars)
	{
		RandomSource randomsource = RandomSource.create(seed);
		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
		
		for(int i = 0; i < numberOfStars; ++i)
		{
			/* 
			 * Since I couldn't find any explanation for how this works,
			 * I've taken it upon myself to explain everything in as much detail as I can
			 */

			// This generates random coordinates for the Star close to the camera
			double x = (double) (randomsource.nextFloat() * 2.0F - 1.0F);
			double y = (double) (randomsource.nextFloat() * 2.0F - 1.0F);
			double z = (double) (randomsource.nextFloat() * 2.0F - 1.0F);
			double starSize = (double) (0.15F + randomsource.nextFloat() * 0.1F); // This randomizes the Star size
			double distance = x * x + y * y + z * z;
			
			if(distance < 1.0D && distance > 0.01D)
			{
				distance = 1.0D / Math.sqrt(distance);
				x *= distance;
				y *= distance;
				z *= distance;
				
				// This effectively pushes the Star away from the camera
				double starX = x * 100.0D;
				double starY = y * 100.0D;
				double starZ = z * 100.0D;
				
				/* These very obviously represent Spherical Coordinates (r, theta, phi)
				 * 
				 * Spherical equations (adjusted for Minecraft, since usually +Z is up, while in Minecraft +Y is up):
				 * 
				 * r = sqrt(x * x + y * y + z * z)
				 * theta = arctan2(x, z)
				 * phi = arccos(y / r)
				 * 
				 * x = r * sin(phi) * sin(theta)
				 * y = r * cos(phi)
				 * z = r * sin(phi) * cos(theta)
				 * 
				 * Polar equations
				 * z = r * cos(theta)
				 * x = r * sin(theta)
				 */
				
				double sphericalTheta = Math.atan2(x, z);
				double sinTheta = Math.sin(sphericalTheta);
				double cosTheta = Math.cos(sphericalTheta);
				
				double xzLength = Math.sqrt(x * x + z * z);
				double sphericalPhi = Math.atan2(xzLength, y);
				double sinPhi = Math.sin(sphericalPhi);
				double cosPhi = Math.cos(sphericalPhi);
				
				// sin and cos are used to effectively clamp the random number between two values without actually clamping it,
				// which would result in some awkward lines as Stars would be brought to the clamped values
				// Both affect Star size and rotation
				double random = randomsource.nextDouble() * Math.PI * 2.0D;
				double sinRandom = Math.sin(random);
				double cosRandom = Math.cos(random);
				
				// This loop creates the 4 corners of a Star
				for(int j = 0; j < 4; ++j)
				{
					/* Bitwise AND is there to multiply the size by either 1 or -1 to reach this effect:
					 * Where a coordinate is written as (A,B)
					 * 		(-1,1)		(1,1)
					 * 		x-----------x
					 * 		|			|
					 * 		|			|
					 * 		|			|
					 * 		|			|
					 * 		x-----------x
					 * 		(-1,-1)		(1,-1)
					 * 								|	A	B
					 * 0 & 2 = 000 & 010 = 000 = 0	|	x
					 * 1 & 2 = 001 & 010 = 000 = 0	|	x	x
					 * 2 & 2 = 010 & 010 = 010 = 2	|	x	x
					 * 3 & 2 = 011 & 010 = 010 = 2	|	x	x
					 * 4 & 2 = 100 & 000 = 000 = 0	|		x
					 * 
					 * After you subtract 1 one from each of them, you get this:
					 * j:	0	1	2	3
					 * --------------------
					 * A:	-1	-1	1	1
					 * B:	-1	1	1	-1
					 * Which corresponds to:
					 * UV:	00	01	11	10
					 */
					double aLocation = (double) ((j & 2) - 1) * starSize;
					double bLocation = (double) ((j + 1 & 2) - 1) * starSize;
					
					/* These are the values for cos(random) = sin(random)
					 * (random is simply there to randomize the star rotation)
					 * j:	0	1	2	3
					 * -------------------
					 * A:	0	-2	0	2
					 * B:	-2	0	2	0
					 * 
					 * A and B are there to create a diamond effect on the Y-axis and X-axis respectively
					 * (Pretend it's not as stretched as the slashes make it looked)
					 * Where a coordinate is written as (B,A)
					 * 
					 * 			(0,2)
					 * 			/\
					 * 	 (-2,0)/  \(2,0)
					 * 		   \  /
					 * 			\/
					 * 			(0,-2)
					 * 
					 */
					double height = aLocation * cosRandom - bLocation * sinRandom;
					double width = bLocation * cosRandom + aLocation * sinRandom;
					
					double heightProjectionY = height * sinPhi; // Y projection of the Star's height
					
					double heightProjectionXZ = - height * cosPhi; // If the Star is angled, the XZ projected height needs to be subtracted from both X and Z 
					
					/* 
					 * projectedX:
					 * Projected height is projected onto the X-axis using sin(theta) and then gets subtracted (added because it's already negative)
					 * Width is projected onto the X-axis using cos(theta) and then gets subtracted
					 * 
					 * projectedZ:
					 * Width is projected onto the Z-axis using sin(theta)
					 * Projected height is projected onto the Z-axis using cos(theta) and then gets subtracted (added because it's already negative)
					 * 
					 */
					double projectedX = heightProjectionXZ * sinTheta - width * cosTheta;
					double projectedZ = width * sinTheta + heightProjectionXZ * cosTheta;
					
					builder.addVertex((float) (starX + projectedX), (float) (starY + heightProjectionY), (float) (starZ + projectedZ)).setColor(190, 160, 0, 0xAA);
				}
			}
		}
		return builder.build();
	}
	
	protected void renderStars(ClientLevel level, float partialTicks, float rain, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog)
	{
		float realBrightness = level.getStarBrightness(partialTicks) * rain;
		
        if(realBrightness > 0.0F)
        {
           RenderSystem.setShaderColor(realBrightness, realBrightness, realBrightness, realBrightness);
           FogRenderer.setupNoFog();
           this.starBuffer.bind();
           this.starBuffer.drawWithShader(modelViewMatrix, projectionMatrix, GameRenderer.getPositionShader());
           VertexBuffer.unbind();
           setupFog.run();
        }
	}
	
	public static float[] moveSpherical(float offsetX, float offsetY, float r, double theta, double phi)
	{
		double x = r * Math.sin(phi) * Math.sin(theta);
		double y = r * Math.cos(phi);
		double z = r * Math.sin(phi) * Math.cos(theta);
		
		x += - offsetY * Math.cos(phi) * Math.sin(theta) - offsetX * Math.cos(theta);
		y += offsetY * Math.sin(phi);
		z += - offsetY * Math.cos(phi) * Math.cos(theta) + offsetX * Math.sin(theta);
		
		return new float[] {(float) x, (float) y, (float) z};
	}
	
	public void createCelestialObject(Tesselator tesselator, Matrix4f lastMatrix, ResourceLocation location,
			float size, float distance, float[] uv)
	{
		this.createCelestialObject(tesselator, lastMatrix, location, size, distance, 0.0F, 0.0F, uv);
	}
	
	public void createCelestialObject(Tesselator tesselator, Matrix4f lastMatrix, ResourceLocation location,
			float size, float distance, float theta, float phi, float[] uv)
	{
		float[] u0v0 = moveSpherical(-size, -size, distance, theta, phi);
		float[] u1v0 = moveSpherical(size, -size, distance, theta, phi);
		float[] u1v1 = moveSpherical(size, size, distance, theta, phi);
		float[] u0v1 = moveSpherical(-size, size, distance, theta, phi);
		
		RenderSystem.setShaderTexture(0, location);
		BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(lastMatrix, u0v0[0], u0v0[1], u0v0[2]).setUv(uv[0], uv[1]);
        bufferbuilder.addVertex(lastMatrix, u1v0[0], u1v0[1], u1v0[2]).setUv(uv[2], uv[1]);
        bufferbuilder.addVertex(lastMatrix, u1v1[0], u1v1[1], u1v1[2]).setUv(uv[2], uv[3]);
        bufferbuilder.addVertex(lastMatrix, u0v1[0], u0v1[1], u0v1[2]).setUv(uv[0], uv[3]);
        BufferUploader.drawWithShader(bufferbuilder.build());
	}
	
	protected void renderSun(Tesselator tesselator, Matrix4f lastMatrix, float size)
	{
		this.createCelestialObject(tesselator, lastMatrix, SUN_LOCATION,
				size, 100.0F, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

		RenderSystem.enableBlend();
	}
	
	protected void renderSun(Tesselator tesselator, Matrix4f lastMatrix, float size, float theta, float phi)
	{
		this.createCelestialObject(tesselator, lastMatrix, SUN_LOCATION,
				size, 100.0F, theta, phi, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

		RenderSystem.enableBlend();
	}
	
	protected void renderBlackHole(Tesselator tesselator, Matrix4f lastMatrix, float size, float theta, float phi)
	{
		this.createCelestialObject(tesselator, lastMatrix, BLACK_HOLE_HALO_LOCATION,
				size, 100.0F, theta, phi, new float[] {0.0F, 0.0F, 1.0F, 1.0F});
		
        RenderSystem.disableBlend();
        
		this.createCelestialObject(tesselator, lastMatrix, BLACK_HOLE_LOCATION,
				size, 100.0F, theta, phi, new float[] {0.0F, 0.0F, 1.0F, 1.0F});

		RenderSystem.enableBlend();
	}
	
	protected void renderMoon(Tesselator tesselator, Matrix4f lastMatrix, float size, int phase, float theta, float phi)
	{
        int x = phase % 4;
        int y = phase / 4 % 2;
        float xStart = (float)(x + 0) / 4.0F;
        float yStart = (float)(y + 0) / 2.0F;
        float xEnd = (float)(x + 1) / 4.0F;
        float yEnd = (float)(y + 1) / 2.0F;
        
		this.createCelestialObject(tesselator, lastMatrix, MOON_HALO_LOCATION,
				size, 100.0F, theta, phi, new float[] {0.0F, 0.0F, 1.0F, 1.0F});
		
        RenderSystem.disableBlend();
        
		this.createCelestialObject(tesselator, lastMatrix, MOON_LOCATION,
				size / 4, 100.0F, theta, phi, new float[] {xStart, yStart, xEnd, yEnd});

		RenderSystem.enableBlend();
	}
	
	protected void renderSunrise(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator)
	{
		float[] sunriseColor = level.effects().getSunriseColor(level.getTimeOfDay(partialTicks), partialTicks);
		if(sunriseColor != null)
		{
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			final Matrix4f transformedModelView = new Matrix4f(modelViewMatrix);
			transformedModelView.rotate(Axis.XP.rotationDegrees(90.0F));
			float sunAngle = Mth.sin(level.getSunAngle(partialTicks)) < 0.0F ? 180.0F : 0.0F;
			transformedModelView.rotate(Axis.ZP.rotationDegrees(sunAngle));
			transformedModelView.rotate(Axis.ZP.rotationDegrees(90.0F));
			float sunriseR = sunriseColor[0];
			float sunriseG = sunriseColor[1];
			float sunriseB = sunriseColor[2];
			float sunriseA = sunriseColor[2];
			Matrix4f sunriseMatrix = transformedModelView;
			BufferBuilder bufferbuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.addVertex(sunriseMatrix, 0.0F, 100.0F, 0.0F).setColor(sunriseR, sunriseG, sunriseB, sunriseA);
			
			for(int i = 0; i <= 16; ++i)
			{
				// Create a circle to act as the slanted portion of the sunrise
				float rotation = (float)i * ((float)Math.PI * 2F) / 16.0F;
				float x = Mth.sin(rotation);
				float y = Mth.cos(rotation);
				// The Z coordinate is multiplied by -y to make the circle angle upwards towards the sun
				bufferbuilder.addVertex(sunriseMatrix, x * 120.0F, y * 120.0F, -y * 40.0F * sunriseA).setColor(sunriseR, sunriseG, sunriseB, 0.0F);
			}
			
			BufferUploader.drawWithShader(bufferbuilder.build());
		}
	}
	
	protected abstract void renderCelestials(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain);
	
	protected void renderEcliptic(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, Runnable setupFog, Tesselator tesselator, float rain)
	{
		final Matrix4f transformedModelView = new Matrix4f(modelViewMatrix);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, rain);
		transformedModelView.rotate(Axis.YP.rotationDegrees(-90.0F));
		transformedModelView.rotate(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTicks) * 360.0F));
        
        this.renderStars(level, partialTicks, rain, transformedModelView, projectionMatrix, setupFog);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        
        this.renderCelestials(level, partialTicks, transformedModelView, projectionMatrix, setupFog, tesselator, rain);
	}
	
	public void renderSky(ClientLevel level, float partialTicks, Matrix4f modelViewMatrix, Camera camera, Matrix4f projectionMatrix, Runnable setupFog)
	{
		setupFog.run();
		
		if(this.isFoggy(camera))
			return;
		
		Vec3 skyColor = level.getSkyColor(this.minecraft.gameRenderer.getMainCamera().getPosition(), partialTicks);
		float skyX = (float)skyColor.x;
        float skyY = (float)skyColor.y;
        float skyZ = (float)skyColor.z;
        FogRenderer.levelFogColor();
		Tesselator tesselator = Tesselator.getInstance();
		RenderSystem.depthMask(false);
		RenderSystem.setShaderColor(skyX, skyY, skyZ, 1.0F);
		ShaderInstance shaderinstance = RenderSystem.getShader();
		this.skyBuffer.bind();
		this.skyBuffer.drawWithShader(modelViewMatrix, projectionMatrix, shaderinstance);
		VertexBuffer.unbind();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		
		this.renderSunrise(level, partialTicks, modelViewMatrix, projectionMatrix, setupFog, tesselator);
		
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		
		float rain = 1.0F - level.getRainLevel(partialTicks);
		
		this.renderEcliptic(level, partialTicks, modelViewMatrix, projectionMatrix, setupFog, tesselator, rain);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        
        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
        double height = this.minecraft.player.getEyePosition(partialTicks).y - level.getLevelData().getHorizonHeight(level);
        if(height < 0.0D)
        {
			final Matrix4f transformedModelView = new Matrix4f(modelViewMatrix);
			transformedModelView.translate(0.0F, 12.0F, 0.0F);
        	this.darkBuffer.bind();
        	this.darkBuffer.drawWithShader(transformedModelView, projectionMatrix, shaderinstance);
        	VertexBuffer.unbind();
        }
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.depthMask(true);
	}
}
