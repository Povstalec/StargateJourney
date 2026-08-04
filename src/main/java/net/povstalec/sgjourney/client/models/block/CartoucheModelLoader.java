package net.povstalec.sgjourney.client.models.block;

import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.povstalec.sgjourney.StargateJourney;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class CartoucheModelLoader extends SymbolModelLoader<CartoucheModelLoader.CartoucheModelGeometry>
{
	public static final String CARTOUCHE_LOADER = "cartouche_loader";
	
	@Override
	public CartoucheModelGeometry getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint)
	{
		return new CartoucheModelGeometry(elements, symbolTint);
	}
	
	public static void register(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(StargateJourney.sgjourneyLocation(CARTOUCHE_LOADER), new CableModelLoader());
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
	
	}
	
	
	public static class CartoucheModelGeometry extends SymbolModelGeometry<CartoucheModelGeometry>
	{
		public CartoucheModelGeometry(List<BlockElement> elements, int symbolTint)
		{
			super(elements, symbolTint);
		}
		
		@Override
		protected IModelBuilder<?> getBuilder(boolean useAmbientOcclusion, boolean canUseBlockLight, boolean isGui3d,
											  ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											  int symbolTint)
		{
			return SGJourneyModelBuilder.ofCartouche(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, symbolTint);
		}
		
		@Override
		public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			Set<Material> textures = Sets.newHashSet();
			if(context.isTexturePresent("particle"))
				textures.add(context.resolveTexture("particle"));
			for(BlockElement part : elements)
			{
				for(BlockElementFace face : part.faces.values())
				{
					Material texture = context.resolveTexture(face.texture);
					if(texture.texture().equals(MissingTextureAtlasSprite.getLocation()))
						missingTextureErrors.add(Pair.of(face.texture, context.getModelName()));
					textures.add(texture);
				}
			}
			
			return textures;
		}
	}
}
