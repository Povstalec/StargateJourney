---
title: Dialing
nav_order: 10
has_children: false
parent: Survival Guide
---

# Stargate dialing
{: .no_toc }

1. Table of Contents
{:toc}

## The address

So you have Stargate, but you need to activate it now.

To dial the Stargate, you need to know the **destination address**, which can be 6-symbol, 7-symbol, or 8-symbol long.
<!-- TODO: add link to address length explanation -->
You should have found the **cartouche** with the **Abydos address** along with the buried gate.

You can place the cartouche on the ground and see the symbols (from top to bottom),
or right-click it and see the address as numbers (`-1-2-3-4-5-6-`).

<details markdown="block">
<summary><b>I did not find the cartouche</b></summary>

That could happen if you did not find the sealed [Alpha gate]({{ site.baseurl }}/structures/stargates/#buried-stargate)
and instead found the [Beta gate]({{ site.baseurl }}/structures/stargates/#terra-gate-the-beta-gate) 
or other gate structure (a Stargate pedestal added by a datapack, for example).

In that case, here is an image of a cartouche with the Abydos address.

![Cartouche with the Abydoss address]({{ site.baseurl }}/assets/img/survival/cartouche_abydos_address.png)

Its number representation is `-26-6-14-31-11-29-`.

</details>

___


## Dialing

Now, you should decide **how you want to dial** the gate.  
There are a few options:
<details markdown="block">
<summary>1. Dialing using a Dial Home Device (DHD)</summary>

### Dialing using a Dial Home Device (DHD)

![DHD GUI]({{ site.baseurl }}/assets/img/survival/dhd_gui.png)

First, place down the gate, and then place DHD anywhere near it.
The DHD should be able to connect to the gate within a 16-block range.

Right-click the DHD and enter the address (the order of the numbers matters).
Finally, click the big red button in the middle to encode the Point of Origin and activate the gate.

</details>

<details markdown="block">
<summary>2. Manual dialing with redstone</summary>

### Manual dialing with redstone

Stargate reacts to the redstone signal.  
When you place the gate, note the symbol under the top chevron, that is, the Point of Origin (PoO).
You will need it later.

|       Signal strength        |         Action          |
|:----------------------------:|:-----------------------:|
|              0               |         Nothing         |
|      less or equal to 6      | Anti-clockwise rotation |
|      more or equal to 7      |   Clockwise rotation    |
|         equal to 15          |      Open chevron       |
|     change from 15 to 0      |      Close chevron      |

To dial the Stargate with redstone,
use signals to **spin the ring** and position the desired **symbol** under the **top chevron**.
You can see the symbols and their order on the **cartouche**.
Once the symbol is in place, use signal strength **15** to **open the chevron** and then cut the signal (change from 15 to 0) to **close the chevron**.
This way, the symbol will be **encoded**, and the next chevron will light up.
You can proceed to the next symbol.

If you accidentally encoded a **wrong symbol**, you can encode the **Point of Origin** anytime,
resetting the gate (as the encoded address will be invalid).

Once you encode **all symbols** from the address, encode the **Point of Origin**, activating the Stargate.

![Manual redstone dialing setup]({{ site.baseurl }}/assets/img/survival/redstone_dialing.png)

</details>

<details markdown="block">
<summary>3. Dialing using a <a href="https://tweaked.cc/" target="_blank">Computercraft</a></summary>

### Dialing with computercraft

First, you will need a way to connect the computer to the Stargate.
"Interfaces" act as computer peripherals (you can use any computer basic/advanced).

You can craft a basic interface with 4 iron ingots, 2 gold ingots, 2 copper ingots, and one redstone.

![Crafting a basic interface]({{ site.baseurl }}/assets/img/survival/basic_interface_crafting.png)

![Stargate setup with computer and interface]({{ site.baseurl }}/assets/img/survival/gate_interface_setup.png)

Place the interface **facing the gate**,
ensuring that the **black side** is facing **away** from the gate.
The interface can be placed anywhere on the gate.
Then, place the computer next to the interface.

The last thing you need is a **program** that will dial the gate.
The minimal example follows.
You can also check this [repository](https://github.com/Povstalec/StargateJourney-ComputerCraft-Programs) for more examples
or more complex [scripts created by the community]({{ site.baseurl }}/#computercraft-scripts).

Let's make a minimal example of a program dialing the gate with the hardcoded address.  
To create a script, open the computer, enter the command `edit dial.lua`, and press `Enter`,
opening the editor where you can write code.  
Text after `--` is a comment.  
This example is meant for a **Milky Way stargate** and a **basic interface**.
```lua
-- find the connected peripheral basic_interface
interface = peripheral.find("basic_interface")

-- make sure that the address ends with the PoO (zero)
address = {26, 6, 14, 31, 11, 29, 0} -- Abydos address as example

-- this three commands will reset the gate
-- clear currently encoded symbols
interface.disconnectStargate()
-- close chevron if its open 
interface.closeChevron() 
-- clear symbol if it got encoded by closing the chevron
interface.disconnectStargate() 

-- now loop through the address and encode each symbol
for _, symbol in pairs(address) do
    -- tell the gate that it should spin the ring and position the symbol under the top chevron
    interface.rotateClockwise(symbol)
    -- now we need to wait for the gate to finish the rotation
    while (not interface.isCurrentSymbol(symbol)) do
        sleep(0) -- we do not want to do anything while waiting
    end
    
    sleep(1)
    interface.openChevron()
    sleep(1)
    -- you can either explicitly call encodeChevron as follows
    -- or skip it and the encoding will take place automatically on closeChevron
    -- that's the difference between three-phase encoding and two-phase encoding
    -- it's really just aesthetics
    interface.encodeChevron()
    sleep(1)
    interface.closeChevron()
    sleep(1)
end
```

And that's it: save the script, close the editor, and run it.  
> Press sequentially `Ctrl`, `Enter`, `Ctrl`, `right arrow →`, `Enter`,  
> and enter the command `dial` (the script's name).

The gate should now start dialing the address from the script.

If you see an error, check the spelling of the script and the [common errors section]({{ site.baseurl }}/computercraft/common_errors).

</details>


Dialing with DHD is the easiest way.
You push seven buttons, and you are ready to go.

Manual dialing with redstone might be time-consuming,
but it is currently the only way to get the gate spinning without computers.

Computers are between the manual dialing and the DHD.
They can spin the gate ring and dial automatically.
In the later game phase, they can dial the gate without spinning (as DHD).

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
> By default, that would kill you.
> Two-way travel can be enabled in the config.
> It is disabled based on the Stargate franchise.

___

## Abydos
Welcome to the Abyods, a wonderful planet full of... sand. I hope you packed some snacks.

On this planet, you are looking primarily for two things:
an ore called [Naquadah]({{ site.baseurl }}/survival/naquadah/) 
and your [way back home]({{ site.baseurl }}/survival/addresses/) to the Earth (the address of the overworld).

## [Next page: Finding addresses]({{ site.baseurl }}/survival/addresses/)
{: .no_toc }
