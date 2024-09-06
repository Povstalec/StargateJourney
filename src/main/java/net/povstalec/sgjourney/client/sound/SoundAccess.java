package net.povstalec.sgjourney.client.sound;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.sound.sounds.GenericStargateSound;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class SoundAccess
{
	public static final String EMPTY = StargateJourney.EMPTY;
	
	protected static Minecraft minecraft = Minecraft.getInstance();
	
    public static void playWormholeOpenSound(BlockPos pos, boolean incoming)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getWormholeOpenSound(stargate, incoming), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playWormholeIdleSound(BlockPos pos, boolean incoming)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate.wormholeIdleSound == null || !stargate.wormholeIdleSound.hasSound())
    			stargate.wormholeIdleSound = new StargateSoundWrapper.WormholeIdle(stargate, incoming);
    		
    		stargate.playWormholeIdleSound();
    	}
    }
	
    public static void playWormholeCloseSound(BlockPos pos, boolean incoming)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getWormholeCloseSound(stargate, incoming), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playIrisThudSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, SoundInit.IRIS_THUD.get(), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playChevronSound(BlockPos pos, short chevron, boolean incoming, boolean open, boolean encode)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate && (open || encode))
    		{
    			if(open)
    			{
        			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronOpenSound(milkyWayStargate, chevron), 0.5F);
            		minecraft.getSoundManager().play(sound);
    			}
    			else if(encode)
    			{
    				GenericStargateSound sound = new GenericStargateSound(stargate, getChevronEncodeSound(milkyWayStargate, chevron), 0.5F);
            		minecraft.getSoundManager().play(sound);
    			}
    		}
    		else if(incoming)
    		{
    			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronIncomingSound(stargate, chevron), 0.5F);
        		minecraft.getSoundManager().play(sound);
    		}
    		else
    		{
    			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronEngageSound(stargate, chevron), 0.5F);
        		minecraft.getSoundManager().play(sound);
    		}
    	}
    }
	
    public static void playFailSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getFailSound(stargate, Stargate.Feedback.UNKNOWN_ERROR), 0.5F); //TODO Accept different kinds of errors
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playRotationSound(BlockPos pos, boolean stop)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate.spinSound == null)
    		{
    			if(stargate instanceof UniverseStargateEntity universeStargate)
    				stargate.spinSound = new StargateSoundWrapper.UniverseRingRotation(universeStargate);
    			else if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
    				stargate.spinSound = new StargateSoundWrapper.MilkyWayRingRotation(milkyWayStargate);
    			else if(stargate instanceof PegasusStargateEntity pegasusStargate)
    				stargate.spinSound = new StargateSoundWrapper.PegasusRingRotation(pegasusStargate);
    		}
    		
    		if(stop)
    			stargate.stopRotationSound();
    		else
    			stargate.playRotationSound();
    	}
    }
	
    public static void playUniverseStartSound(BlockPos pos) // TODO Maybe merge with playMilkyWayBuildupSound
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof UniverseStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getRotationStartupSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playMilkyWayBuildupSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof MilkyWayStargateEntity stargate)
    	{
    		if(stargate.buildupSound == null || !stargate.buildupSound.hasSound())
    			stargate.buildupSound = new StargateSoundWrapper.MilkyWayRingBuildup(stargate);

    		stargate.playBuildupSound();
    	}
    }
	
    public static void playMilkyWayStopSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof MilkyWayStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getRotationStopSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
    
    
    
    private static SoundEvent getChevronEngageSound(AbstractStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
    	
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).chevronEngagedSounds().getSound(chevron));
		
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).chevronEngagedSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronOpenSound(MilkyWayStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant()).chevronOpenSounds().getSound(chevron));
    	
    	return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant()).chevronOpenSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronEncodeSound(MilkyWayStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant()).chevronEncodeSounds().getSound(chevron));
    	
    	return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant()).chevronEncodeSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronIncomingSound(AbstractStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).chevronIncomingSounds().getSound(chevron));
    	
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).chevronIncomingSounds().getSound(chevron));
    }
    
    public static SoundEvent getRotationStartupSound(AbstractStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getRotatingStargateVariant(stargateVariant.get().clientVariant(), stargate).rotationSounds().rotationStartupSound());
    	
    	return new SoundEvent(ClientStargateVariants.getRotatingStargateVariant(stargate.defaultVariant(), stargate).rotationSounds().rotationStartupSound());
    }
    
    public static SoundEvent getRotationSound(AbstractStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getRotatingStargateVariant(stargateVariant.get().clientVariant(), stargate).rotationSounds().rotationSound());
    	
    	return new SoundEvent(ClientStargateVariants.getRotatingStargateVariant(stargate.defaultVariant(), stargate).rotationSounds().rotationSound());
    }
    
    public static SoundEvent getRotationStopSound(MilkyWayStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant()).rotationSounds().rotationStopSound());
    	
    	return new SoundEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant()).rotationSounds().rotationStopSound());
    }
    
    private static SoundEvent getFailSound(AbstractStargateEntity stargate, Stargate.Feedback stargateFeedback)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).failSounds().getSound(stargateFeedback));
    	
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).failSounds().getSound(stargateFeedback));
    }
    
    private static SoundEvent getWormholeOpenSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getOpenSound(incoming));
    	
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getOpenSound(incoming));
    }
    
    public static SoundEvent getWormholeIdleSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getIdleSound(incoming));
    	
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getIdleSound(incoming));
    }
    
    private static SoundEvent getWormholeCloseSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getCloseSound(incoming));
    	
    	return new SoundEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getCloseSound(incoming));
    }
}
