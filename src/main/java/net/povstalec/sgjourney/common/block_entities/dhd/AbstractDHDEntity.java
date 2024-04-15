package net.povstalec.sgjourney.common.block_entities.dhd;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.dhd.AbstractDHDBlock;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.packets.ClientboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;

public abstract class AbstractDHDEntity extends EnergyBlockEntity
{
	//TODO A temporary addition to make sure people can use DHDs for energy transfer even after updating from older versions
	protected static final String CRYSTAL_MODE = "CrystalMode";
	protected static final String ENERGY_TRANSFER = "ENERGY_TRANSFER";
	
	public static final String STARGATE_POS = "StargatePos";
	
	public static final int DEFAULT_ENERGY_TARGET = 150000;
	public static final int DEFAULT_ENERGY_TRANSFER = 2500;
	public static final int DEFAULT_CONNECTION_DISTANCE = 16;
	
	protected Direction direction;
	
	private Optional<AbstractStargateEntity> stargate = Optional.empty();
	protected Optional<Vec3i> stargateRelativePos = Optional.empty();
	
	protected boolean isCenterButtonEngaged = false;
	protected Address address = new Address(true);
	
	protected boolean enableAdvancedProtocols = false;
	protected boolean enableCFD = false;
	
	protected long energyTarget = DEFAULT_ENERGY_TARGET;
	protected int maxEnergyTransfer = DEFAULT_ENERGY_TRANSFER;
	
