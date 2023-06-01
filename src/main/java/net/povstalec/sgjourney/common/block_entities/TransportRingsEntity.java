package net.povstalec.sgjourney.common.block_entities;

import java.util.List;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.TransportRingsBlock;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundRingsUpdatePacket;

public class TransportRingsEntity extends SGJourneyBlockEntity
{
	ItemStack stack0;
	ItemStack stack1;
	ItemStack stack2;
	
    private BlockPos transportPos;
    private BlockPos targetPos;

    public boolean isSender;
    
    public int emptySpace = 0;
    public int ticks;
    public int progressOld = 0;
    public int progress = 0;
    public int transportHeight = 0;
    
    public int transportLight;
    
    private TransportRingsEntity target;
	
	public TransportRingsEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.TRANSPORT_RINGS.get(), pos, state, SGJourneyBlockEntity.Type.TRANSPORT_RINGS);
	}

	@Override
	public AABB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
	
	@Override
	public CompoundTag addNewToBlockEntityList()
	{
		CompoundTag blockEntity = super.addNewToBlockEntityList();
		TransporterNetwork.get(level).addToNetwork(getID(), BlockEntityList.get(level).getBlockEntities("TransportRings").getCompound(getID()));
		return blockEntity;
	}
	
	@Override
	public CompoundTag addToBlockEntityList()
	{
		CompoundTag blockEntity = super.addToBlockEntityList();
		TransporterNetwork.get(level).addToNetwork(getID(), BlockEntityList.get(level).getBlockEntities("TransportRings").getCompound(getID()));
		return blockEntity;
	}

	@Override
	public void removeFromBlockEntityList()
	{
		super.removeFromBlockEntityList();
		TransporterNetwork.get(level).removeFromNetwork(level, getID());
	}
	
	public boolean canTransport()
	{
		if(this.isActivated())
			return false;
		
		return true;
	}

//========================================================================================================
//**********************************************Transporting**********************************************
//========================================================================================================
	
	private void activate(BlockPos targetPos, boolean isSender)
	{
		target = (TransportRingsEntity) level.getBlockEntity(targetPos);
		
		if(!targetPos.equals(this.getBlockPos()) && !this.isActivated() && !target.isActivated())
		{
			if(isSender)
			{
				target.activate(getBlockPos(), false);
				this.isSender = true;
			}
			else
				target.isSender = false;
			
			setActivated(true);
			
			emptySpace = getEmptySpace();
			
			transportPos = new BlockPos(getBlockPos().getX(), (getBlockPos().getY() + emptySpace), getBlockPos().getZ());
			
			int difference = Math.abs(this.getTransportHeight() - target.getTransportHeight());
			
			if(this.transportHeight >= target.transportHeight)
				ticks = 0;
			else
				ticks = -difference;
			
			progress = 0;

			this.targetPos = targetPos;
			
			target = (TransportRingsEntity) level.getBlockEntity(targetPos);
			
			if(level.isClientSide())
				transportLight = LevelRenderer.getLightColor(level, this.transportPos);
			
			loadChunk(true);
		}
		else
			target = null;
		
		//TODO sync difference with client
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundRingsUpdatePacket(this.getBlockPos(), this.emptySpace, this.transportHeight, this.transportLight));
	}
	
	public int getTransportHeight()
	{
		if(getEmptySpace() > 0) 
			transportHeight = Math.abs(getEmptySpace() * 4) + 8;
		else 
			transportHeight = Math.abs(getEmptySpace() * 4) - 2;
		return transportHeight;
	}
	
	public void activate(BlockPos targetPos)
	{
		activate(targetPos, true);
	}
	
	public void deactivate()
	{
		isSender = false;
		setActivated(false);
		ticks = 0;
		progressOld = 0;
		progress = 0;
		
		loadChunk(false);
	}
	
	private void loadChunk(boolean load)
	{
		if(level.isClientSide())
			return;
		
		ForgeChunkManager.forceChunk(level.getServer().getLevel(level.dimension()), StargateJourney.MODID, this.getBlockPos(), level.getChunk(this.getBlockPos()).getPos().x, level.getChunk(this.getBlockPos()).getPos().z, load, true);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransportRingsEntity rings)
	{
		if(rings.isActivated())
			rings.ticks++;
		
		rings.doProgress();
		
		if(rings.ticks > 0 && rings.progress <= 0)
			rings.deactivate();
		
		if(level.isClientSide())
			return;
		
		if(rings.ticks == (rings.transportHeight + 22) && rings.isSender && level.getBlockEntity(rings.targetPos) instanceof TransportRingsEntity)
			rings.startTransporting();
		      
		//PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(rings.worldPosition)), new ClientboundRingsUpdatePacket(pos, rings.emptySpace, rings.transportHeight, rings.transportLight));
	}
	
	private void doProgress()
	{
		this.progressOld = this.progress;
		
		if(this.ticks > 0 && this.ticks <= this.transportHeight + 17)
			this.progress = this.ticks;
		
		else if(this.ticks >= this.transportHeight + 42 && this.progress > 0)
			this.progress--;
	}
	
	public void setProgress(int progress)
	{
		this.progressOld = this.progress; //TODO This may not work
		this.progress = progress;
	}
	
	public float getProgress(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) this.progress : Mth.lerp(partialTick, this.progressOld, this.progress);
	}
	
	public void getStatus()
	{
	    System.out.println("ID: " + getID());
	    if(this.getBlockPos() != null)
	    	System.out.println("Pos: " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ());
	    if(targetPos != null)
	    	System.out.println("Target: " + targetPos.getX() + " "  + targetPos.getY() + " " + targetPos.getZ());
	    if(transportPos != null)
	    	System.out.println("Transport: " + transportPos.getX() + " "  + transportPos.getY() + " " + transportPos.getZ());
	    System.out.println("Sending: " + isSender);
	    System.out.println("Ticks: " + ticks);
	}
	
	/*public void emergencyDeactivate()
	{
		if(this.targetPos != null)
		{
			((TransportRingsEntity) level.getBlockEntity(this.targetPos)).activated = false;
		}
	}*/
	
