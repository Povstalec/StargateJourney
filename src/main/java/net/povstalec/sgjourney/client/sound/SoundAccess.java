package net.povstalec.sgjourney.client.sound;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.sound.sounds.GenericStargateSound;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.config.ClientStargateConfig;
import net.povstalec.sgjourney.common.stargate.StargateVariant;

public class SoundAccess
{
	public static final String EMPTY = StargateJourney.EMPTY;
	
	protected static Minecraft minecraft = Minecraft.getInstance();
	
    /*public static void playWormholeOpenSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getWormholeOpenSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playWormholeIdleSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate.wormholeIdleSound == null || !stargate.wormholeIdleSound.hasSound())
    			stargate.wormholeIdleSound = new StargateSoundWrapper.WormholeIdle(stargate);
    		
    		stargate.playWormholeIdleSound();
    	}
    }
	
    public static void playWormholeCloseSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getWormholeCloseSound(stargate), 0.75F);
    		minecraft.getSoundManager().play(sound);
    	}
    }
	
    public static void playChevronSound(BlockPos pos, boolean isPrimary, boolean incoming, boolean open, boolean encode)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate && (open || encode))
    		{
    			if(open)
    			{
        			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronOpenSound(milkyWayStargate, isPrimary), 0.5F);
            		minecraft.getSoundManager().play(sound);
    			}
    			else if(encode)
    			{
    				// Primary Chevron can't be encoded, so we're fine on that front
    				GenericStargateSound sound = new GenericStargateSound(stargate, getChevronEncodeSound(milkyWayStargate), 0.5F);
            		minecraft.getSoundManager().play(sound);
    			}
    		}
    		else if(incoming)
    		{
    			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronIncomingSound(stargate, isPrimary), 0.5F);
        		minecraft.getSoundManager().play(sound);
    		}
    		else
    		{
    			GenericStargateSound sound = new GenericStargateSound(stargate, getChevronEngageSound(stargate, isPrimary), 0.5F);
        		minecraft.getSoundManager().play(sound);
    		}
    	}
    }
	
    public static void playFailSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getFailSound(stargate), 0.5F);
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
	
    public static void playUniverseStartSound(BlockPos pos)
    {
    	if(minecraft.level.getBlockEntity(pos) instanceof UniverseStargateEntity stargate)
    	{
    		GenericStargateSound sound = new GenericStargateSound(stargate, getStartupSound(stargate), 0.75F);
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
    
    
    
    private static Optional<StargateVariant> getVariant(String variantString)
    {
    	ClientPacketListener clientPacketListener = minecraft.getConnection();
		RegistryAccess registries = clientPacketListener.registryAccess();
		Registry<StargateVariant> variantRegistry = registries.registryOrThrow(StargateVariant.REGISTRY_KEY);
		
		Optional<StargateVariant> variant = Optional.ofNullable(variantRegistry.get(new ResourceLocation(variantString)));
		
		return variant;
    }
    
    private static SoundEvent getChevronEngageSound(AbstractStargateEntity stargate, boolean isPrimary)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();

    		if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(isPrimary)
            		{
            			if(stargateVariant.getPrimaryChevronEngageSound().isPresent())
                    		return SoundEvent.createVariableRangeEvent(stargateVariant.getPrimaryChevronEngageSound().get());
            		}
        			if(stargateVariant.getChevronEngageSound().isPresent())
        			{
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getChevronEngageSound().get());
        			}
        		}
        	}
    	}
    	
    	if(isPrimary)
    		return stargate.getPrimaryChevronEngageSound();
    	
    	return stargate.getChevronEngageSound();
    }
    
    private static SoundEvent getChevronOpenSound(MilkyWayStargateEntity stargate, boolean isPrimary)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
    		
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(isPrimary)
            		{
            			if(stargateVariant.getPrimaryChevronOpenSound().isPresent())
                    		return SoundEvent.createVariableRangeEvent(stargateVariant.getPrimaryChevronOpenSound().get());
            		}
            		
        			if(stargateVariant.getChevronOpenSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getChevronOpenSound().get());
        		}
        	}
    	}
    	
    	if(isPrimary)
    		return stargate.getPrimaryChevronOpenSound();
    	
    	return stargate.getChevronOpenSound();
    }
    
    public static SoundEvent getStartupSound(UniverseStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
    		
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(stargateVariant.getStartupSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getStartupSound().get());
        		}
        	}
    	}
    	
    	return stargate.getStartupSound();
    }
    
    public static SoundEvent getRotationSound(AbstractStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
    		
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(stargateVariant.getRotationSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getRotationSound().get());
        		}
        	}
    	}
    	
    	return stargate.getRotationSound();
    }
    
    public static SoundEvent getRotationBuildupSound(MilkyWayStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
    		
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(stargateVariant.getRotationBuildupSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getRotationBuildupSound().get());
        		}
        	}
    	}
    	
    	return stargate.getRingRotationBuildupSound();
    }
    
    public static SoundEvent getRotationStopSound(MilkyWayStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
    		
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(stargateVariant.getRotationStopSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getRotationStopSound().get());
        		}
        	}
    	}
    	
    	return stargate.getRingRotationStopSound();
    }
    
    private static SoundEvent getChevronEncodeSound(MilkyWayStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent() && variant.get().getChevronEncodeSound().isPresent())
            		return SoundEvent.createVariableRangeEvent(variant.get().getChevronEncodeSound().get());
        	}
    	}
    	
    	return stargate.getChevronEncodeSound();
    }
    
    private static SoundEvent getChevronIncomingSound(AbstractStargateEntity stargate, boolean isPrimary)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();

    		if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent())
        		{
        			StargateVariant stargateVariant = variant.get();
        			
        			if(isPrimary)
            		{
            			if(stargateVariant.getPrimaryChevronIncomingSound().isPresent())
                    		return SoundEvent.createVariableRangeEvent(stargateVariant.getPrimaryChevronIncomingSound().get());
            		}
            		
        			if(stargateVariant.getChevronIncomingSound().isPresent())
                		return SoundEvent.createVariableRangeEvent(stargateVariant.getChevronIncomingSound().get());
        		}
        	}
    	}
    	
    	if(isPrimary)
    		return stargate.getPrimaryChevronIncomingSound();
    	
    	return stargate.getChevronIncomingSound();
    }
    
    private static SoundEvent getFailSound(AbstractStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);

        		if(variant.isPresent() && variant.get().getFailSound().isPresent())
            		return SoundEvent.createVariableRangeEvent(variant.get().getFailSound().get());
        	}
    	}
    	
    	return stargate.getFailSound();
    }
    
    private static SoundEvent getWormholeOpenSound(AbstractStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent() && variant.get().getWormholeOpenSound().isPresent())
            		return SoundEvent.createVariableRangeEvent(variant.get().getWormholeOpenSound().get());
        	}
    	}
    	
    	return stargate.getWormholeOpenSound();
    }
    
    public static SoundEvent getWormholeIdleSound(AbstractStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent() && variant.get().getWormholeIdleSound().isPresent())
            		return SoundEvent.createVariableRangeEvent(variant.get().getWormholeIdleSound().get());
        	}
    	}
    	
    	return stargate.getWormholeIdleSound();
    }
    
    private static SoundEvent getWormholeCloseSound(AbstractStargateEntity stargate)
    {
    	if(ClientStargateConfig.stargate_variants.get())
    	{
    		String variantString = stargate.getVariant();
        	if(!variantString.equals(EMPTY))
        	{
        		Optional<StargateVariant> variant = getVariant(variantString);
        		
        		if(variant.isPresent() && variant.get().getWormholeCloseSound().isPresent())
            		return SoundEvent.createVariableRangeEvent(variant.get().getWormholeCloseSound().get());
        	}
    	}
    	
    	return stargate.getWormholeCloseSound();
    }*/
}
