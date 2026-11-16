package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.misc.Trinary;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;

import java.util.Set;

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
	
	public static TransporterInfo.FeedbackMessage getRecentFeedback(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getRecentFeedback();
	}
	
	public static TransporterInfo.FeedbackMessage dialCoords(AbstractTransporterEntity<?> transporter, Vec3i coords)
	{
		return transporter.dialTransporter(coords);
	}
	
	//============================================================================================
	//*************************************Crystal Interface**************************************
	//============================================================================================
	
	public static TransporterInfo.FeedbackMessage dialTransporterID(AbstractTransporterEntity<?> transporter, TransporterID transporterID)
	{
		return transporter.dialTransporter(transporterID);
	}
	
	public static TransporterID.Immutable getLocalTransporterID(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getID();
	}
	
	/**
	 * @param transporter Transporter
	 * @return Networks this Transporter is a part of
	 */
	public static Set<Integer> getNetworks(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getNetworks();
	}
	
	/**
	 * Adds the specified Transporter to a network
	 * @param transporter Transporter
	 * @return True if the operation was successful, otherwise false
	 */
	public static boolean addNetwork(AbstractTransporterEntity<?> transporter, int network)
	{
		return transporter.addNetwork(network);
	}
	
	/**
	 * Removes the specified Transporter from a network
	 * @param transporter Transporter
	 * @return True if the operation was successful, otherwise false
	 */
	public static boolean removeNetwork(AbstractTransporterEntity<?> transporter, int network)
	{
		return transporter.removeNetwork(network);
	}
	
	public static void setRestrictNetwork(AbstractTransporterEntity<?> transporter, Trinary restrictNetwork)
	{
		transporter.setRestrictNetwork(restrictNetwork);
	}
	
	public static Trinary getRestrictNetwork(AbstractTransporterEntity<?> transporter)
	{
		return transporter.getRestrictNetwork();
	}
}
