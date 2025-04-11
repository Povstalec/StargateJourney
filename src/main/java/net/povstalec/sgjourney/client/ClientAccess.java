package net.povstalec.sgjourney.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.client.screens.DialerScreen;
import net.povstalec.sgjourney.client.screens.GDOScreen;
import net.povstalec.sgjourney.common.block_entities.CartoucheEntity;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.block_entities.SymbolBlockEntity;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.*;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;
import net.povstalec.sgjourney.common.block_entities.tech.TransportRingsEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.info.IrisInfo;

public class ClientAccess
{
	protected static Minecraft minecraft = Minecraft.getInstance();
	
	public static void updateDialer(BlockPos pos)
	{
		minecraft.setScreen(new DialerScreen());
	}
	
	public static void openGDOScreen(UUID playerId, boolean mainHand, String idc, int frequency)
	{
		minecraft.setScreen(new GDOScreen(playerId, mainHand, idc, frequency));
	}
	
	public static void updateSymbol(BlockPos pos, int symbolNumber, ResourceLocation pointOfOrigin, ResourceLocation symbols)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final SymbolBlockEntity symbolEntity)
		{
			symbolEntity.symbolNumber = symbolNumber;
			symbolEntity.pointOfOrigin = pointOfOrigin;
			symbolEntity.symbols = symbols;
		}
	}
	
	public static void updateCartouche(BlockPos pos, ResourceLocation symbols, int[] address)
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
	
	public static void updateTransceiver(BlockPos pos, boolean editingFrequency, int frequency, String idc)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final TransceiverEntity transceiver)
		{
			transceiver.setEditingFrequency(editingFrequency);
			transceiver.setFrequency(frequency);
			transceiver.setCurrentCode(idc);
		}
	}
	
	public static void updateRings(BlockPos pos, int emptySpace, int transportHeight, int progress)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final TransportRingsEntity rings)
		{
			rings.emptySpace = emptySpace;
			rings.transportHeight = transportHeight;
			rings.updateProgress(progress);
		}
	}
	
	public static void updateRingPanel(BlockPos pos, ArrayList<BlockPos> ringsPos, ArrayList<Component> ringsName)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final RingPanelEntity panel)
		{
			panel.ringsPos = ringsPos;
			panel.ringsName = ringsName;
		}
	}
	
	public static void updateDHD(BlockPos pos, long energy, ResourceLocation pointOfOrigin, ResourceLocation symbols, int[] address, boolean isCenterButtonEngaged)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final AbstractDHDEntity dhd)
		{
			dhd.setEnergy(energy);
			dhd.symbolInfo().setPointOfOrigin(pointOfOrigin);
			dhd.symbolInfo().setSymbols(symbols);
			dhd.setAddress(new Address(true).fromArray(address));
			dhd.setCenterButtonEngaged(isCenterButtonEngaged);
		}
	}
	
	public static void updateStargate(BlockPos pos, int[] address, int[] engagedChevrons, int kawooshTick, int tick, short irisProgress,
									  ResourceLocation pointOfOrigin, ResourceLocation symbols, ResourceLocation variant, ItemStack iris)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final AbstractStargateEntity stargate)
		{
			stargate.setAddress(new Address(address));
			stargate.setEngagedChevrons(engagedChevrons);
			stargate.setKawooshTickCount(kawooshTick);
			stargate.setTickCount(tick);
			stargate.symbolInfo().setPointOfOrigin(pointOfOrigin);
			stargate.symbolInfo().setSymbols(symbols);
			stargate.setVariant(variant);
			
			if(blockEntity instanceof IrisInfo.Interface irisStargate)
			{
				irisStargate.irisInfo().setIrisProgress(irisProgress);
				
				if(!iris.isEmpty())
					irisStargate.irisInfo().setIris(iris);
				else
					irisStargate.irisInfo().unsetIris();
			}
		}
	}
	
	public static void spawnStargateParticles(BlockPos pos, HashMap<StargatePart, BlockState> blockStates)
	{
		final BlockState state = minecraft.level.getBlockState(pos);
		
		if(state.getBlock() instanceof final AbstractStargateBlock stargateBlock)
		{
			StargatePart part = state.getValue(AbstractStargateBlock.PART);
			Direction direction = state.getValue(AbstractStargateBlock.FACING);
			Orientation orientation = state.getValue(AbstractStargateBlock.ORIENTATION);
			
			if(part == null || direction == null || orientation == null)
				return;
			
			BlockPos basePos = part.getBaseBlockPos(pos, direction, orientation);
			
			for(Map.Entry<StargatePart, BlockState> entry : blockStates.entrySet())
			{
				BlockPos coverPos = entry.getKey().getRingPos(basePos, direction, orientation);
				
				minecraft.particleEngine.destroy(coverPos, stargateBlock.defaultBlockState());
			}
		}
	}
	
	public static void updateStargateState(BlockPos pos, StargateConnection.State connectionState, boolean canSinkGate, HashMap<StargatePart, BlockState> blockStates)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final AbstractStargateEntity stargate)
		{
			stargate.setConnectionState(connectionState);
			stargate.blockCover.blockStates = blockStates;
			stargate.blockCover.canSinkGate = canSinkGate;
		}
	}
	
	public static void updateRotatingStargate(BlockPos pos, int rotation, int oldRotation, int signalStrength, boolean computerRotation, boolean rotateClockwise, int desiredRotation)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final RotatingStargateEntity stargate)
		{
			stargate.setRotation(oldRotation, rotation);
			stargate.signalStrength = signalStrength;
			stargate.rotating = computerRotation;
			stargate.rotateClockwise = rotateClockwise;
			stargate.desiredRotation = desiredRotation;
		}
	}
	
	public static void updateUniverseStargate(BlockPos pos, int symbolBuffer, int[] addressBuffer)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final UniverseStargateEntity stargate)
		{
			stargate.symbolBuffer = symbolBuffer;
			stargate.addressBuffer.fromArray(addressBuffer);
		}
	}
	
	public static void updateMilkyWayStargate(BlockPos pos, boolean isChevronOpen)
	{
		final BlockEntity blockEntity = minecraft.level.getBlockEntity(pos);
		
		if(blockEntity instanceof final MilkyWayStargateEntity stargate)
			stargate.isChevronOpen = isChevronOpen;
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