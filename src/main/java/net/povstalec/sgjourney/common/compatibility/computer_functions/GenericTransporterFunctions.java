package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;

public class GenericTransporterFunctions
{
	//============================================================================================
	//**************************************Basic Interface***************************************
	//============================================================================================
	
	public static String getTransporterType(AbstractTransporterEntity<?> transporter)
	{
		return BlockEntityType.getKey(transporter.getType()).toString();
	}
	
	public static boolean isTransporterConnected(AbstractTransporterEntity<?> transporter)
	{
		return transporter.isConnected();
	}
	
	public static long getTransporterEnergy(AbstractTransporterEntity<?> transporter)
	{
		return transporter.energyStorage.getTrueEnergyStored();
	}
	
	public static TransporterInfo.Feedback getRecentFeedback(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getRecentFeedback();
	}
	
	public static TransporterInfo.Feedback dialCoords(AbstractTransporterEntity<?> transporter, Vec3i coords)
	{
		return transporter.dialTransporter(coords);
	}
	
	//============================================================================================
	//*************************************Crystal Interface**************************************
	//============================================================================================
	
	public static TransporterInfo.Feedback dialTransporterID(AbstractTransporterEntity<?> transporter, TransporterID transporterID)
	{
		return transporter.dialTransporter(transporterID);
	}
	
	public static TransporterID.Immutable getLocalTransporterID(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getID();
	}
	
	//TODO
	/*public static TransporterID getConnectedTransporterID(AbstractTransporterEntity transporter)
	{
		Transporter connectedTransporter = transporter.getConnectedTransporter();
		if(connectedTransporter == null)
			return new TransporterID.Immutable();
	
		return connectedTransporter.getID();
	}
	
	public static int getNetwork(AbstractTransporterEntity transporter)
	{
		return transporter.getNetwork();
	}
	
	public static void setNetwork(AbstractTransporterEntity transporter, int network)
	{
		transporter.setNetwork(network);
	}
	
	public static void setRestrictNetwork(AbstractTransporterEntity transporter, boolean restrictNetwork)
	{
		transporter.setRestrictNetwork(restrictNetwork);
	}
	
	public static boolean isNetworkRestricted(AbstractTransporterEntity transporter)
	{
		return transporter.getRestrictNetwork();
	}*/
}
