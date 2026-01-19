---
title: Frequently Asked Questions
nav_order: 10
description: "Frequently Asked Questions about Stargate Journey Minecraft mod"
---

# Frequently Asked Questions
{: .no_toc }

{: .highlight }
If you are playing All the Mods 9 modpack, check [its section]({{ site.baseurl }}/atm9) first. 

[//]: # (TODO: update ATM9 link)

## I am having an issue with the mod. How do I fix it?
Check out Troubleshooting for common problems.

___

## I dialed a Milky Way Stargate. Why isn't it rotating?
Milky Way Stargates do not spin if DHD is used, and symbols are instead "encoded directly."
If you want the gate to spin, use the [interface and computercraft mod](), or dial the gate [manually (with redstone)](). 

[//]: # (TODO: add links for dialing)

{: .future }
A designated Dialing Computer block is planned for the future that will allow easy dialing with the gate spinning
without the need for computercraft.

___

## Is there an example of a ComputerCraft dialing program?
Yes, [here](https://github.com/Povstalec/StargateJourney-ComputerCraft-Programs) is a repository with some example programs.

You can also join [Discord server]({{ site.discord_invite_link }}) 
to find community [creations](https://discord.com/channels/1011344665678708818/1194755632302141552).

There is also an **outdated** video tutorial that goes through the steps of creating a basic dialing program.
<details>
    <summary>YouTube video</summary>
    {% include youtubePlayer.html id="qNi9NUAmOJM" %}
</details>

___

## How to find addresses of other dimensions? <br> How to find Cartouches?
In survival mode, check [Survival Guide / Finding addresses]({{ site.baseurl }}/survival/addresses/).
<details markdown="block">
<summary>Creative mode (with command)</summary>
You can use the command `/sgjourney stargateNetwork address <dimension>`,
which will tell you the **7-chevron** address of the specified dimension.
Check the [commands section]({{ site.baseurl }}/commands) for details and other available commands.
</details>

___

## The Stargate "glitch" / lags when spinning
This is primarily noticeable on the Universe Stargate.
It is because the Stargate movement is "actually happening."
And it is not just a client-side animation, so it is more prone to low tps and network lags.
The movement is synchronized for all players, which is also required for redstone dialing to work properly.

___

## I dialed an Address, but the gate won't connect.
Please read the error message.
You can find the most up-to-date list of all feedback codes and their names on [GitHub](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/sgjourney/StargateInfo.java#L120).  
The most common errors are:
- `Invalid address` - The error means that the dialed address is invalid (does not exist) for the current galaxy.
Verify that the address is correct and the target solar system belongs to the same galaxy (when 7-chevron address is used).
For solar systems from other galaxies it is required to use 8 or 9-chevron address.
- `Not enough power` - Gates require power in order to establish and maintain a connection.
The error simply means that the gate is not charged enough for the connection.
You can check the current amount of energy in the gate by right-clicking it with a PDA or using a computer from [CC:Tweaked](https://tweaked.cc/).
If you are trying to dial **Lantea**, or other dimension in distant galaxy, 
you need a huge amount of energy to establish a connection (`100 000 000 000 FE` by [default](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/config/CommonStargateConfig.java#L200)).
- `Target Stargate is obstructed` - The destination gate is obstructed by blocks.
Some naturally generated gates are obstructed by blocks (in the Nether, the End and Glacio) 
and needs to be found and unobstructed before dialing.
This mechanic is in place to prevent skipping the intended progress of the game.
- `Cannot dial the same Solar System` - You are using 7-chevron address to dial a stargate that is in your current solar system.
You need to use 9-chevron address to dial a specific stargate.
- `Dialed Solar System has no Stargates` - The dialed solar system does not have any Stargates.
Stargates only generate in some dimensions specified by the mod or by a datapack.
You need to reach the dimension otherwise first and place your own Stargate there.  
If the dimension should have generated a Stargate (e.g. dimension is from Stargate Journey), then the gate either didn't generate or was broken by a player.
The gate may not generate in some cases - e.g. when a mod that modifies structure generation (or lookups) is installed.
Please check the [known incompatibilities]({{ '/#known-incompatibilities' | absolute_url }}) for more information.
If you want to make a gate generate in the dimension, you can create a datapack by following this [guide]({{ '/datapacks/datapacks_outdated/#adding-a-dimension-to-stargate-network' | absolute_url }}).

[//]: # (TODO: add link to list of errors and their explanations)
[//]: # (TODO: add link to powering stargate docs)
[//]: # (TODO: add link to PDA)
[//]: # (TODO: add link to address types explanation)
[//]: # (TODO: add link to datapack creation guide for stargate generation)


___

## How can I find the 9-chevron address of my Stargate?
There are three possible ways:
1. **Using a PDA** - Right-click the Stargate with a PDA which will print the 9-chevron address in the chat.
2. **Using CC:Tweaked** - The advanced crystal interface is capable of reading the local address of the Stargate with [`getLocalAddress()`]({{ '/computercraft/stargate_interface/#getLocalAddress' | absolute_url }}).
3. **Item tooltip** - If the Stargate was created as a classic Stargate (possibly upgraded afterward),
or the `always_display_stargate_id` config option is enabled (disabled by default), you can break the gate and check its tooltip in the inventory.

<details markdown="block">
<summary>Tooltip image</summary>
![Classic Stargate tooltip]({{ site.baseurl }}/assets/img/classic_stargate_tooltip.png)
</details>

[//]: # (TODO: add link to PDA)

___

## How do I make a Stargate?
Check [Survival Guide / End game - Creating a Stargate]({{ site.baseurl }}/survival/end_game#creating-a-stargate).

___

## How can I get an item XY? It has no recipe.
Some items are not yet implemented, are partially implemented,
or they are implemented but not yet obtainable in survival.  
This includes for example: Ancient gene detector, Hand device, Ring remote, Personal shield, ZPM, ZPM hub and others.  
Note that the mod is still in development.

{: .note }
> **About ZPM specifically**  
> As of now, the ZPM is a creative-only item, because it's very overpowered. 
> Its capacity is `10 000 0000 000 000 FE`, which isn't a small number.
> 
> It will stay a creative-only item until a custom cable system is added to Stargate Journey, 
> which will allow modpack authors to specify whether a ZPM is allowed to power blocks and items that don't come from Stargate Journey. 
> This should make balancing tech mods easier and give them more breathing room, 
> as otherwise all ZPMs would always need to be endgame-only items.

___

## Why I keep finding only cartouches with the Overworld address?
If you are on Abyods and you are only finding cartouches with Overworld address,
you are not searching the cartouche structure carefully enough.

<details markdown="block">
<summary>What should I be looking for?</summary>
Once you are inside the structure underground, there are 4 more cartouches in a room to the left from the overworld cartouche.  
The entrance to the room is obstructed.
Behind the collapsed wall, you will find 2 random addresses of Stargate Journey dimensions
and 2 random addresses of non-Stargate Journey dimensions (Vanilla or added by other mods).

![Abydos cartouche structure]({{ '/assets/img/survival/abydos_cartouche_first.png' | absolute_url }})
![Abydos cartouche hidden room]({{ '/assets/img/survival/abydos_cartouche_second.png' | absolute_url }})
</details>

## How can I find the Beta Stargate?
You can leverage Stargates with DHD taking priority by removing DHD from all your Stargates in the Overworld.
Break the DHD and the Stargate, and place the Stargate back without the DHD.
It is required to break the Stargate as well to remove the information about the previously linked DHD.
Dial the Stargate manually (with redstone) or with a computer from [CC:Tweaked](https://tweaked.cc/).
Then you can dial the Terra system back from any other Solar system (using 7 or 8-chevron address) and you should end up at the Beta Stargate.

If you are able to locate specific structures using other mods or the `/locate` command, you can search for `sgjourney:stargate/milky_way/terra_stargate` structure.

`/locate structure sgjourney:stargate/milky_way/terra_stargate`


## How can I find an obstructed Stargate?
By default, obstructed Stargates are generated in the Nether, the End and Glacio from Ad Astra mod.

You can find them in the same way as the buried Stargates in the Overworld.
Take an archeologist villager to the dimension and level him up to the master level to unlock the map trade.
**You can't use already leveled up villager since he will always offer the same map.**
