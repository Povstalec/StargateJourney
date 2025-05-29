---
title: Finding a Stargate
nav_order: 0
parent: Survival Guide
---

# Finding a Stargate
{: .no_toc }

1. Table of Contents
{:toc}

{: .note }
This guide will try its best to provide **hints** on how to proceed through the mod.
And yet not reveal how it works behind the scenes and what the structures look like.
**Spoilers** will be in **collapsible sections**,
so **it's up to you** if you want to see them,
or you'll **enjoy the moment** when you find them for the first time in the game.

To gain the ability to travel to distant planets, you will first need a Stargate.
It is an advanced technology, so you **cannot build** it yourself (yet).
You need to **find it** somewhere first.

<details markdown="block" id="locate-command">
<summary><b>[Spoiler]</b> A cheat way to find a Stargate using a command</summary>
You can use the locate command.  
`/locate structure #sgjourney:buried_stargate`

[No structure found?](#no-stargate-generated)

[//]: # (TODO: add link to troubleshooting)
</details>

An Archeologist villager could find a way.
To employ a villager, you will need an [Archeology table]({{ '/blocks/archeology-table/' | absolute_url }}),
which can be crafted with a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}).

___

## Find a Golden Idol in Goa'uld temple

**[Golden idol]({{ '/blocks/golden-idol/' | absolute_url }})** is a relic left in this world by older civilizations.
To find the idol, you must travel a little bit and find some Goa'uld temple.
It's a small spoiler to say it can be generated in deserts, badlands, and jungles.

<details markdown="block">
<summary><b>[Spoiler]</b> Goa'uld temple overworld generation</summary>
There are three types of Goa'uld temples according to the biome in which they are generated: 
[badlands ziggurats]({{ '/structures/goauld_temples/#badlands-ziggurat' | absolute_url }}), 
[desert pyramids]({{ '/structures/goauld_temples/#abandoned-desert-pyramid' | absolute_url }}), 
and [jungle pyramids]({{ '/structures/goauld_temples/#jungle-pyramid' | absolute_url }}).

Each temple has a room with loot, a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }})
and a room with [transport rings]({{ '/blocks/technological-blocks/transport-rings/' | absolute_url }})
that are connected to six other nearby rings (possibly in other temples).

**A cheat way:** You can use the locate command to find the coordinates of the closest temple  
`/locate structure #sgjourney:goauld_temple`
</details>

___

## Archeologist villager
With a [golden idol]({{ '/blocks/golden-idol/' | absolute_url }}), you can now craft an [archeology table]({{ '/blocks/archeology-table/' | absolute_url }})
that can be used as a workstation for a villager.
Remember that there are [Nitwits](https://minecraft.wiki/w/Villager#Nitwit) that cannot have a profession.

For crafting, you will need a book, a golden idol, 3 wooden slabs, and 2 (wooden) sticks.

![Archeology table crafting]({{ site.baseurl }}/assets/img/survival/archeology_table_crafting.png)

Now, you can trade a **Map to Chappa'ai** (Stargate) on the villager's last (**Master**) level.

{: .warning #no-stargate-generated }
> If the villager is refusing to give you the map and the [locate command](#locate-command) results in no structure found,
> please check the [Known incompatibilities]({{ site.baseurl }}/#known-incompatibilities) section on the main page.

[//]: # (TODO: add link to troubleshooting)

![Villager map trade]({{ site.baseurl }}/assets/img/survival/stargate_map_trade.png)

{: .note }
> The map must be named **Map to Chappa'ai** or **Map to the Ring of Gods**.
> The villager can also give you an **Archeologist Map** which leads to a Goa'uld temple.


___

## The Map to Chappa'ai
The big red X on the map will lead you to a buried Stargate.
Search and dig for the X. The Stargate is somewhere in there.

![Map with red X]({{ site.baseurl }}/assets/img/survival/map.png)

The gate is usually around 5 blocks under the surface (but not strictly).

{: .note }
> There is a known bug that can cause the map to lack the red X mark.
> In that case, you can read the location with the `/data` command (while holding the map):
>
> `/data get entity PlayerName SelectedItem.components."minecraft:map_decorations".+`


<blockquote class="warning">
<p>If you have trouble finding the Stargate on the X mark, you can check this spoiler / hint.</p>
<details markdown="block">
<summary><b>[Spoiler / Hint]</b> Buried Stargate location</summary>
The gate is always generated in the same location inside the chunk.
So you can go to the X and press `F3 + G` to see chunk boundaries.  
Press `F3` to see your coordinates - find line looking like this:

`Block: 256 64 256 [13 0 3]` (the numbers will be different)

The first three numbers are the coordinates of the block in the world.
The last three numbers in square brackets are the block coordinates in the chunk.
Dig on `[13 y 3]` and you should find the Stargate (the middle number is height and will be different for each world).
Note that you might be in a wrong chunk, so if you can't find the gate, try to dig in the neighboring chunks as well.

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
By default, **two stargates** are generated in the **overworld** (this can be changed by [datapacks]({{ site.baseurl }}/datapacks)).  
Both gates are generated and buried underground with DHD.  
The [Alpha gate]({{ '/structures/stargates/#buried-stargate-the-alpha-gate' | absolute_url }}) is generated in a horizontal position and with a seal and abydos cartouche.  
The [Beta gate]({{ '/structures/stargates/#terra-gate-the-beta-gate' | absolute_url }}) is generated in vertical position in a small cave.  

In the overworld, the map should always lead to the sealed alpha gate.
Although there are known cases where it does not (usually when datapacks are involved),
</details>

## [Next page: Dialing]({{ site.baseurl }}/survival/dialing)
{: .no_toc }
