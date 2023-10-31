package net.povstalec.sgjourney.common.items;

import net.minecraft.world.item.Item;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.init.BlockInit;

public abstract class StargateUpgradeItem extends Item
{
	public StargateUpgradeItem(Properties properties)
	{
		super(properties);
	}

	public abstract AbstractStargateBaseBlock getStargateBaseBlock();
	
	public abstract AbstractStargateRingBlock getStargateRingBlock();
	
	
	
	public static class UniverseUpgrade extends StargateUpgradeItem
	{
		public UniverseUpgrade(Properties properties)
		{
			super(properties);
		}
		
		public AbstractStargateBaseBlock getStargateBaseBlock()
		{
			return BlockInit.UNIVERSE_STARGATE.get();
		}
		
		public AbstractStargateRingBlock getStargateRingBlock()
		{
			return BlockInit.UNIVERSE_RING.get();
		}
	}
	
	public static class MilkyWayUpgrade extends StargateUpgradeItem
	{
		public MilkyWayUpgrade(Properties properties)
		{
			super(properties);
		}
		
		public AbstractStargateBaseBlock getStargateBaseBlock()
		{
			return BlockInit.MILKY_WAY_STARGATE.get();
		}
		
		public AbstractStargateRingBlock getStargateRingBlock()
		{
			return BlockInit.MILKY_WAY_RING.get();
		}
	}
	
	public static class PegasusUpgrade extends StargateUpgradeItem
	{
		public PegasusUpgrade(Properties properties)
		{
			super(properties);
		}
		
		public AbstractStargateBaseBlock getStargateBaseBlock()
		{
			return BlockInit.PEGASUS_STARGATE.get();
		}
		
		public AbstractStargateRingBlock getStargateRingBlock()
		{
			return BlockInit.PEGASUS_RING.get();
		}
	}
	
	public static class TollanUpgrade extends StargateUpgradeItem
	{
		public TollanUpgrade(Properties properties)
		{
			super(properties);
		}
		
		public AbstractStargateBaseBlock getStargateBaseBlock()
		{
			return BlockInit.TOLLAN_STARGATE.get();
		}
		
		public AbstractStargateRingBlock getStargateRingBlock()
		{
			return BlockInit.TOLLAN_RING.get();
		}
	}
}
