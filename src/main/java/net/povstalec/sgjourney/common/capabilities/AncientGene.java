package net.povstalec.sgjourney.common.capabilities;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.AttachmentTypeInit;

import java.util.Random;

public class AncientGene
{
	public static final String ANCIENT_GENE = "ancient_gene";
	
	public static final Codec CODEC = StringRepresentable.fromValues(() -> new ATAGene[]{ATAGene.ANCIENT, ATAGene.INHERITED, ATAGene.ARTIFICIAL, ATAGene.NONE, ATAGene.UNDECIDED});
	
	public static final EntityCapability<AncientGene, Void> ANCIENT_GENE_CAPABILITY = EntityCapability.createVoid(
			StargateJourney.sgjourneyLocation(ANCIENT_GENE), AncientGene.class);
	
	private LivingEntity entity;
	private ATAGene gene;
	
	public AncientGene(LivingEntity entity)
	{
		this.entity = entity;
		this.gene = this.entity.getData(AttachmentTypeInit.ATA_GENE);
	}
	
	public enum ATAGene implements StringRepresentable
	{
		ANCIENT("ancient", true),
		INHERITED("inherited", true),
		ARTIFICIAL("artificial", true),
		NONE("none", false),
		UNDECIDED("undecided", false);
		
		private final String name;
		private boolean canActivate;
		
		private ATAGene(String name, boolean canActivate)
		{
			this.name = name;
			this.canActivate = canActivate;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
		}
		
		private boolean canActivate()
		{
			return this.canActivate;
		}
	}
	
	public ATAGene getGeneType()
	{
		return this.gene;
	}
	
	public boolean canUseAncientTechnology()
	{
		return this.gene.canActivate();
	}
	
	public boolean isAncient()
	{
		return this.gene.equals(ATAGene.ANCIENT);
	}
	
	public boolean isInherited()
	{
		return this.gene.equals(ATAGene.INHERITED);
	}
	
	public boolean isArtificial()
	{
		return this.gene.equals(ATAGene.ARTIFICIAL);
	}
	
	public boolean isLacking()
	{
		return this.gene.equals(ATAGene.NONE);
	}
	
	public void setGene(ATAGene gene)
	{
		this.gene = gene;
		this.entity.setData(AttachmentTypeInit.ATA_GENE, this.gene);
	}
	
	public static void spawnAncientGene(Entity entity)
	{
		AncientGene ancientGene = entity.getCapability(ANCIENT_GENE_CAPABILITY);
		if(ancientGene != null)
		{
			if(ancientGene.firstJoin())
				ancientGene.setGene(ATAGene.ANCIENT);
		}
	}
	
	public static void spawnInheritedGene(Entity entity)
	{
		AncientGene ancientGene = entity.getCapability(ANCIENT_GENE_CAPABILITY);
		if(ancientGene != null)
		{
			if(ancientGene.firstJoin())
				ancientGene.setGene(ATAGene.INHERITED);
		}
	}
	
	public static void spawnInheritedGene(long seed, Entity entity, int inheritanceChance)
	{
		Random random = new Random(seed);
		int chance = random.nextInt(1, 101);
		
		spawnInheritedGene(entity, inheritanceChance, chance);
	}
	
	public static void spawnInheritedGene(Entity entity, int inheritanceChance)
	{
		Random random = new Random();
		int chance = random.nextInt(1, 101);
		
		spawnInheritedGene(entity, inheritanceChance, chance);
	}
	
	private static void spawnInheritedGene(Entity entity, int inheritanceChance, int randomValue)
	{
		AncientGene cap = entity.getCapability(ANCIENT_GENE_CAPABILITY);
		if(cap != null)
		{
			if(cap.firstJoin())
			{
				if(randomValue <= inheritanceChance)
					cap.setGene(ATAGene.INHERITED);
				else
					cap.setGene(ATAGene.NONE);
			}
		}
	}
	
	public static void spawnArtificialGene(Entity entity)
	{
		AncientGene ancientGene = entity.getCapability(ANCIENT_GENE_CAPABILITY);
		if(ancientGene != null)
		{
			if(ancientGene.firstJoin())
				ancientGene.setGene(ATAGene.ARTIFICIAL);
		}
	}
	
	public static void spawnNoGene(Entity entity)
	{
		AncientGene ancientGene = entity.getCapability(ANCIENT_GENE_CAPABILITY);
		if(ancientGene != null)
		{
			if(ancientGene.firstJoin())
				ancientGene.setGene(ATAGene.NONE);
		}
	}
	
	
	public boolean firstJoin()
	{
		return this.gene == ATAGene.UNDECIDED;
	}
	
	public void copyFrom(AncientGene source)
	{
		this.gene = source.gene;
	}
}
