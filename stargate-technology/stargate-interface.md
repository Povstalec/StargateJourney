---
title: Stargate Interface
parent: Stargate Technology
nav_order: 30
---


[//]: # (Probably temporary placement, before mechanics category is done or something)

1. Table of Contents
{:toc}

# Stargate Interface

_[Basic Interface]({{ site.baseurl }}/blocks/technological_blocks/#basic-interface) / [Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#crystal-interface) / [Advanced Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#advanced-crystal-interface)_

The interface allows controlling some alien technology with [computers](#computercraft), supplying it with [power](#energy-target)
or getting feedback with [redstone signals](#redstone).
At first, your control over the alien technology will be fairly limited. However, you will also gain more control as you upgrade to better Interfaces.

___

## Redstone

The interface does not provide a redstone signal directly but **through a [redstone comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator)** (like a hopper or lectern).
The provided redstone signal differs based on selected interface mode.

The current mode can be changed by `shift-right-clicking` the interface with an **empty hand**.
Some interface mods are also able to react to a redstone signal as an input (e.g. the [iris mode](#iris-mode)).

![Interface setup with Stargate and comparator]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_setup.png)

<details markdown="block">
<summary>A <a href="https://minecraft.fandom.com/wiki/Redstone_Comparator">comparator</a> can read from any interface (basic / crystal / advanced crystal) and any side of an interface.</summary>
[Comparators](https://minecraft.fandom.com/wiki/Redstone_Comparator) can read even through an opaque block.

![Interface setup with comparators on sides]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_setup_sides.png)
</details>

## Default mode
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode.png)

The default mode does **not** provide any redstone signal through a [redstone comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator). It may be used with [computers](#computercraft).

___

## Segment mode
![Interface segment mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_segment.png)

The interface in the segment mode provides rough information about a Stargate rotation.
For the Milky Way Stargate, it gives details about the inner ring rotation.
For the Pegasus and Universe Stargates, it provides information about the last encoded symbol.

The Milky Way Stargate ring is divided into **three segments**, each with **13 symbols**.
The interface provides a redstone signal based on the **segment** to which a symbol **under the top chevron** belongs.

The Universe Stargate has only 36 symbols
and is also divided into **three segments**, each with **12 symbols**.
The interface provides a redstone signal based on the **segment** to which the last encoded symbol belongs.

**Redstone output:**

| Signal strength | Description                                                           |
|:---------------:|-----------------------------------------------------------------------|
|      **5**      | A symbol from the **first (green)** segment is under the top chevron. |
|     **10**      | A symbol from the **second (red)** segment is under the top chevron.  |
|     **15**      | A symbol from the **third (blue)** segment is under the top chevron.  |


<details markdown="block">
<summary>Stargate segments</summary>
![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_segments.png)
![Universe Stargate segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/universe_stargate_segments.png)
</details>

___

## Rotation mode
![Interface rotation mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_rotation.png)

The interface in the rotation mode provides more precise information about a rotation/symbol in the current segment.

Each segment of the Milky Way Stargate ring has 13 symbols.
Based on the symbol under the top chevron, the interface provides the redstone signal.

Each segment of the Universe Stargate has 12 symbols,
Based on the last encoded symbol, the interface provides a redstone signal
with strength from **0** to **12** always skipping the strength **1** (0, 2, 3...12).

{: .future }
The different signal strength is considered a bug and will be fixed in some future release.  
Segment and rotation modes are also planned for other gate types.

<!-- TODO: fix based on the response from Wold -->
<!-- TODO: describe pegasus once it works in game -->

**Redstone output:**

| Signal strength | Description                                                              |
|:---------------:|--------------------------------------------------------------------------|
|   **1 - 13**    | Based on the symbol under the top chevron of the **Milky Way** Stargate. |
|   **0 - 12**    | Based on the last encoded symbol of the **Universe** Stargate.           |

<details markdown="block">
<summary>Stargate ring rotation</summary>
The numbers indicate redstone signal strength for a specific symbol.

![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_rotation.png)
<!-- TODO: image for universe -->
</details>

___

## Chevron mode
![Interface chevron mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_chevron.png)

The interface in the chevron mode provides redstone signal strength from **0** to **9** based on the number of active chevrons.

**Redstone output:**

| Signal strength | Description                             |
|:---------------:|-----------------------------------------|
|    **0 - 9**    | Based on the number of active chevrons. |

___

## Wormhole mode
![Interface wormhole mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_wormhole.png)

The interface in the wormhole mode provides a redstone signal when there is an active connection.

**Redstone output:**

| Signal strength | Description                               |
|:---------------:|-------------------------------------------|
|      **0**      | When there is no active connection.       |
|      **7**      | When there is an **incoming** connection. |
|     **15**      | When there is an **outgoing** connection. |

___

## Iris mode
![Interface iris mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_iris.png)

{: .note }
For shielding mode to work, you need to install [the iris]({{ site.baseurl }}/stargate-network/stargate_iris) in the Stargate first.

The interface in the shielding mode is able to provide and to accept a redstone signal for communication with the iris.

**Redstone output:**

| Signal strength | Description                        |
|:---------------:|------------------------------------|
|      **0**      | When the iris is fully open.       |
|   **1 - 14**    | Based on the openness of the iris. |
|     **15**      | When the iris is fully closed.     |

You can also provide the interface with a redstone signal controlling the iris.

**Redstone input:**

| Signal strength | Description                     |
|:---------------:|---------------------------------|
|      **0**      | Nothing, iris will stop moving. |
|    **1 - 7**    | Iris will close.                |
|   **8 - 15**    | Iris will open.                 |

Example setup with iris and GDO is described on the iris page [Stargate Network / Stargate iris]({{ site.baseurl }}/stargate-network/stargate_iris/).

<h2>Shielding mode <p class="label label-purple">Advanced Crystal Interface</p></h2>
![Interface shielding mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_shielding.png)

Shielding mode is available only for the **advanced crystal interface**.
Currently, this mode has no practical use.
It is prepared for a future release when the energy shield is added to the mod.
Then, this mode will be used to control the shield similarly to the iris.

___

## Computercraft

To use an interface with a [computer](https://tweaked.cc/), connect it as any other computercraft peripheral.
For details, check
[Survival Guide / Dialing - Dialing using a Computercraft]({{ site.baseurl }}/survival/dialing/#dialing-with-computercraft)
and the [Computercraft section]({{ site.baseurl }}/computercraft).

___

## Energy Target

{: .note }
In order for the Stargate to require power, the power requirements needs to be enabled in the mod configuration file.  
`disable_energy_requirements = false` <!-- TODO: link to mod configuration page -->

Due to some alien technology's incredibly high energy capacity, the interfaces will, by default,
only push energy into it until a **targeted energy level** is reached **within the technology**.

For example, The Stargate can hold several **billion** FE units.
By default, the interface will **stop** pushing energy once it reaches `200 000` FE to prevent early energy drain,
even though the Stargate is not near full capacity.

The amount of energy the alien technology is charged with is called the **Energy Target**.
It can be set with **Computercraft** using the
[`setEnergyTarget`]({{ site.baseurl }}/computercraft/stargate_interface/#setEnergyTarget) method
