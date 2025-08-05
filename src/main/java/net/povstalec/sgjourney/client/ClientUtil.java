package net.povstalec.sgjourney.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ClientUtil
{
	private static final ResourceLocation UNIVERSAL = StargateJourney.sgjourneyLocation("universal");
	
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	public static PointOfOrigin getPointOfOrigin(ResourceLocation pointOfOrigin)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		if(pointOfOrigin != null)
			return registry.get(pointOfOrigin);
		
		return registry.get(UNIVERSAL);
	}
	
	public static Symbols getSymbols(ResourceLocation symbols)
	{
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<Symbols> registry = registries.registryOrThrow(Symbols.REGISTRY_KEY);
		
		if(symbols != null)
			return registry.get(symbols);
		
		return registry.get(UNIVERSAL);
	}
	
	//============================================================================================
	//*******************************************Models*******************************************
	//============================================================================================
	
	public static void addVertex(VertexConsumer consumer, TextureAtlasSprite sprite, Vec3 pos, Vec3 normal, float u, float v)
	{
		consumer.addVertex((float) pos.x(), (float) pos.y(), (float) pos.z());
		consumer.setUv(sprite.getU(u / 16F), sprite.getV(v / 16F));
		consumer.setUv2(0, 0);
		consumer.setColor(1.0F, 1.0F, 1.0F, 1.0F);
		consumer.setNormal((float) normal.x(), (float) normal.y(), (float) normal.z());
	}
	
	public static BakedQuad bakeQuad(TextureAtlasSprite sprite, Vec3 vec1, Vec3 vec2, Vec3 vec3, Vec3 vec4)
	{
		return bakeQuad(sprite, vec1, vec2, vec3, vec4, 0, 0, 16, 16);
	}
	
	public static BakedQuad bakeQuad(TextureAtlasSprite sprite, Vec3 vec1, Vec3 vec2, Vec3 vec3, Vec3 vec4, float uStart, float vStart, float uEnd, float vEnd)
	{
		Vec3 normal = vec3.subtract(vec2).cross(vec1.subtract(vec2)).normalize();
		
		QuadBakingVertexConsumer consumer = new QuadBakingVertexConsumer();
		consumer.setSprite(sprite);
		consumer.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
		
		addVertex(consumer, sprite, vec1, normal, uStart, vEnd);
		addVertex(consumer, sprite, vec2, normal, uEnd, vEnd);
		addVertex(consumer, sprite, vec3, normal, uEnd, vStart);
		addVertex(consumer, sprite, vec4, normal, uStart, vStart);
		
		return consumer.bakeQuad();
	}
}
