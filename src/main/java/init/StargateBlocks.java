package init;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import woldericz_junior.stargatejourney.tileentities.MovieStargateTile;

public class StargateBlocks 
{
	public static Block movie_stargate;
	public static Block movie_stargate_hitbox;
	public static Block milky_way_dhd;
	public static Block naquadah_ore;
	public static Block naquadah_block;
	public static Block naquadah_battery;
	public static Block sandstone_hieroglyphs;
	
	
	@ObjectHolder("sgjourney:movie_stargate")
	public static TileEntityType<MovieStargateTile> MOVIESTARGATE_TILE;
}
