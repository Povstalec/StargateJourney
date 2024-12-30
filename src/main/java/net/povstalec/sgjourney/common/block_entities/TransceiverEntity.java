package net.povstalec.sgjourney.common.block_entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.blocks.TransceiverBlock;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.TransceiverPeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.packets.ClientboundTransceiverUpdatePacket;
import net.povstalec.sgjourney.common.stargate.ITransmissionReceiver;

public class TransceiverEntity extends BlockEntity implements ITransmissionReceiver
{
	public static final String IDC = "idc";
	public static final String FREQUENCY = "frequency";
	public static final String EDIT_FREQUENCY = "edit_frequency";
	
	private static final String EVENT_TRANSMISSION_RECEIVED = "transceiver_transmission_received";
	
	private static final short MAX_TRANSMISSION_TICKS = 20;
	
	private boolean editingFrequency = false;
	private int frequency = 0;
	private String idc = "";
	
	private short transmissionTicks = 0;
	
	protected TransceiverPeripheralWrapper peripheralWrapper;
	
	public TransceiverEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.TRANSCEIVER.get(), pos, state);
		
		if(ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			peripheralWrapper = new TransceiverPeripheralWrapper(this);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
	}
	
	@Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
    	super.loadAdditional(tag, registries);

    	editingFrequency = tag.getBoolean(EDIT_FREQUENCY);
    	frequency = tag.getInt(FREQUENCY);
    	idc = tag.getString(IDC);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);

		tag.putBoolean(EDIT_FREQUENCY, editingFrequency);
		tag.putInt(FREQUENCY, frequency);
		tag.putString(IDC, idc);
	}
	
	public float transmissionRadius()
	{
		return CommonTransmissionConfig.max_transceiver_transmission_distance.get();
	}
	
	public float transmissionRadius2()
	{
		return transmissionRadius() * transmissionRadius();
	}
	
	public void setFrequency(int frequency)
	{
		this.frequency = frequency;
		this.setChanged();
	}
	
	public int getFrequency()
	{
		return frequency;
	}
	
	public void setEditingFrequency(boolean editingFrequency)
	{
		this.editingFrequency = editingFrequency;;
	}
	
	public boolean editingFrequency()
	{
		return editingFrequency;
	}
	
	public void setCurrentCode(String idc)
	{
		this.idc = idc;
		this.setChanged();
	}
	
	public String getCurrentCode()
	{
		return idc;
	}
    
    public void toggleFrequency()
    {
    	editingFrequency = !editingFrequency;
		this.setChanged();
    }
	
	@Override
	public void receiveTransmission(int transmissionJumps, int frequency, String transmission)
	{
		if(level.isClientSide() || frequency != getFrequency())
			return;
		
		boolean codeIsCorrect = getCurrentCode().equals(transmission);

		queueEvent(EVENT_TRANSMISSION_RECEIVED, frequency, transmission, codeIsCorrect);

		Level level = getLevel();
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		
		if(state.getBlock() instanceof TransceiverBlock transceiver)
			transceiver.receiveTransmission(state, level, pos, codeIsCorrect);
	}
	
	public void sendTransmission()
	{
		int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
		
		for(int x = -roundedRadius; x <= roundedRadius; x++)
		{
			for(int z = -roundedRadius; z <= roundedRadius; z++)
			{
				ChunkAccess chunk = level.getChunk(getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					BlockEntity blockEntity = level.getBlockEntity(pos);
					
					if(blockEntity instanceof ITransmissionReceiver receiver && blockEntity != this)
						receiver.receiveTransmission(0, getFrequency(), getCurrentCode());
				});
			}
		}
		
		Level level = getLevel();
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		
		level.setBlock(pos, state.setValue(TransceiverBlock.TRANSMITTING, true), 2);
		this.transmissionTicks = MAX_TRANSMISSION_TICKS;
	}
	
	public int checkShieldingState()
	{
		int roundedRadius = (int) Math.ceil(transmissionRadius() / 16);
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -roundedRadius; x <= roundedRadius; x++)
		{
			for(int z = -roundedRadius; z <= roundedRadius; z++)
			{
				ChunkAccess chunk = level.getChunk(getBlockPos().east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate && distance2(getBlockPos(), stargate.getBlockPos()) <= transmissionRadius2())
					{
						stargates.add(stargate);
					}
				});
			}
		}

		if(stargates.size() == 0)
			return -1; // No Stargates nearby
		
		stargates.sort((stargateA, stargateB) ->
				Double.valueOf(distance2(getBlockPos(), stargateA.getBlockPos()))
						.compareTo(Double.valueOf(distance2(getBlockPos(), stargateB.getBlockPos()))));
		
		AbstractStargateEntity stargate = stargates.get(0);
		
		if(!stargate.isConnected())
			return -2; // Stargate is not connected
		
		return (int) Math.round(stargate.checkConnectionShieldingState());
	}
    
    public void addToCode(int number)
    {
    	if(!editingFrequency)
    	{
    		if(idc.length() >= 16)
        		return;
        	
        	idc = idc + String.valueOf(number);
    	}
    	else
    	{
    		long tempFrequency = frequency;
    		tempFrequency = tempFrequency * 10 + number;
    		
    		if(tempFrequency > Integer.MAX_VALUE)
    			return;
    		
    		frequency = (int) tempFrequency;
    	}
		this.setChanged();
    }
    
    public void removeFromCode()
    {
    	if(!editingFrequency)
    	{
        	if(idc.length() <= 0)
        		return;
        	
        	idc = idc.substring(0, idc.length() - 1);
    	}
    	else
    	{
    		if(frequency <= 0)
        		return;
        	
    		frequency = frequency / 10;
    	}
		this.setChanged();
    }
	
	public void updateClient()
	{
		if(level.isClientSide())
			return;
		
		PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, level.getChunkAt(this.worldPosition).getPos(), new ClientboundTransceiverUpdatePacket(this.worldPosition, this.editingFrequency, this.idc, this.frequency));
	}
	
	private static double distance2(BlockPos pos, BlockPos targetPos)
	{
		int x = Math.abs(targetPos.getX() - pos.getX());
		int y = Math.abs(targetPos.getY() - pos.getY());
		int z = Math.abs(targetPos.getZ() - pos.getZ());
		
		return x*x + y*y + z*z;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	public TransceiverPeripheralWrapper getPeripheralWrapper()
	{
		if(!ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			return null;
		
		return this.peripheralWrapper;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(!ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			return;
		
		if(this.peripheralWrapper != null)
			this.peripheralWrapper.queueEvent(eventName, objects);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IPeripheral getPeripheral(Direction side)
	{
		return peripheralWrapper.getPeripheral();
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	private void handleTransmissionTicks()
	{
		if(transmissionTicks > 0)
		{
			transmissionTicks--;
			
			if(transmissionTicks == 0)
			{
				Level level = getLevel();
				BlockPos pos = getBlockPos();
				BlockState state = getBlockState();
				
				if(state.getBlock() instanceof TransceiverBlock transceiver)
					transceiver.stopTransmitting(state, level, pos);
			}
		}
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransceiverEntity transceiver)
    {
		transceiver.handleTransmissionTicks();
		
		transceiver.updateClient();
    }
}
