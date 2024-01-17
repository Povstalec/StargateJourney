package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.misc.MatrixHelper;
import net.povstalec.sgjourney.common.stargate.Stargate.WormholeTravel;

public class Wormhole implements ITeleporter
{
	private static final String EVENT_DECONSTRUCTING_ENTITY = "stargate_deconstructing_entity";
	private static final String EVENT_RECONSTRUCTING_ENTITY = "stargate_reconstructing_entity";
	
	protected Map<Integer, Vec3> entityLocations = new HashMap<Integer, Vec3>();
	protected List<Entity> localEntities = new ArrayList<Entity>();
	protected boolean used = false;
	
	public Wormhole()
    {
		
    }
	
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
		
		Stream<Entity> entityStream = localEntities.stream();
		Iterator<Entity> iterator = entityStream.iterator();
		
		while(iterator.hasNext())
		{
			if(!EntityType.getKey(iterator.next().getType()).getNamespace().equals(StargateJourney.CREATE_MODID))
				return true;
		}
		
		return false;
	}
	
	public boolean wormholeEntities(AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, Stargate.WormholeTravel twoWayWormhole)
	{
		this.used = false;
		Direction direction = initialStargate.getDirection();
		Direction orientationDirection = Orientation.getEffectiveDirection(direction, initialStargate.getOrientation());
		Map<Integer, Vec3> entityLocations = new HashMap<Integer, Vec3>();
		localEntities.stream().forEach((traveler) ->
		{
			if(this.entityLocations.containsKey(traveler.getId()))
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
					return;
				
				if(orientationDirection.getAxis() == Direction.Axis.X)
				{
					unitDistance = initialStargate.getCenterPos().getX() - initialStargate.getCenterPos().relative(orientationDirection).getX();
					previousTravelerPos = initialStargate.getCenterPos().getX() + 0.5 - previousX;
					travelerPos = initialStargate.getCenterPos().getX() + 0.5 - traveler.getX();
					axisMomentum = momentum.x();
				}
				else if(orientationDirection.getAxis() == Direction.Axis.Z)
				{
					unitDistance = initialStargate.getCenterPos().getZ() - initialStargate.getCenterPos().relative(orientationDirection).getZ();
					previousTravelerPos = initialStargate.getCenterPos().getZ() + 0.5 - previousZ;
					travelerPos = initialStargate.getCenterPos().getZ() + 0.5 - traveler.getZ();
					axisMomentum = momentum.z();
				}
				else
				{
					unitDistance = initialStargate.getCenterPos().getY() - initialStargate.getCenterPos().relative(orientationDirection).getY();
					previousTravelerPos = initialStargate.getCenterPos().getY() + initialStargate.getGateAddition() - previousY;
					travelerPos = initialStargate.getCenterPos().getY() + initialStargate.getGateAddition() - traveler.getY();
					axisMomentum = momentum.y();
				}
				
				if(shouldWormhole(initialStargate, traveler, unitDistance, previousTravelerPos, travelerPos, axisMomentum))
					doWormhole(initialStargate, targetStargate, traveler, momentum, twoWayWormhole);
				else
					entityLocations.put(traveler.getId(), new Vec3(traveler.getX(), traveler.getY(), traveler.getZ()));
				
			}
			else
				entityLocations.put(traveler.getId(), new Vec3(traveler.getX(), traveler.getY(), traveler.getZ()));
		});
		
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
    
    public void doWormhole(AbstractStargateEntity initialStargate, AbstractStargateEntity targetStargate, Entity traveler, Vec3 momentum, Stargate.WormholeTravel twoWayWormhole)
    {
		Level level = traveler.level();
		playWormholeSound(level, traveler);
		
		if(level.isClientSide())
			return;
		
		if(twoWayWormhole == WormholeTravel.ENABLED || (traveler instanceof Player player && player.isCreative() && twoWayWormhole == WormholeTravel.CREATIVE_ONLY))
		{
			ServerLevel destinationlevel = (ServerLevel) targetStargate.getLevel();
	        
	        if (destinationlevel == null)
	        {
	        	System.out.println("Dimension is null");
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
		        
	    		Vec3 position = preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, new Vec3(traveler.getX() - (initialStargate.getCenterPos().getX() + 0.5), traveler.getY() - (initialStargate.getCenterPos().getY() + initialYAddition), traveler.getZ() - (initialStargate.getCenterPos().getZ() + 0.5)));
	    		
	    		if(traveler instanceof ServerPlayer player)
		    	{
		    		deconstructEvent(initialStargate, player, false);
		        	player.teleportTo(destinationlevel, targetStargate.getCenterPos().getX() + 0.5 + position.x(), targetStargate.getCenterPos().getY() + destinationYAddition + position.y(), targetStargate.getCenterPos().getZ() + 0.5 + position.z(), preserveYRot(initialDirection, destinationDirection, player.getYRot()), player.getXRot());
		        	player.setDeltaMovement(preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum));
		        	player.connection.send(new ClientboundSetEntityMotionPacket(traveler));
		    		playWormholeSound(level, player);
		    		reconstructEvent(targetStargate, player);
		    	}
		    	else
		    	{
		    		deconstructEvent(initialStargate, traveler, false);
		    		Entity newTraveler = traveler;
		    		if((ServerLevel) level != destinationlevel)
		    			newTraveler = traveler.changeDimension(destinationlevel, this);

		    		newTraveler.moveTo(targetStargate.getCenterPos().getX() + 0.5 + position.x(), targetStargate.getCenterPos().getY() + destinationYAddition + position.y(), targetStargate.getCenterPos().getZ() + 0.5 + position.z(), preserveYRot(initialDirection, destinationDirection, traveler.getYRot()), traveler.getXRot());
		    		newTraveler.setDeltaMovement(preserveRelative(initialDirection, initialOrientation, destinationDirection, destinationOrientation, momentum));
		    		playWormholeSound(level, newTraveler);
		    		reconstructEvent(targetStargate, newTraveler);
		    	}
	    		this.used = true;
	        }
		}
		else
		{
			if(CommonStargateConfig.reverse_wormhole_kills.get())
			{
				if(traveler instanceof Player player && player.isCreative())
					player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
				else
				{
		    		deconstructEvent(initialStargate, traveler, true);
					traveler.kill();
				}
			}
			else
			{
				if(traveler instanceof Player player)
					player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.one_way_wormhole").withStyle(ChatFormatting.DARK_RED), true);
			}
		}
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

	private static Vec3 preserveRelative(Direction initialDirection, Orientation initialOrientation, Direction destinationDirection, Orientation destinationOrientation, Vec3 initial)
    {
    	return MatrixHelper.rotateVector(initial, initialDirection, initialOrientation, destinationDirection, destinationOrientation);
    }
	
	private static float preserveYRot(Direction initialDirection, Direction destinationDirection, float yRot)
	{
		float initialStargateDirection = Mth.wrapDegrees(initialDirection.toYRot());
    	float destinationStargateDirection = Mth.wrapDegrees(destinationDirection.toYRot());
    	
    	float relativeRot = destinationStargateDirection - initialStargateDirection;
    	
    	yRot = yRot + relativeRot + 180;
    	
    	return yRot;
	}
	
	private static void playWormholeSound(Level level, Entity traveler)
	{
		level.playSound((Player)null, traveler.blockPosition(), SoundInit.WORMHOLE_ENTER.get(), SoundSource.BLOCKS, 0.25F, 1F);
	}
}
