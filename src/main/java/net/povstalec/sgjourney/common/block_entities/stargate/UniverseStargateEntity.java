package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.compatibility.cctweaked.CCTweakedCompatibility;
import net.povstalec.sgjourney.common.compatibility.cctweaked.StargatePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundUniverseStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.ConnectionState;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

public class UniverseStargateEntity extends AbstractStargateEntity
{
	public static final int WAIT_TICKS = 20;
	public static final int RESET_DEGREES = 240;
	
	public int animationTicks = 1;
	
	protected static final String UNIVERSAL = StargateJourney.MODID + ":universal";
	protected static final String POINT_OF_ORIGIN = UNIVERSAL;
	protected static final String SYMBOLS = UNIVERSAL;

	public int oldRotation = RESET_DEGREES;
	public int rotation = RESET_DEGREES;
	
	public Address addressBuffer = new Address(true);
	public int symbolBuffer = 0;
	
	public UniverseStargateEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.UNIVERSE_STARGATE.get(), pos, state, Stargate.Gen.GEN_1, 1);
		this.setOpenSoundLead(8);
	}
	
	@Override
	public void onLoad()
	{
		if(level.isClientSide())
			return;
		
		setPointOfOrigin(POINT_OF_ORIGIN);
        setSymbols(SYMBOLS);
        
        super.onLoad();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
        super.load(tag);
        
        rotation = tag.getInt("Rotation");
        oldRotation = rotation;
        addressBuffer.fromArray(tag.getIntArray("AddressBuffer"));
        symbolBuffer = tag.getInt("SymbolBuffer");
    }
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		
		tag.putInt("Rotation", rotation);
		tag.putIntArray("AddressBuffer", addressBuffer.toArray());
		tag.putInt("SymbolBuffer", symbolBuffer);
	}

	@Override
	public SoundEvent getChevronEngageSound()
	{
		return SoundInit.UNIVERSE_CHEVRON_ENGAGE.get();
	}

	@Override
	public SoundEvent getWormholeOpenSound()
	{
		return SoundInit.UNIVERSE_WORMHOLE_OPEN.get();
	}

	@Override
	public SoundEvent getWormholeCloseSound()
	{
		return SoundInit.UNIVERSE_WORMHOLE_CLOSE.get();
	}

	@Override
	public SoundEvent getFailSound()
	{
		return SoundInit.UNIVERSE_DIAL_FAIL.get();
	}
	
	public double angle()
	{
		return (double) 360 / 54;
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
		return this.rotation != this.oldRotation;
	}
	
	@Override
	public Stargate.Feedback engageSymbol(int symbol)
	{
		if(level.isClientSide())
			return Stargate.Feedback.NONE;
		
		if(getAddress().containsSymbol(symbol))
			return Stargate.Feedback.SYMBOL_ENCODED;
		
		if(symbol > 35)
			return Stargate.Feedback.SYMBOL_OUT_OF_BOUNDS;
		
		if(symbol == 0)
		{
			if(isConnected())
				return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
			else if(!isConnected() && addressBuffer.getLength() == 0)
				return Stargate.Feedback.INCOMPLETE_ADDRESS;
		}
		
		if(addressBuffer.getLength() == 0 && address.getLength() == 0)
			startSound();
		
		addressBuffer.addSymbol(symbol);
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundUniverseStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer.toArray(), this.animationTicks, this.rotation, this.oldRotation));
		return Stargate.Feedback.SYMBOL_ENCODED;
	}
	
	@Override
	protected Stargate.Feedback lockPrimaryChevron()
	{
		return super.lockPrimaryChevron();
	}
	
	@Override
	public Stargate.Feedback encodeChevron(int symbol, boolean incoming, boolean encode)
	{
		symbolBuffer++;
		animationTicks++;
		
		Stargate.Feedback feedback = super.encodeChevron(symbol, incoming, encode);
		return feedback;
	}
	
	public void startSound()
	{
		if(!level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.UniverseStart(this.worldPosition));
	}
	
	public SoundEvent getStartSound()
	{
		return SoundInit.UNIVERSE_DIAL_START.get();
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, UniverseStargateEntity stargate)
	{
		if(!stargate.isConnected() && stargate.addressBuffer.getLength() > stargate.symbolBuffer)
		{
			if(stargate.animationTicks <= 0)
				stargate.rotateToSymbol(stargate.addressBuffer.getSymbol(stargate.symbolBuffer));
			else if(stargate.animationTicks >= WAIT_TICKS)
				stargate.animationTicks = 0;
			else if(stargate.animationTicks > 0)
				stargate.animationTicks++;
		}
		else if(!stargate.isConnected() && stargate.addressBuffer.getLength() == 0)
			stargate.rotateToDefault();
		else
			stargate.updateClient();
		
		if(!stargate.level.isClientSide())
		{
			if(stargate.isRotating())
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientBoundSoundPackets.StargateRotation(stargate.worldPosition, false));
			else
				PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(stargate.worldPosition)), new ClientBoundSoundPackets.StargateRotation(stargate.worldPosition, true));
		}
		
		AbstractStargateEntity.tick(level, pos, state, (AbstractStargateEntity) stargate);
	}
	
	public void rotate(boolean clockwise)
	{
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
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		double desiredPosition = 3 * (angle() / 2) + whole * 40 + (angle() * leftover);
		
		double position = (double) rotation;
		double lowerBound = (double) (desiredPosition - 1);
		double upperBound = (double) (desiredPosition + 1);
		
		if(position > lowerBound && position < upperBound)
			return true;
		
		return false;
	}
	
	public float getRotation(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getRotation() : Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	private void rotateToSymbol(int desiredSymbol)
	{
		oldRotation = rotation;
		
		if(isCurrentSymbol(desiredSymbol))
		{
			updateClient();
			
			if(isCurrentSymbol(0))
				this.lockPrimaryChevron();
			else
				this.encodeChevron(desiredSymbol, false, false);
			
			updateClient();
		}
		else
			rotate(getBestRotationDirection(desiredSymbol));
	}
	
	private void rotateToDefault()
	{
		oldRotation = rotation;
		
		if(rotation == RESET_DEGREES)
			updateClient();
		else
			rotate(getBestRotationDirection(RESET_DEGREES, rotation));
	}
	
	private boolean getBestRotationDirection(int desiredSymbol)
	{
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		double desiredPosition = 3 * (angle() / 2) + whole * 40 + angle() * leftover;
		
		double position = (double) rotation;
		
		return getBestRotationDirection(desiredPosition, position);
	}
	
	private static boolean getBestRotationDirection(double desiredRotation, double rotation)
	{
		double difference = desiredRotation - rotation;
		
		if(difference >= 180.0D)
			rotation =+ 360.0D;
		else if(difference <= -180.0D)
			rotation =- 360.0D;
		
		double lowerBound = (double) (desiredRotation - 1);
		
		if(rotation > lowerBound)
			return true;
		else
			return false;
	}
	
	@Override
	public Stargate.Feedback resetStargate(Stargate.Feedback feedback)
	{
		animationTicks = 1;
		symbolBuffer = 0;
		addressBuffer.reset();
		
		if(isConnected())
		{
			closeWormholeSound();
			setConnected(ConnectionState.IDLE);
		}
		
		resetAddress();
		this.connectionID = EMPTY;
		
		if(feedback.playFailSound() && !level.isClientSide())
			PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientBoundSoundPackets.Fail(this.worldPosition));
		
		setChanged();
		StargateJourney.LOGGER.info("Reset Stargate at " + this.getBlockPos().getX() + " " + this.getBlockPos().getY() + " " + this.getBlockPos().getZ());
		return setRecentFeedback(feedback);
	}
	
	@Override
	public void updateClient()
	{
		super.updateClient();
		
		if(this.level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundUniverseStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer.toArray(), this.animationTicks, this.rotation, this.oldRotation));
	}

	@Override
	public void playRotationSound()
	{
		if(!this.spinSound.isPlaying())
			this.spinSound.playSound();
	}

	@Override
	public void stopRotationSound()
	{
		this.spinSound.stopSound();
	}

	@Override
	public ChevronLockSpeed getChevronLockSpeed()
	{
		return CommonStargateConfig.universe_chevron_lock_speed.get();
	}

	@Override
	public void registerInterfaceMethods(StargatePeripheralWrapper wrapper)
	{
		CCTweakedCompatibility.registerUniverseStargateMethods(wrapper);
	}
}
