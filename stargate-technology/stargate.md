---
title: Stargate
parent: Stargate Technology
nav_order: 1
description: "Documentation for Stargates from the Stargate Journey Minecraft mod."
---

# Stargate
{: .no_toc }

If you don't know what Stargate is, 
check [What’s that Stargate thing anyway?]({{ '/what-is-stargate/' | absolute_url }})

In order to interact with the Stargate, to read it's state using redstone
or communicate with [CC:Tweaked](https://tweaked.cc) computers, 
an [interface]({{ '/stargate-technology/interface/' | absolute_url }}) is required.

<details>
<summary>Dr. Jackson's instructional video on Stargate</summary>
{% include youtubePlayer.html id="CAK_x-hQFUs" %}
</details>

___

## Symbols
Sometimes called glyphs, are used for [address]({{ '/stargate-network/stargate-address/' | absolute_url }}) composition.
Usually each [galaxy]({{ '/stargate-network/galaxy/' | absolute_url }}) has its own set of symbols. Optionally a player can enable `unique_symbols` in the Stargate Journey client config.
When enabled, each [solar system]({{ '/stargate-network/solar-system/' | absolute_url }}) will have a slightly different symbols, usually following a common style within the [galaxy]({{ '/stargate-network/galaxy/' | absolute_url }}).
The used symbols can also be overridden by a [stargate variant](#stargate-variants).

{: .lore }
The `unique_symbols` configuration option was inspired by the original Stargate movie where the symbols for each solar system were different.

Each Stargate, except for the Universe, will pick the symbols of the local solar system when generated,
or placed for the first time. 
Additionally, since the Pegasus Stargate is digital, it will change the symbols each time it is placed,
unless dynamic symbols were [disabled by a computer]({{ '/computercraft/stargate_interface/#dynamicSymbols' | absolute_url }}).

Each symbol represents a number that is used for a more user-friendly address representation.
Zero always represents the [point of origin](#point-of-origin) and is at the end of every address.

There are 4 symbol sets for galaxies, and additional sets for each solar system, when unique symbols are enabled.
Those are in the base mod, more symbols can be added by [resourcepacks]({{ '/datapacks' | absolute_url }}).
The symbols below do not include points of origin, so the first symbol on the left represents the number one
and the others follow sequentially.

<style>
.min-width-1000 {
    min-width: 1000px;
}

.symbols-container {
    width: 100%;
    overflow: scroll;
}

.symbol-numbers-img {
    visibility: hidden;
}
.symbol-numbers-img.visible {
    visibility: visible;
}
</style>

<input type="checkbox" id="toggle-symbol-numbers" onchange="toggleSymbolNumbers()"/>
<label for="toggle-symbol-numbers">Show symbol numbers</label>

<script>
function toggleSymbolNumbers() {
    document.querySelectorAll(".symbol-numbers-img").forEach(img => {
        img.classList.toggle("visible");
    })
}
</script>

<div class="symbols-container">

Milky Way
<img alt="Milky Way symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/7a24f017dbda18a34a5f64a5186a8ded243ececb/src/main/resources/assets/sgjourney/textures/symbols/milky_way/galaxy_milky_way.png">
<img alt="Symbol numbers" class="symbol-numbers-img width-100 min-width-1000 pixel-image" src="{{ '/assets/img/symbol_numbers.png' | absolute_url }}">

Pegasus  
<img alt="Pegasus symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/7a24f017dbda18a34a5f64a5186a8ded243ececb/src/main/resources/assets/sgjourney/textures/symbols/pegasus/galaxy_pegasus.png">
<img alt="Symbol numbers" class="symbol-numbers-img width-100 min-width-1000 pixel-image" src="{{ '/assets/img/symbol_numbers.png' | absolute_url }}">

Universal  
<img alt="Universal symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/7a24f017dbda18a34a5f64a5186a8ded243ececb/src/main/resources/assets/sgjourney/textures/symbols/universal/universal.png">
<img alt="Symbol numbers" class="symbol-numbers-img width-100 min-width-1000 pixel-image" src="{{ '/assets/img/universal_symbol_numbers.png' | absolute_url }}">

Kaliem  
<img alt="Kaliem symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/7a24f017dbda18a34a5f64a5186a8ded243ececb/src/main/resources/assets/sgjourney/textures/symbols/kaliem/galaxy_kaliem.png">
<img alt="Symbol numbers" class="symbol-numbers-img width-100 min-width-1000 pixel-image" src="{{ '/assets/img/symbol_numbers.png' | absolute_url }}">

</div>

___

### Point of Origin

The Point of Origin is a unique* symbol on each Stargate that represents the location from which the Stargate is dialing.    
*There is a limited amount of point of origin symbols for each galaxy,
so they are not exactly unique, but randomly chosen for each Stargate based on the current Solar System and Galaxy. 

The point of origin can also be overridden by a [stargate variant](#stargate-variants), or, in case of Pegasus Stargate, [with a computer]({{ '/computercraft/stargate-interface/#overridePointOfOrigin' | absolute_url }}).

The point of origin always represents the number zero.
To confirm dialed address, the point of origin (number zero) must be encoded at the end of every address.

___

### Symbols and Symbol sets
"Symbols" are small collections of symbols unique for a solar system.
Those symbols are used when the `unique_symbols` configuration option is enabled.

When unique symbols are disabled, Stargates fall back to symbol set to which the "symbols" collection belongs.
Symbol sets contains all the symbols (or a subset) from their respective "symbols" collections, so they share the visual style.
Usually each galaxy has its own symbol set and each solar system has its own "symbols" collection belonging to the galaxy's symbol set.
This results in all solar systems in a galaxy sharing the same symbols when unique symbols are disabled.

___

## Energy supply

The primary source of energy for a Stargate is the [Dial Home Device (DHD)]({{ '/stargate-technology/dhd' | absolute_url }}).
From ancient times, each DHD contains a [fusion core]({{ '/items/functional-items/fusion-core' | absolute_url }})
capable of providing power for common Stargate usage for a vast time.
However, the fusion core cannot be refueled; once it runs out, it must be replaced.
See the [Dial Home Device (DHD)]({{ '/stargate-technology/dhd' | absolute_url }}) page for DHD crystal configuration
and fusion core replacement options.

In addition to DHD, the Stargate can be powered directly using a cable (**only on 1.21.1**) or with an [interface]({{ '/stargate-technology/interface/' | absolute_url }})
and any source of **Forge energy (FE)**.
Note that the Stargate itself has a large amount of energy capacity (1 TFE), if you connect a cable directly to the Stargate, 
it wont stop draining the power until its capacity is full.
You can use the [interface]({{ '/stargate-technology/interface/' | absolute_url }}) 
to limit the amount of power to which the Stargate should be charged using the [Energy Target]({{ '/stargate-technology/energy-target/' | absolute_url }}).


![Stargate interface powering the Stargate]({{ '/assets/img/survival/stargate_interface_power.png' | absolute_url }})
{: .max-width-512 }

[//]: # (TODO: 0.6.45 introduced direct connection of cables to the Stargate - only on 1.21.1 - update the schema)

The Stargate interface must face the Stargate (the black side facing away from the Stargate).
And there must be a power supply connected to the interface from any side.
The image shows the naquadah generator connected to a basic interface with a small naquadah cable 
and an energy cube from Mekanism connected to the crystal interface with a universal cable.

___

{% include /section_includes/stargate_dialing.md %}

___


## Natural Generation
Stargates generate only in pre-defined dimensions.
By default, a single Stargate[*](#beta-stargate) generates in 
[Overworld]({{ '/dimensions/overworld' | absolute_url }}),
[Nether]({{ '/dimensions/nether' | absolute_url }}),
[The End]({{ '/dimensions/the-end' | absolute_url }}),
[Stargate Journey dimensions]({{ '/dimensions/' | absolute_url }}),
and [Glacio from Ad Astra]({{ '/dimensions/glacio' | absolute_url }}).
Generation in other dimensions can be achieved with [datapacks]({{ '/datapacks' | absolute_url }}).

[//]: # (TODO: add link to generation datapacks)

In the [Overworld]({{ '/dimensions/overworld' | absolute_url }})
the **Alpha Stargate** is buried with a seal containing the address to the [Abydos]({{ '/dimension/abydos' | absolute_url }}).
The Stargate can be found with an archeologist's help.  
[Survival Guide / Finding a Stargate]({{ '/survival/finding-gate' | absolute_url }}) can guide you through the steps.

![Stargate buried with seal in desert]({{ '/assets/img/structures/stargate/buried_desert.png' | absolute_url }})
{: .max-width-768 }

<details markdown="block">
<summary>Beta Stargate</summary>

*There are two Stargates in the **Overworld**.

{: .lore }
> The first Stargate (**Beta Stargate**), originally built on Earth by the Ancients,
> was buried in ice for thousands of years in Antarctica.
> The SG-1 discovered this Stargate after the first Stargate in Egypt,
> hence the Beta Stargate.
> The Stargate found in Giza, Egypt, was the second Earth's Stargate
> brought by the Goa'uld System Lord Ra.
> It was the first Stargate found (in 1928)
> and operated by [SGC](https://www.gateworld.net/wiki/Stargate_Command),
> hence the Alpha Stargate.

The **Beta Stargate** can be found in a cave.
To find it, dial your Stargate to a different dimension and remove the DHD from your Stargate in the overworld.
That way, the Beta Stargate in the cave will be the only Stargate in the overworld with a DHD
and will **take priority** once you dial back to the overworld.
However, that will only work when the **Beta Stargate** structure was already generated.

![Stargate buried in a cave]({{ '/assets/img/structures/stargate/terra_gate.png' | absolute_url }})
{: .max-width-768 }

</details>


[//]: # (TODO: other dimensions generation)
[//]: # (TODO: link to common Stargates config and explanation)


___

## Crafting

The Classic Stargate can be created by building a structure described below.
The [Classic Stargate](#classic-stargate) can be "upgraded" to other types using an [upgrade crystal]({{ '/stargate-technology/crystals/stargate-upgrade-crystals/' | absolute_url }}).
The Stargate visuals can be changed with a [variant crystal]({{ '/stargate-technology/crystals/stargate-variant-crystals/' | absolute_url }}).

To build a Classic Stargate, you will need the following:
- **&ensp;1x** Classic Stargate Base Block
- **&ensp;9x** Classic Stargate Chevron Blocks
- **14x** Classic Stargate Ring Blocks

{% minecraft_recipe_crafting item:"sgjourney:classic_stargate_base_block" %}
{% minecraft_recipe_crafting item:"sgjourney:classic_stargate_chevron_block" %}
{% minecraft_recipe_crafting item:"sgjourney:classic_stargate_ring_block" %}

With the mentioned blocks, you need to build this structure:

![Classic Stargate Block Structure]({{ site.baseurl }}/assets/img/survival/classic_stargate_block_structure.png)
{: .max-width-512 }

The Classic Stargate will form from the structure once you right-click the Classic Stargate Base Block with an empty hand.

<details markdown="block">
<summary>Address choice</summary>

When the address choice is allowed in the Stargate Journey Common config (`enable_address_choice`, **it is disabled by default**),
the base block can be right-clicked with a renamed [control crystal]({{ '/stargate-technology/crystals/dhd-crystals/#control-crystal' | absolute_url }}),
the 9-chevron address from the name of the control crystal will be used for the Stargate.
The crystal can be renamed in the vanilla anvil and the name needs to follow the format `-1-2-3-4-5-6-7-8-`.

<div class="mcui">
<span 
    class="invslot invslot-item invslot-item-image" 
    data-minetip-title="-1-2-3-4-5-6-7-8-" 
    data-minetip-text="&9Stargate Journey">
        <a href="{{ '/stargate-technology/crystals/dhd-crystals/' | absolute_url }}">
            <img src="{{ '/assets/img/items/crafting/sgjourney/dynamic/control_crystal.png' | absolute_url }}">
        </a>
</span>
</div>

</details>

___

## Stargate generations

{: .lore }
Ancients developed 3 generations of Stargates over time.
Starting with Universe Stargate, the generation 1, improving the Stargate in generation 2 for the Milky Way galaxy
and digital generation 3 built for the Pegasus galaxy.
Tollans and Nox built their own "Tollan Stargate" based on the generation 2 Stargates in the Milky Way galaxy.

All four Stargate types are available in the mod alongside with the Classic Stargate introduced as the generation 0
which was inspired by the [SGCraft](https://www.curseforge.com/minecraft/mc-mods/sg-craft) mod.

Below is description for each Stargate type and their variants available in the base mod.
Note that datapacks and resourcepacks can add more custom variants.

<style>
.tick::before {
    content: "✓";
    color: green;
    font-weight: bold;
}
.cross::before {
    content: "✗";
    color: red;
    font-weight: bold;
}
</style>

### Classic Stargate
Generation 0

<details>
    <summary>Classic dialing sequence video</summary>
    {% include youtubePlayer.html id="TllvQYYwdu8" %}
</details>

![Classic Stargate]({{ '/assets/img/blocks/technological/classic_stargate.png' | absolute_url }})
{: .max-width-512 }

Initially inspired by the [SGCraft](https://www.curseforge.com/minecraft/mc-mods/sg-craft) mod.

The Stargate has 38 physical symbols (+ point of origin).
Can't dial Symbols above 38.
It can be dialed by rotating the ring with a redstone or any interface.
Additionally, can also be dialed by directly engaging the symbols with a DHD or a crystal interface.

When generated or placed for a first time, the Stargate will use the symbols of the solar system and will keep them even after breaking.
The only way to change them on an existing Stargate is with a command or a variant that overrides the symbols.

The available variants can be found at [Stargate Technology / Crystals / Variant crystals]({{ '/stargate-technology/crystals/stargate-variant-crystals/#classic-stargate' | absolute_url }}).

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">DHD</th>
            <th rowspan="2">Redstone</th>
            <th colspan="2">Computer</th>
        </tr>
        <tr>
            <th>Rotate</th>
            <th>Engage</th>
        </tr>
    </thead>
    <tbody class="td-bold">
        <tr>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

___

### Universe Stargate
Generation 1

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="7MEE5h36Fjs" %}
</details>

{: .lore }
The first Stargate created by the ancients.
Those Stargates were automatically built by [seed ships](https://www.gateworld.net/wiki/Seed_ship) and distributed along their path.
Universe Stargates are not much durable and are created from common materials collected by the ships from common planets.

![Universe Stargate]({{ '/assets/img/blocks/technological/universe_stargate.png' | absolute_url }})
{: .max-width-512 }

The Stargate has only 35 physical symbols (+ point of origin).
Can't dial symbols above 35, unless [Symbol remapping](#symbol-remapping) is used.
The Stargate has always the Universal symbols, unless changed by a Stargate variant.

The whole Stargate rotates during dialing, always encoding the symbol at the top.
It can be dialed by rotating the ring with a redstone or any interface.
Additionally, can also be dialed by directly engaging the symbols with a (advanced) crystal interface.

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">DHD</th>
            <th rowspan="2">Redstone</th>
            <th colspan="2">Computer</th>
        </tr>
        <tr>
            <th>Rotate</th>
            <th>Engage</th>
        </tr>
    </thead>
    <tbody class="td-bold">
        <tr>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

___


### Milky Way Stargate
Generation 2

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="w4uUZ5zNEOA" %}
</details>

{: .lore }
A second generation of Stargates built by the ancients.
The Stargate is made primarily of naquadah, which allows it to hold a large amount of energy
and be quite durable, even withstanding a meteorite impact.
Those Stargates are known to be located in the Milky Way galaxy.

![Milky Way Stargate]({{ '/assets/img/blocks/technological/milkyway_stargate.png' | absolute_url }})
{: .max-width-512 }

The Stargate has 38 physical symbols (+ point of origin).
Can't dial Symbols above 38.  
The Stargate ring can rotate when powered by redstone or when instructed to by a computer.  
It can be dialed by rotating the ring with a redstone or any Stargate interface.
Additionally, can also be dialed by directly engaging the symbols with a DHD or a (advanced) crystal interface.

When generated or placed for a first time, the Stargate will use the symbols of the solar system and will keep them even after breaking.
The only way to change them on an existing Stargate is with a command or a variant that overrides the symbols.

The available variants can be found at [Stargate Technology / Crystals / Variant crystals]({{ '/stargate-technology/crystals/stargate-variant-crystals/#milky-way-stargate' | absolute_url }}).

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">DHD</th>
            <th rowspan="2">Redstone</th>
            <th colspan="2">Computer</th>
        </tr>
        <tr>
            <th>Rotate</th>
            <th>Engage</th>
        </tr>
    </thead>
    <tbody class="td-bold">
        <tr>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

___

### Tollan Stargate
Generation 2

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="Y3gaR9EG-uY" %}
</details>

{: .lore }
The Tollan Stargate was built by Tollans and Nox based on the second generation of Stargates in the Milky Way galaxy.
There is only one known Stargate of this type on the home planet of Tollans.

![Tollan Stargate]({{ '/assets/img/blocks/technological/tollan_stargate.png' | absolute_url }})
{: .max-width-512 }

The Stargate has no symbols on it.
But it can dial **any** Symbol.   
It **cannot** be dialed with redstone nor using a rotation with a Stargate interface.
The Stargate can be dialed **only** by directly engaging the symbols with a DHD or a (advanced) crystal interface.

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">DHD</th>
            <th rowspan="2">Redstone</th>
            <th colspan="2">Computer</th>
        </tr>
        <tr>
            <th>Rotate</th>
            <th>Engage</th>
        </tr>
    </thead>
    <tbody class="td-bold">
        <tr>
            <td class="tick"></td>
            <td class="cross"></td>
            <td class="cross"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

___

### Pegasus Stargate
Generation 3

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="DxB9vEU02XY" %}
</details>

{: .lore }
The newest Stargates built by the ancients are known to be located in the Pegasus galaxy.
They are digital, have no moving parts and cannot be manually dialed.

![Pegasus Stargate]({{ '/assets/img/blocks/technological/pegasus_stargate.png' | absolute_url }})
{: .max-width-512 }

The Stargate has only 36 digital symbols (+ point of origin).
But it can dial **any** Symbol.   
It **cannot** be dialed with redstone nor using a rotation with a Stargate interface.
The Stargate can be dialed **only** by directly engaging the symbols with a DHD or a (advanced) crystal interface.

The Pegasus Stargate will change its symbols to the current solar system each time it is placed.
This behavior can be disabled with a computer by [disabling dynamic symbols]({{ '/computercraft/stargate_interface/#dynamicSymbols' | absolute_url }})
and/or [overriding them]({{ '/computercraft/stargate_interface/#overrideSymbols' | absolute_url }}).
The symbols can also be overridden by a Stargate variant.

The available variants can be found at [Stargate Technology / Crystals / Variant crystals]({{ '/stargate-technology/crystals/stargate-variant-crystals/#pegasus-stargate' | absolute_url }}).

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">DHD</th>
            <th rowspan="2">Redstone</th>
            <th colspan="2">Computer</th>
        </tr>
        <tr>
            <th>Rotate</th>
            <th>Engage</th>
        </tr>
    </thead>
    <tbody class="td-bold">
        <tr>
            <td class="tick"></td>
            <td class="cross"></td>
            <td class="cross"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

<details markdown="block">
<summary>Pegasus Stargate with Milky Way symbols</summary>

This is not a variant.    
By default, the Pegasus Stargate picks the symbols of the galaxy it is currently placed in.
This is how it looks like when placed in the Milky Way galaxy.

![Pegasus Stargate Milky Way symbols]({{ site.baseurl }}/assets/img/blocks/technological/variants/pegasus_milkyway_symbols.png)
{: .max-width-512 }

</details>

___

## Network restrictions

Each Stargate can be in one or more **networks**.
Each such network is identified by a number (integer).
Initially a Stargate is in a network matching the **generation** of the Stargate and network restrictions are **disabled**,
meaning the Stargate can connect to any other Stargate.

Networks of the Stargate can be changed either using **Communication Crystals** placed in the DHD 
or using [computers]({{ '/computercraft/stargate-interface/#getNetworks' | absolute_url }}).

<div class="mcui">
    <span 
        class="invslot invslot-item invslot-item-image" 
        data-minetip-title="&eCommunication Crystal" 
        data-minetip-text="&7Frequency: 123/&rCommunication Range Increase: 0 blocks/&8Can be tuned to a specific frequency or increase communication range/&7Frequency can be changed with a Crystal Computer">
            <a href="{{ '/stargate-technology/crystals/dhd-crystals/' | absolute_url }}">
                <img src="{{ '/assets/img/items/crafting/sgjourney/dynamic/communication_crystal.png' | absolute_url }}">
            </a>
    </span>
</div>
<div class="mcui">
    <span 
        class="invslot invslot-item invslot-item-image" 
        data-minetip-title="&eControl Crystal" 
        data-minetip-text="&7When placed in DHD, activates &bnetwork restrictions&7 of the Stargate">
            <a href="{{ '/stargate-technology/crystals/dhd-crystals/' | absolute_url }}">
                <img src="{{ '/assets/img/items/crafting/sgjourney/dynamic/control_crystal.png' | absolute_url }}">
            </a>
    </span>
</div>

[//]: # (FIXME: Add link to DHD crystal configurations using communication crystal)

Network restrictions can be enabled by placing a **Control Crystal** into the DHD or using [computers]({{ '/computercraft/stargate-interface/#getNetworks' | absolute_url }}).  
When **network restrictions** are activated, the Stargate can connect only to Stargates that are **at least in one matching network**.
The restrictions apply to both incoming and outgoing connection.



<table class="text-center" style="min-width: auto">
  <tr>
    <th>Stargate</th>
    <th>Default<br/>Network</th>
  </tr>
  <tr>
    <td><a href="#classic-stargate">Classic</a></td>
    <td>0</td>
  </tr>
  <tr>
    <td><a href="#universe-stargate">Universe</a></td>
    <td>1</td>
  </tr>
  <tr>
    <td><a href="#milky-way-stargate">Milky Way</a></td>
    <td>2</td>
  </tr>
  <tr>
    <td><a href="#tollan-stargate">Tollan</a></td>
    <td>2</td>
  </tr>
  <tr>
    <td><a href="#pegasus-stargate">Pegasus</a></td>
    <td>3</td>
  </tr>
</table>


___

## Symbol remapping

Certain Stargate types have a limited range of symbols they can encode.
Classic and Milky Way Stargates are limited to 38 symbols.  
And mainly the **Universe Stargate** is limited only to **35 symbols**.

Symbol remapping allows the Stargate to represent **any symbol** using any **other available symbol**.
The only exception is the Point of Origin (symbol `0`) which must not be remapped.

**DHD** is capable of automatic symbol remapping when **advanced protocols** are active.
To activate advanced protocols, insert **Large Control Crystal** into the center of the DHD's crystal inventory.

Alternatively, symbol remapping is also available to [**computers using a crystal interface**]({{ '/computercraft/stargate-interface/#remapSymbol' | absolute_url }}).

The image below shows Milky Way DHD connected to a Universe Stargate.
The DHD needed to encode the symbol `38` which is not physically present on the Stargate.
The DHD activated symbol remapping and **remapped symbol `4` to symbol `38`**.
**Physically** on the Stargate, the symbol `4` was encoded, 
however, thanks to the remapping, symbol `38` was **added to the address** that is being dialed.

If the DHD attempted to encode symbol `4` now, it would be again remapped to a different available symbol, 
because `4` was already used to encode a different symbol.


![Universe Stargate with Symbol 4 encoded]({{ '/assets/img/stargate-technology/stargate/universe_encoded_symbol_4.png' | absolute_url }})
{: .max-width-512 }

![DHD with remapped symbol 4 to symbol 38]({{ '/assets/img/stargate-technology/stargate/dhd_remapped_symbol.png' | absolute_url }})
{: .max-width-512 }

___

[//]: # (TODO: rename all occurences of Stargate interface to just interface, they can now be used with transporters)

## Stargate Feedback

Stargate is a complicated device which can enter numerous states
and a lot of things can go wrong.
Feedback system is in place to provide a list of status codes (and their description)
indicating the result of the last action the Stargate performed.
A value above zero indicating a success and value below zero an error.

To read the last feedback, right-click the Stargate with PDA, the Stargate information will be printed to the chat.
Alternatively, computers can read the feedback and obtain it as a result of some commands.
See the [computercraft documentation for the Stargate interface]({{ '/computercraft/stargate-interface' | absolute_url }}) 
for details.

The most recent list of feedback codes can be found in the source code at [GitHub / StargateInfo](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/sgjourney/StargateInfo.java#L120).

The codes are formatted as  
```java
FEEDBACK_NAME ( feedback_code_number, feedback_type, feedback_name )
```

Below, you can find a list explaining some of the feedback **errors**.

- `unknown` - _"Unknown Error"_ - The unknown error is a result of a faulty and unexpected behavior of the Stargate network. 
Please report any occurrence, include description of actions that caused it and the log from the session (or server).
[GitHub issues](https://github.com/Povstalec/StargateJourney/issues) or [Discord]({{ site.discord_invite_link }})

- `symbol_in_address` - _"Symbol X is already encoded"_ - This error indicates that the symbol is already encoded in the current address.
A single symbol cannot be present in the address twice.
This feedback can often be observed while using 3-way chevron encoding using a computercraft where both actions `encode chevron` and `close chevron`
attempts to encode the current symbol, resulting in two encodings of the same symbol. 
In such case this feedback can be ignored when returned from the `close chevron` action.

- `symbol_out_of_bonds` - _"Symbol X is out of bounds"_ - The Stargate cannot encode the symbol. 
Happens, for example, when trying to encode a symbol above 35 on [Universe Stargate](#universe-stargate) 
and the [symbol remapping](#symbol-remapping) is not available.

- `encode_when_connected` - _"Cannot encode Symbols when connected"_ - An attempt to encode a symbol failed because the Stargate already has an active connection.

- `incomplete_address` - _"Incomplete Address"_ - The encoded address had less than 7 symbols or was missing the Point of Origin.
The PoO is not a standalone symbol on the DHD, see the [FaQ]({{ '/faq/#i-dialed-an-address-but-dhd-says-incomplete-address' | absolute_url }}).

- `invalid_address` - _"Invalid Address"_ - The address is not valid. 
Happens when the 7-chevron address does not exist in the current galaxy (it may exist in a different galaxy),
or the 8-chevron (or 9-chevron) address does not exist at all.
If you are sure that the address is correct, but you did not obtain it in-game, 
it is possible that your game is configured to generate addresses randomly.
If you are using 7 chervon address, verify that it is an address for your current galaxy, 
otherwise you need to use 8 or 9 chevron address.

- `not_enough_power` - _"Not enough power (X required)"_ - The Stargate does not have enough power to establish a connection to the destination.
You can check the amount of power inside the Stargate in the DHD inventory, by hovering the cursor over the red rectangle; 
using PDA on the Stargate or using computercraft.  
Dialing Stargates in a different galaxy using 8 or 9 chevron addresses requires a lot of power (`100 000 000 000` FE by default).
In some situations when a new dimension is created in an existing world (e.g. using [RFTools Dimensions](https://modrinth.com/mod/rftools-dimensions))
the dimension may be registered outside of any galaxy and lack 7 and 8 chevron address.
In such case, the dimension is "floating" somewhere between galaxies and connections to it are considered intergalactic as well.

- `self_obstructed` - _"Local Stargate is obstructed"_ - The local Stargate is obstructed by blocks. 
Remove blocks from the inside of the Stargate and try dialing the address again. 

- `target_obstructed` - _"Target Stargate is obstructed"_ - The destination Stargate is obstructed by blocks.
You will need to find the Stargate first and break the blocks.

- `same_system_dial` - _"Cannot dial the same Region"_ - The Stargate cannot dial the same Solar System (Address Region) it is currently in.
To dial a Stargate in the same region, use 9 chevron address (can be restricted by the mod config, allowed by default).

- `already_connected` - _"Target Stargate is already connected"_ - The target (destination) Stargate has an already active connection.
You need to wait until the connection is closed and the target Stargate becomes available.

- `no_galaxy` - _"Stargate is not located inside any Galaxy"_ - **This code should not happen in SGJ 0.6.45 or later.**  
The Stargate is not located in a galaxy.
This could indicate a solar system misconfiguration from datapacks.
Or possibly the current dimension was dynamically added, you can try executing the
[stellar update]({{ '/commands/#sgjourney-stargatenetwork-forcestellarupdate' | absolute_url }}).

- `no_dimensions` - _"Dialed Region has no Dimensions"_ - The dialed Solar System (Address Region) has no dimensions.
This indicates an address region misconfiguration from datapacks or other mods.

- `no_stargates` - _"Dialed Region has no Stargates"_ - The dialed Solar System (Address Region) has no Stargates.
Note that not every dimension automatically generates a Stargate.
You need to reach the dimension by other means and place a Stargate there yourself.  
See [datapacks]({{ '/datapacks' | absolute_url }}) section
for options to add a Stargate generation to a new dimension.

- `self_restricted` - _"Local Stargate is restricted"_ - The local Stargate is in a restricted network and cannot connect to other Stargates outside the same network(s).
If the local Stargate is in netowrks `1` and `23`, and is restricted, it can only connect to Stargates that are either in network `1` or `23`.  
See [Network restriction](#network-restrictions) for more information.

- `target_restricted` - _"Dialed Stargate is restricted"_ - The target (destination) Stargate is in a restricted network and cannot connect to other Stargates outside the same network(s).
If the target Stargate is in netowrks `1` and `23`, and is restricted, it can only connect to Stargates that are either in network `1` or `23`.  
See [Network restriction](#network-restrictions) for more information.

- `invalid_8_chevron_address` - _"8-chevron address can't dial within the same Galaxy"_ - 8 chevron address cannot be used to dial a solar system in the same galaxy.
You need to use the 7 chevron address.
This behavior can be changed in the mod config file.

- `invalid_system_wide_connection` - _"Cannot connect within the same Region"_ - Stargate cannot dial other Stargate in the same Solar System (Address Region).
This behavior can be changed in the mod config file (system-wide connections are allowed by default).

- `target_not_whitelisted` - _"Target Stargate is not whitelisted"_ - The local (dialing) Stargate has active address filter 
and the dialed address is not whitelisted.  
See [computercraft compatibility]({{ '/computercraft/stargate-interface/#addToWhitelist' | absolute_url }}) 
on how an address can be added to the whitelist.

- `not_whitelisted_by_target` - _"Stargate is not on Target Stargate's whitelist"_ - The target (destination) Stargate has active address filter
and the address of the local (dialing) Stargate is not whitelisted.  
See [computercraft compatibility]({{ '/computercraft/stargate-interface/#addToWhitelist' | absolute_url }})
on how an address can be added to the whitelist, the address has to be added on the target Stargate.

- `target_blacklisted` - _"Target Stargate is blacklisted"_ - The local (dialing) Stargate has active address filter
and the dialed address is blacklisted.  
See [computercraft compatibility]({{ '/computercraft/stargate-interface/#removeFromBlacklist' | absolute_url }})
on how an address can be removed from the blacklist.

- `blacklisted_by_target` - _"Stargate is on Target Stargate's blacklist"_ - The target (destination) Stargate has active address filter
and the address of the local (dialing) Stargate is blacklisted.  
See [computercraft compatibility]({{ '/computercraft/stargate-interface/#removeFromBlacklist' | absolute_url }})
on how an address can be removed from the blacklist, the address has to be removed on the target Stargate.

- `exceeded_connection_time` - _"Stargate has exceeded its max connection time"_ - Stargate connection was closed
because the maximum connection time run out. (`228` seconds by default, `3.8` minutes)

- `ran_out_of_power` - _"Ran out of power"_ - The connection was closed because the Stargate run out of power to maintain it. 

- `wrong_disconnect_side` - _"Stargate cannot be disconnected from this side"_ - The connection can only be disconnected from the dialing (source) side.
This behavior can be changed in the mod config.

- `connection_forming` - _"Cannot disconnect Stargate while a Connection is forming"_

[//]: # (Does this even need an explanation?)

- `stargate_destroyed` - _"Stargate was destroyed"_ - The target (destination) Stargate was destroyed.

- `could_not_reach_target_stargate` - _"Could not reach target Stargate"_ - The connection was closed because the target (destination) Stargate become unreachable.

- `interrupted_by_incoming_connection` - _"Stargate was interrupted by incoming connection"_

- `rotation_blocked` - _"Rotation is blocked by open chevron"_ - Close the chevron to allow the Stargate to rotate.

- `not_rotating` - _"Failed to stop rotation, not rotation"_ - The rotation cannot be stopped because the Stargate is not rotating.

- `chevron_already_opened` - _"Chevron is already opened"_ - Cannot open the chevron, it is already opened

- `chevron_already_closed` - _"Chevron is already closed"_ - Cannot close the chevron, it is already closed

- `chevron_not_open` - _"Chevron not open"_ - Attempt to encode the current symbol failed because the chevron is not opened.
Open the chevron first.

- `target_not_loaded` - _"Target Stargate is not loaded"_ - Target Stargate was not being loaded by any players or chunk loaders
and the mod failed to load it, or Stargate loading is disabled in the mod config.

- `self_outside_stargate_network` - _"Stargate is outside the Stargate Network"_ - The local (dialing) Stargate is not registered in the Stargate Network.
Try to break the Stargate and place it again. If the Stargate will not be registered in the network, please 
report the bug on [GitHub issues](https://github.com/Povstalec/StargateJourney/issues) or [Discord]({{ site.discord_invite_link }}).

- `target_outside_stargate_network` - _"Target Stargate is outside the Stargate Network"_ - The target (destination) Stargate is not registered in the Stargate Network.
Try to break the target (destination) Stargate and place it again. If the Stargate will not be registered in the network, please
report the bug on [GitHub issues](https://github.com/Povstalec/StargateJourney/issues) or [Discord]({{ site.discord_invite_link }}).