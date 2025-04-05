package net.povstalec.sgjourney.common.sgjourney;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.advancements.WormholeTravelCriterion;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.config.CommonIrisConfig;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.DamageSourceInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.init.StatisticsInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo.WormholeTravel;
import org.jetbrains.annotations.Nullable;

public class Wormhole
{
	private static final String EVENT_DECONSTRUCTING_ENTITY = "stargate_deconstructing_entity";
	private static final String EVENT_RECONSTRUCTING_ENTITY = "stargate_reconstructing_entity";
	private static final String EVENT_IRIS_THUD = "iris_thud";
	
	public static final double MIN_SPEED = 0.4;
	
	protected Map<Integer, Vec3> entityLocations = new HashMap<Integer, Vec3>();
	protected List<Entity> localEntities = new ArrayList<Entity>();
	protected boolean used = false;
	
	public Wormhole() {}
	
	public boolean hasCandidates()
	{
		return localEntities.isEmpty();
	}
	
	public boolean findCandidates(Level level, Vec3 centerPos, Direction direction)
	{
		AABB localBox = new AABB(
			centerPos.x - 2.5, centerPos.y - 2.5, centerPos.z - 2.5, 
			centerPos.x + 2.5, centerPos.y + 2.5, centerPos.z + 2.5);
		
		localEntities = level.getEntitiesOfClass(Entity.class, localBox);
		
		if(localEntities.isEmpty())
			return false;
		
		for(Entity entity : localEntities)
		{
			if(!entity.getType().is(TagInit.Entities.WORMHOLE_IGNORES))
				return true;
		}
		
		return false;
	}
	
	public boolean wormholeEntity(AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, StargateInfo.WormholeTravel twoWayWormhole,
							   Entity traveler, Direction orientationDirection, Map<Integer, Vec3> entityLocations)
	{
		double previousX = this.entityLocations.get(traveler.getId()).x();
		double previousY = this.entityLocations.get(traveler.getId()).y();
		double previousZ = this.entityLocations.get(traveler.getId()).z();
		
		Vec3 momentum = new Vec3(traveler.getX() - previousX, traveler.getY() - previousY, traveler.getZ() - previousZ);
		
		int unitDistance;
		double previousTravelerPos;
		double travelerPos;
		double axisMomentum;
		
		if(orientationDirection == null)
			return true;
		
		if(orientationDirection.getAxis() == Direction.Axis.X)
		{
			unitDistance = initialStargate.getCenterPos().getX() - initialStargate.getCenterPos().relative(orientationDirection).getX();
			previousTravelerPos = initialStargate.getCenterPos().getX() + 0.5 - previousX;
			travelerPos = initialStargate.getCenterPos().getX() + 0.5 - traveler.getX();
			axisMomentum = momentum.x();
			
			if(Math.abs(momentum.x()) < MIN_SPEED)
				momentum = new Vec3(reverseIfNeeded(unitDistance < 0, MIN_SPEED), momentum.y(), momentum.z());
		}
		else if(orientationDirection.getAxis() == Direction.Axis.Z)
		{
			unitDistance = initialStargate.getCenterPos().getZ() - initialStargate.getCenterPos().relative(orientationDirection).getZ();
			previousTravelerPos = initialStargate.getCenterPos().getZ() + 0.5 - previousZ;
			travelerPos = initialStargate.getCenterPos().getZ() + 0.5 - traveler.getZ();
			axisMomentum = momentum.z();
			
			if(Math.abs(momentum.z()) < MIN_SPEED)
				momentum = new Vec3(momentum.x(), momentum.y(), reverseIfNeeded(unitDistance < 0, MIN_SPEED));
		}
		else
		{
			unitDistance = initialStargate.getCenterPos().getY() - initialStargate.getCenterPos().relative(orientationDirection).getY();
			previousTravelerPos = initialStargate.getCenterPos().getY() + initialStargate.getGateAddition() - previousY;
			travelerPos = initialStargate.getCenterPos().getY() + initialStargate.getGateAddition() - traveler.getY();
			axisMomentum = momentum.y();
			
			if(Math.abs(momentum.y()) < MIN_SPEED)
				momentum = new Vec3(momentum.x(), reverseIfNeeded(unitDistance < 0, MIN_SPEED), momentum.z());
		}
		
		if(shouldWormhole(initialStargate, traveler, unitDistance, previousTravelerPos, travelerPos, axisMomentum))
			doWormhole(initialStargate, targetStargate, traveler, momentum, twoWayWormhole);
		else
			entityLocations.put(traveler.getId(), new Vec3(traveler.getX(), traveler.getY(), traveler.getZ()));
		
		return false;
	}
	
