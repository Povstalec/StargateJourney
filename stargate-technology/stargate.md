---
title: Stargate
parent: Stargate Technology
nav_order: 0
---


{: .note }
This content was migrated from the old wiki and is awaiting an update.

1. Table of Contents
{:toc}

# Stargate

## Generation
By default, two Stargates generate in the Overworld and then one Stargate generates in each of the Dimensions added by Stargate Journey. Other Dimensions like Nether and End are still reachable through the Stargate Network, but they do not have their own Stargates by default.

## Symbols
Stargates use Addresses to dial other Stargates. These Addresses are formed by Symbols and while there dozens upon dozens of Symbols in the mod, all of them fit under one of these two categories:

### Regular Symbols
Regular Symbols are Symbols numbered 1, 2, 3,... which actually form an Address. Different Solar Systems will generaly share regular Symbols if they're located in the same Galaxy. Even though different Galaxies may use different sets of Symbols, those Symbols still represent numbers starting from 1, 2, 3,... and as such, they will work the exact same way in different Galaxies.

### Point Of Origin
The Point of Origin is a special kind of symbol with the number 0. Each Solar System usually has a unique Point Of Origin representing it (Though they may look different, all of them share the number 0).

## Natural Generation
Only one Stargate will generate per dimension by default(the only exception to this is Overworld, which will generate 2 Stargates in the form of a Buried Stargate and a Stargate Pedestal), however, this can be changed with the use of datapacks.

## Variants

### Universe Stargate
* Generation: 1
* Only has 36 Symbols (Can't dial Symbols above 35)
* Always has the same set of Symbols, that being the Universal Symbols.

### Milky Way Stargate
* Generation: 2
* Only has 39 Symbols (Can't dial Symbols above 38)
* Can be dialed Manually: [Manual Dialing Tutorial]({{ site.baseurl }}/survival/dialing/#manual-dialing-with-redstone)
* When first placed, the Stargate will adapt the set of Symbols belonging to the Solar System it was placed in uses. It will remember those Symbols when broken.

### Pegasus Stargate
* Generation: 3
* Only displays 36 Symbols at a time, but can actually dial any Symbol
* When placed, it will use the set of Symbols belonging to the Solar System it is currently in. When broken and placed in another Solar System, it will switch to that Solar System's Symbols.

## Feedback Codes
Whenever Stargate performs any action that may succeed or fail, it will return a Feedback Code, which may be used to better understand what caused the success or failure. The codes are return in the form of an integer, with error codes being negative integers and success codes being positive.

* `0` NONE

### Success Codes
* `1` SYMBOL_ENCODED
* `2` CONNECTION_ESTABILISHED_SYSTEM_WIDE
* `3` CONNECTION_ESTABILISHED_INTERSTELLAR
* `4` CONNECTION_ESTABILISHED_INTERGALACTIC
* `7` CONNECTION_ENDED_BY_DISCONNECT
* `8` CONNECTION_ENDED_BY_POINT_OF_ORIGIN
* `9` CONNECTION_ENDED_BY_NETWORK
* `10` CONNECTION_ENDED_BY_AUTOCLOSE
* `11` CHEVRON_RAISED

### Error Codes
* `-1` UNKNOWN_ERROR
* `-2` SYMBOL_IN_ADDRESS
* `-3` SYMBOL_OUT_OF_BOUNDS
* `-4` INCOPLETE_ADDRESS
* `-5` INVALID_ADDRESS
* `-6` NOT_ENOUGH_POWER
* `-7` SELF_OBSTRUCTED
* `-8` TARGET_OBSTRUCTED
* `-9` SELF_DIAL
* `-10` SAME_SYSTEM_DIAL
* `-11` ALREADY_CONNECTED
* `-12` NO_GALAXY
* `-13` NO_DIMENSIONS
* `-14` NO_STARGATES
* `-15` EXCEEDED_CONNECTION_TIME
* `-16` RAN_OUT_OF_POWER
* `-17` CONNECTION_REROUTED
* `-18` WRONG_DISCONNECT_SIDE
* `-19` STARGATE_DESTROYED
* `-20` TARGET_STARGATE_DOES_NOT_EXIST
* `-21` CHEVRON_ALREADY_RAISED
* `-22` CHEVRON_ALREADY_LOWERED


# Stargate Variants
{: .no_toc }
[Datapacks]({{ site.baseurl }}/datapacks) are able to add custom Stargate variants.  
These variants are available in the base mod.  
To change the gate variant, you need a [Stargate variant crystal]({{ site.baseurl }}/items/crystals/#stargate-variant-crystals).

{: .future }
It is planned to distinguish the variant crystals for different Stargates.

1. Table of Contents
{:toc}

## Classic Stargate

Initially inspired by mod [SGCraft](https://www.curseforge.com/minecraft/mc-mods/sg-craft).
The gate ring cannot move.
Each chevron lights up and moves when locked.

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="8i-3zoKVpp4" %}
</details>

___

### Milky Way
`classic_milky_way`

Milky way variant of classic Stargate.

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="gQbaDO334c4" %}
</details>


![Classic Stargate Milky Way variant]({{ site.baseurl }}/assets/img/blocks/technological/variants/classic_milkyway.png)
{: .max-width-512 }

![Classic Stargate Milky Way variant crystal]({{ site.baseurl }}/assets/img/items/crystals/stargate_variant_crystal_classic_milkyway.png)

___

## Milky Way Stargate

The gate ring can rotate when the gate is powered with redstone or used with computercraft.  
The symbol being encoded is always under the top chevron.

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="bOG_9Q9396E" %}
</details>

___

### Movie
`milkyway_movie`

Chevrons do not glow.
Each chevron opens and closes individually and moves on both sides of the gate instead of just the front.
The primary chevron is the only one that doesn't move.
Tt has open space under it, so the symbol under it is easier to see.
<details markdown="block">
<summary>The event horizon forms a strudel behind the gate</summary>
![Milky Way Stargate movie variant]({{ site.baseurl }}/assets/img/blocks/technological/variants/strudel.png)
{: .max-width-256 }
</details>

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="ptVJKO5nW20" %}
</details>

![Milky Way Stargate movie variant]({{ site.baseurl }}/assets/img/blocks/technological/variants/milkyway_movie.png)
{: .max-width-512 }

![Milky Way Stargate movie variant crystal]({{ site.baseurl }}/assets/img/items/crystals/stargate_variant_crystal_milkyway_movie.png)

___

### Promo
`milkyway_promo`

Same as the [Milky Way Movie variant](#movie), but the chevrons are lit and there is no vortex behind the gate.

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="gf6m-AcZwMg" %}
</details>

![Milky Way Stargate promo variant]({{ site.baseurl }}/assets/img/blocks/technological/variants/milkyway_promo.png)
{: .max-width-512 }

![Milky Way Stargate promo variant crystal]({{ site.baseurl }}/assets/img/items/crystals/stargate_variant_crystal_milkyway_promo.png)

___

### SG-1
`milkyway_sg-1`

Same as the default gate variant (chevrons glow, do not move, except the top one and there is no vortex)
Milky Way symbols and the Tau'ri Point of Origin (Giza) is set to be used permanently.

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="Fwc8eTm0Ph8" %}
</details>

