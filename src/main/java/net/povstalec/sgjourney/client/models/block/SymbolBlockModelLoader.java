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

public class SymbolBlockModelLoader extends SymbolModelLoader<SymbolBlockModelLoader.SymbolBlockModelGeometry>
{
	public static final String SYMBOL_BLOCK_LOADER = "symbol_block_loader";
	
	@Override
	public SymbolBlockModelGeometry getGeometry(JsonObject jsonObject, JsonDeserializationContext deserializationContext, List<BlockElement> elements, int symbolTint)
	{
		return new SymbolBlockModelGeometry(elements, symbolTint);
	}
	
	public static void register(ModelRegistryEvent event)
	{
		ModelLoaderRegistry.registerLoader(StargateJourney.sgjourneyLocation(SYMBOL_BLOCK_LOADER), new CableModelLoader());
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
	
	}
	
	
	public static class SymbolBlockModelGeometry extends SymbolModelGeometry<SymbolBlockModelGeometry>
	{
		protected int symbolNumber;
		protected ResourceLocation symbol;
		
		public SymbolBlockModelGeometry(List<BlockElement> elements, int symbolTint)
		{
			super(elements, symbolTint);
			
			this.symbolNumber = -1;
		}
		
		public SymbolBlockModelGeometry(List<BlockElement> elements, int symbolTint, int symbolNumber, ResourceLocation symbol)
		{
			super(elements, symbolTint);
			
			this.symbolNumber = symbolNumber;
			this.symbol = symbol;
		}
		
		public SymbolBlockModelGeometry withSymbolNumberAndTexture(int symbolNumber, ResourceLocation symbol)
		{
			if(symbolNumber >= 0 && symbol != null)
				return new SymbolBlockModelGeometry(this.elements, this.symbolTint, symbolNumber, symbol);
			else
				return new SymbolBlockModelGeometry(this.elements, this.symbolTint);
		}
		
		/*@Override
		public BakedModel bake(IGeometryBakingContext context, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			if(symbolNumber >= 0)
			{
				TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
				
				var itemContext = StandaloneGeometryBakingContext.builder(context).build(modelLocation);
				var modelBuilder = CompositeModel.Baked.builder(context, particle, new SymbolBlockItemOverrides(overrides, baker, itemContext, this), context.getTransforms());
				
				// Symbol Layer
				TextureAtlasSprite symbolSprite = SymbolBlockBakedModel.getSymbolSprite(symbolNumber, symbol);
				if(symbolSprite != null)
					modelBuilder.addQuads(new RenderTypeGroup(RenderType.translucent(), Sheets.translucentCullBlockSheet()), SymbolBlockBakedModel.makeSymbolQuad(Direction.NORTH, Orientation.REGULAR, symbolSprite, symbolTint));
				
				// Block Layer
				modelBuilder.addLayer(super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this), modelLocation));
				
				return modelBuilder.build();
			}
			else
				return super.bake(context, baker, spriteGetter, modelState, new SymbolBlockItemOverrides(overrides, baker, context, this), modelLocation);
		}*/
		
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
		
		@Override
		protected IModelBuilder<?> getBuilder(boolean useAmbientOcclusion, boolean canUseBlockLight, boolean isGui3d,
											  ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle,
											  int symbolTint)
		{
			if(symbolNumber >= 0)
				return SGJourneyModelBuilder.ofSymbol(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, symbolTint, symbolNumber, symbol);
			else
				return SGJourneyModelBuilder.ofSymbol(useAmbientOcclusion, canUseBlockLight, isGui3d, transforms, overrides, particle, symbolTint);
		}
	}
}
