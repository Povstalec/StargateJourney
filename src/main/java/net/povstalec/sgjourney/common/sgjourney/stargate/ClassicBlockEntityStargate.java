package net.povstalec.sgjourney.common.sgjourney.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;

public class ClassicBlockEntityStargate extends ClassicStargate implements BlockEntityStargate<ClassicStargateEntity>
{
	protected WeakReference<ClassicStargateEntity> stargate;
	
	protected BlockPos blockPos;
	
	// Direction cache
	@Nullable
	protected Vec3 forward = null;
	@Nullable
	protected Vec3 up = null;
	@Nullable
	protected Vec3 right = null;
	
	public ClassicBlockEntityStargate(StargateType<?> type)
	{
		super(type);
	}
	
	@Override
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	private ClassicStargateEntity cacheStargateEntity(ClassicStargateEntity stargate)
	{
		//this.stargate = new WeakReference(stargate); //TODO Bring caching back once Stargates are more flexible
		
		return stargate;
	}
	
	private @Nullable ClassicStargateEntity tryCacheStargateEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof ClassicStargateEntity stargate)
			return cacheStargateEntity(stargate);
		
		return null;
	}
	
	@Override
	public @Nullable ClassicStargateEntity getStargateEntity(MinecraftServer server)
	{
		//if((this.stargate != null && this.stargate.get() != null) || server == null)
		//	return this.stargate.get();
		
		return tryCacheStargateEntity(server);
	}
	
	@Override
	public void loadFromBlockEntity(AbstractStargateEntity<?> stargate)
	{
		this.id9ChevronAddress = stargate.get9ChevronAddress();
		
		this.dimension = stargate.getLevel().dimension();
		this.blockPos = stargate.getBlockPos();
		
		this.hasDHD = stargate.dhdInfo().hasDHD();
		this.timesOpened = stargate.getTimesOpened();
		this.networks = stargate.getNetworks();
	}
	
	//============================================================================================
	//*************************************Basic functionality************************************
	//============================================================================================
	
	@Override
	public @Nullable Vec3 getForward(MinecraftServer server)
	{
		if(forward == null)
		{
			forward = stargateReturn(server, stargate ->
			{
				Direction direction = stargate.getDirection();
				Orientation orientation = stargate.getOrientation();
				
				return Orientation.getForwardVector(direction, orientation);
			}, null);
		}
		
		return forward;
	}
	
	@Override
	public @Nullable Vec3 getUp(MinecraftServer server)
	{
		if(up == null)
		{
			up = stargateReturn(server, stargate ->
			{
				Direction direction = stargate.getDirection();
				Orientation orientation = stargate.getOrientation();
				
				return Orientation.getUpVector(direction, orientation);
			}, null);
		}
		
		return up;
	}
	
	@Override
	public Vec3 getRight(MinecraftServer server)
	{
		if(right == null)
		{
			if(getForward(server) != null && getUp(server) != null)
				right = CoordinateHelper.Relative.vecRight(getForward(server), getUp(server));
		}
		
		return right;
	}
	
	// Updating
	
	@Override
	public void update(MinecraftServer server)
	{
		stargateRun(server, stargate ->
		{
			this.hasDHD = stargate.dhdInfo().hasDHD();
			this.timesOpened = stargate.getTimesOpened();
			this.networks = stargate.getNetworks();
		});
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	@Override
	public void serializeNBT(CompoundTag tag)
	{
		tag.putIntArray(COORDINATES, Conversion.blockPosToIntArray(blockPos));
		
		super.serializeNBT(tag);
	}
	
	@Override
	public void deserializeNBT(MinecraftServer server, Address.Immutable id9ChevronAddress, CompoundTag tag)
	{
		blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		super.deserializeNBT(server, id9ChevronAddress, tag);
		
		if(!tag.contains(HAS_DHD) || !tag.contains(TIMES_OPENED) || !tag.contains(NETWORKS))
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof ClassicStargateEntity stargate)
				loadFromBlockEntity(stargate);
		}
	}
}
