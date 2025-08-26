package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.povstalec.sgjourney.common.advancements.WormholeTravelCriterion;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.config.CommonIrisConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.DamageSourceInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.WormholeTravel;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.Nullable;

public class Wormhole
{
	public static final String EVENT_DECONSTRUCTING_ENTITY = "stargate_deconstructing_entity";
	public static final String EVENT_RECONSTRUCTING_ENTITY = "stargate_reconstructing_entity";
	public static final String EVENT_IRIS_THUD = "iris_thud";
	
	public static final double INNER_RADIUS = 2.5;
	public static final double INNER_RADIUS_SQR = INNER_RADIUS * INNER_RADIUS;
	
	protected Map<Integer, Vec3> entityLocations = new HashMap<>();
	
	//============================================================================================
	//***************************************Transport out****************************************
	//============================================================================================
	
	protected boolean shouldWormhole(Vec3 center, Entity traveler, double oldTravelerX, double travelerX, double momentumX)
	{
		if(traveler.isPassenger())
			return false;
		
		Vec3 travelerPos = traveler.getBoundingBox().getCenter();
		if(center.distanceToSqr(travelerPos) > INNER_RADIUS_SQR)
			return false;
		
		return oldTravelerX > 0 && travelerX < 0 && momentumX < 0;
	}
	
	protected boolean wormholeEntity(MinecraftServer server, Stargate initialStargate, Stargate destinationStargate, StargateInfo.WormholeTravel twoWayWormhole, Vec3 centerPos, Vec3 forward, Vec3 up, Vec3 right, Map<Integer, Vec3> entityLocations, Entity traveler)
	{
		Vec3 relativePosition = CoordinateHelper.StargateCoords.fromStargateCoords(traveler.position().subtract(centerPos), forward, up, right, INNER_RADIUS);
		Vec3 oldRelativePos = this.entityLocations.get(traveler.getId());
		
		if(oldRelativePos != null)
		{
			Vec3 relativeMomentum = relativePosition.subtract(oldRelativePos);
			
			if(shouldWormhole(centerPos, traveler, oldRelativePos.x(), relativePosition.x(), relativeMomentum.x()))
			{
				playWormholeSound(traveler.getLevel(), traveler);
				
				if(twoWayWormhole == WormholeTravel.ENABLED || (twoWayWormhole == WormholeTravel.CREATIVE_ONLY && traveler instanceof Player player && (player.isCreative() || player.isSpectator())))
				{
					Vec3 relativeLookAngle = CoordinateHelper.StargateCoords.fromStargateCoords(traveler.getLookAngle(), forward, up, right);
					
					if(!SGJourneyEvents.onWormholeTravel(server, initialStargate, destinationStargate, traveler, twoWayWormhole) && destinationStargate.receiveTraveler(server, initialStargate, traveler, relativePosition, relativeMomentum, relativeLookAngle))
					{
						deconstructEvent(server, initialStargate, traveler, false);
						return true;
					}
				}
				else
					handleReverseWormhole(server, initialStargate, traveler);
			}
		}
		
		entityLocations.put(traveler.getId(), relativePosition);
		return false;
	}
	
	public boolean wormholeEntities(MinecraftServer server, Stargate initialStargate, Stargate destinationStargate, StargateInfo.WormholeTravel twoWayWormhole, Vec3 centerPos, Vec3 forward, Vec3 up, Vec3 right, List<Entity> wormholeCandidates)
	{
		boolean used = false;
		Map<Integer, Vec3> entityLocations = new HashMap<>();
		
		for(Entity traveler : wormholeCandidates)
		{
			if(wormholeEntity(server, initialStargate, destinationStargate, twoWayWormhole, centerPos, forward, up, right, entityLocations, traveler))
				used = true;
		}
		
		this.entityLocations = entityLocations;
		return used;
	}
	
	public void handleReverseWormhole(MinecraftServer server, Stargate initialStargate, Entity traveler)
	{
		recursiveExecute(traveler, (entity) ->
		{
			if(CommonStargateConfig.reverse_wormhole_kills.get())
			{
				if(entity.isAlive())
				{
					if(entity instanceof ServerPlayer player && (player.isCreative() || player.isSpectator()))
						player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
					else
					{
						if(entity instanceof LivingEntity livingEntity)
						{
							if(entity instanceof ServerPlayer player)
								player.awardStat(StatisticsInit.TIMES_KILLED_BY_WORMHOLE.get());
							
							livingEntity.die(DamageSourceInit.REVERSE_WORMHOLE);
						}
						entity.kill();
					}
				}
			}
			else
			{
				if(entity instanceof ServerPlayer player)
					player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
			}
			
			deconstructEvent(server, initialStargate, entity, true);
		});
	}
	
