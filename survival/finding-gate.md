---
title: Finding a Stargate
nav_order: 0
parent: Survival Guide
---

# Finding a Stargate
{: .no_toc }

To gain the ability to travel to distant planets, you will first need a Stargate.
It is an advanced technology, so you **cannot build** it yourself (yet).
You need to **find it** somewhere.
All information has been lost in the mists of time
You could employ an Archeologist villager to help you.
But first, you must attract his attention, perhaps with some valuable relic.

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
<summary><b>[Spoiler]</b> Goa'uld temple overworld generation</summary>
There are three types of Goa'uld temples depending on the biome in which they are generated: 
[badlands ziggurats]({{ '/structures/goauld_temples/#badlands-ziggurat' | absolute_url }}), 
[desert pyramids]({{ '/structures/goauld_temples/#abandoned-desert-pyramid' | absolute_url }}), 
and [jungle pyramids]({{ '/structures/goauld_temples/#jungle-pyramid' | absolute_url }}).
Each temple has a room with a loot, a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}),
and a room with [transport rings]({{ '/blocks/technological-blocks/transport-rings/' | absolute_url }})
that are connected to six other nearby rings (possibly in other temples).

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
that can be used as a workstation for a villager.
Remember that there are [Nitwits](https://minecraft.wiki/w/Villager#Nitwit) who cannot have a profession.

![Archeology table crafting]({{ '/assets/img/survival/archeology_table_crafting.png' | absolute_url }})

The villager offers few trades:
- Paper for emeralds
- Bones for emeralds
- Golden idol for emeralds
- Gold ingots for emeralds
- Sandstone Hieroglyphs for emeralds
- Emeralds for:
  - **Archeologist Map** (which leads to a Goa'uld temple)
  - Compass
  - Writable book
  - Fire pit
  - Sandstone with lapis
  - Sandstone, Red sandstone and Stone symbol
  - **Map to Chappa'ai** (Stargate) at the last Master level

[//]: # (TODO: replace with trade images)

To unlock the **Map to Chappa'ai** trade, you need to level up the villager to **Master**.
In case the villager refused to give you the map at the Master level, 
check [troubleshooting steps]({{ '/troubleshooting/#i-created-a-new-world-but-the-stargate-isnt-generating' | absolute_url }})
when the stargate is not generating.

![Villager map trade]({{ '/assets/img/survival/stargate_map_trade.png' | absolute_url }})

___

## The Map to Chappa'ai
The big red **X** on the map will lead you to a buried Stargate.
Search and dig for the X. The Stargate is somewhere in there.

![Map with red X]({{ '/assets/img/survival/map.png' | absolute_url }})

The gate is usually around 5 blocks under the surface (but not strictly, you certainly don't have to dig all the way to the bedrock).

{: .warning }
> There is a known bug that can cause the map to lack the red **X** mark.
> In that case, you can read the location with the `/data` command (while holding the map):
>
> ```
> /data get entity PlayerName SelectedItem.components."minecraft:map_decorations".+
> ```
> or just use the [`/locate` command](#locate-command).  
> _"If the game is not playing fair, why should you?"_

<blockquote class="note">
<p>If you have trouble finding the Stargate on the <b>X</b> mark, you can check this spoiler / hint.</p>
<details markdown="block">
<summary><b>[Spoiler / Hint]</b> Buried Stargate location</summary>
The gate is always generated in the exact location inside the chunk.
So you can go to the **X** mark and press `F3 + G` to see chunk boundaries.  
Press `F3` to see your coordinates - find the line looking like this:

`Block: 256 64 256 [13 0 3]` (the numbers will be different)

The first three numbers are the coordinates of the block in the world on which you are standing.
The last three numbers in square brackets are the block coordinates in the chunk.
Dig on `[13 y 3]`, and you should find the Stargate (the middle number is height and will be different for each world).
Note that you might be in the wrong chunk, so if you can't find the gate, try to dig in the neighboring chunks as well.

![Chunk border with buried Stargate]({{ '/assets/img/survival/chunk_border_buried_stargate.png' | absolute_url }})

</details>
</blockquote>

{: .tip }
> Once you find it, you want to look for three essential things.
> - The Stargate
> - Dial Home Device (DHD) buried somewhere nearby/next to the gate
> - The cartouche placed in the middle of the seal  
>
> The Gate and the DHD can be broken with any tool (even by hand), but a pickaxe is best for the job.
> Use a **stone pickaxe** (or a better one) to break the cartouche.


<details markdown="block">
<summary><b>[Spoiler]</b> Stargate overworld generation</summary>
By default, **two stargates** are generated in the **overworld** ([datapacks]({{ '/datapacks' | absolute_url }}) can change that).  
Both gates are generated and buried underground with DHD.  
The [Alpha gate]({{ '/structures/stargates/#buried-stargate-the-alpha-gate' | absolute_url }}) is generated in a horizontal position and with a seal and Abydos cartouche.  
The [Beta gate]({{ '/structures/stargates/#terra-gate-the-beta-gate' | absolute_url }}) is generated in a vertical position in a small cave.  

In the overworld, the map should always lead to the sealed alpha gate.
</details>

## [Next page: Dialing]({{ site.baseurl }}/survival/dialing)
{: .no_toc }
