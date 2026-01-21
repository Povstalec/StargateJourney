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
		NONE(0, FeedbackType.INFO, "none"),
		UNKNOWN_ERROR(-1, FeedbackType.ERROR, "unknown"), // Error usually used when Stargate isn't present for some unknown reason
		
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
		TARGET_OBSTRUCTED(-9, FeedbackType.SKIPPABLE_ERROR, "target_obstructed"),
		SELF_DIAL(-10, FeedbackType.MAJOR_ERROR, "self_dial"),
		SAME_SYSTEM_DIAL(-11, FeedbackType.MAJOR_ERROR, "same_system_dial"),
		ALREADY_CONNECTED(-12, FeedbackType.MAJOR_ERROR, "already_connected"),
		NO_GALAXY(-13, FeedbackType.MAJOR_ERROR, "no_galaxy"),
		NO_DIMENSIONS(-14, FeedbackType.MAJOR_ERROR, "no_dimensions"),
		NO_STARGATES(-15, FeedbackType.MAJOR_ERROR, "no_stargates"),
		TARGET_RESTRICTED(-16, FeedbackType.SKIPPABLE_ERROR, "target_restricted"),
		INVALID_8_CHEVRON_ADDRESS(-17, FeedbackType.MAJOR_ERROR, "invalid_8_chevron_address"),
		INVALID_SYSTEM_WIDE_CONNECTION(-18, FeedbackType.MAJOR_ERROR, "invalid_system_wide_connection"),
		TARGET_NOT_WHITELISTED(-19, FeedbackType.MAJOR_ERROR, "target_not_whitelisted"),
		NOT_WHITELISTED_BY_TARGET(-20, FeedbackType.SKIPPABLE_ERROR, "not_whitelisted_by_target"),
		TARGET_BLACKLISTED(-21, FeedbackType.MAJOR_ERROR, "target_blacklisted"),
		BLACKLISTED_BY_TARGET(-22, FeedbackType.SKIPPABLE_ERROR, "blacklisted_by_target"),

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
		CHEVRON_NOT_OPEN(-35, FeedbackType.ERROR, "chevron_not_open"),
		CANNOT_ENCODE_POINT_OF_ORIGIN(-36, FeedbackType.ERROR, "cannot_encode_point_of_origin"),
		
		TARGET_NOT_LOADED(-37, FeedbackType.ERROR, "target_not_loaded");
		
		private int code;
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
