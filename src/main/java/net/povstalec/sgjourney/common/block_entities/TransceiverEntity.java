package net.povstalec.sgjourney.common.block_entities;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.blocks.TransceiverBlock;
import net.povstalec.sgjourney.common.config.CommonTransmissionConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundTransceiverUpdatePacket;
import net.povstalec.sgjourney.common.stargate.ITransmissionReceiver;

public class TransceiverEntity extends BlockEntity implements ITransmissionReceiver
{
	public static final String IDC = "idc";
	public static final String FREQUENCY = "frequency";
	public static final String EDIT_FREQUENCY = "edit_frequency";
	
	private boolean editingFrequency = false;
	private int frequency = 0;
	private String idc = "";
	
	public TransceiverEntity(BlockPos pos, BlockState state) 
	{
		super(BlockEntityInit.TRANSCEIVER.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
	}
	
	@Override
    public void load(CompoundTag tag)
    {
    	super.load(tag);

    	editingFrequency = tag.getBoolean(EDIT_FREQUENCY);
    	frequency = tag.getInt(FREQUENCY);
    	idc = tag.getString(IDC);
	}
	
	@Override
    protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);

		tag.putBoolean(EDIT_FREQUENCY, editingFrequency);
		tag.putInt(FREQUENCY, frequency);
		tag.putString(IDC, idc);
	}
	
	public float transmissionRadius()
	{
		return CommonTransmissionConfig.max_transceiver_transmission_distance.get();
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
		if(frequency != this.frequency)
			return;
		
		if(!getCurrentCode().equals(transmission))
			return;
		
		Level level = getLevel();
		BlockPos pos = getBlockPos();
		BlockState state = getBlockState();
		
		if(state.getBlock() instanceof TransceiverBlock transceiver)
			transceiver.receiveTransmission(state, level, pos);
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
						receiver.receiveTransmission(0, 0, getCurrentCode());
				});
			}
		}
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
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(this.worldPosition)), new ClientboundTransceiverUpdatePacket(this.worldPosition, this.editingFrequency, this.frequency, this.idc));
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, TransceiverEntity transceiver)
    {
		transceiver.updateClient();
    }
}
