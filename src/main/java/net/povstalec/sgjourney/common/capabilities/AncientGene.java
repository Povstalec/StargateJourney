package net.povstalec.sgjourney.common.capabilities;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class AncientGene
{
	public static final String ANCIENT_GENE = "AncientGene";
	
	public enum ATAGene
	{
		ANCIENT(true),
		INHERITED(true),
		ARTIFICIAL(true),
		NONE(false),
		UNDECIDED(false);
		
		private boolean canActivate;
		
		private ATAGene(boolean canActivate)
		{
			this.canActivate = canActivate;
		}
		
		private boolean canActivate()
		{
			return this.canActivate;
		}
	}
	
	private ATAGene gene = ATAGene.NONE;
	
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
	}
	
	public static void spawnAncientGene(Entity entity)
	{
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap ->
		{
			if(cap.firstJoin())
				cap.setGene(ATAGene.ANCIENT);
		});
	}
	
	public static void spawnInheritedGene(Entity entity)
	{
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap ->
		{
			if(cap.firstJoin())
				cap.setGene(ATAGene.INHERITED);
		});
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
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> 
		{
			if(cap.firstJoin())
			{
				if(randomValue <= inheritanceChance)
					cap.setGene(ATAGene.INHERITED);
				else
					cap.setGene(ATAGene.NONE);
			}
		});
	}
	
	public static void spawnArtificialGene(Entity entity)
	{
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap ->
		{
			if(cap.firstJoin())
				cap.setGene(ATAGene.ARTIFICIAL);
		});
	}
	
	public static void spawnNoGene(Entity entity)
	{
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap ->
		{
			if(cap.firstJoin())
				cap.setGene(ATAGene.NONE);
		});
	}
	
	
	public boolean firstJoin()
	{
		return this.gene == ATAGene.UNDECIDED;
	}
	
	public void copyFrom(AncientGene source)
	{
		this.gene = source.gene;
	}
	
	public void saveData(CompoundTag tag)
	{
		tag.putString(ANCIENT_GENE, this.gene.toString().toUpperCase());
	}
	
	public void loadData(CompoundTag tag)
	{
		this.gene = ATAGene.valueOf(tag.getString(ANCIENT_GENE));
	}
}
