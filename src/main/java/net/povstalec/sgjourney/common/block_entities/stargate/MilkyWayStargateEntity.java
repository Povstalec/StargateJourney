package net.povstalec.sgjourney.common.block_entities.stargate;

import java.util.Map;
import java.util.Random;

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
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.packets.ClientboundMilkyWayStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Addressing;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public class MilkyWayStargateEntity extends AbstractStargateEntity
{
	private static final double angle = (double) 360 / 39;
	
	private int rotation = 0;
	public int oldRotation = 0;
	public boolean isChevronRaised = false;
	private Map<StargatePart, Integer> signalMap = Maps.newHashMap();
	public int signalStrength = 0;
	
	public boolean computerRotation = false;
	public int desiredSymbol = 0;
	public boolean rotateClockwise = true;
	
	public MilkyWayStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.MILKY_WAY_STARGATE.get(), pos, state, Stargate.Gen.GEN_2);
	}
	
	@Override
    public void onLoad()
	{
        //Rotate the ring randomly
        if(!this.level.isClientSide() && !addToNetwork)
        {
        	Random random = new Random();
        	setRotation(2 * random.nextInt(0, 181));
        }
		
        super.onLoad();
        
        if(this.level.isClientSide())
        	return;
        
        if(!isPointOfOriginValid(this.level))
        {
        	StargateJourney.LOGGER.info("PoO is not valid " + this.pointOfOrigin);
        	setPointOfOrigin(this.getLevel());
        }
        
        if(!areSymbolsValid(this.level))
        {
        	StargateJourney.LOGGER.info("Symbols are not valid " + this.symbols);
        	setSymbols(this.getLevel());
        }
    }
	
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		
		this.pointOfOrigin = nbt.getString("PointOfOrigin");
		this.symbols = nbt.getString("Symbols");
		this.rotation = nbt.getInt("Rotation");
		this.oldRotation = this.rotation;
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
		
		if(this.signalStrength > 0)
		{
			if(this.signalStrength == 15)
			{
				if(!isConnected())
					raiseChevron();
				else
					disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_POINT_OF_ORIGIN);
			}
		}
		else
		{
			lowerChevron();
		}
		
		if(!this.level.isClientSide())
			synchronizeWithClient(this.level);
	}
	
	private void setBestRedstoneSignal()
	{
		this.signalStrength = 0;
		this.signalMap.forEach((stargatePart, signal) -> 
		{
			if(signal > this.signalStrength)
				this.signalStrength = signal;
		});
	}
	
	public void updateSignal(StargatePart part, int signal)
	{
		if(!CommonStargateConfig.enable_redstone_dialing.get())
			return;
		
		if(this.signalMap.containsKey(part))
			this.signalMap.remove(part);
		this.signalMap.put(part, signal);
		
		manualDialing();
	}
	
	public int getRotation()
	{
		return this.rotation;
	}
	
	public float getRotation(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getRotation() : Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isRotating()
	{
		return this.rotation != this.oldRotation;
	}
	
	public Stargate.Feedback raiseChevron()
	{
		if(!this.isChevronRaised && !Addressing.addressContainsSymbol(getAddress(), getCurrentSymbol()))
		{
			this.level.playSound((Player)null, this.worldPosition, SoundInit.MILKY_WAY_CHEVRON_ENCODE.get(), SoundSource.BLOCKS, 0.25F, 1F);
			this.isChevronRaised = true;
			return Stargate.Feedback.CHEVRON_RAISED;
		}
		return Stargate.Feedback.CHEVRON_ALREADY_RAISED;
	}
	
	public Stargate.Feedback lowerChevron()
	{
		if(this.isChevronRaised)
		{
			engageSymbol(getCurrentSymbol());
			this.isChevronRaised = false;
			return Stargate.Feedback.CHEVRON_LOWERED;
		}
		
		if(!this.level.isClientSide())
			synchronizeWithClient(this.level);
		
		return Stargate.Feedback.CHEVRON_ALREADY_LOWERED;
	}
	
	public int getCurrentSymbol()
	{
		int currentSymbol;
		double position = this.rotation / angle;
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
		if(!isConnected() && !this.isChevronRaised)
		{
			if(this.computerRotation)
			{
				if(isCurrentSymbol(this.desiredSymbol))
					endRotation();
				else
					rotate(this.rotateClockwise);
			}
			else if(this.signalStrength > 0 && this.signalStrength < 15)
			{
				if(this.signalStrength > 7)
					rotate(false);
				else
					rotate(true);
			}
			else
				syncRotation();
		}
		else
			syncRotation();
		setChanged();
	}
	
	public void rotate(boolean clockwise)
	{
		this.oldRotation = this.rotation;
		
		if(clockwise)
			this.rotation -= 2;
		else
			this.rotation += 2;
		
		if(this.rotation >= 360)
		{
			this.rotation -= 360;
			this.oldRotation -= 360;
		}
		else if(this.rotation < 0)
		{
			this.rotation += 360;
			this.oldRotation += 360;
		}
		setChanged();
	}
	
	public boolean isCurrentSymbol(int desiredSymbol)
	{
		double position = this.rotation / angle;
		double lowerBound = (double) (desiredSymbol - 0.1);
		double upperBound = (double) (desiredSymbol + 0.1);
		
		if(position > lowerBound && position < upperBound)
			return true;
		
		return false;
	}
	
	private void synchronizeWithClient(Level level)
	{
		if(level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundMilkyWayStargateUpdatePacket(this.worldPosition, this.rotation, this.oldRotation, this.isChevronRaised, this.signalStrength, this.computerRotation, this.rotateClockwise, this.desiredSymbol));
	}
	
	private void syncRotation()
	{
		this.oldRotation = this.rotation;
		if(!this.level.isClientSide())
			synchronizeWithClient(this.level);
	}
	
	public void startRotation(int desiredSymbol, boolean rotateClockwise)
	{
		this.computerRotation = true;
		this.desiredSymbol = desiredSymbol;
		this.rotateClockwise = rotateClockwise;
		
		synchronizeWithClient(this.level);
	}
	
	public void endRotation()
	{
		this.computerRotation = false;
		
		synchronizeWithClient(this.level);
	}
}
