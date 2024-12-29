package net.povstalec.sgjourney.common.items;

import java.util.List;
import java.util.Random;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.capabilities.AncientGene;
import net.povstalec.sgjourney.common.config.CommonGeneticConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class SyringeItem extends Item
{
	public static final Codec CONTENTS_CODEC = StringRepresentable.fromValues(() -> new Contents[]{Contents.EMPTY, Contents.BLOOD, Contents.PROTOTYPE_ATA, Contents.ATA});
	
	public SyringeItem(Properties properties)
	{
		super(properties);
	}
	
	public enum Contents implements StringRepresentable
	{
		EMPTY("empty", "tooltip.sgjourney.syringe.empty", ChatFormatting.GRAY),
		BLOOD("blood", "tooltip.sgjourney.syringe.blood", ChatFormatting.DARK_RED),
		PROTOTYPE_ATA("prototype_ata", "tooltip.sgjourney.syringe.prototype_ata_gene", ChatFormatting.AQUA),
		ATA("ata", "tooltip.sgjourney.syringe.ata_gene", ChatFormatting.AQUA);
		
		private String name;
		private String text;
		private ChatFormatting formatting;
		
		Contents(String name, String text, ChatFormatting formatting)
		{
			this.name = name;
			this.text = text;
			this.formatting = formatting;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
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
			{
				if(this.tryToApplyEffects(player, player.getItemInHand(hand)))
					player.getItemInHand(hand).set(DataComponentInit.SYRINGE_CONTENTS, Contents.EMPTY);
			}
		}
		
		return InteractionResultHolder.fail(player.getItemInHand(hand));
	}

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
    	tooltipComponents.add(Component.translatable("tooltip.sgjourney.syringe.contents").append(Component.literal(": ")).withStyle(ChatFormatting.GRAY));
		
		Contents contents = stack.getOrDefault(DataComponentInit.SYRINGE_CONTENTS, Contents.EMPTY);
		String text = contents.getText();
		
		tooltipComponents.add(Component.translatable(text).withStyle(contents.getFormatting()));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
    
    private boolean tryToApplyEffects(Entity target, ItemStack stack)
    {
		Contents contents = stack.getOrDefault(DataComponentInit.SYRINGE_CONTENTS, Contents.EMPTY);
		
		switch(contents)
		{
			case EMPTY:
				return false;
			case BLOOD:
				return false;
			case PROTOTYPE_ATA:
				return applyAncientGene(target, CommonGeneticConfig.prototype_ata_gene_therapy_success_rate.get());
			case ATA:
				return applyAncientGene(target, CommonGeneticConfig.ata_gene_therapy_success_rate.get());
		}
    	
    	return false;
    }
    
    private boolean applyAncientGene(Entity target, int probability)
    {
		AncientGene cap = target.getCapability(AncientGene.ANCIENT_GENE_CAPABILITY);
		
    	if(cap == null)
    		return false;
		
		if(cap.isLacking())
		{
			Random random = new Random();
			int chance = random.nextInt(1, 101);
			
			if(chance <= probability)
			{
				cap.implantGene();
				
				if(target instanceof Player player)
					player.sendSystemMessage(Component.translatable("message.sgjourney.syringe.got_ancient_gene").withStyle(ChatFormatting.AQUA));
			}
		}
		else
		{
			if(target instanceof Player player)
				player.sendSystemMessage(Component.translatable("message.sgjourney.syringe.has_ancient_gene").withStyle(ChatFormatting.AQUA));
		}
		
		return true;
    }
	
	public static ItemStack addContents(Contents contents)
	{
		ItemStack stack = new ItemStack(ItemInit.SYRINGE.get());
		stack.set(DataComponentInit.SYRINGE_CONTENTS, contents);
		
		return stack;
	}
}
