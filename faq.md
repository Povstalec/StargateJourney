---
title: Frequently Asked Questions
nav_order: 5
has_children: false
---

# Frequently asked Questions
{: .no_toc }

{: .highlight }
If you are playing All the Mods 9 modpack, check [its section](/atm9) first. 

1. Table of Contents
{:toc}

## I am having an issue with the mod, how do I fix it?
Check out Troubleshooting for common problems.

___

## I dialed a Milky Way Stargate, why isn't it rotating?
Milky Way Stargates do not spin if DHD is used, and symbols are instead "encoded directly."
If you want the gate to spin, use the [interface and computercraft mod](), or dial the gate [manually (with redstone)](). <!-- TODO: add links for dialing -->

{: .future }
A designated Dialing Computer block is planned for the future that will allow easy dialing with the gate spinning
without the need for computercraft.

___

## Is there an example of a ComputerCraft dialing program?
Yes, [here](https://github.com/Povstalec/StargateJourney-ComputerCraft-Programs) is a repository with some example programs.

You can also join our [Discord server]({{ site.discord_invite_link }}) where you can find community [creations](https://discord.com/channels/1011344665678708818/1194755632302141552).

There is also an **outdated** video tutorial,
which goes through the steps of creating a basic dialing program.
<details>
    <summary>YouTube video</summary>
    {% include youtubePlayer.html id="qNi9NUAmOJM" %}
</details>

___

## How to find addresses of other dimensions? <br> How to find Cartouches?
In survival mode, check our [Survival Guide / Finding addresses](/survival/addresses/).
<details>
    <summary>Creative mode (with command)</summary>
    You can use the command <code>/sgjourney stargateNetwork address &lt;dimension&gt;</code>,
    this command will tell you the <b>7-chevron</b> address of the specified dimension.
    Check the <a href="/commands">commands section</a> for details and other available commands.
</details>

___

## I dialed the Address of Nether/The End/Other Dimension, why is the game telling me "Dialed Solar System has no Stargates" ?
Stargates only generate in the Overworld and Dimensions added by Stargate Journey. 
Other Dimensions have their own Addresses and can house Stargates, 
but unless you use a Datapack, they won't generate Stargates by default. 
If you want to make a Dimension generate Stargates, follow this [Guide](https://github.com/Povstalec/StargateJourney/wiki/Guides/#adding-a-dimension-to-stargate-network).
<!-- TODO: Guide link is dead -->

___

## How can I find the 9-chevron address of my Stargate?
No matter what, you can use [PDA]()<!-- TODO: add link to PDA -->; 
right-clicking the gate will print info about it to the chat.

Or you can break the gate and check its tooltip in the inventory.
The 9-chevron address will be in the tooltip if it is a **classic (or upgraded) Stargate**
or the `always_display_stargate_id` config option is enabled (disabled by default).
<details>
    <summary>Tooltip image</summary>
    <img src="/assets/img/classic_stargate_tooltip.png" alt="Classic Stargate tooltip">
</details>

___

## How do I make a Stargate?
Check our [Survival Guide / End game - Creating a Stargate](/survival/end_game#creating-a-stargate).

___
