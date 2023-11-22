package net.povstalec.sgjourney.common.items.crystals;

public class ControlCrystalItem extends AbstractCrystalItem
{
	public ControlCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	public enum FunctionTarget
	{
		CRYSTAL_INTERFACE,
		STARGATE,
		DHD,
		TRANSPORT_RINGS;
	}
	
	public enum Function
	{
		SEND_REDSTONE_SIGNAL(FunctionTarget.CRYSTAL_INTERFACE),
		SAVE_TO_MEMORY_CRYSTAL(FunctionTarget.CRYSTAL_INTERFACE),
		
		INPUT_SYMBOL(FunctionTarget.STARGATE);
		
		Function(FunctionTarget target)
		{
			
		}
	}

    /*@Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.control_crystal"));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }*/
	
	public static class Large extends ControlCrystalItem
	{
		public Large(Properties properties)
		{
			super(properties);
		}
	}
	
	public static class Advanced extends ControlCrystalItem
	{
		public Advanced(Properties properties)
		{
			super(properties);
		}
	}
}
