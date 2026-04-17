package net.povstalec.sgjourney.common.block_entities.transporter;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.povstalec.sgjourney.common.blocks.transporter.TransportRingsBlock;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.SGJourneyPeripheralWrapper;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransporterPeripheral;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.TransporterInit;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransportRings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TransportRingsEntity extends AbstractTransporterEntity<TransportRings>
{
	public static final String EMPTY_SPACE = "empty_space";
	public static final String TRANSPORT_HEIGHT = "transport_height";
	public static final String PROGRESS = "progress";
	
	public static final int MAX_TRANSPORT_HEIGHT = 16;
	
	@Nullable
	private BlockPos transportPos = null;
	public int emptySpace = 0;
	public int transportHeight = 0;
	
	public int progress = -1;
	public int progressOld = -1;
	
	public TransportRingsEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.GOAULD_TRANSPORT_RINGS.get(), TransporterInit.GOAULD_TRANSPORT_RINGS.get(), pos, state, 1);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putInt(EMPTY_SPACE, emptySpace);
		tag.putInt(TRANSPORT_HEIGHT, transportHeight);
		tag.putInt(PROGRESS, progress);
		
		return tag;
	}
	
	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet)
	{
		CompoundTag tag = packet.getTag();
		if(tag != null)
		{
			emptySpace = tag.getInt(EMPTY_SPACE);
			transportHeight = tag.getInt(TRANSPORT_HEIGHT);
			updateProgress(tag.getInt(PROGRESS));
		}
	}

	@Override
	public AABB getRenderBoundingBox()
    {
        return new AABB(getBlockPos().getX() - 3, getBlockPos().getY() - (3 + MAX_TRANSPORT_HEIGHT), getBlockPos().getZ() - 3, getBlockPos().getX() + 4, getBlockPos().getY() + (4 + MAX_TRANSPORT_HEIGHT), getBlockPos().getZ() + 4);
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
	
	@Override
	public boolean connectTransporter(UUID connectionID)
	{
		transportPos();
		return super.connectTransporter(connectionID);
	}
	
	@Override
	public TransporterInfo.Feedback resetTransporter(TransporterInfo.Feedback feedback)
	{
		this.emptySpace = 0;
		this.transportHeight = 0;
		this.transportPos = null;
		
		return super.resetTransporter(feedback);
	}
	
	@Override
	public void registerInterfaceMethods(SGJourneyPeripheralWrapper<TransporterPeripheral> wrapper)
	{
		CCTweakedCompatibility.registerTransportRingsMethods(wrapper);
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
		if(state.is(BlockInit.GOAULD_TRANSPORT_RINGS.get()))
			return this.level.getBlockState(pos).getValue(TransportRingsBlock.ACTIVATED);
		
		return false;
	}
	
	@Override
	public boolean isObstructed()
	{
		return getEmptySpace() == 0;
	}
	
	@Override
	public void setConnected(boolean connected)
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(state.is(BlockInit.GOAULD_TRANSPORT_RINGS.get()))
			level.setBlock(pos, state.setValue(TransportRingsBlock.ACTIVATED, connected), 2);
		
		loadChunk(connected);
	}
	
	private int getEmptySpace()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(!state.is(BlockInit.GOAULD_TRANSPORT_RINGS.get()))
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
	public long getCapacity()
	{
		return 0; //TODO Change
	}

	@Override
	public long getMaxReceive()
	{
		return 0; //TODO Change
	}

	@Override
	public long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxDeplete()
	{
		return 0; //TODO Change
	}
	
	@Override
	protected Component getDefaultName()
	{
		return Component.translatable("block.sgjourney.transport_rings");
	}
	
}
