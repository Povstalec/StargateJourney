package net.povstalec.sgjourney.common.sgjourney.info;

import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;

import java.util.ArrayList;
import java.util.List;

public class TransporterIDFilterInfo
{
	public static final String FILTER_TYPE = "filter_type";
	public static final String WHITELIST = "whitelist";
	public static final String BLACKLIST = "blacklist";
	
	private FilterType filterType;
	private final ArrayList<HiddenID> whitelist;
	private final ArrayList<HiddenID> blacklist;
	
	public TransporterIDFilterInfo()
	{
		filterType = FilterType.NONE;
		whitelist = new ArrayList<HiddenID>();
		blacklist = new ArrayList<HiddenID>();
	}
	
	public void deserializeFilters(CompoundTag tag)
	{
		if(tag.contains(FILTER_TYPE))
			this.filterType = FilterType.getFilterType(tag.getInt(FILTER_TYPE));
		
		if(tag.contains(WHITELIST))
		{
			CompoundTag whitelistTag = tag.getCompound(WHITELIST);
			
			for(String addressString : whitelistTag.getAllKeys())
			{
				this.whitelist.add(new HiddenID(new TransporterID.Immutable(addressString), whitelistTag.getBoolean(addressString)));
			}
		}
		
		if(tag.contains(BLACKLIST))
		{
			CompoundTag blacklistTag = tag.getCompound(BLACKLIST);
			
			for(String addressString : blacklistTag.getAllKeys())
			{
				this.blacklist.add(new HiddenID(new TransporterID.Immutable(addressString), blacklistTag.getBoolean(addressString)));
			}
		}
	}
	
	public void serializeFilters(CompoundTag tag)
	{
		tag.putInt(FILTER_TYPE, filterType.getIntegerValue());
		
		CompoundTag whitelistTag = new CompoundTag();
		CompoundTag blacklistTag = new CompoundTag();
		
		for(HiddenID hiddenID : this.whitelist)
		{
			whitelistTag.putBoolean(hiddenID.transporterID().toString(), hiddenID.isVisible());
		}
		
		for(HiddenID hiddenID : this.blacklist)
		{
			blacklistTag.putBoolean(hiddenID.transporterID().toString(), hiddenID.isVisible());
		}
		
		tag.put(WHITELIST, whitelistTag);
		tag.put(BLACKLIST, blacklistTag);
	}
	
	public FilterType setFilterType(int integerValue)
	{
		this.filterType = FilterType.getFilterType(integerValue);
		
		return this.filterType;
	}
	
	public FilterType getFilterType()
	{
		return this.filterType;
	}
	
	public boolean isIDWhitelisted(TransporterID transporterID)
	{
		return this.whitelist.contains(transporterID);
	}
	
	public boolean addToWhitelist(TransporterID.Immutable transporterID, boolean isVisible)
	{
		if(this.whitelist.contains(transporterID))
		{
			for(HiddenID hiddenID : this.whitelist)
			{
				if(hiddenID.transporterID().equals(transporterID))
				{
					hiddenID.isVisible = isVisible;
					break;
				}
			}
			return false;
		}
		
		this.whitelist.add(new HiddenID(transporterID, isVisible));
		
		return true;
	}
	
	public boolean removeFromWhitelist(TransporterID transporterID)
	{
		if(!this.whitelist.contains(transporterID))
			return false;
		
		this.whitelist.remove(transporterID);
		
		return true;
	}
	
	public List<HiddenID> getWhitelist()
	{
		return this.whitelist;
	}
	
	public void clearWhitelist()
	{
		this.whitelist.clear();
	}
	
	public boolean isIDBlacklisted(TransporterID transporterID)
	{
		return this.blacklist.contains(transporterID);
	}
	
	public boolean addToBlacklist(TransporterID transporterID, boolean isVisible)
	{
		if(this.blacklist.contains(transporterID))
		{
			for(HiddenID hiddenID : this.blacklist)
			{
				if(hiddenID.transporterID().equals(transporterID))
				{
					hiddenID.isVisible = isVisible;
					break;
				}
			}
			return false;
		}
		
		this.blacklist.add(new HiddenID(transporterID, isVisible));
		
		return true;
	}
	
	public boolean removeFromBlacklist(TransporterID.Immutable transporterID)
	{
		if(!this.blacklist.contains(transporterID))
			return false;
		
		this.blacklist.remove(transporterID);
		
		return true;
	}
	
	public List<HiddenID> getBlacklist()
	{
		return this.blacklist;
	}
	
	public void clearBlacklist()
	{
		this.blacklist.clear();
	}
	
	
	
	public enum FilterType
	{
		NONE(0),
		WHITELIST(1),
		BLACKLIST(-1);
		
		private final int integerValue;
		
		FilterType(int integerValue)
		{
			this.integerValue = integerValue;
		}
		
		public int getIntegerValue()
		{
			return this.integerValue;
		}
		
		public boolean shouldFilter()
		{
			return this != NONE;
		}
		
		public boolean isWhitelist()
		{
			return this == WHITELIST;
		}
		
		public boolean isBlacklist()
		{
			return this == BLACKLIST;
		}
		
		public static FilterType getFilterType(int integerValue)
		{
			return switch(integerValue)
			{
				case 1 -> WHITELIST;
				case -1 -> BLACKLIST;
				default -> NONE;
			};
		}
	}
	
	public static class HiddenID
	{
		private final TransporterID.Immutable transporterID;
		private boolean isVisible;
		
		public HiddenID(TransporterID transporterID, boolean isVisible)
		{
			this.transporterID = new TransporterID.Immutable(transporterID);
			this.isVisible = isVisible;
		}
		
		public TransporterID.Immutable transporterID()
		{
			return this.transporterID;
		}
		
		public boolean isVisible()
		{
			return this.isVisible;
		}
		
		@Override
		public boolean equals(Object object)
		{
			return transporterID.equals(object);
		}
	}
	
	
	
	public interface Interface
	{
		TransporterIDFilterInfo transporterIDFilterInfo();
	}
}
