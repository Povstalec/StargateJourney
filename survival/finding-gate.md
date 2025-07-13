---
title: Finding a Stargate
nav_order: 0
parent: Survival Guide
description: "A survival guide to finding the buried stargate from the Stargate Journey Minecraft mod."
---

# Finding a Stargate
{: .no_toc }

To gain the ability to travel to distant planets, you will first need a Stargate.
It is an advanced technology, so you **cannot build** it yourself (yet).
You need to **find it** somewhere.
All information has been lost in the mists of time.
You could employ an Archeologist villager to help you.
But first, you must get his interest, perhaps with some valuable relic.

<details markdown="block" id="locate-command">
<summary><b>[Spoiler]</b> A cheat way to find a Stargate using a command</summary>
You can use the locate command.

```
/locate structure #sgjourney:buried_stargate
```
{: .width-fit-content }

[No structure found? Check troubleshooting steps.]({{ '/troubleshooting/#i-created-a-new-world-but-the-stargate-isnt-generating' | absolute_url }})

</details>

To employ a villager as an Archeologist, you will need an [Archeology table]({{ '/blocks/archeology-table/' | absolute_url }}),
which can be crafted with a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}).

___

## Find the Golden Idol

**[Golden idol]({{ '/blocks/golden-idol/' | absolute_url }})** is a relic left behind by 
Goa'ulds - _an ancient and powerful race whose temples now lie buried in the forgotten corners of the world._
To find the idol, you must brave the ruins scattered across deserts, badlands, and jungles.

<details markdown="block">
<summary><b>[Spoiler]</b> Goa'uld temple overworld generation and a cheat way of finding a temple using a command</summary>
There are three types of Goa'uld temples depending on the biome in which they are generated: 
[badlands ziggurats]({{ '/structures/goauld-temples/#badlands-ziggurat' | absolute_url }}), 
[desert pyramids]({{ '/structures/goauld-temples/#abandoned-desert-pyramid' | absolute_url }}), 
and [jungle pyramids]({{ '/structures/goauld-temples/#jungle-pyramid' | absolute_url }}).
Each temple has a room with loot, a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}),
and [transport rings]({{ '/blocks/technological-blocks/transport-rings/' | absolute_url }})
connected to six other nearby rings (possibly in other temples) - as long as they were generated before.

**A cheat way:** You can use the locate command to find the coordinates of the closest temple  

```
/locate structure #sgjourney:goauld_temple
```
{: .width-fit-content }

If the command fails, look for [known incompatibilities]({{ '/#known-incompatibilities' | absolute_url }})
or other world generation mods that might prevent the temple from generating.

[//]: # (Using code block to include the automatic copy button)
</details>

___

## Archeologist villager
With a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}), you can now craft an [archeology table]({{ '/blocks/archeology-table/' | absolute_url }})
that can be used as a [workstation for a villager](https://minecraft.wiki/w/Villager#Job_site_blocks).
Remember that there are [Nitwits](https://minecraft.wiki/w/Villager#Nitwit) who cannot have a profession.

{% minecraft_recipe_crafting item:"sgjourney:archeology_table" %}

You can find available trades in the archeology table description at [Blocks / Archeology table]({{ '/blocks/archeology-table/#archeologist-villager' | absolute_url }}).

To unlock the **Map to Chappa'ai** trade, you must level up the villager to **Master**.
If the villager refuses to give you the map at the Master level, he failed to locate the structure in the current dimension.
Check [Troubleshooting / Stargate isn't generating]({{ '/troubleshooting/#i-created-a-new-world-but-the-stargate-isnt-generating' | absolute_url }}).

![Villager map trade]({{ '/assets/img/survival/stargate_map_trade.png' | absolute_url }})

___

## The Map to Chappa'ai
The big red **X** on the map will lead you to a buried stargate.
Search and dig for the X, the stargate is somewhere in there.

{: .warning #map-name-warning }
> Note that the map must be named **Map to Chappa'ai** and traded on the **Master** level.
> The villager may also offer an **Archeologist's map** that leads to a goa'uld temple, not a stargate.

The gate is usually around 5 blocks under the surface (but not strictly, you certainly don't have to dig all the way to the bedrock).
If you are having trouble finding the gate, see the note below with a hint.

Once you find it, you want to look for three essential things.
- The Stargate
- Dial Home Device (DHD) buried somewhere nearby/next to the gate
- The cartouche placed in the middle of the seal

The Gate and DHD can be broken with any tool (even by hand), but a pickaxe is best for the job.
You need a **stone pickaxe** (or a better one) to obtain the cartouche.


<details markdown="block">
<summary><b>[Spoiler]</b> Stargate overworld generation</summary>
By default, **two stargates** are generated in the **overworld** ([datapacks]({{ '/datapacks' | absolute_url }}) can change that).
Both gates are generated and buried underground with a DHD.  
The [Alpha gate]({{ '/structures/stargates/#buried-stargate-the-alpha-gate' | absolute_url }}) is generated in a horizontal position and with a seal and Abydos cartouche.  
The [Beta gate]({{ '/structures/stargates/#terra-gate-the-beta-gate' | absolute_url }}) is generated in a vertical position in a small cave. There is no seal or a cartouche.

More gate pedestals may be generated when the `common_stargate_generation` config option is enabled.

[//]: # (TODO: add link to page about config options and common stargates especially)

In the overworld, the map should always lead to the sealed alpha gate with the cartouche.
</details>

![Map with red X]({{ '/assets/img/survival/map.png' | absolute_url }})

{: .warning #map-missing-x-bug-warning }
> There is a known bug that can cause the map to lack the red **X** mark.
> In that case, you can read the location with the `/data` command (while holding the map):
>
> ```
> /data get entity PlayerName SelectedItem.components."minecraft:map_decorations".+
> ```
> {: .width-fit-content }
> or just use the [`/locate` command](#locate-command).  
> _"If the game is not playing fair, why should you?"_

<blockquote class="note" id="spoiler-hint-buried-stargate-location">
<p>If you have trouble finding the Stargate on the <b>X</b> mark, you can check this spoiler / hint.</p>
<details markdown="block" id="spoiler-hint-buried-stargate-location">
<summary><b>[Spoiler / Hint]</b> Buried Stargate location</summary>
The gate is always generated in the exact location inside the chunk.
So you can go to the **X** mark and press `F3 + G` to see chunk boundaries.  
Press `F3` to see your coordinates - find the line looking like this:

`Block: 256 64 256 [13 0 3]` (the numbers will be different)

The first three numbers are the coordinates of the block in the world on which you are standing.
The last three numbers in square brackets are the block coordinates in the chunk.
Dig on `[13 y 3]`, and you should find the Stargate (the middle number is height and will be different for each world).
Note that you might be in the wrong chunk, so if you can't find the gate, try to dig in the neighboring chunks as well.

If you really can't find the gate, you can use the [`/locate` command](#locate-command) to find the exact coordinates 
or switch to spectator mode and fly around.

If you could confirm the location using the [`/locate` command](#locate-command) and found out in the spectator mode that the gate is not there, 
then the gate was probably not generated in your world.
Check [Troubleshooting / Stargate isn't generating]({{ '/troubleshooting/#i-created-a-new-world-but-the-stargate-isnt-generating' | absolute_url }}) for further steps.

![Chunk border with buried Stargate]({{ '/assets/img/survival/chunk_border_buried_stargate.png' | absolute_url }})

</details>
</blockquote>

## [Next page: Dialing]({{ '/survival/dialing' | absolute_url }})
{: .no_toc }
