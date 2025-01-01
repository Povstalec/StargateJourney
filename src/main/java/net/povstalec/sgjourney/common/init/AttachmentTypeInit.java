package net.povstalec.sgjourney.common.init;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.capabilities.AncientGene;

public class AttachmentTypeInit
{
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, StargateJourney.MODID);
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<AncientGene.ATAGene>> ATA_GENE = ATTACHMENT_TYPES.register("ata_gene", () -> AttachmentType.builder(() -> AncientGene.ATAGene.UNDECIDED)
			.serialize(AncientGene.CODEC, ancientGene -> ancientGene != AncientGene.ATAGene.UNDECIDED)
			.copyHandler((ancientGene, holder, provider) -> ancientGene == AncientGene.ATAGene.UNDECIDED ? null : ancientGene)
			.build());
	
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> BLOODSTREAM_NAQUADAH = ATTACHMENT_TYPES.register("bloodstream_naquadah", () -> AttachmentType.builder(() -> false)
			.serialize(Codec.BOOL, bloodstreamNaquadah -> bloodstreamNaquadah)
			.copyHandler((bloodstreamNaquadah, holder, provider) -> bloodstreamNaquadah)
			.build());
	
	public static void register(IEventBus eventBus)
	{
		ATTACHMENT_TYPES.register(eventBus);
	}
}
