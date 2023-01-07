package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.block_entities.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.MilkyWayStargateEntity;
import net.povstalec.sgjourney.block_entities.PegasusStargateEntity;
import net.povstalec.sgjourney.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.block_entities.address.SymbolBlockEntity;

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
    
    public static void updateRings(BlockPos pos, int ticks, int emptySpace, int progress, int transportHeight, int transportLight)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final TransportRingsEntity rings)
        {
        	rings.emptySpace = emptySpace;
        	rings.ticks = ticks;
        	rings.progress = progress;
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
    
    public static void updateStargate(BlockPos pos, int chevronsActive, boolean isBusy, int tick, String pointOfOrigin, String symbols, int currentSymbol)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final AbstractStargateEntity stargate)
        {
        	stargate.chevronsActive = chevronsActive;
        	stargate.tick = tick;
        	stargate.currentSymbol = currentSymbol;
        	stargate.pointOfOrigin = pointOfOrigin;
        	stargate.symbols = symbols;
        }
    }
    
    public static void updateMilkyWayStargate(BlockPos pos, short degrees,boolean isChevronRaised)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final MilkyWayStargateEntity stargate)
        {
        	stargate.degrees = degrees;
        	stargate.isChevronRaised = isChevronRaised;
        }
    }
    
    public static void updatePegasusStargate(BlockPos pos, int[] address, int symbolBuffer, int[] addressBuffer)
    {
        final BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        
        if (blockEntity instanceof final PegasusStargateEntity stargate)
        {
        	stargate.updateAddressAndChevrons(address, address.length);
        	stargate.symbolBuffer = symbolBuffer;
        	stargate.addressBuffer = addressBuffer;
        }
    }
}