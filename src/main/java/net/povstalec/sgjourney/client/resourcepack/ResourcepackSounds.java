package net.povstalec.sgjourney.client.resourcepack;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.Stargate.IncomingOutgoing;

public class ResourcepackSounds
{
	public static class Chevron
	{
		public static final String DEFAULT = "default";
		public static final String PRIMARY = "primary_chevron";
		public static final String CHEVRON_1 = "chevron_1";
		public static final String CHEVRON_2 = "chevron_2";
		public static final String CHEVRON_3 = "chevron_3";
		public static final String CHEVRON_4 = "chevron_4";
		public static final String CHEVRON_5 = "chevron_5";
		public static final String CHEVRON_6 = "chevron_6";
		public static final String CHEVRON_7 = "chevron_7";
		public static final String CHEVRON_8 = "chevron_8";
		
		public final ResourceLocation defaultSound;
		@Nullable
		public ResourceLocation primaryChevron;
		@Nullable
		public ResourceLocation chevron1;
		@Nullable
		public ResourceLocation chevron2;
		@Nullable
		public ResourceLocation chevron3;
		@Nullable
		public ResourceLocation chevron4;
		@Nullable
		public ResourceLocation chevron5;
		@Nullable
		public ResourceLocation chevron6;
		@Nullable
		public ResourceLocation chevron7;
		@Nullable
		public ResourceLocation chevron8;
		
