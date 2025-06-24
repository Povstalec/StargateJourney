---
title: Troubleshooting
nav_order: 20
description: "Troubleshooting steps for common issues with Stargate Journey Minecraft mod"
---

# Troubleshooting
{: .no_toc }

If you can't find a solution to your problem on this page or if one of the solutions below doesn’t work, 
you can visit the [Discord server]({{ site.discord_invite_link }}) and ask there.

## Stargate is not dialing / not disconnecting / causing any trouble
**With access to commands:**  
Use the force stellar update command.

```
/sgjourney stargateNetwork forceStellarUpdate
```
{: .width-fit-content }

**Without access to commands, but with access to world files:**  
1. Go to your `saves/<world name here>/data` folder  
2. Delete `sgjourney-stargate_network.dat`

{: .warning }
> **UNDER NO CIRCUMSTANCES EVER DELETE** `sgjourney-block_enties.dat`.
> It holds info with **locations** of all the block entities, 
> like **Stargates** and **Transport Rings**,
> and deleting it means Stargate Network won't be able to find the Stargates anymore.

After all that, log in to your world again to let the Stargate Network regenerate, and everything should be fine.

___

## I created a new world, but the Stargate isn't generating.
Villager is not giving me the **map to Chappa'ai**, or the **locate command** results in an error.

If you are trying to locate the buried Stargate with the locate command, which results in an error,
try executing the command positioned at the Stargate generation center.

Leveling up the villager for the map trade should have the same effect.
If the villager is not giving you the map, the positioned command will probably not work either.

The stargate generation center is by default at `X = 0` `Z = 0` coordinates,
though you may have changed it in the config file
(`stargate_generation_center_x_chunk_offset`, `stargate_generation_center_z_chunk_offset`).
If that is the case, multiply the values from the config by `16`
and use them in the command.

By default, this command should work:
```
/execute in minecraft:overworld positioned 0 0 0 run locate structure #sgjourney:has_stargate
```
{: .width-fit-content }

Or use this one and replace `X` and `Z` with the actual values from the config:
```
/execute in minecraft:overworld positioned X 0 Z run locate structure #sgjourney:has_stargate
```
{: .width-fit-content }

**If the locating still fails, it's most likely that the stargate structure is not generating.**

In this case, there are some possible reasons:
1. Stargate Journey was added to an existing world. [See the next section.](#i-added-the-mod-to-an-existing-world-but-stargate-isnt-generating)
2. An incompatible mod is installed.
   - Check the [known incompatibilities]({{ '/#known-incompatibilities' | absolute_url }})
   - A mod altering the structures generation or their locating is installed.
     This usually results in structures being generated in places different from what Stargate Journey expects.
     With some mods, it is possible to fix the problem by excluding the Stargate Journey structures.

   - A world generation mod that alters biomes and doesn't use proper (overworld) tags prevents the generation of structures.
     Removing the world generation mod will fix the problem.
     To keep the mod, it will probably be necessary to create a compatibility datapack
     that will allow the generation of the Stargate Journey structures in the modded biomes.

___


## I added the mod to an existing world, but Stargate isn't generating.
The locations of Stargate structures are fixed positions in the world based on the seed.
The Stargate structure can only be generated in **new chunks**.
If the chunk that should contain the Stargate structure has already been generated, it will not generate there.

By default, Stargate structures positions are picked from `64` chunks (`1024` blocks) radius, centered at `X = 0` `Z = 0`.

You can move the generation area with config if the default area was already generated.  
Setting following values in the `sgjourney-common.toml` config file:

```toml
  ["Stargate Journey Common Config"."Generation Config".server]
      # ... other values ...
      # X chunk center offset of structures that contain a Stargate
      # Default: 0
      # Range: -512 ~ 512
      stargate_generation_center_x_chunk_offset = 200
      # Z chunk center offset of structures that contain a Stargate
      # Default: 0
      # Range: -512 ~ 512
      stargate_generation_center_z_chunk_offset = 200
      # ... other values ...
```

will move the generation area to `X = 3200` `Z = 3200` coordinates (chunk is an area of `16x16` blocks).
With the default radius bonds (`64` chunks) it will generate the structure somewhere between `X = 2176` `Z = 2176` and `X = 4224` `Z = 4224`.

