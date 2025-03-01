package net.povstalec.sgjourney.common.stargate.info;

import net.minecraft.nbt.CompoundTag;
import net.povstalec.sgjourney.common.stargate.Address;

import java.util.ArrayList;
import java.util.List;

public class AddressFilterInfo
{
	public static final String FILTER_TYPE = "FilterType";
	public static final String WHITELIST = "Whitelist";
	public static final String BLACKLIST = "Blacklist";
	
	private FilterType filterType;
	private ArrayList<HiddenAddress> whitelist;
	private ArrayList<HiddenAddress> blacklist;
	
	public AddressFilterInfo()
	{
		filterType = FilterType.NONE;
		whitelist = new ArrayList<HiddenAddress>();
		blacklist = new ArrayList<HiddenAddress>();
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
				this.whitelist.add(new HiddenAddress(new Address.Immutable(addressString), whitelistTag.getBoolean(addressString)));
			}
		}
		
		if(tag.contains(BLACKLIST))
		{
			CompoundTag blacklistTag = tag.getCompound(BLACKLIST);
			
			for(String addressString : blacklistTag.getAllKeys())
			{
				this.blacklist.add(new HiddenAddress(new Address.Immutable(addressString), blacklistTag.getBoolean(addressString)));
			}
		}
	}
	
	public void serializeFilters(CompoundTag tag)
	{
		tag.putInt(FILTER_TYPE, filterType.getIntegerValue());
		
		CompoundTag whitelistTag = new CompoundTag();
		CompoundTag blacklistTag = new CompoundTag();
		
		for(HiddenAddress hiddenAddress : this.whitelist)
		{
			whitelistTag.putBoolean(hiddenAddress.address().toString(), hiddenAddress.isVisible());
		}
		
		for(HiddenAddress hiddenAddress : this.blacklist)
		{
			blacklistTag.putBoolean(hiddenAddress.address().toString(), hiddenAddress.isVisible());
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
	
	public boolean isAddressWhitelisted(Address.Immutable address)
	{
		return this.whitelist.contains(address);
	}
	
	public boolean addToWhitelist(Address.Immutable address, boolean isVisible)
	{
		if(this.whitelist.contains(address))
		{
			for(HiddenAddress hiddenAddress : this.whitelist)
			{
				if(hiddenAddress.address().equals(address))
				{
					hiddenAddress.isVisible = isVisible;
					break;
				}
			}
			return false;
		}
		
		this.whitelist.add(new HiddenAddress(address, isVisible));
		
		return true;
	}
	
	public boolean removeFromWhitelist(Address.Immutable address)
	{
		if(!this.whitelist.contains(address))
			return false;
		
		this.whitelist.remove(address);
		
		return true;
	}
	
	public List<HiddenAddress> getWhitelist()
	{
		return this.whitelist;
	}
	
	public void clearWhitelist()
	{
		this.whitelist.clear();
	}
	
	public boolean isAddressBlacklisted(Address.Immutable address)
	{
		return this.blacklist.contains(address);
	}
	
	public boolean addToBlacklist(Address.Immutable address, boolean isVisible)
	{
		if(this.blacklist.contains(address))
		{
			for(HiddenAddress hiddenAddress : this.blacklist)
			{
				if(hiddenAddress.address().equals(address))
				{
					hiddenAddress.isVisible = isVisible;
					break;
				}
			}
			return false;
		}
		
		this.blacklist.add(new HiddenAddress(address, isVisible));
		
		return true;
	}
	
	public boolean removeFromBlacklist(Address.Immutable address)
	{
		if(!this.blacklist.contains(address))
			return false;
		
		this.blacklist.remove(address);
		
		return true;
	}
	
	public List<HiddenAddress> getBlacklist()
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
		
		private int integerValue;
		
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
			switch(integerValue)
			{
				case 1:
					return WHITELIST;
				case -1:
					return BLACKLIST;
				default:
					return NONE;
			}
		}
	}
	
	public static class HiddenAddress
	{
		private Address.Immutable address;
		private boolean isVisible;
		
		public HiddenAddress(Address.Immutable address, boolean isVisible)
		{
			this.address = address;
			this.isVisible = isVisible;
		}
		
		public Address.Immutable address()
		{
			return this.address;
		}
		
		public boolean isVisible()
		{
			return this.isVisible;
		}
	}
	
	
	
	public interface Interface
	{
		AddressFilterInfo addressFilterInfo();
	}
}
