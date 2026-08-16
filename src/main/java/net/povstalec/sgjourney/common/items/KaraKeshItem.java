package net.povstalec.sgjourney.common.items;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.tech.GoauldTech;

public class KaraKeshItem extends Item implements GoauldTech
{
	public static final String MODE = "mode";
	
	public enum Mode
	{
		KNOCKBACK,
		TERROR
	}
	
	public KaraKeshItem(Properties properties)
	{
		super(properties);
	}
	
	public Mode getMode(ItemStack stack)
	{
		if(stack.hasTag())
			return stack.getTag().getBoolean(MODE) ? Mode.TERROR : Mode.KNOCKBACK;
		
		return Mode.KNOCKBACK;
	}
	
	public void setMode(ItemStack stack, Mode mode)
	{
		stack.getOrCreateTag().putBoolean(MODE, mode == Mode.TERROR);
	}
	
	public boolean canUse(LivingEntity user)
	{
		return true/*CommonTechConfig.disable_kara_kesh_requirements.get() || canUseGoauldTech(user)*/;
	}
	
	@Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);
		
		if(canUse(player) && player.isShiftKeyDown())
		{
			ItemStack stack = player.getItemInHand(usedHand);
			
			Mode oldMode = getMode(stack);
			setMode(stack, oldMode == Mode.KNOCKBACK ? Mode.TERROR : Mode.KNOCKBACK);
			
			if(oldMode == Mode.KNOCKBACK)
				player.displayClientMessage(Component.translatable("tooltip.sgjourney.kara_kesh.terror").withStyle(ChatFormatting.RED), true);
			else
				player.displayClientMessage(Component.translatable("tooltip.sgjourney.kara_kesh.knockback").withStyle(ChatFormatting.GOLD), true);
			
			return InteractionResultHolder.success(player.getItemInHand(usedHand));
		}
		else
        	return super.use(level, player, usedHand);
    }
	
	public @NotNull InteractionResult interactLivingEntity(@NotNull ItemStack stack, Player player, @NotNull LivingEntity target, @NotNull InteractionHand hand)
	{
		if(canUse(player) && !player.getCooldowns().isOnCooldown(this) && !player.isShiftKeyDown())
		{
			if(getMode(stack) == Mode.KNOCKBACK)
			{
				target.knockback(2.0F, player.getX() - target.getX(), player.getZ() - target.getZ());
				player.getCooldowns().addCooldown(this, 50);
			}
			else
			{
				target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1));
				target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
				target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 255));
				player.getCooldowns().addCooldown(this, 200);
			}
			target.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, player.level.random.nextFloat() * 0.4F + 0.8F);
			return InteractionResult.PASS;
		}
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean canAttackBlock(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos position, Player player)
	{
		return !player.isCreative();
	}

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag isAdvanced)
    {
		if(getMode(stack) == Mode.TERROR)
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.kara_kesh.terror").withStyle(ChatFormatting.RED));
		else
			tooltipComponents.add(Component.translatable("tooltip.sgjourney.kara_kesh.knockback").withStyle(ChatFormatting.GOLD));
    	
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.kara_kesh.terror_knockback").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.kara_kesh.use").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

}
