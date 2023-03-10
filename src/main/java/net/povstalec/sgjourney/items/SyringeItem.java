package net.povstalec.sgjourney.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.capabilities.AncientGeneProvider;
import net.povstalec.sgjourney.config.ServerGeneticConfig;
import net.povstalec.sgjourney.init.ItemInit;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class SyringeItem extends Item
{

	public SyringeItem(Properties properties)
	{
		super(properties);
	}
	
	public enum Contents
	{
		EMPTY("tooltip.sgjourney.syringe_empty", ChatFormatting.GRAY),
		BLOOD("tooltip.sgjourney.syringe_blood", ChatFormatting.DARK_RED),
		PROTOTYPE_ATA("tooltip.sgjourney.syringe_prototype_ata_gene", ChatFormatting.AQUA),
		ATA("tooltip.sgjourney.syringe_ata_gene", ChatFormatting.AQUA);
		
		private String text;
		private ChatFormatting formatting;
		
		Contents(String text, ChatFormatting formatting)
		{
			this.text = text;
			this.formatting = formatting;
		}
		
		public String getText()
		{
			return this.text;
		}
		
		public ChatFormatting getFormatting()
		{
			return this.formatting;
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		if(!level.isClientSide())
		{
			if(player.isShiftKeyDown())
				return this.tryToApplyEffects(player, player.getItemInHand(hand)) ? InteractionResultHolder.pass(player.getItemInHand(hand)) : InteractionResultHolder.fail(player.getItemInHand(hand));
		}
		
		return InteractionResultHolder.fail(player.getItemInHand(hand));
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.syringe_contents").withStyle(ChatFormatting.GRAY));
    	
        if(stack.hasTag() && stack.getTag().contains("Contents"))
        {
        	Contents contents = Contents.valueOf(stack.getTag().getString("Contents"));
        	String text = contents.getText();
            
            tooltipComponents.add(Component.translatable(text).withStyle(contents.getFormatting()));
        }
        else
            tooltipComponents.add(Component.translatable("tooltip.sgjourney.syringe_empty").withStyle(ChatFormatting.GRAY));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
    
    private boolean tryToApplyEffects(Entity target, ItemStack stack)
    {
    	if(stack.hasTag())
    	{
    		Contents contents = Contents.valueOf(stack.getTag().getString("Contents"));
    		
    		switch(contents)
    		{
    		case EMPTY:
    			return false;
    		case BLOOD:
    			return false;
    		case PROTOTYPE_ATA:
    			return applyAncientGene(target, ServerGeneticConfig.prototype_ata_gene_therapy_success_rate.get());
    		case ATA:
    			return applyAncientGene(target, ServerGeneticConfig.ata_gene_therapy_success_rate.get());
    		}
    	}
    	
    	return false;
    }
    
    private boolean applyAncientGene(Entity target, int probability)
    {
    	if(!target.getCapability(AncientGeneProvider.ANCIENT_GENE).isPresent())
    		return false;
    	
		target.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> 
		{
			if(cap.isLacking())
			{
				Random random = new Random();
				int chance = random.nextInt(1, 101);
				
				if(chance <= probability)
				{
					cap.implantGene();
					
					if(target instanceof Player player)
						player.sendSystemMessage(Component.translatable("message.sgjourney.syringe_got_ancient_gene").withStyle(ChatFormatting.AQUA));
				}
			}
			else
			{
				if(target instanceof Player player)
					player.sendSystemMessage(Component.translatable("message.sgjourney.syringe_has_ancient_gene").withStyle(ChatFormatting.AQUA));
			}
		});
		
		return true;
    }
	
	public static ItemStack addContents(Contents contents)
	{
		ItemStack stack = new ItemStack(ItemInit.SYRINGE.get());
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.putString("Contents", contents.name());
		stack.setTag(compoundtag);
		return stack;
	}
}
