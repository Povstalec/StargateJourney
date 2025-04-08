---
title: Stargate Iris
nav_order: 10
parent: Stargate Network
iris_types: [Naquadah Alloy, Steel, Copper, Iron, Golden, Diamond, Netherite, Bronze]
---

1. Table of Contents
{:toc}

___

# Stargate Iris

> The iris is a metal covering on the Earth Stargate which is used to screen incoming traffic.  
> When the iris is closed, it forms a barrier less than three μm from the wormhole's event horizon, 
> thereby preventing most forms of matter from properly reintegrating.  
> Source: [stargate.fandom.com](https://stargate.fandom.com/wiki/Iris)

Stargate is a gateway to other worlds, hiding many things to discover but also many dangers.

Surely, you don't want any uninvited visitors; 
there is a very *democratic* way of preventing such occurrences -- build a wall and let every visitor smash into it, 
and by the wall, I mean the iris.

[//]: # (Idk man, its 1AM, what do you want from me?)

<details markdown="block">
<summary id="iris-types">Iris types</summary>

[//]: # (This loop will print each iris type defined in the page header as the name and image)
{% for type in page.iris_types %}

{{ type }} iris  
`{{ type | downcase | replace: " ", "_" }}_iris`  

![{{ type }} iris]({{ site.baseurl }}/assets/img/blocks/technological/iris/{{ type | downcase | replace: " ", "_" }}_iris.png)
{: .max-width-512 }

___

{% endfor %}

</details>

___

## Crafting

You will need a Stargate shielding ring first to craft an iris, 
which you can craft with four iron ingots and four redstone dusts.

![Stargate shielding ring recipe]({{ site.baseurl }}/assets/img/blocks/technological/iris/stargate_shielding_ring_recipe.png)

Then, you can use the ring in the middle of the crafting table 
and surround it with a material for the [iris type](#iris-types).

![Stargate naquadah alloy iris]({{ site.baseurl }}/assets/img/blocks/technological/iris/stargate_iris_recipe.png)

___

## Setting up

Once you have an iris, 
you can install it into any Stargate with **right-click** 
(except for [Tollan Stargate]({{ site.baseurl }}/blocks/technological_blocks/#tollan), 
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
The iris can be controlled with a [Stargate interface]({{ site.baseurl }}/stargate_network/interface/) with redstone or computers.
Additionally, you can use a Garage Door Opener (GDO) to open the iris remotely.

{: .warning }
If you don't know redstone well, pay attention to each block and its direction, comparators and repeaters don't work both ways.

First, you need to set up the **basic interface** in the shielding mode (not that other interfaces can't be used yet).  
[Stargate Network / Stargate Interface / Shielding mode]({{ site.baseurl }}/stargate_network/interface/#shielding-mode)

![Basic interface in shielding mode]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/basic_interface_shielding.png)
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

![Basic lever iris control]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/basic_lever.png)
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

![Minimalistic lever iris control]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/minimalistic_lever.png)
{: .max-width-512 }

___

### Automatic iris closing

What if you wanted the iris to automatically open for outgoing connections, 
so you can use the gate, 
and closed for incoming connections reflecting any uninvited visitors, 
unless opened with a lever?  
It is indeed possible with a very simple setup.

Using an interface in the [wormhole mode]({{ site.baseurl }}/stargate_network/interface/#wormhole-mode) 
will provide appropriate signal strength for the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode)
to automatically open/close the iris based on the connection direction.

During an incoming connection, the iris can be easily opened with a redstone signal of strength **8** or higher.

![Iris auto close]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/auto_close.png)

You can also instruct the iris to **open automatically** when there is **no connection**. 
With another comparator, you can read from the interface in the 
[wormhole mode]({{ site.baseurl }}/stargate_network/interface/#wormhole-mode) whether there is an active connection. 
**Negating** this signal with a **torch** will power the interface in the 
[iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode), 
**opening** the iris when there is **no connection**. 
When there is **any connection**, 
the second comparator will emit **some** signal to the torch, 
**powering it off** and leaving the **first comparator** to provide an appropriate signal to the iris interface.

![Iris auto open-close]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/auto_open_close.png)

___

### Remote iris control

So you have secured the gate with an auto-closing iris for incoming connection, but how you can open the iris
when you are on the other side of the connection?  
[Garage Door Opener (GDO)]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) is a hand-held device 
that allows to send a signal through the Stargate connection to the other side.
For example, automatically opening the iris.

{: .warning }
[Ensure that the whole circuit is in the same chunk as the gate itself.](#controlling)

<details markdown="block">
<summary>Another example redstone circuits</summary>
**A first circuit for iris control with GDO created by Wold**
![Iris setup with GDO by Wold]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/iris_with_gdo_wold.png)
**More compact version by JimmyBlether**
![Iris setup with GDO by Wold]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/iris_with_gdo_jimmyblether.png)
</details>

**The following circuit will be discussed and explained.**  
The circuit automatically closes iris on incoming connection.
Opens the iris on receiving a correct code by the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver).
Automatically opens the iris on outgoing connection.
Additionally, iris can be automatically opened when the gate is idle (there is no connection),
more [transceivers]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) or iris control with a buttons or lever added.

