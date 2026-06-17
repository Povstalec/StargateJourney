package net.povstalec.sgjourney.common.sgjourney;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.EitherCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StargateInfo
{
	public enum Gen
	{
		NONE(0),
		GEN_1(1),
		GEN_2(2),
		GEN_3(3);
		
		private final int gen;
		
		Gen(int gen)
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
	
	public enum ChevronLockSpeed
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
	
	public enum WormholeTravel
	{
		ENABLED,
		CREATIVE_ONLY,
		DISABLED;
	}
	
	public enum FeedbackType
	{
		INFO,
		ERROR,
		SKIPPABLE_ERROR, // An error that can be skipped during dialing if there are other candidates for target Stargate
		MAJOR_ERROR;
		
		public boolean isError()
		{
			return this != INFO;
		}
		
		public boolean shouldPlaySound()
		{
			return this == MAJOR_ERROR || this == SKIPPABLE_ERROR;
		}
	}
	
	public enum Feedback
	{
		NONE(0, FeedbackType.INFO, "none"), // Default state
		UNKNOWN_ERROR(-1, FeedbackType.ERROR, "unknown"), // Error usually used when Stargate isn't present for some unknown reason
		
		// Chevron/Symbol
		SYMBOL_ENCODED(1, FeedbackType.INFO, "symbol_encoded"), // Symbol was encoded successfully
		SYMBOL_IN_ADDRESS(-2, FeedbackType.ERROR, "symbol_in_address"), // Symbol is already in the address
		SYMBOL_OUT_OF_BOUNDS(-3, FeedbackType.ERROR, "symbol_out_of_bounds"), // Symbol is out of bounds
		ENCODE_WHEN_CONNECTED(-4, FeedbackType.ERROR, "encode_when_connected"), // Trying to encode symbols while the Stargate is already connected
		
		// Establishing Connection
		CONNECTION_ESTABLISHED_SYSTEM_WIDE(2, FeedbackType.INFO, "connection_established.system_wide"), // System-wide connection was established successfully
		CONNECTION_ESTABLISHED_INTERSTELLAR(3, FeedbackType.INFO, "connection_established.interstellar"), // Interstellar connection was established successfully
		CONNECTION_ESTABLISHED_INTERGALACTIC(4, FeedbackType.INFO, "connection_established.intergalactic"),// Intergalactic connection was established successfully
		
		INCOMPLETE_ADDRESS(-5, FeedbackType.MAJOR_ERROR, "incomplete_address"), // Trying to dial an incomplete address (only encoded less than 6 symbols)
		INVALID_ADDRESS(-6, FeedbackType.MAJOR_ERROR, "invalid_address"), // Dialed address is invalid because there is no dimension or Stargate with that address
		NOT_ENOUGH_POWER(-7, FeedbackType.MAJOR_ERROR, "not_enough_power"), // Dialing when the Stargate doesn't have enough power
		SELF_OBSTRUCTED(-8, FeedbackType.MAJOR_ERROR, "self_obstructed"), // The dialing Stargate is obstructed
		TARGET_OBSTRUCTED(-9, FeedbackType.SKIPPABLE_ERROR, "target_obstructed"), // The target Stargate is obstructed
		SELF_DIAL(-10, FeedbackType.MAJOR_ERROR, "self_dial"), // The Stargate is attempting to dial itself
		SAME_SYSTEM_DIAL(-11, FeedbackType.MAJOR_ERROR, "same_system_dial"), // The Stargate is attempting to dial the system it's located in
		ALREADY_CONNECTED(-12, FeedbackType.MAJOR_ERROR, "already_connected"), // The dialing Stargate is attempting to connect to an already connected Stargate
		NO_GALAXY(-13, FeedbackType.MAJOR_ERROR, "no_galaxy"), // The Stargate isn't located in any galaxy //TODO currently unused, maybe remove?
		NO_DIMENSIONS(-14, FeedbackType.MAJOR_ERROR, "no_dimensions"), // There are no Dimensions in the dialed Solar System
		NO_STARGATES(-15, FeedbackType.MAJOR_ERROR, "no_stargates"), // There are no Stargates in the dialed Dimension (and by extension, the dialed Solar System)
		SELF_RESTRICTED(-16, FeedbackType.SKIPPABLE_ERROR, "self_restricted"), // Local Stargate is in a restricted network and denied the connection attempt
		TARGET_RESTRICTED(-17, FeedbackType.SKIPPABLE_ERROR, "target_restricted"), // Target Stargate is in a restricted network and denied the connection attempt
		INVALID_8_CHEVRON_ADDRESS(-18, FeedbackType.MAJOR_ERROR, "invalid_8_chevron_address"), // Stargate attempted to dial an 8-chevron address of a Solar System in the same Galaxy, but the config enabling that is disabled
		INVALID_SYSTEM_WIDE_CONNECTION(-19, FeedbackType.MAJOR_ERROR, "invalid_system_wide_connection"), // Stargate attempted to dial a Stargate in the same Solar System, but the config enabling that is disabled
		TARGET_NOT_WHITELISTED(-20, FeedbackType.MAJOR_ERROR, "target_not_whitelisted"), // Dialing Stargate attempted to dial a Stargate that is not whitelisted by the dialing Stargate
		NOT_WHITELISTED_BY_TARGET(-21, FeedbackType.SKIPPABLE_ERROR, "not_whitelisted_by_target"), // Dialing Stargate attempted to dial a Stargate that doesn't have the dialing Stargate on its whitelist
		TARGET_BLACKLISTED(-22, FeedbackType.MAJOR_ERROR, "target_blacklisted"), // Dialing Stargate attempted to dial a Stargate that is blacklisted by the dialing Stargate
		BLACKLISTED_BY_TARGET(-23, FeedbackType.SKIPPABLE_ERROR, "blacklisted_by_target"), // Dialing Stargate attempted to dial a Stargate that has the dialing Stargate on its blacklist

		// Wormhole TODO
		//TRANSPORT_SUCCESSFUL(5, FeedbackType.INFO, createInfo("wormhole.transport_successful")),
		//ENTITY_DESTROYED(6, FeedbackType.INFO, createInfo("wormhole.entity_destroyed")),
		
		// End Connection
		CONNECTION_ENDED_BY_DISCONNECT(7, FeedbackType.INFO, "connection_ended.disconnect"), // Connection ended because the Stargate was disconnected (by DHD or computer)
		CONNECTION_ENDED_BY_POINT_OF_ORIGIN(8, FeedbackType.INFO, "connection_ended.point_of_origin"), // Connection ended because the Point of Origin was encoded again
		CONNECTION_ENDED_BY_NETWORK(9, FeedbackType.INFO, "connection_ended.stargate_network"), // Connection ended by the Stargate Network, either because it was undergoing a Stellar Update, or because the gate was thinking it's conected when it wasn't
		CONNECTION_ENDED_BY_AUTOCLOSE(10, FeedbackType.INFO, "connection_ended.autoclose"), // Connection ended because of the DHD's autoclose function
		EXCEEDED_CONNECTION_TIME(-24, FeedbackType.ERROR, "exceeded_connection_time"), // Connection ended because it exceeded maximum connection time and the energy bypass config is not enabled
		RAN_OUT_OF_POWER(-25, FeedbackType.ERROR, "ran_out_of_power"), // Connection ended because the Stargate ran out of power
		CONNECTION_REROUTED(-26, FeedbackType.ERROR, "connection_rerouted"), // Connection was rerouted //TODO unused for now
		WRONG_DISCONNECT_SIDE(-27, FeedbackType.ERROR, "wrong_disconnect_side"), // Cannot disconnect the Stargate because it did not initiate the connection and the config doesn't allow it
		CONNECTION_FORMING(-28, FeedbackType.ERROR, "connection_forming"), // Cannot end the connection while it is still forming

		STARGATE_DESTROYED(-29, FeedbackType.ERROR, "stargate_destroyed"), // The Stargate at the other end of the connection was destroyed
		COULD_NOT_REACH_TARGET_STARGATE(-30, FeedbackType.MAJOR_ERROR, "could_not_reach_target_stargate"), // Connection ended because target Stargate became unreachable
		INTERRUPTED_BY_INCOMING_CONNECTION(-31, FeedbackType.ERROR, "interrupted_by_incoming_connection"), // Stargate was interrupted by an incoming connection
		
		// Milky Way
		CHEVRON_OPENED(11, FeedbackType.INFO, "chevron_opened"), // Chevron was opened successfully
		
		// Rotating
		ROTATING(12, FeedbackType.INFO, "rotating"), // Stargate started rotating successfully
		ROTATION_BLOCKED(-32, FeedbackType.INFO, "rotation_blocked"), // Rotation is blocked by an open chevron
		NOT_ROTATING(-33, FeedbackType.INFO, "not_rotating"), // Attempted to end rotation even though the Stargate isn't rotating
		ROTATION_STOPPED(13, FeedbackType.INFO, "rotation_stopped"), // Rotation was stopped successfully
		
		// Milky Way
		CHEVRON_ALREADY_OPENED(-34, FeedbackType.ERROR, "chevron_already_opened"), // Attempted to open a chevron that is already open
		CHEVRON_ALREADY_CLOSED(-35, FeedbackType.ERROR, "chevron_already_closed"), // Attempted to close a chevron that is already closed
		CHEVRON_NOT_OPEN(-36, FeedbackType.ERROR, "chevron_not_open"), // Attempted to encode a chevron that is not open
		
		// Other
		TARGET_NOT_LOADED(-37, FeedbackType.ERROR, "target_not_loaded"), // Target Stargate wasn't being loaded by any players or chunkloaders
		SELF_OUTSIDE_STARGATE_NETWORK(-38, FeedbackType.MAJOR_ERROR, "self_outside_stargate_network"), // Stargate is outside the Stargate Network and can't connect as a result
		TARGET_OUTSIDE_STARGATE_NETWORK(-39, FeedbackType.MAJOR_ERROR, "target_outside_stargate_network"); // Target Stargate is outside the Stargate Network and can't connect as a result
		
		private final int code;
		private final FeedbackType type;
		private final String message;
		private final Component feedbackMessage;
		
		Feedback(int code, FeedbackType type, String message)
		{
			this.code = code;
			this.type = type;
			this.message = message;
			
			if(type.isError())
				this.feedbackMessage = createError(message, type == FeedbackType.MAJOR_ERROR || type == FeedbackType.SKIPPABLE_ERROR);
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
		
		public boolean isSkippable()
		{
			return this.type == FeedbackType.SKIPPABLE_ERROR;
		}
		
		public boolean isConnectionEstablished()
		{
			return this == CONNECTION_ESTABLISHED_SYSTEM_WIDE || this == CONNECTION_ESTABLISHED_INTERSTELLAR || this == CONNECTION_ESTABLISHED_INTERGALACTIC;
		}
		
		public static Feedback fromOrdinal(int ordinal)
		{
			if(ordinal < 0 || ordinal >= Feedback.values().length)
				return NONE;
			
			return Feedback.values()[ordinal];
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
		
		IrisMotion(boolean isRedstone)
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
