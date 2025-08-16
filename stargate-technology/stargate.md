---
title: Stargate
parent: Stargate Technology
nav_order: 0
description: "Documentation for stargates from the Stargate Journey Minecraft mod."
---

# Stargate
{: .no_toc }

If you don't know what stargate is, 
check [What’s that Stargate thing anyway?]({{ '/what-is-stargate/' | absolute_url }})

In order to interact with the stargate, to read it's state using redstone
or communicate with [CC:Tweaked](https://tweaked.cc) computers, 
a [stargate interface]({{ '/stargate-technology/stargate-interface/' | absolute_url }}) is required.

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

Each stargate, except for the Universe, will pick the symbols of the local solar system when generated,
or placed for the first time. 
Additionally, since the Pegasus stargate is digital, it will change the symbols each time it is placed,
unless it was [disabled by a computer]({{ '/computercraft/stargate_interface/#dynamicSymbols' | absolute_url }}).

Each symbol represents a number that is used for a more user-friendly address representation.
Starting with zero which is always the [point of origin](#point-of-origin).

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
</style>

<div class="symbols-container">

MilkyWay
<img alt="MilkyWay symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/refs/heads/main/src/main/resources/assets/sgjourney/textures/symbols/milky_way/galaxy_milky_way.png">

Pegasus  
<img alt="Pegasus symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/refs/heads/main/src/main/resources/assets/sgjourney/textures/symbols/pegasus/galaxy_pegasus.png">


Universal  
<img alt="Universal symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/refs/heads/main/src/main/resources/assets/sgjourney/textures/symbols/universal/universal.png">

Kaliem  
<img alt="Kaliem symbols" class="width-100 min-width-1000 pixel-image" src="https://raw.githubusercontent.com/Povstalec/StargateJourney/refs/heads/main/src/main/resources/assets/sgjourney/textures/symbols/kaliem/galaxy_kaliem.png">

</div>

### Point of Origin

Point of Origin is a unique* symbol on each stargate that represents the location from which the stargate is dialing.  
*There is a limited set of point of origin symbols for each galaxy, 
so they are not exactly unique, but randomly chosen for each gate.  
The point of origin can also be overridden by a [stargate variant](#stargate-variants).

The point of origin always represents the number zero.
To confirm dialed address, the point of origin (number zero) must be encoded at the end to initialize the connection.
On the DHD it is automatically encoded by pressing the big button in the middle.

___

## Energy supply

The primary source of energy for a stargate is the [Dial Home Device (DHD)]({{ '/stargate-technology/dhd' | absolute_url }}).
From ancient times, each DHD contains a [fusion core]({{ '/items/functional-items/fusion-core' | absolute_url }})
capable of providing power for common stargate usage for a vast time.
However, the fusion core cannot be refueled; once it runs out, it must be replaced.
See the [Dial Home Device (DHD)]({{ '/stargate-technology/dhd' | absolute_url }}) page for DHD crystal configuration
and fusion core replacement options.

In addition to DHD, the gate can be powered with a [stargate interface]({{ '/stargate-technology/stargate-interface/' | absolute_url }})
and any other source of **Forge energy (FE)**.

![Stargate interface powering the gate]({{ '/assets/img/survival/stargate_interface_power.png' | absolute_url }})
{: .max-width-512 }

The stargate interface must face the gate (the black side facing away from the gate).
And there must be a power supply connected to the interface from any side.
The image shows the naquadah generator connected to a basic interface with a small naquadah cable 
and an energy cube from Mekanism connected to the crystal interface with a universal cable.

___

{% include /section_includes/stargate_dialing.md %}

___


## Natural Generation
Stargates generate only in pre-defined dimensions.
By default, a single gate[*](#beta-stargate) generates in 
[Overworld]({{ '/dimensions/overworld' | absolute_url }}),
[Nether]({{ '/dimensions/nether' | absolute_url }}),
[The End]({{ '/dimensions/the-end' | absolute_url }}),
[Abydos]({{ '/dimensions/abydos' | absolute_url }}),
[Chulak]({{ '/dimensions/chulak' | absolute_url }}),
[Rima]({{ '/dimensions/rima' | absolute_url }}),
[Unitas]({{ '/dimensions/unitas' | absolute_url }}),
[Cavum Tenebrae]({{ '/dimensions/cavum-tenebrae' | absolute_url }}),
[Lantea]({{ '/dimensions/lantea' | absolute_url }}),
[Athos]({{ '/dimensions/athos' | absolute_url }}),
and [Glacio from Ad Astra]({{ '/dimensions/glacio' | absolute_url }}).
Generation in other dimensions can be achieved with datapacks.

[//]: # (TODO: add link to generation datapacks)

In the [Overworld]({{ '/dimensions/overworld' | absolute_url }})
the **Alpha gate** is buried with a seal containing the address to the [Abydos]({{ '/dimension/abydos' | absolute_url }}).
The gate can be found with an archeologist's help.  
[Survival Guide / Finding a Stargate]({{ '/survival/finding-gate' | absolute_url }}) can guide you through the steps.

![Stargate buried with seal in desert]({{ '/assets/img/structures/gate_buried_desert.png' | absolute_url }})
{: .max-width-768 }

<details markdown="block">
<summary>Beta stargate</summary>

*There are two Stargates in the **Overworld**.

{: .lore }
> The first gate (**Beta gate**), originally built on Earth by the Ancients,
> was buried in ice for thousands of years in Antarctica.
> The SG-1 discovered this gate after the first gate in Egypt,
> hence the Beta gate.
> The gate found in Giza, Egypt, was the second Earth's Stargate
> brought by the Goa'uld System Lord Ra.
> It was the first gate found (in 1928)
> and operated by [SGC](https://www.gateworld.net/wiki/Stargate_Command),
> hence the Alpha gate.

The **Beta gate** can be found in a cave.
To find it, dial your stargate to a different dimension and remove the DHD from your stargate in the overworld.
That way, the Beta gate in the cave will be the only gate in the overworld
and will **take priority** once you dial back to the overworld.
However, that will only work when the **Beta gate** structure was already generated.

If you end up again in your stargate, there is a second option.
Travel to other StargateJourney dimension (Chulak, Rima or a different one which has a stargate pedestal).
Note the coordinates (X and Z can be displayed by pressing `F3`)
of the stargate in the other dimension, and visit the same coordinates in the overworld.
Remember that you need to dig.
You are looking for a cave with the beta stargate.
Also, don't forget the DHD that is in the cave with the gate.

![Stargate buried in a cave]({{ '/assets/img/structures/terra_gate.png' | absolute_url }})
{: .max-width-768 }

</details>


[//]: # (TODO: other dimensions generation)
[//]: # (TODO: link to common stargates config and explanation)


___

## Crafting

The classic stargate can be created by building a structure described below.
The [classic stargate](#classic-stargate) can be upgraded to other types using an [upgrade crystal]({{ '/stargate-technology/crystals/stargate-upgrade-crystals/' | absolute_url }}).
The stargate visuals can be changed with a [variant crystal]({{ '/stargate-technology/crystals/stargate-variant-crystals/' | absolute_url }}).

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

The classic stargate will form from the structure once you right-click the Classic Stargate Base Block with an empty hand.

<details markdown="block">
<summary>Address choice</summary>

When the address choice is allowed in the Stargate Journey Common config (`enable_address_choice`, **it is disabled by default**),
the base block can be right-clicked with a renamed [control crystal]({{ '/stargate-technology/crystals/dhd-crystals/#control-crystal' | absolute_url }}),
the 9-chevron address from the name of the control crystal will be used for the stargate.
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
Ancients developed 3 generations of stargates over time.
Starting with Universe stargate, the generation 1, improving the gate in generation 2 for the MilkyWay galaxy
and digital generation 3 built for the Pegasus galaxy.
Tollans and Nox built their own "Tollan stargate" based on the generation 2 stargates in the MilkyWay galaxy.

All four stargate types are available in the mod alongside with the classic stargate introduced as the generation 0
which was inspired by the [SGCraft](https://www.curseforge.com/minecraft/mc-mods/sg-craft) mod.

Below is description for each stargate type and their variants available in the base mod.
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

![Classic stargate]({{ '/assets/img/blocks/technological/classic_stargate.png' | absolute_url }})
{: .max-width-512 }

Initially inspired by the [SGCraft](https://www.curseforge.com/minecraft/mc-mods/sg-craft) mod.

The gate has 38 physical symbols (+ point of origin).
Can't dial Symbols above 38.  
The gate ring can rotate when powered by redstone or when instructed to by a computer.  
It can be dialed by rotating the ring with a redstone or any stargate interface.
Additionally, can also be dialed by directly engaging the symbols with a DHD or a (advanced) crystal interface.

When generated or placed for a first time, the gate will use the symbols of the solar system and will keep them even after breaking.
The only way to change them on an existing stargate is with a command or a variant that overrides the symbols.

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
The first stargate created by the ancients.
Those stargates were automatically built by [seed ships](https://www.gateworld.net/wiki/Seed_ship) and distributed along their path.
Universe stargates are not much durable and are created from common materials collected by the ships from common planets.

![Universe stargate]({{ '/assets/img/blocks/technological/universe_stargate.png' | absolute_url }})
{: .max-width-512 }

The gate has only 35 physical symbols (+ point of origin).
Can't dial symbols above 35.
The gate has always the Universal symbols, unless changed by a gate variant.

The whole gate rotates during dialing, always encoding the symbol at the top.
It can be dialed by rotating the ring with a redstone or any stargate interface.
Additionally, can also be dialed by directly engaging the symbols with a DHD or a (advanced) crystal interface.

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
A second generation of stargates built by the ancients.
The gate is made primarily of naquadah, which allows it to hold a large amount of energy
and be quite durable, even withstanding a meteorite impact.
Those gates are known to be located in the Milky Way galaxy.

![Milky Way stargate]({{ '/assets/img/blocks/technological/milkyway_stargate.png' | absolute_url }})
{: .max-width-512 }

The gate has 38 physical symbols (+ point of origin).
Can't dial Symbols above 38.  
The gate ring can rotate when powered by redstone or when instructed to by a computer.  
It can be dialed by rotating the ring with a redstone or any stargate interface.
Additionally, can also be dialed by directly engaging the symbols with a DHD or a (advanced) crystal interface.

When generated or placed for a first time, the gate will use the symbols of the solar system and will keep them even after breaking.
The only way to change them on an existing stargate is with a command or a variant that overrides the symbols.

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
The Tollan stargate was built by Tollans and Nox based on the second generation of stargates in the Milky Way galaxy.
There is only one known gate of this type on the home planet of Tollans.

![Tollan stargate]({{ '/assets/img/blocks/technological/tollan_stargate.png' | absolute_url }})
{: .max-width-512 }

The gate has no symbols on it.
But it can dial **any** Symbol.   
It **cannot** be dialed with redstone nor using a rotation with a stargate interface.
The gate can be dialed **only** by directly engaging the symbols with a DHD or a (advanced) crystal interface.

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
The newest stargates built by the ancients are known to be located in the Pegasus galaxy.
They are digital, have no moving parts and cannot be manually dialed.

![Pegasus stargate]({{ '/assets/img/blocks/technological/pegasus_stargate.png' | absolute_url }})
{: .max-width-512 }

The gate has only 36 digital symbols (+ point of origin).
But it can dial **any** Symbol.   
It **cannot** be dialed with redstone nor using a rotation with a stargate interface.
The gate can be dialed **only** by directly engaging the symbols with a DHD or a (advanced) crystal interface.

The pegasus stargate will change its symbols to the current solar system each time it is placed.
This behavior can be disabled with a computer by [disabling dynamic symbols]({{ '/computercraft/stargate_interface/#dynamicSymbols' | absolute_url }})
and/or [overriding them]({{ '/computercraft/stargate_interface/#overrideSymbols' | absolute_url }}).
The symbols can also be overridden by a stargate variant.

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
By default, the Pegasus gate picks the symbols of the galaxy it is currently placed in.
This is how it looks like when placed in the Milky Way galaxy.

![Pegasus Stargate Milky Way symbols]({{ site.baseurl }}/assets/img/blocks/technological/variants/pegasus_milkyway_symbols.png)
{: .max-width-512 }

</details>

___

## Stargate Feedback

Stargate is a complicated device which can enter numerous states
and a lot of things can go wrong.
Feedback system is in place to provide a list of status codes (and their description)
indicating the result of the last action the stargate performed.
A value above zero indicating a success and value below zero an error.

To read the last feedback, right-click the gate with PDA, the gate information will be printed to the chat.
Alternatively, computers can read the feedback and obtain it as a result of some commands.
See the [computercraft documentation for the stargate interface]({{ '/computercraft/stargate-interface' | absolute_url }}) 
for details.

The most recent list of feedback codes can be found in the source code at [GitHub / StargateInfo](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/sgjourney/StargateInfo.java#L120).

The codes are formatted as  
```java
FEEDBACK_NAME ( feedback_code_number, feedback_type, feedback_name )
```

Below, you can find a list explaining some of the feedback errors.

- `unknown` - The unknown error is a result of a faulty and unexpected behavior of the stargate network. 
Please report any occurrence and include description of actions that caused it.
- `symbol_in_address` - This error indicates that the symbol is already encoded in the current address.
A single symbol cannot be present in the address twice.
This feedback can often be observed while using 3-way chevron encoding where both actions `encode chevron` and `close chevron`
attempts to encode the current symbol, resulting in two encodings of the same symbol.
- `symbol_out_of_bonds` - The stargate cannot encode the symbol. 
Happens, for example, when trying to encode a symbol above 35 on [Universe stargate](#universe).
- `invalid_address` - The address is not valid. 
Happens when the 7-chevron address does not exist in the current galaxy (it may exist in a different galaxy),
or the 8-chevron (or 9-chevron) address does not exist at all.
If you are sure that the address is correct, but you did not obtain it in-game, 
it is possible that your game is configured to generate addresses randomly.
- `self_obstructed` - The local stargate is obstructed by blocks. 
Remove blocks from the inside of the gate and try dialing the address again. 
- `target_obstructed` - The destination stargate is obstructed by blocks.
You will need to find the gate first and break the blocks.
- `no_galaxy` - The gate is not located in a galaxy. 
This could indicate a solar system misconfiguration from datapacks.
Or possibly the current dimension was dynamically added, you can try executing the 
[stellar update]({{ '/commands/#sgjourney-stargatenetwork-forcestellarupdate' | absolute_url }}).
- `no_dimensions` - The dialed solar system exists but has no dimensions.
This indicates a solar system misconfiguration from datapacks.
- `no_stargates` - The dialed solar system has no stargates.
Note that not every dimension automatically generates a stargate.
See [datapacks]({{ '/datapacks' | absolute_url }}) section
for options to add a stargate generation to a new dimension.
- `target_restricted` - The destination stargate does not accept connections from a different networks.
- `invalid_system_wide_connection` - The common config does not allow establishing a system-wide connection.
- `whitelisted_target` - The dialed address is not whitelisted by the local stargate.
The local stargate needs to add the dialed address to the whitelist.
- `whitelisted_self` - The local address is not whitelisted by the destination stargate.
The destination stargate needs to add the local address to the whitelist.
- `blacklisted_target` - The dialed address is blacklisted by the local stargate.
The local stargate needs to remove the dialed address from the blacklist.
- `blacklisted_self` - The destination stargate has the local address blacklisted.
The destination stargate needs to remove the local address from the blacklist.
- `wrong_disconnect_side` - The connection cannot be closed from this side.
The other side needs to end the connection.
- `could_not_reach_target_stargate` - The stargate network was not able to find the destination gate.
This means an unexpected error occurred, and you should report it.
Before you do that, ensure you do not have any [incompatible mod]({{ '/#known-incompatibilities' | absolute_url }}) installed.
