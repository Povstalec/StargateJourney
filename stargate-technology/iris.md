---
title: Iris
parent: Stargate Technology
nav_order: 40
iris_types: [Naquadah Alloy, Steel, Copper, Iron, Golden, Diamond, Netherite, Bronze]
---


# Stargate Iris
{: .no_toc }

> The iris is a metal covering on the Earth Stargate which is used to screen incoming traffic.  
> When the iris is closed, it forms a barrier less than three μm from the wormhole's event horizon,
> thereby preventing most forms of matter from properly reintegrating.  
> Source: [stargate.fandom.com](https://stargate.fandom.com/wiki/Iris)

Stargate is a gateway to other worlds, hiding many things to discover but also many dangers.

Surely, you don't want any uninvited visitors;
there is a very *democratic* way of preventing such occurrences -- build a wall and let every visitor smash into it,
and by the wall, I mean the iris.

[//]: # (Idk man, its 1AM, what do you want from me?)

___

## Crafting

You will need a Stargate shielding ring first to craft an iris,
which you can craft with four iron ingots and four redstone dusts.

{% minecraft_recipe_crafting item:"sgjourney:stargate_shielding_ring" %}

Then, you can use the ring in the middle of the crafting table
and surround it with a material for the [iris type](#iris-types).

{% minecraft_recipe_crafting item:"sgjourney:naquadah_alloy_iris" %}

<details markdown="block">
<summary id="iris-types">Iris types</summary>

[//]: # (This loop will print each iris type defined in the page header as the name and image)
{% for type in page.iris_types %}

{% assign iris_id = type | downcase | replace: " ", "_" | append: "_iris" %}
{% assign iris_item_id = 'sgjourney:' | append: iris_id %}

{{ type }} iris  
`{{ iris_item_id }}`

{% minecraft_recipe_crafting item:iris_item_id %}

![{{ type }} iris]({{ '/assets/img/stargate-technology/iris/types/' | append: iris_id | append: '.png' | absolute_url }})
{: .max-width-512 }

___

{% endfor %}

</details>

___

## Setting up

Once you have an iris,
you can install it into any Stargate with **right-click**
(except for [Tollan Stargate]({{ '/stargate-technology/stargate/#tollan-stargate' | absolute_url }}),
which is too thin for an iris installation).

Only a single iris can be installed on the gate at the time.
To remove the iris from the gate or replace it with another one,
you must close and break it.

___

## Controlling

{: .warning }
> Ensure all your stuff controlling the gate (the redstone circuit and related)
> is in **the same chunk as the gate itself**.
> Otherwise, when an incoming connection to the gate loads the remote chunk, anything **outside** the gate's chunk
> will not be loaded, so that **it won't work**.
>
> Also, **do not use pistons** in the redstone circuits.
> Pistons and similar mechanics will not work when the gate loads the chunk.

___

### Manual control
The iris can be controlled with a [Stargate interface]({{ '/stargate-technology/stargate-interface/' | absolute_url }}) with redstone or computers.
Additionally, you can use a Garage Door Opener (GDO) to open the iris remotely.

{: .warning }
If you don't know redstone well, pay attention to each block and its direction, comparators and repeaters don't work both ways.

First, you need to set up the **basic interface** in the shielding mode (not that other interfaces can't be used yet).  
[Stargate Network / Stargate Interface / Shielding mode]({{ '/stargate-technology/stargate-interface/#shielding-mode' | absolute_url }})

![Basic interface in shielding mode]({{ '/assets/img/stargate-technology/iris/basic_interface_shielding.png' | absolute_url }})
{: .max-width-512 }

**Basic setup with a lever control**  
For controlling the iris with a single lever, we need to constantly power the interface with a signal of strength 7 or lower.  
That is required to close the iris whenever the lever is turned off.
Then simply powering the interface with a signal of strength 8 or more will open the iris.

There are, of course, plenty of ways how to do it, here are two examples.  
The simple way using a lever and redstone torch.
The torch emits a signal of strength **15**, the left path is **9** blocks long.
Redstone loses a level for each block, leaving the interface with signal strength **7**
(which is the largest strength level that closes the iris).
The direct way from lever has a length of **4** blocks, providing signal strength **12** when lever is switched on
(any signal above **7** will work).

{: .note }
Note that the length of redstone is critical here.

![Basic lever iris control]({{ '/assets/img/stargate-technology/iris/basic_lever.png' | absolute_url }})
{: .max-width-512 }

**More minimalistic way**  
The redstone torch emits signal strength **15**, which powers the comparator from the back.
The right path loses two levels,
providing the comparator with signal strength **13** from the side.
The comparator is set to the subtraction mode, outputting signal strength **2** (`15 - 13 = 2`).
The lever powers the repeater, which provides signal strength 15 to the interface.
Fifteen from the repeater is more than two from the comparator.
So, the iris is opened once the lever is switched on (due to the higher signal strength from the repeater).
And closed when the lever is switched off (due to the only signal from the comparator).

![Minimalistic lever iris control]({{ '/assets/img/stargate-technology/iris/minimalistic_lever.png' | absolute_url }})
{: .max-width-512 }

___

### Automatic iris closing

What if you wanted the iris to automatically open for outgoing connections,
so you can use the gate,
and closed for incoming connections reflecting any uninvited visitors,
unless opened with a lever?  
It is indeed possible with a very simple setup.

Using an interface in the [wormhole mode]({{ '/stargate-technology/stargate-interface/#wormhole-mode' | absolute_url }})
will provide appropriate signal strength for the interface in the [iris mode]({{ '/stargate-technology/stargate-interface/#iris-mode' | absolute_url }})
to automatically open/close the iris based on the connection direction.

During an incoming connection, the iris can be easily opened with a redstone signal of strength **8** or higher.

![Iris auto close]({{ '/assets/img/stargate-technology/iris/auto_close.png' | absolute_url }})

You can also instruct the iris to **open automatically** when there is **no connection**.
With another comparator, you can read from the interface in the
[wormhole mode]({{ '/stargate-technology/stargate-interface/#wormhole-mode' | absolute_url }}) whether there is an active connection.
**Negating** this signal with a **torch** will power the interface in the
[iris mode]({{ '/stargate-technology/stargate-interface/#iris-mode' | absolute_url }}),
**opening** the iris when there is **no connection**.
When there is **any connection**,
the second comparator will emit **some** signal to the torch,
**powering it off** and leaving the **first comparator** to provide an appropriate signal to the iris interface.

![Iris auto open-close]({{ '/assets/img/stargate-technology/iris/auto_open_close.png' | absolute_url }})

___

### Remote iris control

So you have secured the gate with an auto-closing iris for incoming connections, but how can you open the iris
when you are on the other side of the connection?  
[Garage Door Opener (GDO)]({{ '/stargate-technology/transceiver-gdo/#garage-door-opener-gdo' | absolute_url }}) is a hand-held device
that allows to send a signal through the Stargate to the other side.
For example, automatically opening the iris.

{: .warning }
[Ensure that the whole circuit is in the same chunk as the gate itself.](#controlling)

<details markdown="block">
<summary>Another example redstone circuits</summary>
Note that both circuits **do not account for invalid GDO codes**!
The transceiver with the comparator must be placed two blocks further to eliminate the redstone signal of strength 1, which
is sent by the transceiver when an incorrect code is received.

**A first circuit for iris control with GDO created by Wold**

![Iris setup with GDO by Wold]({{ '/assets/img/stargate-technology/iris/iris_with_gdo_wold.png' | absolute_url }})
**More compact version by JimmyBlether**

![Iris setup with GDO by Wold]({{ '/assets/img/stargate-technology/iris/iris_with_gdo_jimmyblether.png' | absolute_url }})
</details>

**The following circuit will be discussed and explained.**  
- The circuit automatically closes iris on incoming connection.
- Opens the iris on receiving a correct code by the [transceiver]({{ site.baseurl }}/stargate-technology/transceiver-gdo/#transceiver).
- Automatically opens the iris on outgoing connection.
- Additionally, iris can be automatically opened when the gate is idle (there is no connection).

All repeaters are set to delay `0` (default state), and all comparators all set to comparator mode (default state).

**Used items:**
- 12 blocks
- 2 stargate interfaces (any type)
- 3 redstone comparators
- 1 [transceiver]({{ site.baseurl }}/stargate-technology/transceiver-gdo/#transceiver)
- 4 redstone torches
- 6 redstone dusts

![Iris setup with GDO front view]({{ '/assets/img/stargate-technology/iris/iris_setup_with_gdo_front.png' | absolute_url }})
![Iris setup with GDO top view]({{ '/assets/img/stargate-technology/iris/iris_setup_with_gdo_top.png' | absolute_url }})


Adding two slabs (**must be slabs**) with redstone dust and a torch instructs the iris to automatically open when the gate is idle.

![Iris setup with GDO with auto open]({{ '/assets/img/stargate-technology/iris/iris_setup_with_gdo_auto_open.png' | absolute_url }})

### Expanding the circuit

Here is an example of adding two levers which can act as a manual override of the iris.
The lever on the left closes the iris when activated.
The lever on the right opens the iris when activated (also overriding the lever on the left).

The right lever is connected directly to the redstone on the original granite slabs, adding one slab.
There are two solid blocks added next to the granite slabs to block neighboring redstone dust.

The left lever powers the block with torches to reset the [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches),
the block below the torch below the interface in the [iris mode]({{ '/stargate-technology/stargate-interface/#iris-mode' | absolute_url }}).
The comparator between the interfaces is powered from the side with a repeater,
and the interface in the [iris mode]({{ '/stargate-technology/stargate-interface/#iris-mode' | absolute_url }}) is powered directly with dust (to keep the signal level low' | absolute_url }}).
The comparator between the interfaces is set to subtracting mode.
The lever will also override the [GDO]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) signal.
In case you do not want the lever to override the [GDO]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) signal,
simply remove the repeater powering the torch under the interface in the [iris mode]({{ '/stargate-technology/stargate-interface/#iris-mode' | absolute_url }}).


![Iris setup with GDO with override top view]({{ '/assets/img/stargate-technology/iris/iris_setup_with_gdo_override_top.png' | absolute_url }})
![Iris setup with GDO with override back view]({{ '/assets/img/stargate-technology/iris/iris_setup_with_gdo_override_back.png' | absolute_url }})

[//]: # (TODO: update structure file)
The finished circuit is available as a [structure file]({{ site.baseurl }}/assets/structure/mechanics/stargate_network/iris/stargate_iris_gdo_manual_override.nbt)
created on 1.21.1 Stargate Journey 0.6.44.  
You can place this file to  
`installation_folder/saves/your_save/generated/minecraft/structures/stargate_iris_gdo_manual_override.nbt`  
replacing the `installation_folder` with the folder name where your game is installed and `your_save` with the folder name of your world.
Then you can place it in-game with command `/place template minecraft:stargate_iris_gdo_manual_override`.
Make sure you have enough space around you that the structure won't break your building.
After placing, you will probably need to break the gate and place it again (remember to add the iris).
