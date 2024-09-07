---
title: Stargate Interface
nav_order: 0
has_children: false
parent: Stargate Network
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

{: .future }
The interface will be able to react to a redstone signal controlling an iris.

The interface does not provide a redstone signal directly but **through a [redstone comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator)** (like a hopper or lectern).
The provided redstone signal differs based on selected interface mode.

![Interface setup with Stargate and comparator]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_setup.png)

<details markdown="block">
<summary>A <a href="https://minecraft.fandom.com/wiki/Redstone_Comparator">comparator</a> can read from any interface (basic / crystal / advanced crystal) and any side of an interface.</summary>
[Comparators](https://minecraft.fandom.com/wiki/Redstone_Comparator) can read even through an opaque block.

![Interface setup with comparators on sides]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_setup_sides.png)
</details>

### Default mode
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode.png)

The default mode does **not** provide any redstone signal through a [redstone comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator). It may be used with [computers](#computercraft).

___

### Segment
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_segment.png)

The interface in the segment mode provides rough information about a Stargate rotation.
For the Milky Way Stargate, it gives details about the inner ring rotation. 
For the Pegasus and Universe Stargates, it provides information about the last encoded symbol.

The Milky Way Stargate ring is divided into **three segments**, each with **13 symbols**.
The interface provides a redstone signal based on the **segment** to which a symbol **under the top chevron** belongs.

The Universe Stargate has only 36 symbols
and is also divided into **three segments**, each with **12 symbols**.
The interface provides a redstone signal based on the **segment** to which the last encoded symbol belongs.

Signal strength: **5** for the first (green), **10** for the second (red), and **15** for the third (blue) ring segment.

<details markdown="block">
<summary>Stargate segments</summary>
![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_segments.png)
![Universe Stargate segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/universe_stargate_segments.png)
</details>

___

### Rotation
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_rotation.png)

The interface in the rotation mode provides more precise information about a rotation/symbol in the current segment.

Each segment of the Milky Way Stargate ring has 13 symbols.
Based on the symbol under the top chevron, the interface provides the redstone signal.

Each segment of the Universe Stargate has 12 symbols,
the signal strength is currently from **0** to **12** always skipping the strength **1** (0, 2, 3...12).
<!-- TODO: fix based on the response from Wold -->
<!-- TODO: describe pegasus once it works in game -->

Signal strength: **1** for the first symbol in the segment, and **13** for the last one.

<details markdown="block">
<summary>Stargate ring rotation</summary>
The numbers indicate redstone signal strength for a specific symbol.

![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_rotation.png)
<!-- TODO: image for universe -->
</details>

___

### Chevron
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_chevron.png)

The interface in the chevron mode provides redstone signal strength from **0** to **9** based on the number of active chevrons.

___

### Wormhole
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_wormhole.png)

The interface in the wormhole mode provides a redstone signal when there is an active connection.

Provides redstone signal strength **15** when there is an **outgoing** connection, and **7** when there is an **incoming** connection.

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
