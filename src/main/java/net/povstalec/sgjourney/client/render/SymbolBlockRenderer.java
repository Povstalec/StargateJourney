package net.povstalec.sgjourney.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.common.block_entities.symbols.SymbolBlockEntity;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;

@OnlyIn(Dist.CLIENT)
public abstract class SymbolBlockRenderer
{
	protected final ModelPart symbol_part;
	
	public SymbolBlockRenderer(BlockEntityRendererProvider.Context context)
	{
		ModelPart symbol_part = context.bakeLayer(Layers.SYMBOL_BLOCK_LAYER);
		this.symbol_part = symbol_part;
	}
	
	public static LayerDefinition createBlockLayer()
	{
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		
		partdefinition.addOrReplaceChild("symbol", CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), 
				PartPose.rotation((float)Math.toRadians(180), 0.0F, 0.0F));
		
		return LayerDefinition.create(meshdefinition, 16, 16);
	}
	
	protected PointOfOrigin getPointOfOrigin(SymbolBlockEntity symbolBlock)
	{
		String pointOfOrigin = symbolBlock.symbol;
		
		Minecraft minecraft = Minecraft.getInstance();
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<PointOfOrigin> registry = registries.registryOrThrow(PointOfOrigin.REGISTRY_KEY);
		
		return registry.get(new ResourceLocation(pointOfOrigin));
	}
	
}