*All repeaters are set to delay 0 (default state), and all comparators all set to comparator mode (default state).*

**Used items:**
- 12 blocks
- 2 stargate interfaces (any type)
- 3 redstone comparators
- 1 [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver)
- 4 redstone torches
- 6 redstone dusts

![Iris setup with GDO front view]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/iris_with_gdo_front.png)
![Iris setup with GDO top view]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/iris_with_gdo_top.png)

Adding two slabs (**must be slabs**) and a torch instructs the iris to automatically open when the gate is idle.
Additionally, buttons can be added. 
The top/left one closes and the bottom/right one opens the iris.

![Iris setup with GDO with auto open]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_auto_open.png)

### Circuit breakdown
So, how the circuit with [GDO]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) works.  

As explained in the [automatic iris closing section]({{ site.baseurl }}/stargate_network/stargate_iris/#automatic-iris-closing),
the interface on the left in the [wormhole mode]({{ site.baseurl }}/stargate_network/interface/#wormhole-mode) 
will provide via comparator a signal of strength
- **0** -- When there is no active connection, 
resulting in no action from the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode) 
on the right.
- **7** -- When there is an **incoming** connection, resulting in **closing** the iris by the interface on the right.
- **15** -- When there is an **outgoing** connection, resulting in **opening** the iris by the interface on the right. 

Next, we need to add the control of the iris with the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver).

![Iris setup with GDO interface highlight]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_interfaces.png)

As a core of our circuit, we use [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches).
This part of the circuit holds the state of the iris.
Only one of the redstone torches (marked in the image with a letter A or B) can be active at the time.
A single redstone signal pulse to a redstone dust on a side of the latch
will switch it to the desired state.
The latch will hold the state until its switched again.

![Iris setup with GDO RS NOR latch highlight]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_rsnorlatch.png)

For the output of the [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches)
a torch marked with letter **A** is used.
However, the torch under the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode) negates the signal.
So in fact, the state of the torch **B** is the same as the state of the torch under the interface.
This indicates whether the iris should open.
When the torch **A** is inactive, the torch under the interface will light up.
That will power the interface with signal of strength **15**
instructing the iris to open.

So, when we want the iris to open, we need to bring a redstone pulse to the redstone behind the torch **A**.

![Iris setup with GDO RS NOR latch highlight with output]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_rsnorlatch_output.png)

*Thats easy, isn't it?*  
All we need to do is add the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) powering the redstone dust behind the torch **A**.
Now whethever the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) receives a valid code, it will emit a single pulse via the comparator.
A single pulse is enough to switch the [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches)
to the state when the torch **A** is inactive and torch **B** is active.
This will activate the torch under the interface and instruct the iris to open.