	public boolean wormholeEntities(AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, StargateInfo.WormholeTravel twoWayWormhole)
	{
		this.used = false;
		Direction direction = initialStargate.getDirection();
		Direction orientationDirection = Orientation.getEffectiveDirection(direction, initialStargate.getOrientation());
		Map<Integer, Vec3> entityLocations = new HashMap<Integer, Vec3>();
		
		for(Entity traveler : localEntities)
		{
			if(!traveler.getType().is(TagInit.Entities.WORMHOLE_CANNOT_TELEPORT) && !traveler.isPassenger() && this.entityLocations.containsKey(traveler.getId()))
			{
				if(wormholeEntity(initialStargate, targetStargate, twoWayWormhole, traveler, orientationDirection, entityLocations))
					return this.used;
			}
			else
				entityLocations.put(traveler.getId(), new Vec3(traveler.getX(), traveler.getY(), traveler.getZ()));
		}
		
		this.entityLocations = entityLocations;
		
		return this.used;
	}
	
	public boolean shouldWormhole(AbstractStargateEntity initialStargate, Entity traveler, int unitDistance, double previousTravelerPos, double travelerPos, double axisMomentum)
	{
		Vec3 centerVector = initialStargate.getCenter();
		Vec3 travelerVector = traveler.getBoundingBox().getCenter();
		if(centerVector.distanceTo(travelerVector) > 2.5)
			return false;
		
		previousTravelerPos = reverseIfNeeded(unitDistance > 0, previousTravelerPos);
		travelerPos = reverseIfNeeded(unitDistance > 0, travelerPos);
		axisMomentum = reverseIfNeeded(unitDistance > 0, axisMomentum);
		
		if(previousTravelerPos < 0 && travelerPos >= 0 && axisMomentum < 0)
			return true;
		
		return false;
	}
	
	public double reverseIfNeeded(boolean shouldReverse, double number)
	{
		return shouldReverse ? -number : number;
	}
	
	/**
	 * 
	 * @param targetStargate
	 * @param destinationPos
	 * @param motionVec
	 * @param traveler
	 * @return true if there were no issues, false if the traveler hit the shielding
	 */
	public boolean handleShielding(IrisStargateEntity targetStargate, Vec3 destinationPos, Vec3 motionVec, Entity traveler)
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
	
	private Entity wormholeTeleportPlayer(ServerPlayer player, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate,
								Direction initialDirection, Direction destinationDirection, Orientation initialOrientation, Orientation destinationOrientation,
								ServerLevel level, ServerLevel destinationlevel, Vec3 destinationPos, Vec3 momentum)
	{
		player.teleportTo(destinationlevel, destinationPos.x(), destinationPos.y(), destinationPos.z(), CoordinateHelper.Relative.preserveYRot(initialDirection, destinationDirection, player.getYRot()), player.getXRot());
		player.setDeltaMovement(CoordinateHelper.Relative.preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum));
		player.connection.send(new ClientboundSetEntityMotionPacket(player));
		reconstructEvent(targetStargate, player);
		
		Level initialLevel = initialStargate.getLevel();
		ResourceLocation initialDimension = initialLevel.dimension().location();
		
		Level targetLevel = targetStargate.getLevel();
		ResourceLocation targetDimension = targetLevel.dimension().location();
		long distanceTraveled = (int) Math.round(DimensionType.getTeleportationScale(initialLevel.dimensionType(), targetLevel.dimensionType()) * Math.sqrt(initialStargate.getCenterPos().distSqr(targetStargate.getCenterPos())));
		
		player.awardStat(StatisticsInit.TIMES_USED_WORMHOLE.get());
		player.awardStat(StatisticsInit.DISTANCE_TRAVELED_BY_STARGATE.get(), (int) distanceTraveled * 100);
		WormholeTravelCriterion.INSTANCE.trigger(player, initialDimension, targetDimension, distanceTraveled);
		
