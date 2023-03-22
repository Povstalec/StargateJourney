package net.povstalec.sgjourney.block_entities.stargate;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.config.ServerStargateConfig;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.init.PacketHandlerInit;
import net.povstalec.sgjourney.init.SoundInit;
import net.povstalec.sgjourney.packets.ClientboundMilkyWayStargateUpdatePacket;
import net.povstalec.sgjourney.stargate.Addressing;
import net.povstalec.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.stargate.StargatePart;

public class MilkyWayStargateEntity extends AbstractStargateEntity
{
	private int rotation = 0;
	public int oldRotation = 0;
	public boolean isChevronRaised;
	private Map<StargatePart, Integer> signalMap = Maps.newHashMap();
	public int signalStrength = 0;
	
	public MilkyWayStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.MILKY_WAY_STARGATE.get(), pos, state, Stargate.Gen.GEN_2);
	}
	
	@Override
    public void onLoad()
	{
        super.onLoad();
        
        if(level.isClientSide())
        	return;
        
        if(!isPointOfOriginValid(level))
        {
        	StargateJourney.LOGGER.info("PoO is not valid " + pointOfOrigin);
        	setPointOfOrigin(this.getLevel());
        }
        
        if(!areSymbolsValid(level))
        {
        	StargateJourney.LOGGER.info("Symbols are not valid " + symbols);
        	setSymbols(this.getLevel());
        }
    }
	
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		
		pointOfOrigin = nbt.getString("PointOfOrigin");
		symbols = nbt.getString("Symbols");
		rotation = nbt.getInt("Rotation");
		oldRotation = rotation;
	}
	
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		
		nbt.putString("PointOfOrigin", pointOfOrigin);
		nbt.putString("Symbols", symbols);
		nbt.putInt("Rotation", rotation);
	}
	
	public SoundEvent chevronEngageSound()
	{
		return SoundInit.MILKY_WAY_CHEVRON_ENGAGE.get();
	}
	
	public SoundEvent failSound()
	{
		return SoundInit.MILKY_WAY_DIAL_FAIL.get();
	}
	
	private void manualDialing()
	{
		setBestRedstoneSignal();
		
		if(signalStrength > 0)
		{
			if(signalStrength == 15)
			{
				if(!isConnected())
					raiseChevron();
				else
					disconnectStargate();
			}
		}
		else
		{
			lowerChevron();
		}
		
		if(!level.isClientSide())
			this.synchronizeWithClient(level);
	}
	
	private void setBestRedstoneSignal()
	{
		signalStrength = 0;
		signalMap.forEach((stargatePart, signal) -> 
		{
			if(signal > signalStrength)
				signalStrength = signal;
		});
	}
	
	public void updateSignal(StargatePart part, int signal)
	{
		if(!ServerStargateConfig.enable_redstone_dialing.get())
			return;
		
		if(signalMap.containsKey(part))
			signalMap.remove(part);
		signalMap.put(part, signal);
		
		manualDialing();
	}
	
	public static double angle()
	{
		return (double) 360/39;
	}
	
	public int getRotation()
	{
		return rotation;
	}
	
	public float getRotation(float partialTick)
	{
		return Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isRotating()
	{
		return rotation != oldRotation;
	}
	
	public boolean raiseChevron()
	{
		if(!isChevronRaised && !Addressing.addressContainsSymbol(getAddress(), getCurrentSymbol()))
		{
			level.playSound((Player)null, worldPosition, SoundInit.MILKY_WAY_CHEVRON_ENCODE.get(), SoundSource.BLOCKS, 0.25F, 1F);
			isChevronRaised = true;
			return true;
		}
		return false;
	}
	
	public boolean lowerChevron()
	{
		if(isChevronRaised)
		{
			engageSymbol(getCurrentSymbol());
			isChevronRaised = false;
			return true;
		}
		
		if(!level.isClientSide())
			this.synchronizeWithClient(this.level);
		
		return false;
	}
	
	public int getCurrentSymbol()
	{
		int currentSymbol;
		double position = rotation / angle();
		currentSymbol = (int) position;
		if(position >= currentSymbol + 0.5)
			currentSymbol++;
		
		if(currentSymbol > 38)
			currentSymbol = currentSymbol - 39;
		
		return currentSymbol;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, MilkyWayStargateEntity stargate)
	{
		stargate.rotate();
		
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
	}
	
	private void rotate()
	{
		if(!isConnected() && !isChevronRaised)
		{
			if(signalStrength > 0 && signalStrength < 15)
			{
				if(signalStrength > 7)
					rotate(false);
				else
					rotate(true);
			}
			else
			{
				this.oldRotation = this.rotation;
				if(!level.isClientSide())
					this.synchronizeWithClient(this.level);
			}
				
		}
		else
		{
			this.oldRotation = this.rotation;
			if(!level.isClientSide())
				this.synchronizeWithClient(this.level);
		}
		setChanged();
	}
	
	public void rotate(boolean clockwise)
	{
		oldRotation = rotation;
		
		if(clockwise)
			rotation -= 2;
		else
			rotation += 2;
		
		if(rotation >= 360)
		{
			rotation -= 360;
			oldRotation -= 360;
		}
		else if(rotation < 0)
		{
			rotation += 360;
			oldRotation += 360;
		}
		setChanged();
	}
	
	public boolean isCurrentSymbol(int desiredSymbol)
	{
		double position = rotation / angle();
		double lowerBound = (double) (desiredSymbol - 0.1);
		double upperBound = (double) (desiredSymbol + 0.1);
		
		if(position > lowerBound && position < upperBound)
			return true;
		
		return false;
	}
	
	private void synchronizeWithClient(Level level)
	{
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundMilkyWayStargateUpdatePacket(this.worldPosition, this.rotation, this.oldRotation, this.isChevronRaised, this.signalStrength));
	}
	
}
