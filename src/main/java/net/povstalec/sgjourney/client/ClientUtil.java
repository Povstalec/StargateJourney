package net.povstalec.sgjourney.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientPointOfOrigin;
import net.povstalec.sgjourney.client.resourcepack.symbols.ClientSymbols;
import net.povstalec.sgjourney.common.misc.ColorUtil;

import java.util.List;

public class ClientUtil
{
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	public static TextureAtlasSprite getTexture(ResourceLocation texture)
	{
		return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(texture);
	}
	
	public static void renderPointOfOrigin(Matrix4f matrix4f, float xStart, float yStart, float xEnd, float yEnd, ClientPointOfOrigin pointOfOrigin, ColorUtil.RGBA rgba)
	{
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha());
		// Using extended texture instead of TextureAtlasSprite here because for some reason, it appears as though some GUI scales are unable to properly deal with 2:1 ratio atlases
		// When 2:1 ratio atlas is used, something akin to floating point error seems to show up, rendering a small portion of the neighboring texture on the U-axis
		RenderSystem.setShaderTexture(0, pointOfOrigin.getExtendedTexture());
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, xStart, yStart, 0F).uv(0F, 0F).endVertex();
		bufferbuilder.vertex(matrix4f, xStart, yEnd, 0F).uv(0F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yEnd, 0F).uv(1F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yStart, 0F).uv(1F, 0F).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}
	
	public static void renderSymbol(Matrix4f matrix4f, float xStart, float yStart, float xEnd, float yEnd, ClientSymbols symbols, int symbol, ColorUtil.RGBA rgba)
	{
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(rgba.red(), rgba.green(), rgba.blue(), rgba.alpha());
		// Using extended texture instead of TextureAtlasSprite here because for some reason, it appears as though some GUI scales are unable to properly deal with 2:1 ratio atlases
		// When 2:1 ratio atlas is used, something akin to floating point error seems to show up, rendering a small portion of the neighboring texture on the U-axis
		RenderSystem.setShaderTexture(0, symbols.getExtendedSymbolTexture(symbol));
		
		BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
		bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
		bufferbuilder.vertex(matrix4f, xStart, yStart, 0F).uv(0F, 0F).endVertex();
		bufferbuilder.vertex(matrix4f, xStart, yEnd, 0F).uv(0F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yEnd, 0F).uv(1F, 1F).endVertex();
		bufferbuilder.vertex(matrix4f, xEnd, yStart, 0F).uv(1F, 0F).endVertex();
		bufferbuilder.end();
		BufferUploader.end(bufferbuilder);
	}
	
	//============================================================================================
	//*******************************************Models*******************************************
	//============================================================================================
	
	public static void addVertex(VertexConsumer consumer, TextureAtlasSprite sprite, Vec3 pos, Vec3 normal, float u, float v)
	{
		consumer.vertex(pos.x(), pos.y(), pos.z()).uv(sprite.getU(u), sprite.getV(v)).uv2(0, 0).color(1.0F, 1.0F, 1.0F, 1.0F).normal((float) normal.x(), (float) normal.y(), (float) normal.z()).endVertex();
	}
	
	public static void addVertex(BakedQuadBuilder builder, TextureAtlasSprite sprite, Vec3 pos, Vec3 normal, float u, float v)
	{
		List<VertexFormatElement> elements = builder.getVertexFormat().getElements();
		for(int i = 0; i < elements.size(); i++)
		{
			VertexFormatElement element = elements.get(i);
			switch(element.getUsage())
			{
				case POSITION -> builder.put(i, (float) pos.x(), (float) pos.y(), (float) pos.z());
				case COLOR -> builder.put(i, 1, 1, 1, 1);
				case UV -> addUV(i, builder, sprite, u, v, element.getIndex());
				case NORMAL -> builder.put(i, (float) normal.x(), (float) normal.y(), (float) normal.z());
				default -> builder.put(i);
			}
		}
	}
	
	public static void addUV(int i, BakedQuadBuilder builder, TextureAtlasSprite sprite, float u, float v, int elementIndex)
	{
		switch(elementIndex)
		{
			case 0 -> builder.put(i, sprite.getU(u), sprite.getV(v)); // UV0
			case 2 -> builder.put(i, (short) 0, (short) 0); // UV2
			default -> builder.put(i); // UV1
		}
	}
	
	public static BakedQuad bakeQuad(TextureAtlasSprite sprite, Vec3 vec1, Vec3 vec2, Vec3 vec3, Vec3 vec4)
	{
		return bakeQuad(sprite, vec1, vec2, vec3, vec4, 0, 0, 16, 16);
	}
	
	public static BakedQuad bakeQuad(TextureAtlasSprite sprite, Vec3 vec1, Vec3 vec2, Vec3 vec3, Vec3 vec4, float uStart, float vStart, float uEnd, float vEnd)
	{
		Vec3 normal = vec3.subtract(vec2).cross(vec1.subtract(vec2)).normalize();
		
		BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
		
		builder.setTexture(sprite);
		builder.setQuadOrientation(Direction.getNearest(normal.x, normal.y, normal.z));
		
		addVertex(builder, sprite, vec1, normal, uStart, vEnd);
		addVertex(builder, sprite, vec2, normal, uEnd, vEnd);
		addVertex(builder, sprite, vec3, normal, uEnd, vStart);
		addVertex(builder, sprite, vec4, normal, uStart, vStart);
		
		return builder.build();
	}
}
