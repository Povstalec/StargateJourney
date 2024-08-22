package net.povstalec.sgjourney.common.block_entities.stargate;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundUniverseStargateUpdatePacket;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.ChevronLockSpeed;

public class UniverseStargateEntity extends AbstractStargateEntity
{
	public static final boolean FAST_ROTATION = CommonStargateConfig.universe_fast_rotation.get();
	
	public static final int MAX_ROTATION = 54 * (FAST_ROTATION ? 2 : 3); // 108 : 162
	public static final int ROTATION_INCREASE = 1; // 1
	
	public static final int ANGLE = MAX_ROTATION / 54;
	public static final int ROTATION_THIRD = MAX_ROTATION / 3;
	public static final int RESET_DEGREES = ROTATION_THIRD * 2;
	
	public static final int WAIT_TICKS = 20;
	
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
		super(BlockEntityInit.UNIVERSE_STARGATE.get(), new ResourceLocation(StargateJourney.MODID, "universe/universe"), pos, state, Stargate.Gen.GEN_1, 1);
		this.setOpenSoundLead(8);
		this.symbolBounds = 35;
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
	public void updateDHD()
	{
		if(hasDHD())
			this.dhd.get().updateDHD(!this.isConnected() || (this.isConnected() && this.isDialingOut()) ? 
					addressBuffer : new Address(), addressBuffer.hasPointOfOrigin() || this.isConnected());
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
		
		if(isSymbolOutOfBounds(symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_OUT_OF_BOUNDS);
		
		if(isConnected())
		{
			if(symbol == 0)
				return disconnectStargate(Stargate.Feedback.CONNECTION_ENDED_BY_DISCONNECT, true);
			else
				return setRecentFeedback(Stargate.Feedback.ENCODE_WHEN_CONNECTED);
		}
		else if(symbol == 0 && !isConnected() && addressBuffer.getLength() == 0)
			return setRecentFeedback(Stargate.Feedback.INCOMPLETE_ADDRESS);
		
		if(this.addressBuffer.containsSymbol(symbol))
			return setRecentFeedback(Stargate.Feedback.SYMBOL_IN_ADDRESS);
		
		if(addressBuffer.getLength() == 0 && address.getLength() == 0)
			startSound();
		
		addressBuffer.addSymbol(symbol);
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundUniverseStargateUpdatePacket(this.worldPosition, this.symbolBuffer, this.addressBuffer.toArray(), this.animationTicks, this.rotation, this.oldRotation));
		return setRecentFeedback(Stargate.Feedback.SYMBOL_ENCODED);
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
	
	public static void tick(Level level, BlockPos pos, BlockState state, UniverseStargateEntity stargate)
	{
		if(!stargate.isConnected() && stargate.addressBuffer.getLength() > stargate.symbolBuffer)
		{
			if(stargate.animationTicks <= 0)
				stargate.rotateToSymbol(stargate.addressBuffer.getSymbol(stargate.symbolBuffer), true);
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
	
	public int getDesiredRotation(int desiredSymbol)
	{
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		return 3 * (ANGLE / 2) + whole * MAX_ROTATION / 9 + (ANGLE * leftover);
	}
	
	public void rotate(boolean clockwise)
	{
		if(clockwise)
			rotation -= ROTATION_INCREASE;
		else
			rotation += ROTATION_INCREASE;

		if(rotation >= MAX_ROTATION)
		{
			rotation -= MAX_ROTATION;
			oldRotation -= MAX_ROTATION;
		}
		else if(rotation < 0)
		{
			rotation += MAX_ROTATION;
			oldRotation += MAX_ROTATION;
		}
		setChanged();
	}
	
	public boolean isCurrentSymbol(int desiredSymbol)
	{
		if(getDesiredRotation(desiredSymbol) == rotation)
			return true;
		
		return false;
	}
	
	@Override
	public int getRedstoneSymbolOutput()
	{
		int halfRotation = rotation / 2;
		
		if(halfRotation % 6 == 0 || halfRotation % 6 == 1)
			return 0;
		
		int emptyASymbols = halfRotation / 6 + 1;
		int emptyBSymbols = halfRotation / 6;
		
		int result = (rotation / 2 - emptyASymbols - emptyBSymbols);
		
		return result % 12 + 1;
	}
	
	@Override
	public int getRedstoneSegmentOutput()
	{
		return ((rotation / ROTATION_THIRD) + 1) * 5;
	}
	
	public float getRotation(float partialTick)
	{
		return StargateJourneyConfig.disable_smooth_animations.get() ?
				(float) getRotation() : Mth.lerp(partialTick, this.oldRotation, this.rotation);
	}
	
	private void rotateToSymbol(int desiredSymbol, boolean engage)
	{
		oldRotation = rotation;
		
		if(isCurrentSymbol(desiredSymbol))
		{
			if(!engage)
				return;
			
			updateClient();
			
			if(isCurrentSymbol(0))
				this.lockPrimaryChevron();
			else
				this.encodeChevron(desiredSymbol, false, false);
			
			updateClient();
		}
		else
		{
			if(CommonStargateConfig.universe_best_direction.get())
				rotate(getBestRotationDirection(desiredSymbol));
			else
				rotate(getAlternatingRotationDirection(this.getAddress().getLength()));
		}
	}
	
	private void rotateToDefault()
	{
		oldRotation = rotation;
		
		if(rotation == RESET_DEGREES)
			updateClient();
		else
			rotate(getBestRotationDirection(RESET_DEGREES, rotation));
	}
	
	private boolean getAlternatingRotationDirection(int addressLength)
	{
		return addressLength % 2 == 1;
	}
	
	private boolean getBestRotationDirection(int desiredSymbol)
	{
		int whole = desiredSymbol / 4;
		int leftover = desiredSymbol % 4;
		
		double desiredPosition = 3 * (ANGLE / 2) + whole * MAX_ROTATION / 9 + ANGLE * leftover;
		
		double position = (double) rotation;
		
		return getBestRotationDirection(desiredPosition, position);
	}
	
	private static boolean getBestRotationDirection(double desiredRotation, double rotation)
	{
		double difference = desiredRotation - rotation;
		
		if(difference >= MAX_ROTATION / 2)
			rotation =+ MAX_ROTATION;
		else if(difference <= -MAX_ROTATION / 2)
			rotation =- MAX_ROTATION;
		
		double lowerBound = (double) (desiredRotation - 1);
		
		if(rotation > lowerBound)
			return true;
		else
			return false;
	}

	@Override
	protected void resetAddress(boolean updateInterfaces)
	{
		animationTicks = 1;
		symbolBuffer = 0;
		addressBuffer.reset();
		super.resetAddress(updateInterfaces);
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
	
	@Override
	public void doWhileDialed(int openTime, Stargate.ChevronLockSpeed chevronLockSpeed)
	{
		if(this.level.isClientSide())
			return;
		
		if(openTime == 1)
			startSound();
		
		this.rotateToSymbol(0, false);
	}
}
