package net.povstalec.sgjourney.client.sound;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.resourcepack.stargate_variant.ClientStargateVariants;
import net.povstalec.sgjourney.client.sound.sounds.GenericStargateSound;
import net.povstalec.sgjourney.common.block_entities.stargate.*;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.StargateVariant;

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
    		GenericStargateSound sound = new GenericStargateSound(stargate, getFailSound(stargate, StargateInfo.Feedback.UNKNOWN_ERROR), 0.5F); //TODO Accept different kinds of errors
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playRotationSound(BlockPos pos, boolean stop)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate.spinSound == null)
    		{
    			if(stargate instanceof RotatingStargateEntity rotatingStargate)
					stargate.spinSound = new StargateSoundWrapper.RingRotation(rotatingStargate);
    			else if(stargate instanceof PegasusStargateEntity pegasusStargate)
    				stargate.spinSound = new StargateSoundWrapper.PegasusRingRotation(pegasusStargate);
    		}
    		
    		if(stop)
    			stargate.stopRotationSound();
    		else
    			stargate.playRotationSound();
    	}
    }
	
    public static void playUniverseDialStartSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof UniverseStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getDialStartSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playRotationStartupSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof RotatingStargateEntity stargate)
    	{
    		if(stargate.buildupSound == null || !stargate.buildupSound.hasSound())
    			stargate.buildupSound = new StargateSoundWrapper.RotationStartup(stargate);

    		stargate.playBuildupSound();
    	}
    }
	
    public static void playRotationStopSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof RotatingStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getRotationStopSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
    
    
    
    private static SoundEvent getChevronEngageSound(AbstractStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
    	
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).chevronEngagedSounds().getSound(chevron));
		
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).chevronEngagedSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronOpenSound(MilkyWayStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant()).chevronOpenSounds().getSound(chevron));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant()).chevronOpenSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronEncodeSound(MilkyWayStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargateVariant.get().clientVariant()).chevronEncodeSounds().getSound(chevron));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getMilkyWayStargateVariant(stargate.defaultVariant()).chevronEncodeSounds().getSound(chevron));
    }
    
    private static SoundEvent getChevronIncomingSound(AbstractStargateEntity stargate, short chevron)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).chevronIncomingSounds().getSound(chevron));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).chevronIncomingSounds().getSound(chevron));
    }
	
	public static SoundEvent getDialStartSound(UniverseStargateEntity stargate)
	{
		Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getUniverseStargateVariant(stargateVariant.get().clientVariant()).dialStartSound());
		
		return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getUniverseStargateVariant(stargate.defaultVariant()).dialStartSound());
	}
    
    public static SoundEvent getRotationStartupSound(AbstractStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargateVariant.get().clientVariant(), stargate).rotationSounds().rotationStartupSound());
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargate.defaultVariant(), stargate).rotationSounds().rotationStartupSound());
    }
    
    public static SoundEvent getRotationSound(AbstractStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargateVariant.get().clientVariant(), stargate).rotationSounds().rotationSound());
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargate.defaultVariant(), stargate).rotationSounds().rotationSound());
    }
    
    public static SoundEvent getRotationStopSound(RotatingStargateEntity stargate)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargateVariant.get().clientVariant(), stargate).rotationSounds().rotationStopSound());
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getRotatingStargateVariant(stargate.defaultVariant(), stargate).rotationSounds().rotationStopSound());
    }
    
    private static SoundEvent getFailSound(AbstractStargateEntity stargate, StargateInfo.Feedback stargateFeedback)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).failSounds().getSound(stargateFeedback));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).failSounds().getSound(stargateFeedback));
    }
    
    private static SoundEvent getWormholeOpenSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getOpenSound(incoming));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getOpenSound(incoming));
    }
    
    public static SoundEvent getWormholeIdleSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getIdleSound(incoming));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getIdleSound(incoming));
    }
    
    private static SoundEvent getWormholeCloseSound(AbstractStargateEntity stargate, boolean incoming)
    {
    	Optional<StargateVariant> stargateVariant = ClientStargateVariants.getVariant(stargate);
		
		if(stargateVariant.isPresent())
			return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargateVariant.get().clientVariant(), stargate).wormholeSounds().getCloseSound(incoming));
    	
    	return SoundEvent.createVariableRangeEvent(ClientStargateVariants.getClientStargateVariant(stargate.defaultVariant(), stargate).wormholeSounds().getCloseSound(incoming));
    }
}
