---
title: Dialing
nav_order: 10
parent: Survival Guide
description: "Guide to dialing the stargate for the first time in the survival with the Stargate Journey Minecraft mod."
---

# Stargate dialing
{: .no_toc }

To activate the stargate, you need to dial an **address** of the destination gate.


## The address

[//]: # (TODO: maybe remove the address description from here and just provide a link to explanation)

Each stargate has a unique **9-chevron** address.
That address is unique for the specific stargate and can never be changed once the gate is built (or generated).

Additionally, the gate can be dialed using the address of **the solar system** in which it is located.
The **7-chevron** address is an address of the solar system within the **current galaxy**.
To dial a solar system in a **different galaxy**, you need to use an **8-chevron** address.

As of the mod's current state, it is impossible to find an 8-chevron address in survival.
Meaning you can't reach other galaxies without commands (yet).
The 9-chevron address of the gate can be obtained by right-clicking the stargate with a PDA.

**The first address** you found on a cartouche buried along with the stargate is an address to the **Abydos** solar system in the Milky Way galaxy.
The address consists of **6&nbsp;symbols**.
The 7th symbol is the **Point of Origin** (PoO).
The point of origin is **not physically present** on the DHD as a standalone symbol,
and it is **automatically encoded by pressing the big button in the middle of the DHD**. 
With other dialing mechanics, the point of origin is always represented by the symbol number `0`.

[//]: # (TODO: add link to further addressing system explanation)

You can place the cartouche on the ground and see the symbols (from top to bottom)
or right-click it and see the address as numbers in the chat.

If you did not find the cartouche along with the stargate, but you found the
[Beta gate]({{ '/structures/stargates/#terra-gate-the-beta-gate' | absolute_url }})
or other stargate pedestal from a datapack instead,
in that case, you can either return to 
[Finding the buried stargate]({{ '/survival/finding-gate/' | absolute_url }}) 
or see the spoiler with the Abydos address below.

<details markdown="block">
<summary><b>[Spoiler]</b> Cartouche with the Abydos address</summary>
![Cartouche with the Abydos address]({{ site.baseurl }}/assets/img/survival/cartouche_abydos_address.png)
{: .max-width-512 }

Its number representation is `-26-6-14-31-11-29-`.
</details>

___

## Power
The stargate requires **power** to operate.
The buried stargate was found with a DHD containing a [**Fusion Core**]({{ '/items/functional-items/fusion-core/' | absolute_url }}) 
that is able to supply the gate with energy
for interstellar travel (in the current galaxy) for some time.
The DHD inventory can be accessed by `shift+right-clicking` it with an **empty hand**.
[Fusion Core]({{ '/items/functional-items/fusion-core/' | absolute_url }}) 
is an Ancient technology, that seems to operate on a cold fusion basis and **doesn't require any fuel**.
However, once the 
[Fusion Core]({{ '/items/functional-items/fusion-core/' | absolute_url }}) 
runs out of energy, it will need to be **replaced with a new power source** (e.g. with 
[Naquadah Generator Core]({{ 'items/functional-items/naquadah-generator-core/' | absolute_url }})).
You should keep an eye on it and have a replacement ready before it runs out.

The DHD will also power the gate even if other dialing methods are used, it just needs to be placed nearby the gate.

Alternatively, the gate can be powered using a **stargate interface** with an **external power supply** (e.g. from other mod).

<details markdown="block">
<summary>Stargate interface powering the gate</summary>
![Stargate interface powering the gate]({{ '/assets/img/survival/stargate_interface_power.png' | absolute_url }})
{: .max-width-512 }

The stargate interface must face the gate (the black side facing away from the gate).
And there must be a power supply connected to the interface from any side.
The image shows the naquadah generator connected to a basic interface with a small naquadah cable
and an energy cube from Mekanism connected to the crystal interface with a universal cable.
</details>

___

{% include /section_includes/stargate_dialing.md %}

___

## Activation

{: .warning }
Upon activation, the gate will create an unstable burst of energy ("kawoosh")
that will **destroy blocks** and **kill entities** in front of the gate in a **7-block range**!  
**Keep the area clear!**

{: .warning }
> Make sure you have **enough food** with you.  
> **Wait** for the "kawoosh" to **finish** and enter the gate.
>
> **DO NOT GO BACK to the gate on the other side!**  
> By default, stargates allows only **one-way travel** from the side that dialed the connection.  
> Two-way travel can be enabled in the config.
> It is disabled by default based on the Stargate franchise.

___

## Abydos
_Welcome to Abydos, a wonderful planet full of... sand. I hope you packed some snacks._

On this planet, you are looking primarily for two things:
an ore called [Naquadah]({{ site.baseurl }}/survival/naquadah/) 
and your [way back home]({{ site.baseurl }}/survival/addresses/) to the Earth (the address of the overworld).

## [Next page: Finding addresses]({{ '/survival/addresses/' | absolute_url }})
{: .no_toc }
