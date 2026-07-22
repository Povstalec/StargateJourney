---
title: Frequently Asked Questions
nav_order: 10
description: "Frequently Asked Questions about Stargate Journey Minecraft mod"
---

# Frequently Asked Questions
{: .no_toc }

{: .highlight }
If you are playing All the Mods 9 modpack, check [its section]({{ site.baseurl }}/atm9) first.

## I am having an issue with the mod. How do I fix it?
Check out Troubleshooting for common problems.

___

## I dialed an address but DHD says "Incomplete address"
The Stargate Journey update 0.6.45 introduced the Point of Origin button on the DHD as a standalone symbol.  
Look for the symbol `0` (Zero) in the left of the DHD screen and encode it before pressing the big button in the middle.

![DHD Point of Origin button]({{ site.baseurl }}/assets/img/faq/DHD_PoO.png)

___

## I dialed a Milky Way Stargate. Why isn't it rotating?
Milky Way Stargates do not spin if DHD is used, and symbols are instead "encoded directly."
If you want the gate to spin, use the [interface and computercraft mod]({{ '/stargate-technology/stargate/#dialing' | absolute_url }}), or dial the gate [manually (with redstone)]({{ '/stargate-technology/stargate/#manual-dialing-with-redstone' | absolute_url }}). 


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

## I dialed an Address, but the gate won't connect.

Depending on the way you used to dial the gate, you need to get the recent feedback explaining why the gate did not connect.
Using DHD, an error message should be displayed above your hotbar when standing near DHD at the time of dial failure.
Using computercraft, the method used to engage the gate returns the feedback code.
Using redstone, you need to check the feedback code with PDA.

You can find the list of feedback codes at the [Stargate page]({{ '/stargate-technology/stargate/#stargate-feedback' | absolute_url }})


___

## How can I find the 9-chevron address of my Stargate?
There are three possible ways:
1. **Using a PDA** - Right-click the Stargate with a PDA which will print the 9-chevron address in the chat.
2. **Using CC:Tweaked** - The advanced crystal interface is capable of reading the local address of the Stargate with [`getLocalAddress()`]({{ '/computercraft/stargate-interface/#getLocalAddress' | absolute_url }}).
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
This includes for example: Ancient gene detector, Hand device, Personal shield, ZPM, ZPM hub and others.  
Note that the mod is still in development.

{: .note }
> **About ZPM specifically**  
> As of now, the ZPM is a creative-only item, because it's very overpowered. 
> Its capacity is `10 000 0000 000 000 FE`, which isn't a small number.
> 
> It will stay a creative-only item until a proper way of obtaining and usage is created 
> (survival progression for Pegasus galaxy, Destiny ship...)

___

## Why I keep finding only cartouches with the Overworld address?
If you are on Abyods and you are only finding cartouches with Overworld address,
you are not searching the [cartouche structure carefully enough]({{ '/survival/addresses/#abydos-cartouche' | absolute_url }}).

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
