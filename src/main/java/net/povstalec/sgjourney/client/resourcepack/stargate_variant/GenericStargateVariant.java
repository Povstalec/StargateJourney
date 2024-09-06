package net.povstalec.sgjourney.client.resourcepack.stargate_variant;

import java.util.Optional;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackModel;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackSounds;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;

public abstract class GenericStargateVariant extends RotatingStargateVariant
{
	public static final String STARGATE_MODEL = "stargate_model";
	
	protected final GenericStargateModel stargateModel;
	
	public GenericStargateVariant(ResourceLocation texture, Optional<ResourceLocation> encodedTexture, ResourceLocation engagedTexture,
			ResourcepackModel.Wormhole wormhole, Optional<ResourcepackModel.Wormhole> shinyWormhole, ResourcepackModel.SymbolsModel symbols,
			GenericStargateModel stargateModel,
			ResourcepackSounds.Chevron chevronEngagedSounds, ResourcepackSounds.Chevron chevronIncomingSounds,
			ResourcepackSounds.Rotation rotationSounds, ResourcepackSounds.Wormhole wormholeSounds, ResourcepackSounds.Fail failSounds)
	{
		super(texture, encodedTexture, engagedTexture, wormhole, shinyWormhole, symbols, chevronEngagedSounds,
				chevronIncomingSounds, rotationSounds, wormholeSounds, failSounds);
		
		this.stargateModel = stargateModel;
	}
	
	public GenericStargateModel stargateModel()
	{
		return stargateModel;
	}
	
	
	
	public static class GenericStargateModel
	{
		public static final String MOVIE_CHEVRON_LOCKING = "movie_chevron_locking";
		public static final String MOVIE_PRIMARY_CHEVRON = "movie_primary_chevron";
		public static final String RAISE_BACK_CHEVRONS = "raise_back_chevrons";
		
		@Nullable
		protected Boolean movieChevronLocking;
		@Nullable
		protected Boolean useMovieStargatePrimaryChevron;
		@Nullable
		protected Boolean raiseBackChevrons;
		
		public static final Codec<GenericStargateModel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				// Gate and chevron textures
				Codec.BOOL.optionalFieldOf(MOVIE_CHEVRON_LOCKING).forGetter(model -> Optional.of(model.movieChevronLocking)),
				Codec.BOOL.optionalFieldOf(MOVIE_PRIMARY_CHEVRON).forGetter(model -> Optional.of(model.useMovieStargatePrimaryChevron)),
				Codec.BOOL.optionalFieldOf(RAISE_BACK_CHEVRONS).forGetter(model -> Optional.of(model.raiseBackChevrons))
				).apply(instance, GenericStargateModel::new));
		
		public GenericStargateModel(Optional<Boolean> movieChevronLocking, Optional<Boolean> useMovieStargatePrimaryChevron,
				Optional<Boolean> raiseBackChevrons)
		{
			if(movieChevronLocking.isPresent())
				this.movieChevronLocking = movieChevronLocking.get();
			
			if(useMovieStargatePrimaryChevron.isPresent())
				this.useMovieStargatePrimaryChevron = useMovieStargatePrimaryChevron.get();
			
			if(raiseBackChevrons.isPresent())
				this.raiseBackChevrons = raiseBackChevrons.get();
		}
		
		public boolean movieChevronLocking()
		{
			if(movieChevronLocking != null)
				return movieChevronLocking;
			
			return false;
		}
		
		public boolean useMovieStargatePrimaryChevron()
		{
			if(useMovieStargatePrimaryChevron != null)
				return useMovieStargatePrimaryChevron;
			
			return false;
		}
		
		public boolean raiseBackChevrons()
		{
			if(raiseBackChevrons != null)
				return raiseBackChevrons;
			
			return false;
		}
		
		
		
		public static class MilkyWay extends GenericStargateModel
		{
			public static final Codec<MilkyWay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					// Gate and chevron textures
					Codec.BOOL.optionalFieldOf(MOVIE_CHEVRON_LOCKING).forGetter(model -> Optional.of(model.movieChevronLocking)),
					Codec.BOOL.optionalFieldOf(MOVIE_PRIMARY_CHEVRON).forGetter(model -> Optional.of(model.useMovieStargatePrimaryChevron)),
					Codec.BOOL.optionalFieldOf(RAISE_BACK_CHEVRONS).forGetter(model -> Optional.of(model.raiseBackChevrons))
					).apply(instance, MilkyWay::new));
			
			public MilkyWay(Optional<Boolean> movieChevronLocking, Optional<Boolean> useMovieStargatePrimaryChevron,
					Optional<Boolean> raiseBackChevrons)
			{
				super(movieChevronLocking, useMovieStargatePrimaryChevron, raiseBackChevrons);
			}

			@Override
			public boolean useMovieStargatePrimaryChevron()
			{
				if(useMovieStargatePrimaryChevron != null)
					return useMovieStargatePrimaryChevron;
				
				return ClientStargateConfig.use_movie_stargate_model.get();
			}
			
			@Override
			public boolean movieChevronLocking()
			{
				if(movieChevronLocking != null)
					return movieChevronLocking;
				
				return ClientStargateConfig.use_movie_stargate_model.get();
			}
		}
	}
}
