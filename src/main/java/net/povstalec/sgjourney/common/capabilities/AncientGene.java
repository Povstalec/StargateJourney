package net.povstalec.sgjourney.common.capabilities;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;

public class AncientGene
{
	public static final String FIRST_JOIN = "first_join";
	public static final String ANCIENT_GENE = "ancient_gene";
	
	public enum ATAGene
	{
		ANCIENT(true),
		INHERITED(true),
		ARTIFICIAL(true),
		NONE(false);
		
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
	
	private boolean firstJoin = true;
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
	
	public void giveGene()
	{
		this.gene = ATAGene.ANCIENT;
	}
	
	public void inheritGene()
	{
		this.gene = ATAGene.INHERITED;
	}
	
	public void implantGene()
	{
		this.gene = ATAGene.ARTIFICIAL;
	}
	
	public void removeGene()
	{
		this.gene = ATAGene.NONE;
	}
	
	public static void addAncient(Entity entity)
	{
		AncientGene cap = entity.getCapability(AncientGeneProvider.ANCIENT_GENE);
		if(cap != null)
			cap.giveGene();
	}
	
	public static void inheritGene(long seed, Entity entity, int inheritanceChance)
	{
		Random random = new Random(seed);
		int chance = random.nextInt(1, 101);
		
		inheritGene(entity, inheritanceChance, chance);
	}
	
	public static void inheritGene(Entity entity, int inheritanceChance)
	{
		Random random = new Random();
		int chance = random.nextInt(1, 101);
		
		inheritGene(entity, inheritanceChance, chance);
	}
	
	private static void inheritGene(Entity entity, int inheritanceChance, int chance)
	{
		AncientGene cap = entity.getCapability(AncientGeneProvider.ANCIENT_GENE);
		if(cap != null)
		{
			if(cap.firstJoin())
			{
				if(chance <= inheritanceChance)
					cap.inheritGene();
				
				cap.markJoined();
			}
		}
	}
	
	
	public boolean firstJoin()
	{
		return this.firstJoin;
	}
	
	public void markJoined()
	{
		this.firstJoin = false;
	}
	
	public void copyFrom(AncientGene source)
	{
		this.gene = source.gene;
	}
	
	public void saveData(CompoundTag tag)
	{
		tag.putBoolean(FIRST_JOIN, firstJoin);
		tag.putString(ANCIENT_GENE, this.gene.toString().toUpperCase());
	}
	
	public void loadData(CompoundTag tag)
	{
		this.firstJoin = tag.getBoolean(FIRST_JOIN);
		this.gene = ATAGene.valueOf(tag.getString(ANCIENT_GENE));
	}
}
