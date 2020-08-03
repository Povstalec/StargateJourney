package woldericz_junior.stargatejourney;

import init.StargateItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class StargateItemGroup extends ItemGroup{

	public StargateItemGroup() 
	{
		super("stargate");
	}

	@Override
	public ItemStack createIcon() 
	{
		return new ItemStack(StargateItems.movie_stargate);
	}

	
}
