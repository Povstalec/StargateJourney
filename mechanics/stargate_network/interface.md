---
title: Stargate Interface
nav_order: 20
has_children: false
parent: Stargate Network
grand_parent: Mechanics
---

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

### Ring segment
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_segment.png)

The interface in the ring segment mode provides rough information about a **Milky Way Stargate** ring rotation.
The Milky Way Stargate ring is divided into **three segments**, each with **13 symbols**.
The interface provides a redstone signal based on the **segment** to which a symbol **under the top chevron** belongs.

Signal strength: **5** for the first (green), **10** for the second (red), and **15** for the third (blue) ring segment.

<details markdown="block">
<summary>Ring segments</summary>
![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_segments.png)
</details>

___

### Ring rotation
![Interface default mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/interface_mode_ring_rotation.png)

The interface in the ring rotation mode provides more precise information about a Milky Way Stargate ring rotation in the current segment.
Each ring segment has 13 symbols.
Based on the symbol under the top chevron, the interface provides a redstone signal with strength from **1** to **13**.

<details markdown="block">
<summary>Stargate ring rotation</summary>
The numbers indicate redstone signal strength for a specific symbol.

![Milky Way Stargate ring segments]({{ site.baseurl }}/assets/img/mechanics/stargate_network/interface/milkyway_stargate_ring_rotation.png)

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