		public static final Codec<ResourcepackSounds.Chevron> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf(DEFAULT).forGetter(chevrons -> chevrons.defaultSound),
				ResourceLocation.CODEC.optionalFieldOf(PRIMARY).forGetter(chevrons -> Optional.of(chevrons.primaryChevron)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_1).forGetter(chevrons -> Optional.of(chevrons.chevron1)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_2).forGetter(chevrons -> Optional.of(chevrons.chevron2)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_3).forGetter(chevrons -> Optional.of(chevrons.chevron3)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_4).forGetter(chevrons -> Optional.of(chevrons.chevron4)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_5).forGetter(chevrons -> Optional.of(chevrons.chevron5)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_6).forGetter(chevrons -> Optional.of(chevrons.chevron6)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_7).forGetter(chevrons -> Optional.of(chevrons.chevron7)),
				ResourceLocation.CODEC.optionalFieldOf(CHEVRON_8).forGetter(chevrons -> Optional.of(chevrons.chevron8))
				).apply(instance, ResourcepackSounds.Chevron::new));
		
		public Chevron(ResourceLocation defaultSound, Optional<ResourceLocation> primaryChevron,
				Optional<ResourceLocation> chevron1, Optional<ResourceLocation> chevron2,
				Optional<ResourceLocation> chevron3, Optional<ResourceLocation> chevron4,
				Optional<ResourceLocation> chevron5, Optional<ResourceLocation> chevron6,
				Optional<ResourceLocation> chevron7, Optional<ResourceLocation> chevron8)
		{
			this.defaultSound = defaultSound;
			
			if(primaryChevron.isPresent())
				this.primaryChevron = primaryChevron.isPresent() ? primaryChevron.get() : this.defaultSound;
			
			if(chevron1.isPresent())
				this.chevron1 = chevron1.isPresent() ? chevron1.get() : this.defaultSound;
			
			if(chevron2.isPresent())
				this.chevron2 = chevron2.isPresent() ? chevron2.get() : this.defaultSound;
			
			if(chevron3.isPresent())
				this.chevron3 = chevron3.isPresent() ? chevron3.get() : this.defaultSound;
			
			if(chevron4.isPresent())
				this.chevron4 = chevron4.isPresent() ? chevron4.get() : this.defaultSound;
			
			if(chevron5.isPresent())
				this.chevron5 = chevron5.isPresent() ? chevron5.get() : this.defaultSound;
			
			if(chevron6.isPresent())
				this.chevron6 = chevron6.isPresent() ? chevron6.get() : this.defaultSound;
			
			if(chevron7.isPresent())
				this.chevron7 = chevron7.isPresent() ? chevron7.get() : this.defaultSound;
			
			if(chevron8.isPresent())
				this.chevron8 = chevron8.isPresent() ? chevron8.get() : this.defaultSound;
		}
		
		public Chevron(ResourceLocation defaultSound)
		{
			this(defaultSound, Optional.empty(),
					Optional.empty(), Optional.empty(),
					Optional.empty(), Optional.empty(),
					Optional.empty(), Optional.empty(),
					Optional.empty(), Optional.empty());
		}
		
		public ResourceLocation getSound(short chevron)
		{
			return switch(chevron)
			{
			case 0 -> primaryChevron;
			case 1 -> chevron1;
			case 2 -> chevron2;
			case 3 -> chevron3;
			case 4 -> chevron4;
			case 5 -> chevron5;
			case 6 -> chevron6;
			case 7 -> chevron7;
			case 8 -> chevron8;
			default -> defaultSound;
			};
		}
	}
	
	public static class Rotation
	{
		public static final String ROTATION_STARTUP_SOUND = "rotation_startup_sound";
		public static final String ROTATION_BUILDUP_SOUND = "rotation_buildup_sound";
		public static final String ROTATION_SOUND = "rotation_sound";
		public static final String ROTATION_STOP_SOUND = "rotation_stop_sound";
		
		@Nullable
		private ResourceLocation rotationStartupSound;
		@Nullable
		private ResourceLocation rotationSound;
		@Nullable
		private ResourceLocation rotationStopSound;
		
		public static final Codec<ResourcepackSounds.Rotation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.optionalFieldOf(ROTATION_STARTUP_SOUND).forGetter(ResourcepackSounds.Rotation::rotationStartupSound),
				ResourceLocation.CODEC.optionalFieldOf(ROTATION_SOUND).forGetter(ResourcepackSounds.Rotation::rotationSound),
				ResourceLocation.CODEC.optionalFieldOf(ROTATION_STOP_SOUND).forGetter(ResourcepackSounds.Rotation::rotationStopSound)
				).apply(instance, ResourcepackSounds.Rotation::new));
		
		public Rotation(Optional<ResourceLocation> rotationStartupSound,
				Optional<ResourceLocation> rotationSound, Optional<ResourceLocation> rotationStopSound)
		{
			if(rotationStartupSound.isPresent())
				this.rotationStartupSound = rotationStartupSound.get();
			if(rotationSound.isPresent())
				this.rotationSound = rotationSound.get();
			if(rotationStopSound.isPresent())
				this.rotationStopSound = rotationStopSound.get();
		}
		
		public Optional<ResourceLocation> rotationStartupSound()
		{
			return Optional.ofNullable(this.rotationStartupSound);
		}
		
		public Optional<ResourceLocation> rotationSound()
		{
			return Optional.ofNullable(this.rotationSound);
		}
		
		public Optional<ResourceLocation> rotationStopSound()
		{
			return Optional.ofNullable(this.rotationStopSound);
		}
	}
	
	public static final Codec<IncomingOutgoing<ResourceLocation>> INCOMING_OUTGOING_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf(IncomingOutgoing.OUTGOING).forGetter(IncomingOutgoing::outgoing),
			ResourceLocation.CODEC.fieldOf(IncomingOutgoing.INCOMING).forGetter(IncomingOutgoing::incoming)
			).apply(instance, IncomingOutgoing::new));
	
	public static class Wormhole
	{
		public static final String OPEN_SOUND = "open";
		public static final String IDLE_SOUND = "idle";
		public static final String CLOSE_SOUND = "close";
		
		public static final Codec<ResourcepackSounds.Wormhole> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.either(INCOMING_OUTGOING_CODEC, ResourceLocation.CODEC).fieldOf(OPEN_SOUND).forGetter(ResourcepackSounds.Wormhole::openSound),
				Codec.either(INCOMING_OUTGOING_CODEC, ResourceLocation.CODEC).fieldOf(IDLE_SOUND).forGetter(ResourcepackSounds.Wormhole::idleSound),
				Codec.either(INCOMING_OUTGOING_CODEC, ResourceLocation.CODEC).fieldOf(CLOSE_SOUND).forGetter(ResourcepackSounds.Wormhole::closeSound)
				// TODO probably add some unstable connection sounds in the future
				).apply(instance, ResourcepackSounds.Wormhole::new));
		
		private final Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> openSound;
		private final Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> idleSound;
		private final Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> closeSound;
		
		public Wormhole(Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> openSound,
				Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> idleSound,
				Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> closeSound)
		{
			this.openSound = openSound;
			this.idleSound = idleSound;
			this.closeSound = closeSound;
		}
		
		public Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> openSound()
		{
			return openSound;
		}
		
		public Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> idleSound()
		{
			return idleSound;
		}
		
		public Either<Stargate.IncomingOutgoing<ResourceLocation>, ResourceLocation> closeSound()
		{
			return closeSound;
		}
		
		public Optional<ResourceLocation> getOpenSound(boolean incoming)
		{
			if(openSound == null)
				return Optional.empty();
			
			if(openSound.left().isPresent())
				return Optional.of(incoming ? openSound.left().get().incoming() : openSound.left().get().outgoing());
			
			return Optional.of(openSound.right().get());
		}
		
		public Optional<ResourceLocation> getIdleSound(boolean incoming)
		{
			if(idleSound == null)
				return Optional.empty();
			
			if(idleSound.left().isPresent())
				return Optional.of(incoming ? idleSound.left().get().incoming() : idleSound.left().get().outgoing());
			
			return Optional.of(idleSound.right().get());
		}
		
		public Optional<ResourceLocation> getCloseSound(boolean incoming)
		{
			if(closeSound == null)
				return Optional.empty();
			
			if(closeSound.left().isPresent())
				return Optional.of(incoming ? closeSound.left().get().incoming() : closeSound.left().get().outgoing());
			
			return Optional.of(closeSound.right().get());
		}
	}
	
	public static class Fail
	{
		public static final String DEFAULT = "default";
		
		public final ResourceLocation defaultSound;
		//TODO Variations of fail sound for different feedback
		
		public static final Codec<ResourcepackSounds.Fail> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf(DEFAULT).forGetter(chevrons -> chevrons.defaultSound)
				).apply(instance, ResourcepackSounds.Fail::new));
		
		public Fail(ResourceLocation defaultSound)
		{
			this.defaultSound = defaultSound;
		}
		
		public ResourceLocation getSound(Stargate.Feedback feedback)
		{
			return switch(feedback)
			{
			default -> defaultSound;
			};
		}
	}
}
