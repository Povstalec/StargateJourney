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
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.InterfaceMethod;
import net.povstalec.sgjourney.common.compatibility.cctweaked.methods.InterfaceMethods;
import net.povstalec.sgjourney.common.compatibility.computer_functions.InterfaceFunctions;

public class InterfacePeripheral implements IDynamicPeripheral
{
	protected AbstractInterfaceEntity interfaceEntity;
	protected HashMap<String, InterfaceMethod<BlockEntity>> methods = new HashMap<String,InterfaceMethod<BlockEntity>>();
	
	public InterfacePeripheral(AbstractInterfaceEntity interfaceEntity)
	{
		this.interfaceEntity = interfaceEntity;
		
		this.registerMethod(new InterfaceMethods.SetEnergyTarget());
		this.registerMethod(new InterfaceMethods.AddressToString());
		
		/*if(this.interfaceEntity.getInterfaceType().hasCrystalMethods())
		{
			this.registerMethod(new InterfaceMethods.AddressToDimension());//TODO Move this to its own block
		}*/
		
		/*if(this.interfaceEntity.getInterfaceType().hasAdvancedCrystalMethods())
		{
			
		}*/
	}
	
	@Override
	public String getType()
	{
		return interfaceEntity.getInterfaceType().getName();
	}

	@Override
	public boolean equals(IPeripheral other)
	{
		if(this == other)
			return true;
		
		return this.getClass() == other.getClass() && this.interfaceEntity == ((InterfacePeripheral) other).interfaceEntity;
	}

    @Override
    public void attach(@Nonnull IComputerAccess computer)
    {
    	interfaceEntity.getPeripheralWrapper().computerList.add(computer);
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer)
    {
    	interfaceEntity.getPeripheralWrapper().computerList.removeIf(computerAccess -> (computerAccess.getID() == computer.getID()));
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
		
		return methods.get(methodName).use(computer, context, this.interfaceEntity, this.interfaceEntity.energyBlockEntity, arguments);
	}
	
	@SuppressWarnings("unchecked")
	public <ConnectedBlockEntity extends BlockEntity> void registerMethod(InterfaceMethod<ConnectedBlockEntity> function)
	{
		methods.put(function.getName(), (InterfaceMethod<BlockEntity>) function);
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================

	@LuaFunction(mainThread = true)
	public final long getEnergy() throws LuaException
	{
		return InterfaceFunctions.getEnergy(interfaceEntity);
	}

	@LuaFunction(mainThread = true)
	public final long getEnergyCapacity() throws LuaException
	{
		return InterfaceFunctions.getEnergyCapacity(interfaceEntity);
	}

	@LuaFunction(mainThread = true)
	public final long getEnergyTarget() throws LuaException
	{
		return InterfaceFunctions.getEnergyTarget(interfaceEntity);
	}
}
