---
title: DHD
parent: Stargate Technology
nav_order: 10
description: "Documentation for Dial Home Device (DHD) from the Stargate Journey Minecraft mod."
---

<div class="flex-row" markdown="block" style="justify-content: space-between; align-items: center;">

![Classic DHD]({{ '/assets/img/blocks/technological/classic_dhd.png' | absolute_url }})
{: .max-width-256 }
![Milky Way DHD]({{ '/assets/img/blocks/technological/milkyway_dhd.png' | absolute_url }})
{: .max-width-256 }
![Pegasus DHD]({{ '/assets/img/blocks/technological/pegasus_dhd.png' | absolute_url }})
{: .max-width-256 }

</div>

# Dial Home Device (DHD)
{: .no_toc }

The Dial Home Device was constructed by the Ancients and can be found alongside stargates.
It allows controlling stargates, encoding symbols, dialing an address and supplies the gate with power.

## Inventory
Each DHD has a dialing user interface with symbol buttons which can be opened by **right-clicking the DHD from the top**.

{: .future }
In the future, the numbers will be replaced with actual symbols
with an option to still view numbers while holding shift key.

<div class="flex-row flex-wrap">
<img alt="Milkyway DHD interface" class="max-width-512" style="margin: 1rem"
    src="{{ '/assets/img/gui/milkyway_dhd_gui.png' | absolute_url }}">
<img alt="Pegasus DHD interface" class="max-width-512" style="margin: 1rem"
    src="{{ '/assets/img/gui/pegasus_dhd_gui.png' | absolute_url }}">
</div>


Secondly, it has a crystal inventory, which can be opened by **right-clicking the DHD from any side, except the top**
(or by shift-right clicking from the top with an empty hand).
The inventory holds crystals modifying DHD capabilities and a power source.


![Empty DHD inventory]({{ '/assets/img/gui/dhd_inventory_empty.png' | absolute_url }})
{: .max-width-512 }

<details>
<summary>Default crystal configuration for a generated DHD</summary>
<img alt="Milkyway DHD inventory" class="max-width-512" src="{{ '/assets/img/gui/milkyway_dhd_inventory.png' | absolute_url }}">
<img alt="Pegasus DHD inventory" class="max-width-512" src="{{ '/assets/img/gui/pegasus_dhd_inventory.png' | absolute_url }}">
<p>Classic and Milkyway DHD on the left, Pegasus DHD with advanced crystals on the right.</p>
</details>

## Power & Energy Target

In order to operate, the DHD itself needs a power,
when the internal buffer has enough power, it can be transferred to the stargate.
Similarly to [stargate interface]({{ '/stargate-technology/stargate-interface/' | absolute_url }}),
the DHD has an **energy target** which defines an energy amount up to which the DHD will charge the gate.

The DHD can be powered either externally with Forge Energy (FE) e.g. by connecting a cable to it.
Or with an internal power source placed in the DHD inventory.

The power source slot to the right can hold **any modded container (a battery)** with Forge Energy (FE).
Naturally generated DHDs contains the [fusion core]({{ '/items/functional-items/fusion-core/' | absolute_url }}).
It can be replaced with the [naquadah generator core]({{ '/items/functional-items/naquadah-generator-core/' | absolute_url }})
which requires fuel to run. The fuel can be provided in the second slot underneath the first one.

In creative, the [Zero Point Module (ZPM)]({{ '/items/functional-items/zpm/' | absolute_url }}) 
can also be used as _the ultimate_ power source in the DHD.

## Crafting

The DHD also generates by default with each stargate, except the gate in The End.

{% minecraft_recipe_crafting item:"sgjourney:classic_dhd" %}
{% minecraft_recipe_crafting item:"sgjourney:milky_way_dhd" %}
{% minecraft_recipe_crafting item:"sgjourney:pegasus_dhd" %}


## Crystals configuration

Placing different crystals into the DHD changes its capabilities -- maximum distance from the gate,
energy transfer rate, energy target and advanced protocols.

When advanced protocols are enabled by the DHD, the gate will auto-close in 10 seconds after the last traveler.

See [Stargate Technology / DHD Crystals]({{ '/stargate-technology/crystals/dhd-crystals' | absolute_url }})
for description of specific crystals.