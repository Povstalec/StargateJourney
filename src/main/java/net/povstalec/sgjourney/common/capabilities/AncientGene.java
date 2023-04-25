package net.povstalec.sgjourney.common.capabilities;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.common.config.CommonGeneticConfig;

public class AncientGene
{
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
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> cap.giveGene());
	}
	
	public static void inheritGene(Entity entity)
	{
		entity.getCapability(AncientGeneProvider.ANCIENT_GENE).ifPresent(cap -> 
		{
			if(cap.firstJoin())
			{
				Random random = new Random();
				int chance = random.nextInt(1, 101);
				
				if(chance <= CommonGeneticConfig.player_ata_gene_inheritance_chance.get())
					cap.inheritGene();
				
				cap.markJoined();
			}
		});
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
		tag.putBoolean("FirstJoin", firstJoin);
		tag.putString("AncientGene", this.gene.toString().toUpperCase());
	}
	
	public void loadData(CompoundTag tag)
	{
		this.firstJoin = tag.getBoolean("FirstJoin");
		this.gene = ATAGene.valueOf(tag.getString("AncientGene"));
	}
}