// Actual Transporting
	
	private void startTransporting()
	{
  		AABB localBox = new AABB((transportPos.getX() - 1), (transportPos.getY()), (transportPos.getZ() - 1), 
  									(transportPos.getX() + 2), (transportPos.getY() + 3), (transportPos.getZ() + 2));
		List<Entity> localEntities = this.level.getEntitiesOfClass(Entity.class, localBox);
		
		AABB targetBox = new AABB((target.transportPos.getX() - 1), (target.transportPos.getY()), (target.transportPos.getZ() - 1), 
									(target.transportPos.getX() + 2), (target.transportPos.getY() + 3), (target.transportPos.getZ() + 2));
		List<Entity> targetEntities = this.level.getEntitiesOfClass(Entity.class, targetBox);
    	
    	if(!localEntities.isEmpty())
    		localEntities.stream().forEach(this::transportToTarget);
    	
    	if(!targetEntities.isEmpty())
    		targetEntities.stream().forEach(this::transportFromTarget);
	}
	
	private void transportToTarget(Entity entity)
	{
		double x_offset = entity.getX() - transportPos.getX();
		double y_offset = entity.getY() - transportPos.getY();
		double z_offset = entity.getZ() - transportPos.getZ();
		
		//entity.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		//((ServerLevel) level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, new ChunkPos(target.transportPos), 1, entity.getId());
		if(entity instanceof ServerPlayer player)
			player.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		else
			entity.teleportTo((target.transportPos.getX() + x_offset), (target.transportPos.getY() + y_offset), (target.transportPos.getZ() + z_offset));
		
		System.out.println("Transporting to target: " + entity.toString());
	}
	
	private void transportFromTarget(Entity entity)
	{
		double x_offset = entity.getX() - target.transportPos.getX();
		double y_offset = entity.getY() - target.transportPos.getY();
		double z_offset = entity.getZ() - target.transportPos.getZ();
		
		//entity.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		if(entity instanceof ServerPlayer player)
			player.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		else
			entity.teleportTo((transportPos.getX() + x_offset), (transportPos.getY() + y_offset), (transportPos.getZ() + z_offset));
		
		
		System.out.println("Transporting from target: " + entity.toString());
	}
	
// Activation
	
	public boolean isActivated()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
		{
			return this.level.getBlockState(pos).getValue(TransportRingsBlock.ACTIVATED);
		}
		return false;
	}
	
	public void setActivated(boolean active)
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
		{
			level.setBlock(pos, state.setValue(TransportRingsBlock.ACTIVATED, active), 2);
		}
	}
	
	private int getEmptySpace()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(!state.is(BlockInit.TRANSPORT_RINGS.get()))
			return 0;
		
		if(state.getValue(TransportRingsBlock.FACING) == Direction.DOWN)
		{
			for(int i = 4; i <= 16; i++)
			{
				if(!level.getBlockState(pos.below(i)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 2)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.below(i - 3)).getMaterial().isReplaceable())
				{
					return -i + 1;
				}
			}
		}
		else
		{
			for(int i = 1; i <= 16; i++)
			{
				if(level.getBlockState(pos.above(i)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.above(i + 1)).getMaterial().isReplaceable() &&
					level.getBlockState(pos.above(i + 2)).getMaterial().isReplaceable())
				{
					return i;
				}
			}
		}
		return 0;
	}

	@Override
	public long capacity()
	{
		return 0;
	}

	@Override
	public long maxReceive()
	{
		return 0;
	}

	@Override
	public long maxExtract()
	{
		return 0;
	}
	
}
