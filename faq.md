---
title: Frequently Asked Questions
nav_order: 10
---

# Frequently Asked Questions
{: .no_toc }

{: .highlight }
If you are playing All the Mods 9 modpack, check [its section]({{ site.baseurl }}/atm9) first. 

1. Table of Contents
{:toc}

## I am having an issue with the mod. How do I fix it?
Check out Troubleshooting for common problems.

___

## I dialed a Milky Way Stargate. Why isn't it rotating?
Milky Way Stargates do not spin if DHD is used, and symbols are instead "encoded directly."
If you want the gate to spin, use the [interface and computercraft mod](), or dial the gate [manually (with redstone)](). <!-- TODO: add links for dialing -->

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
The movement is synchronized for all players.

___

## I dialed the Address of Nether/The End/Other Dimension. Why is the game telling me "Dialed Solar System has no Stargates"?
Stargates only generate in the Overworld and Dimensions added by Stargate Journey. 
Other Dimensions have their Addresses and can house Stargates.
However, unless you use a Datapack, they won't generate Stargates by default.
If you want to make a Dimension generate Stargates, follow this [Guide]({{ site.baseurl }}/datapacks/datapacks_outdated/#adding-a-dimension-to-stargate-network).

___

## How can I find the 9-chevron address of my Stargate?
No matter what, you can use a [PDA]()<!-- TODO: add link to PDA -->; 
right-clicking the gate will print info about it to the chat.

Or you can break the gate and check its tooltip in the inventory.
The 9-chevron address will be in the tooltip if it is a **classic (or upgraded) Stargate**
or the `always_display_stargate_id` config option is enabled (disabled by default).
<details markdown="block">
<summary>Tooltip image</summary>
![Classic Stargate tooltip]({{ site.baseurl }}/assets/img/classic_stargate_tooltip.png)
</details>

___

## How do I make a Stargate?
Check [Survival Guide / End game - Creating a Stargate]({{ site.baseurl }}/survival/end_game#creating-a-stargate).

___