![Milky Way Stargate SG-1 variant]({{ site.baseurl }}/assets/img/blocks/technological/variants/milkyway_sg-1.png)
{: .max-width-512 }

![Milky Way Stargate SG-1 variant crystal]({{ site.baseurl }}/assets/img/items/crystals/stargate_variant_crystal_milkyway_sg-1.png)

___

## Pegasus

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="BcUokqncKYI" %}
</details>

Pegasus Stargate with Milky Way symbols:
> This is not a variant.  
> By default, the Pegasus gate picks the symbols of the galaxy it is currently placed in.

![Pegasus Stargate Milky Way symbols]({{ site.baseurl }}/assets/img/blocks/technological/variants/pegasus_milkyway_symbols.png)
{: .max-width-512 }

___

### Atlantis
`pegasus_atlantis`

Same as the default gate variant.
Pegasus symbols and the Lantea Point of Origin (Subido) are set to be used permanently.

![Pegasus Stargate atlantis_variant]({{ site.baseurl }}/assets/img/blocks/technological/pegasus_stargate.png)
{: .max-width-512 }

![Pegasus Stargate Atlantis variant crystal]({{ site.baseurl }}/assets/img/items/crystals/stargate_variant_crystal_pegasus_atlantis.png)

___

{: .note }
Neither the Universe nor Tollan Stargate has an available alternative variant in the base mod.
However, [datapacks]({{ site.baseurl }}/datapacks) can add them.

## Universe

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="yN7fWUbOnsw" %}
</details>

___

## Tollan

<details>
    <summary>Dialing sequence video</summary>
    {% include youtubePlayer.html id="XX9BfnVoAkc" %}
</details>
