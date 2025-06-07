---
nav_exclude: true
---

{: .warning }
> This is development version of the documentation.  
> The production version can be found at [https://povstalec.github.io/StargateJourney/](https://povstalec.github.io/StargateJourney/)
> 
> The current state of the new structure:  
> 
> |                                    |                        |
> |:----------------------------------:|:----------------------:|
> |  **What's that Stargate anyway?**  |       no changes       |
> |   **Frequently Asked Questions**   | done, ready for review |
> |        **Troubleshooting**         | done, ready for review |
> |         **Survival Guide**         |    ongoing changes     |
> |                                    |                        |
>

<div class="text-center" markdown="block">

![Stargate Journey logo]({{ '/assets/img/logo.png' | absolute_url }})

{% minecraft_recipe_crafting item:"sgjourney:advanced_crystal_base" %}

# Stargate Journey
{: .no_toc }

<div style="display: none">
    <p>// Lets call this an easter egg</p>
    <p>Did you ever hear the tragedy of Darth Plagueis The Wise? I thought not.</p>
    <p>It’s not a story the Jedi would tell you. It’s a Sith legend.</p>
    <p>Darth Plagueis was a Dark Lord of the Sith, so powerful and so wise he could use the Force to influence the midichlorians to create life…</p>
    <p>He had such a knowledge of the dark side that he could even keep the ones he cared about from dying.</p>
    <p>The dark side of the Force is a pathway to many abilities some consider to be unnatural.</p>
    <p>He became so powerful… the only thing he was afraid of was losing his power, which eventually, of course, he did. Unfortunately, he taught his apprentice everything he knew, then his apprentice killed him in his sleep.</p>
    <p>Ironic. He could save others from death, but not himself..</p>
</div>

<a href="https://www.curseforge.com/minecraft/mc-mods/sgjourney" target="_blank"><img src="https://img.shields.io/curseforge/dt/689083?style=for-the-badge&logo=curseforge&color=626e7b" alt="Curseforge"></a>
<a href="https://modrinth.com/mod/sgjourney" target="_blank"><img src="https://img.shields.io/modrinth/dt/sgjourney?style=for-the-badge&logo=modrinth&color=626e7b" alt="Modrinth"></a>
<a href="{{ site.discord_invite_link }}" target="_blank"><img alt="Static Badge" src="https://img.shields.io/badge/Join_our_Discord_server-grey?style=for-the-badge&logo=discord" alt="Discord"></a>
<a href="https://github.com/Povstalec/StargateJourney" target="_blank"><img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/Povstalec/StargateJourney?style=for-the-badge&logo=github&color=626e7b" alt="GitHub"></a>

Stargate Journey is a mod for Minecraft with many additions, such as several dimensions, items, and obviously - Stargates.

</div>

{: .note }
This project is in early development, and a lot of stuff still needs to be implemented.
Things may be buggy, unfinished, or unobtainable in survival.  
If you find a bug, it will be appreciated if you report it to 
[GitHub issues](https://github.com/Povstalec/StargateJourney/issues) or [Discord]({{ site.discord_invite_link }}).

If you are __new to the topic__ and want to know what that thing is...
Stargate, they call it - check [What's that Stargate anyway?]({{ '/what-is-stargate' | absolute_url }}).

{: .highlight }
If you are playing All the Mods 9 modpack, check the [Modpacks / All the Mods 9]({{ '/modpacks/atm9' | absolute_url }}) section.

The documentation is divided into a few main parts:
* [Survival Guide]({{ '/survival' | absolute_url }}): A ~~recommended~~ possible way to progress through the mod in survival.
* [Stargate Technology]({{ '/stargate-technology' | absolute_url }}): A look at Stargate mechanics and related technology, including DHDs, crystals and the iris.
* [Stargate Network]({{ '/stargate-network' | absolute_url }}): How are Stargates addressed and how they connect to each other.
* [Blocks]({{ '/blocks' | absolute_url }}): Details about special blocks, ores, their generation and machines.
* [Items]({{ '/items' | absolute_url }}): Crafting and resources processing, weapons and tools. Although, for crafting recipes it is strongly recommended to use the [JEI](https://www.curseforge.com/minecraft/mc-mods/jei) mod.
* [Dimensions]({{ '/dimensions' | absolute_url }}): A list of new planets, how to get there and what to expect.
* [Structures]({{ '/structures' | absolute_url }}): Information about the structures that generate around the world.
* [Commands]({{ '/commands' | absolute_url }}): List of commands and their usage available for operators.
* [Computercraft]({{ '/computercraft' | absolute_url }}): API documentation for compatibility integration with [ComputerCraft Tweaked](https://tweaked.cc/) mod.
* [Datapacks and Resourcepacks]({{ '/datapacks' | absolute_url }}): Explanation of resourcepack and datapack features available for customization.

{: .highlight }
> Feel free to ask on the [Discord server]({{ site.discord_invite_link }}) if you have a question.
> 
> Before asking a question or reporting a bug,
> please check the [F&Q]({{ '/faq' | absolute_url }}) and [Troubleshooting]({{ '/troubleshooting' | absolute_url }}) sections and try to use the search function on GitHub [issues](https://github.com/Povstalec/StargateJourney/issues) and [discussions](https://github.com/Povstalec/StargateJourney/discussions) or [Discord]({{ site.discord_invite_link }}).
> Someone else may have already asked about that.

## Compatibility with other mods
Stargate Journey is compatible with multiple other mods:
 - [Stellar View](https://www.curseforge.com/minecraft/mc-mods/stellarview) can be used to enhance your experience of different planetary night skies
 - [CC:Tweaked](https://tweaked.cc/), which allows the control of Stargates with interfaces
 - [Ad Astra](https://ad-astra.terrarium.wiki/ad-astra)'s two default Solar Systems are also considered Solar Systems within Stargate Journey

## Known incompatibilities
Some mods have been found to break stargates or their generation: 

- [Dimensional Threading](https://www.curseforge.com/minecraft/mc-mods/dimensional-threads) and similar mods, 
when installed, Stargates won't be able to dial.
- [Sparse Structures](https://modrinth.com/mod/sparsestructures) and similar mods that modifies the structures spacing/position, 
Stargates might not generate, 
you might not be able to find a Stargate and to reach other dimensions (Stargate will be missing there).
However, 
it is possible to disable spreading for specific structures with config ([example config]({{ site.baseurl }}/others/sparse_structures)).
- [Structure Essentials](https://www.curseforge.com/minecraft/mc-mods/structure-essentials-forge-fabric)
Stargates might not generate, you might not be able to find a Stargate and to reach other dimension (Stargate will be missing there).
([Possible fix with config example]({{ site.baseurl }}/others/structure_essentials))

___

## Community Creations
Some cool people found the mod interesting enough to create new content for it.  
These are some of them:

### New Stargate Variants:
   - [More Gates](https://www.curseforge.com/minecraft/mc-mods/more-gates-mod-ver)
   - [Dimension Gates](https://discord.com/channels/1011344665678708818/1200953359650263100) (discord thread link)

### Additional decorative blocks:
   - [Stargate Journey: Deco](https://www.curseforge.com/minecraft/mc-mods/stargate-journey-deco)
   - [Stargate Journey Additions](https://www.curseforge.com/minecraft/mc-mods/stargate-journey-additions)

### Computercraft scripts:
   - [Ktlo's Pocket Stargate](https://github.com/Ktlo/pocket-stargate)
   - [Red's EZ Auto-Dialer Script](https://discord.com/channels/1011344665678708818/1217131207532482662) (discord thread link)

### Resource packs:
   - [Stargate Journey: Refreshed](https://www.curseforge.com/minecraft/texture-packs/stargate-journey-refreshed)

___

## Author
The author and developer of the mod is **Povstalec**, also known as **Wold** (_woldericz_junior_).

<div markdown="block" style="opacity: 0.1">
> Wold, the mighty and true only Wold.  
> *Hallowed is the Wold*  
> He is said to be one of the few Ancients who remained hidden on Terra.
</div>

You can also see all the incredible people who contributed to the development on GitHub 
or [helped otherwise](https://github.com/Povstalec/StargateJourney/blob/main/CREDITS.txt).

<a href="https://github.com/Povstalec/StargateJourney/graphs/contributors" target="_blank">
  <img src="https://contrib.rocks/image?repo=Povstalec/StargateJourney"/>
</a>
