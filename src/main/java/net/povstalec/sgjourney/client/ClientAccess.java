package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.energy_gen.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.block_entities.symbols.SymbolBlockEntity;

@SuppressWarnings("resource")
public class ClientAccess
{
    public static void updateSymbol(BlockPos pos, String symbol)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final SymbolBlockEntity symbolEntity)
        {
        	symbolEntity.symbol = symbol;
        }
    }
    
    public static void updateBasicInterface(BlockPos pos, long energy)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final BasicInterfaceEntity basicInterface)
        {
        	basicInterface.setEnergy(energy);
        }
    }
    
    public static void updateRings(BlockPos pos, int emptySpace, int transportHeight, int transportLight)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final TransportRingsEntity rings)
        {
        	rings.emptySpace = emptySpace;
        	rings.transportHeight = transportHeight;
        	rings.transportLight = transportLight;
        }
    }
    
    public static void updateRingPanel(BlockPos pos, int ringsFound, BlockPos[] ringsPos)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final RingPanelEntity panel)
        {
        	panel.ringsFound = ringsFound;
        	panel.ringsPos = ringsPos;
        }
    }
    
    public static void updateStargate(BlockPos pos, int[] address, boolean dialingOut, int tick, String pointOfOrigin, String symbols)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final AbstractStargateEntity stargate)
        {
        	stargate.setAddress(address);
        	stargate.setDialingOut(dialingOut);
        	stargate.setTickCount(tick);
        	stargate.setPointOfOrigin(pointOfOrigin);
        	stargate.setSymbols(symbols);
        }
    }
    
    public static void updateUniverseStargate(BlockPos pos, int symbolBuffer, int[] addressBuffer, int animationTicks, int rotation, int oldRotation)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final UniverseStargateEntity stargate)
        {
        	stargate.symbolBuffer = symbolBuffer;
        	stargate.addressBuffer = addressBuffer;
        	stargate.animationTicks = animationTicks;
        	stargate.rotation = rotation;
        	stargate.oldRotation = oldRotation;
        }
    }
    
    public static void updateMilkyWayStargate(BlockPos pos, int rotation, int oldRotation, boolean isChevronRaised, int signalStrength, boolean computerRotation, boolean rotateClockwise, int desiredSymbol)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final MilkyWayStargateEntity stargate)
        {
        	stargate.setRotation(rotation);
        	stargate.oldRotation = oldRotation;
        	stargate.isChevronRaised = isChevronRaised;
        	stargate.signalStrength = signalStrength;
        	stargate.computerRotation = computerRotation;
        	stargate.rotateClockwise = rotateClockwise;
        	stargate.desiredSymbol = desiredSymbol;
        }
    }
    
    public static void updatePegasusStargate(BlockPos pos, int symbolBuffer, int[] addressBuffer, int currentSymbol)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final PegasusStargateEntity stargate)
        {
        	stargate.symbolBuffer = symbolBuffer;
        	stargate.addressBuffer = addressBuffer;
        	stargate.currentSymbol = currentSymbol;
        }
    }
    
    public static void updateNaquadahGenerator(BlockPos pos, int reactionProgress, long energy)
    {
    	final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final NaquadahGeneratorEntity generator)
        {
        	generator.setReactionProgress(reactionProgress);
        	generator.setEnergy(energy);
        }
    }
}