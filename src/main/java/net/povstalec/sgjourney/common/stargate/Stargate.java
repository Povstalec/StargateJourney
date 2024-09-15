package net.povstalec.sgjourney.common.stargate;

import java.util.Optional;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Stargate
{
	public static final String DIMENSION = "Dimension";
	public static final String COORDINATES = "Coordinates";

	public static final String HAS_DHD = "HasDHD";
	public static final String GENERATION = "Generation";
	public static final String TIMES_OPENED = "TimesOpened";
	
	public static final String NETWORK = "Network";
	
	private final Address.Immutable address;
	private final ResourceKey<Level> dimension;
	private final BlockPos blockPos;

	private boolean hasDHD;
	private Gen generation;
	private int timesOpened;
	
	//private boolean restrictNetwork;
	private int network;
	
	public Stargate(Address.Immutable address, ResourceKey<Level> dimension, BlockPos blockPos, boolean hasDHD, Gen generation, int timesOpened, int network)
	{
		this.address = address;
		this.dimension = dimension;
		this.blockPos = blockPos;

		this.hasDHD = hasDHD;
		this.generation = generation;
		this.timesOpened = timesOpened;
		
		//this.restrictNetwork = stargate.getRestrictNetwork();
		this.network = network;
	}
	
	public Stargate(AbstractStargateEntity stargate)
	{
		this(stargate.get9ChevronAddress().immutable(), stargate.getLevel().dimension(), stargate.getBlockPos(),
				stargate.hasDHD(), stargate.getGeneration(), stargate.getTimesOpened(), stargate.getNetwork());
	}
	
	public Address.Immutable get9ChevronAddress()
	{
		return this.address;
	}
	
	public ResourceKey<Level> getDimension()
	{
		return this.dimension;
	}
	
	public BlockPos getBlockPos()
	{
		return this.blockPos;
	}
	
	public boolean hasDHD()
	{
		return this.hasDHD;
	}
	
	public Gen getGeneration()
	{
		return this.generation;
	}
	
	public int getTimesOpened()
	{
		return this.timesOpened;
	}
	
	public int getNetwork()
	{
		return this.network;
	}
	
	/*public boolean getRestrictNetwork()
	{
		return this.restrictNetwork;
	}*/
	
	public Optional<AbstractStargateEntity> getStargateEntity(MinecraftServer server)
	{
		ServerLevel level = server.getLevel(dimension);
		
		if(level != null && level.getBlockEntity(blockPos) instanceof AbstractStargateEntity stargate)
			return Optional.of(stargate);
		
		return Optional.empty();
	}
	
	public Stargate.Feedback resetStargate(MinecraftServer server, Stargate.Feedback feedback, boolean updateInterfaces)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			return stargateEntity.get().resetStargate(feedback, updateInterfaces);
		
		return feedback;
	}
	
	public Stargate.Feedback resetStargate(MinecraftServer server, Stargate.Feedback feedback)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			return stargateEntity.get().resetStargate(feedback);
		
		return feedback;
	}
	
	public boolean isConnected(MinecraftServer server)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			return stargateEntity.get().isConnected();
		
		return false;
	}
	
	public boolean isObstructed(MinecraftServer server)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			return stargateEntity.get().isConnected();
		
		return false;
	}
	
	public boolean canExtractEnergy(MinecraftServer server, long energy)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			return stargateEntity.get().canExtractEnergy(energy);
		
		return false;
	}
	
	public void depleteEnergy(MinecraftServer server, long energy, boolean simulate)
	{
		Optional<AbstractStargateEntity> stargateEntity = getStargateEntity(server);
		
		if(stargateEntity.isPresent())
			stargateEntity.get().depleteEnergy(energy, simulate);
	}
	
	
	
	public void update(AbstractStargateEntity stargate)
	{
		this.hasDHD = stargate.hasDHD();
		this.generation = stargate.getGeneration();
		this.timesOpened = stargate.getTimesOpened();
		this.network = stargate.getNetwork();
	}
	
	
	
	@Override
	public String toString()
	{
		return "[ " + this.address.toString() + " | DHD: " + this.hasDHD + " | Generation: " + this.generation + " | Times Opened: " + this.timesOpened + " ]";
	}
	
	public boolean checkStargateEntity(MinecraftServer server)
	{
		Optional<AbstractStargateEntity> stargateOptional = getStargateEntity(server);
		
		if(stargateOptional.isPresent())
		{
			AbstractStargateEntity stargate = stargateOptional.get();
			
			if(stargate == null)
				StargateJourney.LOGGER.error("Stargate does not exist");
			else
			{
				stargate.checkStargate();
				return true;
			}
		}
		else
			StargateJourney.LOGGER.error("Stargate not found");
		
		return false;
	}
	
	
	
	public CompoundTag serialize()
	{
		CompoundTag stargateTag = new CompoundTag();
		ResourceKey<Level> level = this.getDimension();
		BlockPos pos = this.getBlockPos();
		
		stargateTag.putString(DIMENSION, level.location().toString());
		stargateTag.putIntArray(COORDINATES, new int[] {pos.getX(), pos.getY(), pos.getZ()});
		
		stargateTag.putBoolean(HAS_DHD, hasDHD);
		stargateTag.putInt(GENERATION, generation.getGen());
		stargateTag.putInt(TIMES_OPENED, timesOpened);
		stargateTag.putInt(NETWORK, network);
		
		return stargateTag;
	}
	
	public static Stargate deserialize(MinecraftServer server, Address.Immutable address, CompoundTag tag)
	{
		ResourceKey<Level> dimension = Conversion.stringToDimension(tag.getString(DIMENSION));
		BlockPos blockPos = Conversion.intArrayToBlockPos(tag.getIntArray(COORDINATES));
		
		if(!tag.contains(HAS_DHD) || !tag.contains(GENERATION) || !tag.contains(TIMES_OPENED) || !tag.contains(NETWORK))
		{
			if(server.getLevel(dimension).getBlockEntity(blockPos) instanceof AbstractStargateEntity stargate)
				return new Stargate(stargate);
			else
			{
				StargateJourney.LOGGER.info("Failed to deserialize Stargate " + address.toString());
				return null;
			}
		}
		
		boolean hasDHD = tag.getBoolean(HAS_DHD);
		Gen generation = Gen.intToGen(tag.getInt(GENERATION));
		int timesOpened = tag.getInt(TIMES_OPENED);
		int network = tag.getInt(NETWORK);
		
		return new Stargate(address, dimension, blockPos, hasDHD, generation, timesOpened, network);
	}
	
	
	
	public enum Gen
	{
		NONE(0),
		GEN_1(1),
		GEN_2(2),
		GEN_3(3);
		
		private final int gen;
		
		private Gen(int gen)
		{
			this.gen = gen;
		}
		
		public int getGen()
		{
			return this.gen;
		}
		
		public boolean isNewer(Gen generation)
		{
			return this.gen > generation.gen;
		}
		
		public static Gen intToGen(int gen)
		{
			switch(gen)
			{
			case 1:
				return GEN_1;
			case 2:
				return GEN_2;
			case 3:
				return GEN_3;
			default:
				return NONE;
			}
		}
	}
	
	public static enum ChevronLockSpeed
	{
		SLOW(3),
		MEDIUM(2),
		FAST(1);
		
		private int multiplier;
		
		ChevronLockSpeed(int multiplier)
		{
			this.multiplier = multiplier;
		}
		
		public int getMultiplier()
		{
			return this.multiplier;
		}
		
		public int getChevronWaitTicks()
		{
			return this.multiplier * 4;
		}
		
		public int getKawooshStartTicks()
		{
			return getChevronWaitTicks() * 9;
		}
	}
	
	public static enum WormholeTravel
	{
		ENABLED,
		CREATIVE_ONLY,
		DISABLED;
	}
	
	public enum FilterType
	{
		NONE(0),
		WHITELIST(1),
		BLACKLIST(-1);
		// 7-chevron addresses
		// 8-chevron addresses
		// 9-chevron addresses
		
		private int integerValue;
		
		FilterType(int integerValue)
		{
			this.integerValue = integerValue;
		}
		
		public int getIntegerValue()
		{
			return this.integerValue;
		}
		
		public boolean shouldFilter()
		{
			return this != NONE;
		}
		
		public boolean isWhitelist()
		{
			return this == WHITELIST;
		}
		
		public boolean isBlacklist()
		{
			return this == BLACKLIST;
		}
		
		public static FilterType getFilterType(int integerValue)
		{
			switch(integerValue)
			{
			case 1:
				return WHITELIST;
			case -1:
				return BLACKLIST;
			default:
				return NONE;
			}
		}
	}
	
	public enum FeedbackType
	{
		INFO,
		ERROR,
		MAJOR_ERROR;
		
		public boolean isError()
		{
			return this == ERROR || this == MAJOR_ERROR;
		}
		
		public boolean shouldPlaySound()
		{
			return this == MAJOR_ERROR;
		}
	}
	
	public enum Feedback
	{
		NONE(0, FeedbackType.INFO, "none"),
		UNKNOWN_ERROR(-1, FeedbackType.ERROR, "unknown"),
		
		// Chevron/Symbol
		SYMBOL_ENCODED(1, FeedbackType.INFO, "symbol_encoded"),
		SYMBOL_IN_ADDRESS(-2, FeedbackType.ERROR, "symbol_in_address"),
		SYMBOL_OUT_OF_BOUNDS(-3, FeedbackType.ERROR, "symbol_out_of_bounds"),
		ENCODE_WHEN_CONNECTED(-4, FeedbackType.ERROR, "encode_when_connected"),
		
		// Establishing Connection
		CONNECTION_ESTABLISHED_SYSTEM_WIDE(2, FeedbackType.INFO, "connection_established.system_wide"),
		CONNECTION_ESTABLISHED_INTERSTELLAR(3, FeedbackType.INFO, "connection_established.interstellar"),
		CONNECTION_ESTABLISHED_INTERGALACTIC(4, FeedbackType.INFO, "connection_established.intergalactic"),
		
		INCOMPLETE_ADDRESS(-5, FeedbackType.MAJOR_ERROR, "incomplete_address"),
		INVALID_ADDRESS(-6, FeedbackType.MAJOR_ERROR, "invalid_address"),
		NOT_ENOUGH_POWER(-7, FeedbackType.MAJOR_ERROR, "not_enough_power"),
		SELF_OBSTRUCTED(-8, FeedbackType.MAJOR_ERROR, "self_obstructed"),
		TARGET_OBSTRUCTED(-9, FeedbackType.MAJOR_ERROR, "target_obstructed"),
		SELF_DIAL(-10, FeedbackType.MAJOR_ERROR, "self_dial"),
		SAME_SYSTEM_DIAL(-11, FeedbackType.MAJOR_ERROR, "same_system_dial"),
		ALREADY_CONNECTED(-12, FeedbackType.MAJOR_ERROR, "already_connected"),
		NO_GALAXY(-13, FeedbackType.MAJOR_ERROR, "no_galaxy"),
		NO_DIMENSIONS(-14, FeedbackType.MAJOR_ERROR, "no_dimensions"),
		NO_STARGATES(-15, FeedbackType.MAJOR_ERROR, "no_stargates"),
		TARGET_RESTRICTED(-16, FeedbackType.MAJOR_ERROR, "target_restricted"),
		INVALID_8_CHEVRON_ADDRESS(-17, FeedbackType.MAJOR_ERROR, "invalid_8_chevron_address"),
		INVALID_SYSTEM_WIDE_CONNECTION(-18, FeedbackType.MAJOR_ERROR, "invalid_system_wide_connection"),
		WHITELISTED_TARGET(-19, FeedbackType.MAJOR_ERROR, "whitelisted_target"),
		WHITELISTED_SELF(-20, FeedbackType.MAJOR_ERROR, "whitelisted_self"),
		BLACKLISTED_TARGET(-21, FeedbackType.MAJOR_ERROR, "blacklisted_target"),
		BLACKLISTED_SELF(-22, FeedbackType.MAJOR_ERROR, "blacklisted_self"),

		// Wormhole TODO
		//TRANSPORT_SUCCESSFUL(5, FeedbackType.INFO, createInfo("wormhole.transport_successful")),
		//ENTITY_DESTROYED(6, FeedbackType.INFO, createInfo("wormhole.entity_destroyed")),
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, FeedbackType.INFO, "connection_ended.disconnect"),
		CONNECTION_ENDED_BY_POINT_OF_ORIGIN(8, FeedbackType.INFO, "connection_ended.point_of_origin"),
		CONNECTION_ENDED_BY_NETWORK(9, FeedbackType.INFO, "connection_ended.stargate_network"),
		CONNECTION_ENDED_BY_AUTOCLOSE(10, FeedbackType.INFO, "connection_ended.autoclose"),
		EXCEEDED_CONNECTION_TIME(-23, FeedbackType.ERROR, "exceeded_connection_time"),
		RAN_OUT_OF_POWER(-24, FeedbackType.ERROR, "ran_out_of_power"),
		CONNECTION_REROUTED(-25, FeedbackType.ERROR, "connection_rerouted"),
		WRONG_DISCONNECT_SIDE(-26, FeedbackType.ERROR, "wrong_disconnect_side"),
		CONNECTION_FORMING(-27, FeedbackType.ERROR, "connection_forming"),

		STARGATE_DESTROYED(-28, FeedbackType.ERROR, "stargate_destroyed"),
		COULD_NOT_REACH_TARGET_STARGATE(-29, FeedbackType.MAJOR_ERROR, "could_not_reach_target_stargate"),
		INTERRUPTED_BY_INCOMING_CONNECTION(-30, FeedbackType.ERROR, "interrupted_by_incoming_connection"),
		
		// Milky Way
		CHEVRON_RAISED(11, FeedbackType.INFO, "chevron_opened"),
		ROTATING(12, FeedbackType.INFO, "rotating"),
		ROTATION_BLOCKED(-31, FeedbackType.INFO, "rotation_blocked"),
		NOT_ROTATING(-32, FeedbackType.INFO, "not_rotating"),
		ROTATION_STOPPED(13, FeedbackType.INFO, "rotation_stopped"),
		CHEVRON_ALREADY_OPENED(-33, FeedbackType.ERROR, "chevron_already_opened"),
		CHEVRON_ALREADY_CLOSED(-34, FeedbackType.ERROR, "chevron_already_closed"),
		CHEVRON_NOT_RAISED(-35, FeedbackType.ERROR, "chevron_not_raised"),
		CANNOT_ENCODE_POINT_OF_ORIGIN(-36, FeedbackType.ERROR, "cannot_encode_point_of_origin");
		
		private int code;
		private final FeedbackType type;
		private final String message;
		private final Component feedbackMessage;
		
		private Feedback(int code, FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
			
			if(type.isError())
				this.feedbackMessage = createError(message, type == FeedbackType.MAJOR_ERROR);
			else
				this.feedbackMessage = createInfo(message);
		}
		
		public int getCode()
		{
			return this.code;
		}
		
		public String getMessage()
		{
			return this.message;
		}
		
		public Component getFeedbackMessage()
		{
			return this.feedbackMessage;
		}
		
		public boolean playFailSound()
		{
			return this.type.shouldPlaySound();
		}
		
		public boolean isError()
		{
			return this.type.isError();
		}
	}
	
	private static Component createInfo(String feedback)
	{
		return Component.translatable("message.sgjourney.stargate.info." + feedback);
	}
	
	private static Component createError(String feedback, boolean majorError)
	{
		MutableComponent component = Component.translatable("message.sgjourney.stargate.error." + feedback);
		
		return majorError ? component.withStyle(ChatFormatting.DARK_RED) : component.withStyle(ChatFormatting.RED);
	}
	
	public static class IncomingOutgoing<Thing>
	{
		public static final String OUTGOING = "outgoing";
		public static final String INCOMING = "incoming";
		
		private Thing outgoing;
		private Thing incoming;
		
		public IncomingOutgoing(Thing outgoing, Thing incoming)
		{
			this.outgoing = outgoing;
			this.incoming = incoming;
		}
		
		public IncomingOutgoing(Thing thing)
		{
			this.outgoing = thing;
			this.incoming = thing;
		}
		
		public Thing outgoing()
		{
			return outgoing;
		}
		
		public Thing incoming()
		{
			return incoming;
		}
		
		public Thing get(boolean incoming)
		{
			return incoming ? this.incoming : this.outgoing;
		}
		
		public static <Thing> Codec<IncomingOutgoing<Thing>> ioCodec(final Codec<Thing> thing)
		{
			return RecordCodecBuilder.create(instance -> instance.group(
					thing.fieldOf(OUTGOING).forGetter(io -> io.outgoing),
					thing.fieldOf(INCOMING).forGetter(io -> io.incoming)
					).apply(instance, IncomingOutgoing::new));
		}
		
		public static <Thing> Codec<Either<IncomingOutgoing<Thing>, Thing>> bothCodec(final Codec<Thing> thing)
		{
			return new EitherCodec<>(ioCodec(thing), thing);
		}
	}
	
	public enum ChevronSymbolState
	{
		OFF,
		ENCODED,
		ENGAGED
	}
	
	public enum IrisMotion
	{
		IDLE(true),
		OPENING_REDSTONE(true),
		CLOSING_REDSTONE(true),
		OPENING_COMPUTER(false),
		CLOSING_COMPUTER(false);
		
		private boolean isRedstone;
		
		private IrisMotion(boolean isRedstone)
		{
			this.isRedstone = isRedstone;
		}
		
		public boolean isRedstone()
		{
			return isRedstone;
		}
		
		public boolean isOpening()
		{
			return this == OPENING_REDSTONE || this == OPENING_COMPUTER;
		}
		
		public boolean isClosing()
		{
			return this == CLOSING_REDSTONE || this == CLOSING_COMPUTER;
		}
	}
}