	public AbstractDHDEntity(BlockEntityType<?> blockEntity, BlockPos pos, BlockState state)
	{
		super(blockEntity, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		
		if(this.getLevel().isClientSide())
			return;
		
		this.setStargate();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		if(tag.contains(STARGATE_POS))
		{
			int[] pos = tag.getIntArray(STARGATE_POS);
			stargateRelativePos = Optional.of(new Vec3i(pos[0], pos[1], pos[2]));
		}
		
		super.load(tag);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		if(stargateRelativePos.isPresent())
		{
			Vec3i pos = stargateRelativePos.get();
			tag.putIntArray(STARGATE_POS, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		}
	}
	
	public int getMaxDistance()
	{
		return DEFAULT_CONNECTION_DISTANCE;
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget;
	}
	
	public long getMaxEnergyTransfer()
	{
		return this.maxEnergyTransfer;
	}
	
	protected void updateStargate()
	{
		if(this.stargate.isEmpty())
			return;
		
		AbstractStargateEntity stargate = this.stargate.get();
		
		if(stargate == null)
			return;
		
		stargate.setDHD(this, this.enableAdvancedProtocols ? 10 : 0);
	}
	
	protected boolean setStargateFromPos(BlockPos pos)
	{
		BlockEntity blockEntity = this.getLevel().getBlockEntity(pos);
		if(blockEntity instanceof AbstractStargateEntity stargate)
		{
			//this.stargateRelativePos = Optional.of(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
			this.stargate = Optional.of(stargate);
			
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Sets the DHD's Stargate to the position specified by the DHD's saved Stargate relative position.
	 * If there is no Stargate at that position, the DHD will remove that position from its memory.
	 * If there is no position saved, DHD will attempt to find a new Stargate
	 */
	public void setStargate()
	{
		if(this.getLevel() == null)
			return;
			
		updateStargate();
		
		if(stargate.isPresent())
			return;

		if(stargateRelativePos.isEmpty())
			stargateRelativePos = findNearestStargate(getMaxDistance());

		if(stargateRelativePos.isPresent())
		{
			Vec3i pos = stargateRelativePos.get();
			
			Direction direction = getDirection();
			
			if(direction != null)
			{
				BlockPos stargatePos = CoordinateHelper.Relative.getOffsetPos(direction, this.getBlockPos(), pos);
				
				if(stargatePos != null && !setStargateFromPos(stargatePos))
					stargateRelativePos = Optional.empty();
			}
		}
		
		this.setChanged();
	}
	
	public void unsetStargate()
	{
		if(stargate.isPresent())
		{
			stargate.get().unsetDHD(false);
			stargate = Optional.empty();
		}
		
		if(stargateRelativePos.isPresent())
			stargateRelativePos = Optional.empty();
		
		updateDHD(new Address(), false);
		
		this.setChanged();
	}
	
	public void updateDHD(Address address, boolean isStargateConnected)
	{
		this.setAddress(address);
		this.setCenterButtonEngaged(isStargateConnected);
		this.updateClient();
	}
	
	public void setAddress(Address address)
	{
		this.address = address;
	}
	
	public Address getAddress()
	{
		return this.address;
	}
	
	public void setCenterButtonEngaged(boolean isCenterButtonEngaged)
	{
		this.isCenterButtonEngaged = isCenterButtonEngaged;
	}
	
	public boolean isCenterButtonEngaged()
	{
		return this.isCenterButtonEngaged;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return false;
	}

	@Override
	protected long capacity()
	{
		return 0;
	}

	@Override
	protected long maxReceive()
	{
		return 0;
	}

	@Override
	protected long maxExtract()
	{
		return 0;
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		if(!this.stargate.isPresent())
			return;
		
		AbstractStargateEntity stargate = this.stargate.get();
		
		if(stargate == null)
			return;
		
		if(stargate.getEnergyStored() < getEnergyTarget())
		{
			long needed = getEnergyTarget() - stargate.getEnergyStored();
			
			long energySent = needed > getMaxEnergyTransfer() ? getMaxEnergyTransfer() : needed;
			
			stargate.receiveEnergy(energySent, false);
		}
	}

	public void setCFDState(boolean state){
		this.enableCFD = state;
	}

	public boolean getCFDState(){
		return this.enableCFD;
	}
    
    protected BlockState getState()
    {
    	BlockPos gatePos = this.getBlockPos();
		return this.level.getBlockState(gatePos);
    }
	
	public Direction getDirection()
	{
		if(this.direction == null)
		{
			BlockState gateState = getState();
			
			if(gateState.getBlock() instanceof AbstractDHDBlock)
				this.direction = gateState.getValue(AbstractDHDBlock.FACING);
			else
				StargateJourney.LOGGER.error("Couldn't find DHD Direction");
		}
		
		return this.direction;
	}
	
	protected List<AbstractStargateEntity> getNearbyStargates(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = this.level.getChunk(this.getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(this.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
						stargates.add(stargate);
				});
			}
		}
		
		return stargates;
	}
	
	private double distance(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		double stargateDistance = Math.sqrt(x*x + y*y + z*z);
		
		return stargateDistance;
	}
	
	public Optional<Vec3i> findNearestStargate(int maxDistance)
	{
		List<AbstractStargateEntity> stargates = getNearbyStargates(maxDistance);
		
		stargates.sort((stargateA, stargateB) ->
				Double.valueOf(distance(this.getBlockPos(), stargateA.getBlockPos()))
				.compareTo(Double.valueOf(distance(this.getBlockPos(), stargateB.getBlockPos()))));
		
		if(!stargates.isEmpty())
		{
			Iterator<AbstractStargateEntity> iterator = stargates.iterator();
			
			while(iterator.hasNext())
			{
				AbstractStargateEntity stargate = iterator.next();
				
				if(!stargate.hasDHD())
				{
					Direction direction = getDirection();
					
					if(direction != null)
					{
						this.stargate = Optional.of(stargate);
						return Optional.of(CoordinateHelper.Relative.getRelativeOffset(direction, this.getBlockPos(), stargate.getBlockPos()));
					}
				}
				
			}
		}
		
		return Optional.empty();
	}
	
	public void sendMessageToNearbyPlayers(Component message, int distance)
	{
		AABB localBox = new AABB((getBlockPos().getX() - distance), (getBlockPos().getY() - distance), (getBlockPos().getZ() - distance), 
				(getBlockPos().getX() + 1 + distance), (getBlockPos().getY() + 1 + distance), (getBlockPos().getZ() + 1 + distance));
		level.getEntitiesOfClass(Player.class, localBox).stream().forEach((player) -> player.displayClientMessage(message, true));
	}
	
	protected abstract SoundEvent getEnterSound();
	
	protected abstract SoundEvent getPressSound();
	
	/**
	 * Engages the next Stargate chevron
	 * @param symbol
	 */
	public void engageChevron(int symbol)
	{
		if(this.stargate.isPresent())
		{
			AbstractStargateEntity stargate = this.stargate.get();
			
			if(symbol == 0)
				level.playSound((Player)null, this.getBlockPos(), getEnterSound(), SoundSource.BLOCKS, 0.5F, 1F);
			else
				level.playSound((Player)null, this.getBlockPos(), getPressSound(), SoundSource.BLOCKS, 0.25F, 1F);
			
			stargate.engageSymbol(symbol);
		}
		else
			sendMessageToNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED), 5);
	}
	
	public boolean isSymbolEngaged(int symbol)
	{
		return this.address.containsSymbol(symbol);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractDHDEntity dhd)
    {
		if(level.isClientSide())
			return;
		
		dhd.outputEnergy(null);
    }
	
	public void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundDHDUpdatePacket(this.worldPosition, StargateJourney.EMPTY, this.address.toArray(), this.isCenterButtonEngaged));
	}
}
