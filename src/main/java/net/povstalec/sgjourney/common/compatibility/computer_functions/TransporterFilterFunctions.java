package net.povstalec.sgjourney.common.compatibility.computer_functions;

import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.info.TransporterIDFilterInfo;

import java.util.ArrayList;
import java.util.List;

public class TransporterFilterFunctions
{
	//============================================================================================
	//*********************************Advanced Crystal Interface*********************************
	//============================================================================================
	
	public static int getFilterType(AbstractTransporterEntity<?> transporter)
	{
		return transporter.transporterIDFilterInfo().getFilterType().getIntegerValue();
	}
	
	public static int setFilterType(AbstractTransporterEntity<?> transporter, int filterType)
	{
		return transporter.transporterIDFilterInfo().setFilterType(filterType).getIntegerValue();
	}
	
	public static boolean addToWhitelist(AbstractTransporterEntity<?> transporter, int[] idArray, boolean isVisible)
	{
		// Should Handle the following exceptions
		/*if(idArray.length > FULL_ID_LENGTH)
			throw new IllegalArgumentException("Transporter ID is too long <0, 7>");
		
		for(int j : idArray)
		{
			if(j < MIN_SYMBOL || j > MAX_SYMBOL)
				throw new IllegalArgumentException("Transporter ID symbol " + j + " out of bounds <1, 8>");
		}*/
		
		return transporter.transporterIDFilterInfo().addToWhitelist(new TransporterID.Immutable(idArray), isVisible);
	}
	
	public static boolean addToBlacklist(AbstractTransporterEntity<?> transporter, int[] idArray, boolean isVisible)
	{
		// Should Handle the following exceptions
		/*if(idArray.length > FULL_ID_LENGTH)
			throw new IllegalArgumentException("Transporter ID is too long <0, 7>");
		
		for(int j : idArray)
		{
			if(j < MIN_SYMBOL || j > MAX_SYMBOL)
				throw new IllegalArgumentException("Transporter ID symbol " + j + " out of bounds <1, 8>");
		}*/
		
		return transporter.transporterIDFilterInfo().addToBlacklist(new TransporterID.Immutable(idArray), isVisible);
	}
	
	public static String removeFromWhitelist(AbstractTransporterEntity<?> transporter, int[] idArray)
	{
		// Should Handle the following exceptions
		/*if(idArray.length > FULL_ID_LENGTH)
			throw new IllegalArgumentException("Transporter ID is too long <0, 7>");
		
		for(int j : idArray)
		{
			if(j < MIN_SYMBOL || j > MAX_SYMBOL)
				throw new IllegalArgumentException("Transporter ID symbol " + j + " out of bounds <1, 8>");
		}*/
		
		if(transporter.transporterIDFilterInfo().removeFromWhitelist(new TransporterID.Immutable(idArray)))
			return "Address removed from whitelist successfully";
		else
			return "Address is not whitelisted";
	}
	
	public static boolean removeFromBlacklist(AbstractTransporterEntity<?> transporter, int[] idArray)
	{
		// Should Handle the following exceptions
		/*if(idArray.length > FULL_ID_LENGTH)
			throw new IllegalArgumentException("Transporter ID is too long <0, 7>");
		
		for(int j : idArray)
		{
			if(j < MIN_SYMBOL || j > MAX_SYMBOL)
				throw new IllegalArgumentException("Transporter ID symbol " + j + " out of bounds <1, 8>");
		}*/
		
		return transporter.transporterIDFilterInfo().removeFromBlacklist(new TransporterID.Immutable(idArray));
	}
	
	public static List<TransporterID.Immutable> getPublicWhitelist(AbstractTransporterEntity<?> transporter)
	{
		List<TransporterID.Immutable> transporterIDs = new ArrayList<>();
		for(TransporterIDFilterInfo.HiddenID transporterID : transporter.transporterIDFilterInfo().getWhitelist())
		{
			if(transporterID.isVisible())
				transporterIDs.add(transporterID.transporterID());
		}
		
		return transporterIDs;
	}
	
	public static List<TransporterID.Immutable> getPublicBlacklist(AbstractTransporterEntity<?> transporter)
	{
		List<TransporterID.Immutable> transporterIDs = new ArrayList<>();
		for(TransporterIDFilterInfo.HiddenID transporterID : transporter.transporterIDFilterInfo().getBlacklist())
		{
			if(transporterID.isVisible())
				transporterIDs.add(transporterID.transporterID());
		}
		
		return transporterIDs;
	}
	
	public static String clearWhitelist(AbstractTransporterEntity<?> transporter)
	{
		transporter.transporterIDFilterInfo().clearWhitelist();
		
		return "Whitelist cleared";
	}
	
	public static String clearBlacklist(AbstractTransporterEntity<?> transporter)
	{
		transporter.transporterIDFilterInfo().clearBlacklist();
		
		return "Blacklist cleared";
	}
}
