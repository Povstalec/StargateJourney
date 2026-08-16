package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.AncientGene;

import java.util.Objects;

public class AttachmentTypeInit
{
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, StargateJourney.MODID);
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<AncientGene.ATAGene>> ATA_GENE = ATTACHMENT_TYPES.register("ata_gene", () -> AttachmentType.builder(() -> AncientGene.ATAGene.UNDECIDED)
			.serialize(AncientGene.CODEC, ancientGene -> ancientGene != AncientGene.ATAGene.UNDECIDED)
			.copyHandler((ancientGene, holder, provider) -> ancientGene == AncientGene.ATAGene.UNDECIDED ? null : ancientGene)
			.build());
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<CompoundTag>> GOAULD_HOST = ATTACHMENT_TYPES.register("goauld_host", () -> AttachmentType.builder(() -> new CompoundTag())
			.serialize(CompoundTag.CODEC, Objects::nonNull)
			.copyHandler((goauldHost, holder, provider) -> goauldHost)
			.build());
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<CompoundTag>> JAFFA_POUCH = ATTACHMENT_TYPES.register("jaffa_pouch", () -> AttachmentType.builder(() -> new CompoundTag())
			.serialize(CompoundTag.CODEC, Objects::nonNull)
			.copyHandler((jaffaPouch, holder, provider) -> jaffaPouch)
			.build());
	
	public static void register(IEventBus eventBus)
	{
		ATTACHMENT_TYPES.register(eventBus);
	}
}
