package net.povstalec.sgjourney.stargate;

import com.google.common.base.Function;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.init.SoundInit;

public class Wormhole implements ITeleporter
{
	private AbstractStargateEntity dialingStargate;
	private AbstractStargateEntity dialedStargate;
	private long connectionCost;
	
	public Wormhole(AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, long connectionCost)
    {
        this.dialingStargate = dialingStargate;
        this.dialedStargate = dialedStargate;
        this.connectionCost = connectionCost;
    }
    
    @Override
    public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld)
    {
        return false;
    }
    
    public long getConnectionCost()
    {
    	return this.connectionCost;
    }
    
    public AbstractStargateEntity getDialingStargate()
    {
    	return this.dialingStargate;
    }
    
    public AbstractStargateEntity getDialedStargate()
    {
    	return this.dialedStargate;
    }
    
    public static void doWormhole(String uuid, Entity traveler)
    {
		Level level = traveler.getLevel();
		playWormholeSound(level, traveler);
    	
    	if(traveler instanceof LocalPlayer player)
    	{
        	Vec3 momentum = traveler.getDeltaMovement();
        	player.connection.send(new ServerboundMovePlayerPacket.PosRot(momentum.x, momentum.y, momentum.z, player.getYRot(), player.getXRot(), player.isOnGround()));
    	}
    	if(traveler instanceof ServerPlayer player)
    	{
        	Vec3 momentum = traveler.getDeltaMovement();
    	}
    	
		if(!level.isClientSide())
		{
			AbstractStargateEntity dialingStargate = StargateNetwork.get(level).getDialingStargate(level.getServer(), uuid);
			
			int[] targetCoords = StargateNetwork.get(level).getConnection(uuid).getCompound("DialedStargate").getIntArray("Coordinates");
			ServerLevel destinationlevel = level.getServer().getLevel(StargateNetwork.get(level).getDialedDimension(uuid));
	        
	        if (destinationlevel == null)
	        {
	        	System.out.println("Dimension is null");
	            return;
	        }
	        
	        AbstractStargateEntity dialedStargate = StargateNetwork.get(level).getDialedStargate(level.getServer(), uuid);
	        
	        if(dialedStargate != null)
	        {
		        Direction initialDirection = dialingStargate.getDirection();
	        	Direction destinationDirection = dialedStargate.getDirection();
		        if(traveler instanceof ServerPlayer player)
		    	{
		        	//Vec3 playerMomentum = new Vec3(player.getX() - player.xOld, player.getY() - player.yOld, player.getZ() - player.zOld);
		        	player.teleportTo(destinationlevel, targetCoords[0] + 0.5, targetCoords[1] + 2, targetCoords[2] + 0.5, preserveYRot(initialDirection, destinationDirection, player.getYRot()), player.getXRot());
		        	//player.setDeltaMovement(preserveMomentum(initialDirection, destinationDirection, playerMomentum));
		        	player.connection.send(new ClientboundSetEntityMotionPacket(traveler));
		    	}
		    	else
		    	{
		        	Vec3 momentum = traveler.getDeltaMovement();
		    		if((ServerLevel) level != destinationlevel)
		    		{
		    			Entity newTraveler = traveler.changeDimension(destinationlevel, new Wormhole(dialingStargate, dialedStargate, 10));
		    			newTraveler.moveTo(targetCoords[0] + 0.5, targetCoords[1] + 2, targetCoords[2] + 0.5, preserveYRot(initialDirection, destinationDirection, traveler.getYRot()), traveler.getXRot());
		    			newTraveler.setDeltaMovement(preserveMomentum(initialDirection, destinationDirection, momentum));
		    		}
		    		else
		    		{
		    			traveler.moveTo(targetCoords[0] + 0.5, targetCoords[1] + 2, targetCoords[2] + 0.5, preserveYRot(initialDirection, destinationDirection, traveler.getYRot()), traveler.getXRot());
		    			traveler.setDeltaMovement(preserveMomentum(initialDirection, destinationDirection, momentum));
		    		}
		    	}
	        }
		}
    }
    
    private static Vec3 preserveMomentum(Direction initialDirection, Direction destinationDirection, Vec3 initialMomentum)
    {
    	double x = initialMomentum.x();
    	double y = initialMomentum.y();
    	double z = initialMomentum.z();
    	
    	double newx = x;
    	double newz = z;
    	
    	if(initialDirection == destinationDirection.getClockWise())
    	{
    		newx = -z;
    		newz = x;
    	}
    	else if(initialDirection == destinationDirection.getClockWise().getClockWise())
    	{
    		newx = -x;
    		newz = -z;
    	}
    	else if(initialDirection == destinationDirection.getCounterClockWise())
    	{
    		newx = z;
    		newz = -x;
    	}
    	
    	Vec3 destinationMomentum = new Vec3(newx, y, newz);
    	return destinationMomentum;
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
