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
	
    public static void playWormholeOpenSound(BlockPos pos)
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
    		GenericStargateSound sound = new GenericStargateSound(stargate, stargate.getStartSound(), 0.75F);
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
    		GenericStargateSound sound = new GenericStargateSound(stargate, stargate.getRingRotationStopSound(), 0.75F);
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
            			if(stargateVariant.getPrimaryChevronOpenSound().isPresent())
                    		return new SoundEvent(stargateVariant.getPrimaryChevronEngageSound().get());
            		}
            		
        			if(stargateVariant.getChevronOpenSound().isPresent())
                		return new SoundEvent(stargateVariant.getChevronEngageSound().get());
        		}
        	}
    	}
    	
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
            				new SoundEvent(stargateVariant.getPrimaryChevronOpenSound().get());
            		}
            		
        			if(stargateVariant.getChevronOpenSound().isPresent())
        				new SoundEvent(stargateVariant.getChevronOpenSound().get());
        		}
        	}
    	}
    	
    	return stargate.getChevronOpenSound();
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
            		return new SoundEvent(variant.get().getChevronEncodeSound().get());
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
            			if(stargateVariant.getPrimaryChevronOpenSound().isPresent())
                    		return new SoundEvent(stargateVariant.getPrimaryChevronIncomingSound().get());
            		}
            		
        			if(stargateVariant.getChevronOpenSound().isPresent())
                		return new SoundEvent(stargateVariant.getChevronIncomingSound().get());
        		}
        	}
    	}
    	
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
            		return new SoundEvent(variant.get().getFailSound().get());
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
            		return new SoundEvent(variant.get().getWormholeOpenSound().get());
        	}
    	}
    	
    	return stargate.getWormholeOpenSound();
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
            		return new SoundEvent(variant.get().getWormholeCloseSound().get());
        	}
    	}
    	
    	return stargate.getWormholeCloseSound();
    }
}
