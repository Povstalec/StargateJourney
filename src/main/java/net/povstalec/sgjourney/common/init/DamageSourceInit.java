package net.povstalec.sgjourney.common.init;

import net.minecraft.world.damagesource.DamageSource;

public class DamageSourceInit
{
	public static final DamageSource KAWOOSH = (new DamageSource("sgjourney.kawoosh"))
			.bypassArmor().bypassInvul().bypassMagic().bypassEnchantments().setNoAggro();
	
	public static final DamageSource REVERSE_WORMHOLE = (new DamageSource("sgjourney.reverseWormhole"))
			.bypassArmor().bypassInvul().bypassMagic().bypassEnchantments().setNoAggro();
	
	public static final DamageSource IRIS = (new DamageSource("sgjourney.iris"))
			.bypassArmor().bypassInvul().bypassMagic().bypassEnchantments().setNoAggro();
}
