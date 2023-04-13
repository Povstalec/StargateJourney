package net.povstalec.sgjourney.items.crystals;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoryCrystalItem extends Item
{
	private static final String MEMORY = "Memory-";
	
	private static final String COORDINATES = "Coordinates";
	private static final String ADDRESS = "Address";
	private static final String EXTRAGALACTIC_ADDRESS = "ExtragalacticAddress";
	private static final String STARGATE_ADDRESS = "StargateAddress";
	
	public MemoryCrystalItem(Properties properties)
	{
		super(properties);
	}
	
	//TODO Saves the Type of memory and then saves the memory itself
	
	public static CompoundTag tagSetup() //TODO
	{
		CompoundTag tag = new CompoundTag();
		
		return tag;
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
	{
		if(!level.isClientSide)
		{
			ItemStack memory_crystal = player.getItemInHand(usedHand);
			
        	if(memory_crystal.hasTag() && player.isShiftKeyDown())
        	{
        		memory_crystal.setTag(new CompoundTag());
        	}
		}
        return super.use(level, player, usedHand);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return stack.hasTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
        if(stack.hasTag())
        {
            String location = stack.getTag().getString("location");
            tooltipComponents.add(Component.literal(location).withStyle(ChatFormatting.BLUE));
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