		return player;
	}
	
	private Entity wormholeTeleportEntity(Entity traveler, AbstractStargateEntity targetStargate,
										Direction initialDirection, Direction destinationDirection, Orientation initialOrientation, Orientation destinationOrientation,
										ServerLevel level, ServerLevel destinationlevel, Vec3 destinationPos, Vec3 momentum)
	{
		if(level != destinationlevel)
			traveler = traveler.changeDimension(destinationlevel, new WormholeTeleporter(destinationPos,
					CoordinateHelper.Relative.preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum),
					CoordinateHelper.Relative.preserveYRot(initialDirection, destinationDirection, traveler.getYRot()), traveler.getXRot()));
		else
		{
			traveler.moveTo(destinationPos.x(), destinationPos.y(), destinationPos.z(),CoordinateHelper.Relative.preserveYRot(initialDirection, destinationDirection, traveler.getYRot()), traveler.getXRot());
			traveler.setDeltaMovement(CoordinateHelper.Relative.preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum));
		}
		
		reconstructEvent(targetStargate, traveler);
		
		return traveler;
	}
	
	private Entity recursivePassengerTeleport(Entity traveler, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate,
											  Direction initialDirection, Direction destinationDirection, Orientation initialOrientation, Orientation destinationOrientation,
											  ServerLevel level, ServerLevel destinationlevel, Vec3 destinationPos, Vec3 momentum)
	{
		ArrayList<Entity> passengers = new ArrayList<>();
		if(level != destinationlevel)
		{
			// Prepares passengers
			for(Entity passenger : traveler.getPassengers())
			{
				passengers.add(recursivePassengerTeleport(passenger, initialStargate, targetStargate, initialDirection, destinationDirection,
						initialOrientation, destinationOrientation, level, destinationlevel, destinationPos, momentum));
			}
		}
		
		// Teleports traveler
		if(traveler instanceof ServerPlayer player)
			traveler = wormholeTeleportPlayer(player, initialStargate, targetStargate, initialDirection, destinationDirection,
					initialOrientation, destinationOrientation, level, destinationlevel, destinationPos, momentum);
		else
			traveler = wormholeTeleportEntity(traveler, targetStargate, initialDirection, destinationDirection,
					initialOrientation, destinationOrientation, level, destinationlevel, destinationPos, momentum);
		
		if(level != destinationlevel)
		{
			// Brings passengers
			for(Entity passenger : passengers)
			{
				passenger.startRiding(traveler, true);
			}
		}
		
		return traveler;
	}
	
	private void wormholeTeleport(Entity traveler, AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate,
											Direction initialDirection, Direction destinationDirection, Orientation initialOrientation, Orientation destinationOrientation,
											ServerLevel level, ServerLevel destinationlevel, Vec3 destinationPos, Vec3 momentum)
	{
		playWormholeSound(level, traveler);
		
		recursivePassengerTeleport(traveler, initialStargate, targetStargate, initialDirection, destinationDirection,
				initialOrientation, destinationOrientation, level, destinationlevel, destinationPos, momentum);
		
		playWormholeSound(destinationlevel, traveler);
	}
	
    public void doWormhole(AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, Entity traveler, Vec3 momentum, StargateInfo.WormholeTravel twoWayWormhole)
    {
		Level level = traveler.level();
		playWormholeSound(level, traveler);
		
		if(level.isClientSide())
			return;
		
		if(twoWayWormhole == WormholeTravel.ENABLED || (traveler instanceof Player player && player.isCreative() && twoWayWormhole == WormholeTravel.CREATIVE_ONLY))
		{
			ServerLevel destinationlevel = (ServerLevel) targetStargate.getLevel();
	        
	        if(destinationlevel == null)
	        {
	        	StargateJourney.LOGGER.error("Can't teleport Entity because Dimension is null");
	            return;
	        }
	        
	        if(targetStargate != null)
	        {
		        Direction initialDirection = initialStargate.getDirection();
		        Orientation initialOrientation = initialStargate.getOrientation();
	        	Direction destinationDirection = targetStargate.getDirection();
		        Orientation destinationOrientation = targetStargate.getOrientation();
		        double initialYAddition = initialStargate.getGateAddition();
		        double destinationYAddition = targetStargate.getGateAddition();
		        
	    		Vec3 relativePos = CoordinateHelper.Relative.preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, new Vec3(traveler.getX() - (initialStargate.getCenterPos().getX() + 0.5), traveler.getY() - (initialStargate.getCenterPos().getY() + initialYAddition), traveler.getZ() - (initialStargate.getCenterPos().getZ() + 0.5)));
	    		
	    		Vec3 destinationPos = new Vec3(targetStargate.getCenterPos().getX() + 0.5 + relativePos.x(), targetStargate.getCenterPos().getY() + destinationYAddition + relativePos.y(), targetStargate.getCenterPos().getZ() + 0.5 + relativePos.z());
	    		
	    		Vec3 motionVec = CoordinateHelper.Relative.preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum);
	    		
	    		if(targetStargate instanceof IrisStargateEntity irisStargate && !handleShielding(irisStargate, destinationPos, motionVec, traveler))
	    		{
					recursiveExecute(traveler, (entity) ->
					{
						if(entity instanceof ServerPlayer player && player.isCreative())
						{
							if(!CommonIrisConfig.creative_ignores_iris.get())
								player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.iris").withStyle(ChatFormatting.DARK_RED), true);
						}
						else
						{
							if(entity instanceof ServerPlayer player)
								player.awardStat(StatisticsInit.TIMES_SMASHED_AGAINST_IRIS.get());
							
							irisThudEvent(irisStargate, entity);
							irisStargate.irisInfo().decreaseIrisDurability();
							entity.hurt(DamageSourceInit.damageSource(level.getServer(), DamageSourceInit.IRIS), Float.MAX_VALUE);
							entity.kill();
						}
					});
					
					irisStargate.irisInfo().playIrisThudSound(); // Only playing one sound
					this.used = true;
					return;
	    		}
				
				deconstructEvent(initialStargate, traveler, false);
				wormholeTeleport(traveler, initialStargate, targetStargate, initialDirection, destinationDirection, initialOrientation, destinationOrientation,
						(ServerLevel) level, destinationlevel, destinationPos, momentum);
	    		this.used = true;
	        }
		}
		else
		{
			recursiveExecute(traveler, (entity) ->
			{
				if(CommonStargateConfig.reverse_wormhole_kills.get())
				{
					if(entity.isAlive())
					{
						if(entity instanceof ServerPlayer player && player.isCreative())
							player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
						else
						{
							if(entity instanceof ServerPlayer player)
								player.awardStat(StatisticsInit.TIMES_KILLED_BY_WORMHOLE.get());
							
							entity.hurt(DamageSourceInit.damageSource(level.getServer(), DamageSourceInit.REVERSE_WORMHOLE), Float.MAX_VALUE);
							entity.kill();
						}
					}
				}
				else
				{
					if(entity instanceof ServerPlayer player)
						player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
				}
				
				deconstructEvent(initialStargate, entity, true);
			});
		}
    }
	
	private void recursiveExecute(Entity traveler, WormholeFunction func)
	{
		for(Entity passenger : traveler.getPassengers())
		{
			recursiveExecute(passenger, func);
		}
		
		func.run(traveler);
	}
    
    private void irisThudEvent(AbstractStargateEntity targetStargate, Entity traveler)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	targetStargate.updateInterfaceBlocks(EVENT_IRIS_THUD, travelerType, displayName, uuid);
    }
    
    private void deconstructEvent(AbstractStargateEntity initialStargate, Entity traveler, boolean disintegrated)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	initialStargate.updateInterfaceBlocks(EVENT_DECONSTRUCTING_ENTITY, travelerType, displayName, uuid, disintegrated);
    }
    
    private void reconstructEvent(AbstractStargateEntity targetStargate, Entity traveler)
    {
    	String travelerType = EntityType.getKey(traveler.getType()).toString();
    	String displayName = traveler instanceof Player player ? player.getGameProfile().getName() : traveler.getName().getString();
    	String uuid = traveler.getUUID().toString();
    	
    	targetStargate.updateInterfaceBlocks(EVENT_RECONSTRUCTING_ENTITY, travelerType, displayName, uuid);
    }
	private static void playWormholeSound(Level level, Entity traveler)
	{
		level.playSound((Player)null, traveler.blockPosition(), SoundInit.WORMHOLE_ENTER.get(), SoundSource.BLOCKS, 0.5F, 1F);
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
