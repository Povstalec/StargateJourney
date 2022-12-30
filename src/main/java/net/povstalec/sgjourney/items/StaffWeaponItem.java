package net.povstalec.sgjourney.items;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.entities.PlasmaProjectile;
import net.povstalec.sgjourney.init.EntityInit;
import net.povstalec.sgjourney.init.SoundInit;

public class StaffWeaponItem extends Item
{

	public StaffWeaponItem(Properties properties)
	{
		super(properties);
	}
	
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		
		ItemStack itemstack = player.getItemInHand(hand);
		level.playSound(player, player.blockPosition(), SoundInit.MATOK_FIRE.get(), SoundSource.PLAYERS, 0.25F, 1F);
	      if (!level.isClientSide)
	      {
	    	  PlasmaProjectile plasmaProjectile = new PlasmaProjectile(EntityInit.JAFFA_PLASMA.get(), player, level);
	    	  plasmaProjectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 5.0F, 1.0F);
              level.addFreshEntity(plasmaProjectile);
          }

	      player.awardStat(Stats.ITEM_USED.get(this));
	      player.getCooldowns().addCooldown(this, 25);

	      return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
	}
	
	public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
	{
		return !player.isCreative();
	}
}
