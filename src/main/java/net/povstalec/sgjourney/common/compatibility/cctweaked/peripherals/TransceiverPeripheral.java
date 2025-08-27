package net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals;

import java.util.HashMap;

import javax.annotation.Nonnull;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.TransceiverMethod;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.TransceiverMethods;
import net.povstalec.sgjourney.common.compatibility.computer_functions.TransceiverFunctions;

public class TransceiverPeripheral implements IPeripheral, IDynamicPeripheral
{
	protected TransceiverEntity transceiverEntity;
	protected HashMap<String, TransceiverMethod> methods = new HashMap<String, TransceiverMethod>();
	
	public TransceiverPeripheral(TransceiverEntity interfaceEntity)
	{
		this.transceiverEntity = interfaceEntity;
		
		this.registerMethod(new TransceiverMethods.SetCurrentCode());
		this.registerMethod(new TransceiverMethods.SetFrequency());
		this.registerMethod(new TransceiverMethods.SendTransmission());
		this.registerMethod(new TransceiverMethods.CheckConnectedShielding());
	}
	
	@Override
	public String getType()
	{
		return "transceiver";
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		return this.getClass() == other.getClass() && this.transceiverEntity == ((TransceiverPeripheral) other).transceiverEntity;
	}

    @Override
    public void attach(@Nonnull IComputerAccess computer)
    {
    	transceiverEntity.getPeripheralWrapper().computerList.add(computer);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer)
    {
    	transceiverEntity.getPeripheralWrapper().computerList.removeIf(computerAccess -> (computerAccess.getID() == computer.getID()));
    }

	@Override
	public String[] getMethodNames()
	{
		return methods.keySet().toArray(new String[0]);
	}

	@Override
	public MethodResult callMethod(IComputerAccess computer, ILuaContext context, int method, IArguments arguments)
			throws LuaException
	{
		String methodName = getMethodNames()[method];
		
		return methods.get(methodName).use(computer, context, this.transceiverEntity, arguments);
	}
	
	public <ConnectedBlockEntity extends BlockEntity> void registerMethod(TransceiverMethod function)
	{
		methods.put(function.getName(), function);
	}
	
	//============================================================================================
	//*************************************CC: Tweaked Events*************************************
	//============================================================================================
	
	public void queueEvent(String eventName, Object... objects)
	{
		for(IComputerAccess computer : transceiverEntity.getPeripheralWrapper().computerList)
		{
			int length = objects.length + 1;
			Object[] attachmentObjects = new Object[length];
			
			attachmentObjects[0] = computer.getAttachmentName();
			
			for(int i = 1; i < length; i++)
			{
				attachmentObjects[i] = objects[i - 1];
			}
			
			computer.queueEvent(eventName, attachmentObjects);
		}
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================

	@LuaFunction(mainThread = true)
	public final int getFrequency() throws LuaException
	{
		return TransceiverFunctions.getFrequency(transceiverEntity);
	}

	@LuaFunction(mainThread = true)
	public final String getCurrentCode() throws LuaException
	{
		return TransceiverFunctions.getCurrentCode(transceiverEntity);
	}
}
