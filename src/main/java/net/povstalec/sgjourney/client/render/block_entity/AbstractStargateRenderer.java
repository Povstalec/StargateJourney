package net.povstalec.sgjourney.client.render.block_entity;

import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.models.IrisModel;
import net.povstalec.sgjourney.client.models.ShieldModel;
import net.povstalec.sgjourney.client.models.WormholeModel;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariant;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public abstract class AbstractStargateRenderer<StargateEntity extends AbstractStargateEntity, Variant extends ClientStargateVariant> implements BlockEntityRenderer<StargateEntity>
{
	private static Minecraft minecraft = Minecraft.getInstance();
	
	private final ResourceLocation stargateLocation;
	
	protected final WormholeModel wormholeModel;
	protected final ShieldModel shieldModel;
	protected final IrisModel irisModel;
	
	private final RandomSource randomsource = RandomSource.create();
	
	public AbstractStargateRenderer(BlockEntityRendererProvider.Context context, ResourceLocation stargateLocation,
			float maxDefaultDistortion, boolean renderWhenOpen, float maxOpenIrisDegrees)
	{
		this.stargateLocation = stargateLocation;
		
		this.shieldModel = new ShieldModel();
		this.irisModel = new IrisModel(renderWhenOpen, maxOpenIrisDegrees);
		this.wormholeModel = new WormholeModel(maxDefaultDistortion);
	}
	
	public ResourceLocation getResourceLocation()
	{
		return stargateLocation;
	}
	
	@Override
	public int getViewDistance()
	{
		return 128;
	}
	
	/**
	 * Method for getting the common variant of the Stargate
	 * @param stargate
	 * @return
	 */
	public static Optional<StargateVariant> getVariant(AbstractStargateEntity stargate)
	{
		Optional<StargateVariant> optional = Optional.empty();
		
		if(!ClientStargateConfig.stargate_variants.get())
			return optional;
		
		String variantString = stargate.getVariant();
		
		if(variantString.equals(StargateJourney.EMPTY))
			return optional;
		
		ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
		
		optional = Optional.ofNullable(variantRegistry.get(new ResourceLocation(variantString)));
		
		return optional;
	}
	
	/**
	 * Method for getting the client variant of the Stargate
	 * @param stargate
	 * @return
	 */
	protected abstract Variant getClientVariant(StargateEntity stargate);
	
	protected void renderWormhole(AbstractStargateEntity stargate, Variant stargateVariant, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
		if(stargate.isConnected())
	    	this.wormholeModel.renderWormhole(stargate, stack, source, stargateVariant.getWormhole(), combinedLight, combinedOverlay);
	}
	
	protected void renderCover(AbstractStargateEntity stargate, PoseStack stack, MultiBufferSource source, int combinedLight, int combinedOverlay)
	{
	    for(Map.Entry<StargatePart, BlockState> entry : stargate.blockCover.blockStates.entrySet())
	    {
	    	renderCoverBlock(stargate, entry.getValue(), entry.getKey(), stack, source, combinedOverlay);
	    }
	}
	
	protected void renderCoverBlock(AbstractStargateEntity stargate, BlockState state, StargatePart part, PoseStack stack, MultiBufferSource source, int combinedOverlay)
	{
		Level level = stargate.getLevel();
		Direction direction = stargate.getDirection();
		Orientation orientation = stargate.getOrientation();
		
		if(direction != null && orientation != null)
		{
			Vec3 relativeBlockPos = part.getRelativeRingPos(stargate.getBlockPos(), direction, orientation);
			BlockPos absolutePos = part.getRingPos(stargate.getBlockPos(), stargate.getDirection(), stargate.getOrientation());
			
			stack.pushPose();
			
			stack.translate(relativeBlockPos.x(), relativeBlockPos.y(), relativeBlockPos.z());
			BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();//Minecraft.getInstance().getBlockColors().
			//dispatcher.renderSingleBlock(state, stack, source, LevelRenderer.getLightColor(level, absolutePos), combinedOverlay, ModelData.EMPTY, null);
			
			
			BakedModel model = dispatcher.getBlockModel(state);
			for(RenderType renderType : model.getRenderTypes(state, randomsource, ModelData.EMPTY))
			{
				dispatcher.renderBatched(state, absolutePos, level, stack, source.getBuffer(renderType), true, randomsource, model.getModelData(level, absolutePos, state, ModelData.EMPTY), null);
			}
			
			stack.popPose();
		}
	}
	
	protected boolean canSink(AbstractStargateEntity stargate)
	{
	    return stargate.blockCover.canSinkGate;
	}
}
