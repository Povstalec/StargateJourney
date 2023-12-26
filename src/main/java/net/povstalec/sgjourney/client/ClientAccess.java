package net.povstalec.sgjourney.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.block_entities.TransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.stargate.Address;

public class ClientAccess
{
	protected static Minecraft minecraft = Minecraft.getInstance();
	
    public static void updateSymbol(BlockPos pos, String symbol)
    {
        final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final SymbolBlockEntity symbolEntity)
        {
        	symbolEntity.symbol = symbol;
        }
    }
    
    public static void updateCartouche(BlockPos pos, String symbols, int[] address)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final CartoucheEntity cartouche)
        {
    		cartouche.setSymbols(symbols);
    		cartouche.setAddress(new Address(address));
        }
    }
    
    public static void updateInterface(BlockPos pos, long energy)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final AbstractInterfaceEntity interfaceEntity)
        {
        	interfaceEntity.setEnergy(energy);
        }
    }
    
    public static void updateRings(BlockPos pos, int emptySpace, int transportHeight, int transportLight)
    {
        final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final TransportRingsEntity rings)
        {
        	rings.emptySpace = emptySpace;
        	rings.transportHeight = transportHeight;
        	rings.transportLight = transportLight;
        }
    }
    
    public static void updateRingPanel(BlockPos pos, int ringsFound, BlockPos[] ringsPos)
    {
        final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final RingPanelEntity panel)
        {
        	panel.ringsFound = ringsFound;
        	panel.ringsPos = ringsPos;
        }
    }
    
    public static void updateStargate(BlockPos pos, int[] address, int[] engagedChevrons, int kawooshTick, int tick, String pointOfOrigin, String symbols, String variant)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final AbstractStargateEntity stargate)
        {
        	stargate.setAddress(new Address(address));
        	stargate.setEngagedChevrons(engagedChevrons);
        	stargate.setKawooshTickCount(kawooshTick);
        	stargate.setTickCount(tick);
        	stargate.setPointOfOrigin(pointOfOrigin);
        	stargate.setSymbols(symbols);
        	stargate.setVariant(variant);
        }
    }
    
    public static void updateUniverseStargate(BlockPos pos, int symbolBuffer, int[] addressBuffer, int animationTicks, int rotation, int oldRotation)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final UniverseStargateEntity stargate)
        {
        	stargate.symbolBuffer = symbolBuffer;
        	stargate.addressBuffer.fromArray(addressBuffer);
        	stargate.animationTicks = animationTicks;
        	stargate.rotation = rotation;
        	stargate.oldRotation = oldRotation;
        }
    }
    
    public static void updateMilkyWayStargate(BlockPos pos, int rotation, int oldRotation, boolean isChevronRaised, int signalStrength, boolean computerRotation, boolean rotateClockwise, int desiredSymbol)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final MilkyWayStargateEntity stargate)
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
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final PegasusStargateEntity stargate)
        {
        	stargate.symbolBuffer = symbolBuffer;
        	stargate.addressBuffer.fromArray(addressBuffer);
        	stargate.currentSymbol = currentSymbol;
        }
    }

    public static void updateNaquadahGenerator(BlockPos pos, int reactionProgress, long energy)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final NaquadahGeneratorEntity generator)
        {
        	generator.setReactionProgress(reactionProgress);
        	generator.setEnergy(energy);
        }
    }

    public static void updateCrystallizer(BlockPos pos, FluidStack fluidStack, int progress)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final AbstractCrystallizerEntity crystallizer)
        {
        	crystallizer.setFluid(fluidStack);
        	crystallizer.progress = progress;
        }
    }

    public static void updateNaquadahLiquidizer(BlockPos pos, FluidStack fluidStack1, FluidStack fluidStack2, int progress)
    {
    	final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
        
        if(blockEntity instanceof final AbstractNaquadahLiquidizerEntity naquadahLiquidizer)
        {
        	naquadahLiquidizer.setFluid1(fluidStack1);
        	naquadahLiquidizer.setFluid2(fluidStack2);
        	naquadahLiquidizer.progress = progress;
        }
    }
}