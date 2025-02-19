package net.povstalec.sgjourney.common.block_entities.stargate;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.client.sound.SoundWrapper;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundRotatingStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Stargate;

import javax.annotation.Nullable;
import java.util.Map;

public abstract class RotatingStargateEntity extends IrisStargateEntity
{
	public static final String ROTATION = "Rotation"; //TODO Change to "rotation"
	
	public static final int SEGMENTS = 3;
	public static final int ROTATION_INCREASE = 1;
	
	// Rotation stuff
	protected final int totalSymbols;
	protected final int maxRotation;
	protected final int stepsPerSymbol;
	protected final int symbolAddition;
	
	protected int rotation;
	protected int oldRotation;
	
	@Nullable
	public SoundWrapper buildupSound = null;
	
	// Redstone signal stuff (Manual dialing)
	protected Map<StargatePart, Integer> signalMap; //TODO Maybe start saving the map?
	public int previousSignalStrength;
	public int signalStrength;
	
	public boolean computerRotation;
	public int desiredSymbol;
	public boolean rotateClockwise;
	
	public RotatingStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork,
							  float verticalCenterHeight, float horizontalCenterHeight, int totalSymbols, int maxRotation)
	{
		super(blockEntity, defaultVariant, pos, state, gen, defaultNetwork, verticalCenterHeight, horizontalCenterHeight);
		
		this.totalSymbols = totalSymbols;
		this.maxRotation = maxRotation;
		this.stepsPerSymbol = this.maxRotation / this.totalSymbols;
		this.symbolAddition = this.stepsPerSymbol / 2;
		
		this.rotation = 0;
		this.oldRotation = 0;
		
		this.signalMap = Maps.newHashMap();
		this.previousSignalStrength = 0;
		this.signalStrength = 0;
		
		this.computerRotation = false;
		this.desiredSymbol = 0;
		this.rotateClockwise = true;
	}
	
	public RotatingStargateEntity(BlockEntityType<?> blockEntity, ResourceLocation defaultVariant, BlockPos pos, BlockState state, Stargate.Gen gen, int defaultNetwork,
								  int totalSymbols, int maxRotation)
	{
		this(blockEntity, defaultVariant, pos, state, gen, defaultNetwork, VERTICAL_CENTER_STANDARD_HEIGHT, HORIZONTAL_CENTER_STANDARD_HEIGHT, totalSymbols, maxRotation);
	}
	
	@Override
	public CompoundTag serializeStargateInfo(CompoundTag tag)
	{
		super.serializeStargateInfo(tag);
		
		tag.putInt(ROTATION, rotation);
		
		return tag;
	}
	
	@Override
	public void deserializeStargateInfo(CompoundTag tag, boolean isUpgraded)
	{
		if(tag.contains(ROTATION))
			rotation = tag.getInt(ROTATION);
		this.oldRotation = this.rotation;
		
		super.deserializeStargateInfo(tag, isUpgraded);
	}
	
	public int totalSymbols()
	{
		return this.totalSymbols;
	}
	
	//============================================================================================
	//******************************************Rotation******************************************
	//============================================================================================
	
	public int getRotation()
	{
		return this.rotation;
	}
	
	public double getRotationDegrees()
	{
		return (double) getRotation() / maxRotation * 360F;
	}
	
	public float getRotation(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getRotation() : Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	public float getRotationDegrees(float partialTick)
	{
		return getRotation(partialTick) / maxRotation * 360F;
	}
	
	public void setRotation(int oldRotation, int rotation)
	{
		this.oldRotation = oldRotation;
		this.rotation = rotation;
	}
	
	public void setRotation(int rotation)
	{
		this.rotation = rotation;
	}
	
	public boolean isRotating()
	{
		return this.rotation != this.oldRotation;
	}
	
	protected void rotate()
	{
		if(!isConnected())
		{
			if(this.computerRotation)
			{
				if(isCurrentSymbol(this.desiredSymbol))
					endRotation(false);
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
			this.rotation -= ROTATION_INCREASE;
		else
			this.rotation += ROTATION_INCREASE;
		
		if(this.rotation >= this.maxRotation)
		{
			this.rotation -= this.maxRotation;
			this.oldRotation -= this.maxRotation;
		}
		else if(this.rotation < 0)
		{
			this.rotation += this.maxRotation;
			this.oldRotation += this.maxRotation;
		}
		setChanged();
	}
	
	public Stargate.Feedback startRotation(int desiredSymbol, boolean rotateClockwise)
	{
		this.computerRotation = true;
		this.desiredSymbol = desiredSymbol;
		this.rotateClockwise = rotateClockwise;
		
		if(!this.level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.RotationStartup(worldPosition));
		
		synchronizeWithClient();
		
		return Stargate.Feedback.ROTATING;
	}
	
	public Stargate.Feedback endRotation(boolean playSound)
	{
		if(!this.computerRotation)
			return Stargate.Feedback.NOT_ROTATING;
		
		this.computerRotation = false;
		
		if(!this.level.isClientSide() && playSound)
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new ClientBoundSoundPackets.RotationStop(worldPosition));
		
		synchronizeWithClient();
		
		return Stargate.Feedback.ROTATION_STOPPED;
	}
	
	protected void syncRotation()
	{
		this.oldRotation = this.rotation;
		if(!this.level.isClientSide())
			synchronizeWithClient();
	}
	
	public void playBuildupSound()
	{
		if(this.buildupSound.isPlaying())
			this.buildupSound.stopSound();
		this.buildupSound.playSound();
	}
	
	@Override
	public void playRotationSound()
	{
		if(!this.spinSound.isPlaying())
		{
			this.spinSound.stopSound();
			this.spinSound.playSound();
		}
	}
	
	@Override
	public void stopRotationSound(){}
	
	//============================================================================================
	//***************************************Redstone Signal**************************************
	//============================================================================================
	
	protected boolean hadBestRedstoneSignalChanged()
	{
		this.previousSignalStrength = this.signalStrength;
		this.signalStrength = 0;
		this.signalMap.forEach((stargatePart, signal) ->
		{
			if(signal > this.signalStrength)
				this.signalStrength = signal;
		});
		
		return previousSignalStrength != signalStrength;
	}
	
	public void updateSignal(StargatePart part, int signal)
	{
		if(!CommonStargateConfig.enable_redstone_dialing.get())
			return;
		
		if(this.signalMap.containsKey(part))
			this.signalMap.remove(part);
		this.signalMap.put(part, signal);
		
		if(hadBestRedstoneSignalChanged())
			manualDialing();
	}
	
	//============================================================================================
	//***************************************Manual Dialing***************************************
	//============================================================================================
	
	public Stargate.Feedback encodeChevron()
	{
		if(!level.isClientSide())
			synchronizeWithClient();
		
		return setRecentFeedback(engageSymbol(getCurrentSymbol()));
	}
	
	public boolean isCurrentSymbol(int desiredSymbol)
	{
		return desiredSymbol * this.stepsPerSymbol == this.rotation;
	}
	
	public int getCurrentSymbol()
	{
		int symbolPosition = this.rotation + this.symbolAddition;
		
		int currentSymbol = (symbolPosition / stepsPerSymbol) % totalSymbols;
		
		return currentSymbol;
	}
	
	protected void manualDialing()
	{
		if(this.signalStrength == 15 && this.previousSignalStrength != this.signalStrength)
		{
			if(!isConnected())
				engageSymbol(getCurrentSymbol());
			else
				disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_POINT_OF_ORIGIN, true);
		}
		
		if(!this.level.isClientSide())
			synchronizeWithClient();
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		return (getCurrentSymbol() % (totalSymbols / SEGMENTS)) + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return (getCurrentSymbol() / (totalSymbols / SEGMENTS) + 1) * 5;
	}
	
	protected boolean synchronizeWithClient()
	{
		if(this.level.isClientSide())
			return false;
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.level.getChunkAt(this.worldPosition)), new ClientboundRotatingStargateUpdatePacket(this.worldPosition, this.rotation, this.oldRotation, this.signalStrength, this.computerRotation, this.rotateClockwise, this.desiredSymbol));
		return true;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, RotatingStargateEntity stargate)
	{
		stargate.rotate();
		if(stargate.isRotating() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientBoundSoundPackets.StargateRotation(stargate.worldPosition, false));
		
		AbstractStargateEntity.tick(level, pos, state, stargate);
	}
}