![Iris setup with GDO RS NOR latch highlight with transceiver]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_rsnorlatch_transceiver.png)

*Iris opened*  
Leaving the circuit like that will never switch the latch back to active torch **A** 
and the iris will never close for the next incoming connection.
We need to reset the latch state when the connection is over.

With a new comparator, you can read from the interface in the 
[wormhole mode]({{ site.baseurl }}/stargate_network/interface/#wormhole-mode) whether there is an active connection.
This signal can be negated with a redstone torch which can power the latch behind the torch **B**.
So whenever the gate is idle (there is no active connection), the comparator won't output any signal,
leaving the torch active.
The torch will power the latch, switching it to the default state allowing the iris to close 
(the torch under the iris interface is off).
When there is any active connection, the comparator reading the wormhole interface will light up
which will turn off the torch allowing the [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches) to be switched.

![Iris setup with GDO RS NOR latch highlight with reset]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_rsnorlatch_reset.png)

As mentioned before, the iris can be also instructed to automatically open when the gate is idle.
We already have a place, which can emit a signal when the gate is idle.
Hooking the added torch directly to the interface in the 
[iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode) will power the interface with signal strength **14**
opening the iris whenever the gate is not active.

![Iris setup with GDO with auto open]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_auto_open.png)

**Summary**

When the gate is active, the left side of the latch should be active.
The torch below the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode) should be inactive, 
leaving the comparator between interfaces to instruct
the iris based on the connection type (open for outgoing, close for incoming).
The second comparator reading from the interface in the 
[wormhole mode]({{ site.baseurl }}/stargate_network/interface/#wormhole-mode) disables the torch resetting the latch,
allowing it to be switched to the other state by the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) 
(to open the iris when requested).

![Iris setup with GDO active connection]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_active_connection.png)

When the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) receives the correct code, it will send a pulse to the latch, switching it to the other state.
That will activate the torch below the interface instructing the iris to open.

![Iris setup with GDO open iris]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_open_iris.png)

### Expanding the circuit

Here is an example of adding two levers which can act as a manual override of the iris.
The lever on the left closes the iris when activated.
The lever on the right opens the iris when activated (also overriding the lever on the left).

The right lever is connected directly to the redstone on the original granite slabs, adding one slab.
There are two solid blocks added next to the granite slabs to block neighboring redstone dust.

The left lever powers the block with torches to reset the [RS NOR Latch](https://minecraft.wiki/w/Redstone_circuits/Memory#RS-NOR_latches),
the block below the torch below the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode).
The comparator between the interfaces is powered from the side with a repeater,
and the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode) is powered directly with dust (to keep the signal level low).
The comparator between the interfaces is set to subtracting mode.
The lever will also override the [GDO]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) signal.
In case you do not want the lever to override the [GDO]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) signal,
simply remove the repeater powering the torch under the interface in the [iris mode]({{ site.baseurl }}/stargate_network/interface/#iris-mode).


![Iris manual override front]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_manual_override_front.png)
![Iris manual override side]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_manual_override_side.png)
![Iris manual override back]({{ site.baseurl }}/assets/img/mechanics/stargate_network/iris/gdo_breakdown/iris_with_gdo_manual_override_back.png)

The finished circuit is available as a [structure file]({{ site.baseurl }}/assets/structure/mechanics/stargate_network/iris/stargate_iris_gdo_manual_override.nbt)
created on 1.20.1 Stargate Journey 0.6.31.  
You can place this file to  
`installation_folder/saves/your_save/generated/minecraft/structures/stargate_iris_gdo_manual_override.nbt`  
replacing the `installation_folder` with the folder name where your game is installed and `your_save` with the folder name of your world.
Then you can place it in-game with command `/place template minecraft:stargate_iris_gdo_manual_override`.
Make sure you have enough space around you that the structure won't break your building.
After placing, you will probably need to break the gate and place it again (remember to add the iris).
