package net.povstalec.sgjourney.block_entities.stargate;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
	private int oldRotation = 0;
	public boolean isChevronRaised;
	private Map<StargatePart, Integer> signalMap = Maps.newHashMap();
	private int signalStrength = 0;
	
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
		System.out.println(nbt.getString("PointOfOrigin"));
		System.out.println(nbt.getString("Symbols"));
		System.out.println(nbt.getInt("Rotation"));
		System.out.println(nbt.getString("ID"));
		System.out.println("Loaded Stargate");
	}
	
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		
		nbt.putString("PointOfOrigin", pointOfOrigin);
		nbt.putString("Symbols", symbols);
		nbt.putInt("Rotation", rotation);
		System.out.println(pointOfOrigin);
		System.out.println(symbols);
		System.out.println(rotation);
		System.out.println(getID());
		System.out.println("Saved Stargate");
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
			inputSymbol(getCurrentSymbol());
			isChevronRaised = false;
			return true;
		}
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
		
		if(level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientboundMilkyWayStargateUpdatePacket(stargate.worldPosition, stargate.getRotation(), stargate.isChevronRaised));
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
				
		}
		
		if(rotation >= 360)
			rotation = (short) (rotation - 360);
		else if(rotation < 0)
			rotation = (short) (rotation + 360);
		
		setChanged();
	}
	
	public void rotate(boolean clockwise)
	{
		oldRotation = rotation;
		
		if(clockwise)
			rotation -= 2;
		else
			rotation += 2;
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
	
}
