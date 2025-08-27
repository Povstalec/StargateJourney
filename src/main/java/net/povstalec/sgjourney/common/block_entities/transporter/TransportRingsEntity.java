package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.blocks.transporter.TransportRingsBlock;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.packets.ClientboundRingsUpdatePacket;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;

public class TransportRingsEntity extends AbstractTransporterEntity
{
	public static final int MAX_TRANSPORT_HEIGHT = 16;
	
	@Nullable
	private BlockPos transportPos = null;
	public int emptySpace = 0;
	public int transportHeight = 0;
	
	public int progress = -1;
	public int progressOld = -1;
	
	public TransportRingsEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.TRANSPORT_RINGS.get(), pos, state);
	}
	
	@Override
	public int getTimeOffset()
	{
		return getTransportHeight();
	}
	
	//========================================================================================================
	//**********************************************Transporting**********************************************
	//========================================================================================================
	
	@Override
	@Nullable
	public List<Entity> entitiesToTransport()
	{
		AABB localBox = new AABB((transportPos.getX() - 1), (transportPos.getY()), (transportPos.getZ() - 1),
				(transportPos.getX() + 2), (transportPos.getY() + 3), (transportPos.getZ() + 2));
		
		return this.level.getEntitiesOfClass(Entity.class, localBox);
	}
	
	@Override
	public BlockPos transportPos()
	{
		if(transportPos == null)
		{
			this.emptySpace = getEmptySpace();
			this.transportPos = new BlockPos(getBlockPos().getX(), (getBlockPos().getY() + this.emptySpace), getBlockPos().getZ());
		}
		
		return this.transportPos;
	}
	
	public void updateClient()
	{
		if(!level.isClientSide())
			PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(),
					new ClientboundRingsUpdatePacket(this.getBlockPos(), this.emptySpace, this.transportHeight, this.progress));
	}
	
	public void startTransport(Transporter target)
	{
		Transporter transporter = getTransporter();
		
		if(transporter == null) //TODO Maybe some kind of feedback when it goes wrong?
			return;
		
		TransporterNetwork.get(level).createConnection(level.getServer(), transporter, target);
	}
	
	public boolean connectTransporter(UUID connectionID)
	{
		transportPos();
		return super.connectTransporter(connectionID);
	}
	
	public void resetTransporter()
	{
		this.emptySpace = 0;
		this.transportHeight = 0;
		this.transportPos = null;
		
		super.resetTransporter();
	}
	
	
	
	public int getTransportHeight()
	{
		if(transportHeight == 0)
		{
			int emptySpace = getEmptySpace();
			
			if(emptySpace > 0)
				transportHeight = Math.abs(emptySpace * 4) + 8;
			else
				transportHeight = Math.abs(emptySpace * 4);
		}
		
		return transportHeight;
	}
	
	@Override
	public void updateTicks(int connectionTime)
	{
		this.progress = connectionTime;
		this.progressOld = connectionTime;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransportRingsEntity rings)
	{
		if(rings.isConnected())
			rings.doClientProgress();
		else
		{
			rings.progress = -1;
			rings.progressOld = -1;
		}
		
		rings.updateClient();
	}
	
	private void doClientProgress()
	{
		if(!this.level.isClientSide() || this.progress < 0)
			return;
		
		this.progressOld = this.progress;
		this.progress++;
	}
	
	// Only updates the progress if there is none - should prevent jittering
	public void updateProgress(int progress)
	{
		if(this.progress == -1 || progress == -1)
		{
			this.progress = progress;
			this.progressOld = progress;
		}
	}
	
	public void setProgress(int progress)
	{
		this.progressOld = this.progress;
		this.progress = progress;
	}
	
	public float getProgress(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) this.progress : Mth.lerp(partialTick, this.progressOld, this.progress);
	}
	
	@Override
	public boolean isConnected()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
		{
			return this.level.getBlockState(pos).getValue(TransportRingsBlock.ACTIVATED);
		}
		return false;
	}
	
	@Override
	public void setConnected(boolean connected)
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(state.is(BlockInit.TRANSPORT_RINGS.get()))
			level.setBlock(pos, state.setValue(TransportRingsBlock.ACTIVATED, connected), 2);
		
		loadChunk(connected);
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
				if(!level.getBlockState(pos.below(i)).canBeReplaced() &&
						level.getBlockState(pos.below(i - 1)).canBeReplaced() &&
						level.getBlockState(pos.below(i - 2)).canBeReplaced() &&
						level.getBlockState(pos.below(i - 3)).canBeReplaced())
				{
					return -i + 1;
				}
			}
		}
		else
		{
			for(int i = 1; i <= 16; i++)
			{
				if(level.getBlockState(pos.above(i)).canBeReplaced() &&
						level.getBlockState(pos.above(i + 1)).canBeReplaced() &&
						level.getBlockState(pos.above(i + 2)).canBeReplaced())
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
	
	@Override
	protected Component getDefaultName()
	{
		return Component.translatable("block.sgjourney.transport_rings");
	}
	
}