	//============================================================================================
	//*************************************Receive transport**************************************
	//============================================================================================
	
	protected Entity transportEntity(ServerLevel destinationLevel, Stargate destinationStargate, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		if(traveler.getLevel() != destinationLevel)
			traveler = traveler.changeDimension(destinationLevel, new WormholeTeleporter(destinationPosition, destinationMomentum,
					CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot()));
		else
		{
			traveler.moveTo(destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), traveler.getXRot());
			traveler.setDeltaMovement(destinationMomentum);
		}
		
		if(traveler != null)
			reconstructEvent(destinationLevel.getServer(), destinationStargate, traveler);
		
		return traveler;
	}
	
	protected Entity transportPlayer(ServerLevel destinationLevel, Stargate destinationStargate, ServerPlayer player, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		Level initialLevel = player.getLevel();
		Vec3 initialPos = player.position();
		
		player.teleportTo(destinationLevel, destinationPosition.x(), destinationPosition.y(), destinationPosition.z(), CoordinateHelper.CoordinateSystems.lookAngleY(destinationLookAngle), player.getXRot());
		player.setDeltaMovement(destinationMomentum);
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		
		reconstructEvent(destinationLevel.getServer(), destinationStargate, player);
		
		ResourceLocation initialDimension = initialLevel.dimension().location();
		ResourceLocation targetDimension = destinationLevel.dimension().location();
		long distanceTraveled = Math.round(DimensionType.getTeleportationScale(initialLevel.dimensionType(), destinationLevel.dimensionType()) * Math.sqrt(initialPos.distanceTo(player.position())));
		
		player.awardStat(StatisticsInit.TIMES_USED_WORMHOLE.get());
		player.awardStat(StatisticsInit.DISTANCE_TRAVELED_BY_STARGATE.get(), (int) distanceTraveled * 100);
		WormholeTravelCriterion.INSTANCE.trigger(player, initialDimension, targetDimension, distanceTraveled);
		
		return player;
	}
	
	protected Entity recursivePassengerTeleport(ServerLevel destinationLevel, Stargate destinationStargate, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		Level initialLevel = traveler.getLevel();
		ArrayList<Entity> passengers = new ArrayList<>();
		if(initialLevel != destinationLevel)
		{
			// Prepares passengers
			for(Entity passenger : traveler.getPassengers())
			{
				passengers.add(recursivePassengerTeleport(destinationLevel, destinationStargate, passenger, destinationPosition, destinationMomentum, destinationLookAngle));
			}
		}
		
		// Teleports traveler
		if(traveler instanceof ServerPlayer player)
			traveler = transportPlayer(destinationLevel, destinationStargate, player, destinationPosition, destinationMomentum, destinationLookAngle);
		else
			traveler = transportEntity(destinationLevel, destinationStargate, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		
		if(initialLevel != destinationLevel)
		{
			// Brings passengers
			for(Entity passenger : passengers)
			{
				passenger.startRiding(traveler, true);
			}
		}
		
		return traveler;
	}
	
	public boolean receiveTraveler(ServerLevel destinationLevel, Stargate destinationStargate, Entity traveler, Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
	{
		traveler = recursivePassengerTeleport(destinationLevel, destinationStargate, traveler, destinationPosition, destinationMomentum, destinationLookAngle);
		playWormholeSound(destinationLevel, traveler);
		return true;
	}
	
	/**
	 *
	 * @param targetStargate
	 * @param destinationPos
	 * @param motionVec
	 * @param traveler
	 * @return true if there were no issues, false if the traveler hit the shielding
	 */
	public boolean checkShielding(IrisStargateEntity targetStargate, Vec3 destinationPos, Vec3 motionVec, Entity traveler)
	{
		if(targetStargate.irisInfo().isIrisClosed()) // No need to check, we know it's closed
			return false;
		
		EntityDimensions dimension = traveler.getDimensions(traveler.getPose());
		// Creates a bounding box at the destination and takes its center
		Vec3 travelerCenter = dimension.makeBoundingBox(destinationPos).getCenter();
		
		Vec3 fromVec = travelerCenter.subtract(motionVec);
		Vec3 toVec = travelerCenter.add(motionVec);
		
		Level targetLevel = targetStargate.getLevel();
		BlockPos pos = targetStargate.getBlockPos();
		BlockState state = targetLevel.getBlockState(targetStargate.getBlockPos());
		
		if(state.getBlock() instanceof AbstractStargateBlock stargateBlock)
		{
			for(ShieldingPart part : stargateBlock.getShieldingParts())
			{
				BlockPos shieldingPos = part.getShieldingPos(pos, state.getValue(AbstractStargateBlock.FACING), state.getValue(AbstractStargateBlock.ORIENTATION));
				BlockState shieldingState = targetLevel.getBlockState(shieldingPos);
				
				if(shieldingState.getBlock() instanceof AbstractShieldingBlock)
				{
					if(shieldingState.getCollisionShape(targetLevel, shieldingPos).clip(fromVec, toVec, shieldingPos) != null)
						return false;
				}
			}
		}
		
		return true;
	}
	
	public void handleShielding(IrisStargateEntity irisStargate, Entity traveler)
	{
		recursiveExecute(traveler, (entity) ->
		{
			if(entity instanceof ServerPlayer player && (player.isCreative() || player.isSpectator()))
			{
				if(!CommonIrisConfig.creative_ignores_iris.get())
					player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.iris").withStyle(ChatFormatting.DARK_RED), true);
			}
			else
			{
				if(entity instanceof LivingEntity livingEntity)
				{
					if(entity instanceof ServerPlayer player)
						player.awardStat(StatisticsInit.TIMES_SMASHED_AGAINST_IRIS.get());
					
					livingEntity.die(DamageSourceInit.IRIS);
				}
				else
					entity.kill();
				
				irisThudEvent(irisStargate, entity);
				irisStargate.irisInfo().decreaseIrisDurability();
			}
		});
		
		irisStargate.irisInfo().playIrisThudSound(); // Only playing one sound
	}
	
	//============================================================================================
	//*******************************************Events*******************************************
	//============================================================================================
	
	protected void irisThudEvent(AbstractStargateEntity targetStargate, Entity traveler)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	targetStargate.updateInterfaceBlocks(EVENT_IRIS_THUD, travelerType, displayName, uuid);
    }
	
	protected void deconstructEvent(MinecraftServer server, Stargate initialStargate, Entity traveler, boolean disintegrated)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	initialStargate.updateInterfaceBlocks(server, null, EVENT_DECONSTRUCTING_ENTITY, travelerType, displayName, uuid, disintegrated);
    }
    
    protected void reconstructEvent(MinecraftServer server, Stargate targetStargate, Entity traveler)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	targetStargate.updateInterfaceBlocks(server, null, EVENT_RECONSTRUCTING_ENTITY, travelerType, displayName, uuid);
    }
	
	public static void recursiveExecute(Entity traveler, WormholeFunction func)
	{
		for(Entity passenger : traveler.getPassengers())
		{
			recursiveExecute(passenger, func);
		}
		
		func.run(traveler);
	}
	
	public static void playWormholeSound(Level level, Entity traveler)
	{
		level.playSound(null, traveler.blockPosition(), SoundInit.WORMHOLE_ENTER.get(), SoundSource.BLOCKS, 0.5F, 1F);
	}
	
	
	
	public interface WormholeFunction
	{
		void run(Entity entity);
	}
	
	public class WormholeTeleporter implements ITeleporter
	{
		private Vec3 pos;
		private Vec3 momentum;
		private float newYRot;
		private float newXRot;
		
		public WormholeTeleporter(Vec3 pos, Vec3 momentum, float newYRot, float newXRot)
		{
			this.pos = pos;
			this.momentum = momentum;
			this.newYRot = newYRot;
			this.newXRot = newXRot;
		}
		
		@Override
		public @Nullable PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo)
		{
			return new PortalInfo(pos, momentum, newYRot, newXRot);
		}
		
		@Override
		public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld)
		{
			return false;
		}
	}
}
