---
title: Dialing
nav_order: 10
parent: Survival Guide
---

# Stargate dialing
{: .no_toc }

To activate the stargate, you need to dial an **address** of the destination gate.


## The address

Each stargate has a unique **9 chevron** long address.
That address is unique for the specific stargate and can never be changed once the gate is built (or generated).

Additionally, the gate can be dialed using an address of **the solar system** it is located in.
**7-chevron** address is an address of the solar system within the **current galaxy**.
To dial a solar system in a **different galaxy**, you need to use an **8-chevron** address.

**The first address cartouche** you found buried along with the stargate is an address to the Abydos solar system in Milky Way galaxy.
The address consists of **6 symbols**, the 7th symbol is the **Point of Origin** (PoO).

[//]: # (TODO: add link to further addressing system explanation)

You can place the cartouche on the ground and see the symbols (from top to bottom),
or right-click it and see the address as numbers in the chat.

<details markdown="block">
<summary><b>[Spoiler / Hint]</b> I did not find the cartouche</summary>

That could happen if you did not find the sealed [Alpha gate]({{ site.baseurl }}/structures/stargates/#buried-stargate)
and instead found the [Beta gate]({{ site.baseurl }}/structures/stargates/#terra-gate-the-beta-gate) 
or other gate structure (a Stargate pedestal added by a datapack, for example).
In that case, here is an image of the cartouche with the Abydos address.

![Cartouche with the Abydos address]({{ site.baseurl }}/assets/img/survival/cartouche_abydos_address.png)

Its number representation is `-26-6-14-31-11-29-`.

</details>

___

## Power
The stargate requires **power** to operate.
The buried stargate was found with a DHD containing a **Fusion Core** that is able to supply the gate with energy for some time.
The DHD inventory can be accessed by `shift+right-clicking` it with an **empty hand**.
Fusion Core operates on cold fusion technology and so **doesn't require any fuel**.
However, once the Fusion Core runs out of energy, it will need to be **replaced with a new power source** (e.g. with Naquadah Generator Core).
It should be able to power the gate for standard **interstellar** travel (inside the same galaxy).

[//]: # (TODO: fusion core & naquadah generator core link)

The DHD will also power the gate even if other dialing methods are used, it just needs to be placed nearby the gate.

Alternatively, the gate can be powered using a **stargate interface** powered by a generator from **other mod**.

[//]: # (TODO: show interface connected to a gate and powered by a cable (e.g. from Mekanism)

## Dialing

There are several ways to encode the address based on the stargate type used.
The options are: Using a **Dial Home Device (DHD)**, **manual dialing with redstone**, or using a **computer** from CC:Tweaked (Computercraft).
When a computer is used, it can either rotate the gate encoding the symbol at the top, or engage the symbols directly, like a DHD.

Table below summarizes the available dialing methods for each Stargate type.

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

<table class="text-center">
    <thead>
        <tr>
            <th rowspan="2">Stargate type</th>
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
            <td>Classic Stargate</td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
        <tr>
            <td>Universe Stargate</td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
        <tr>
            <td>Milky Way Stargate</td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
            <td class="tick"></td>
        </tr>
        <tr>
            <td>Pegasus Way Stargate</td>
            <td class="tick"></td>
            <td class="cross"></td>
            <td class="cross"></td>
            <td class="tick"></td>
        </tr>
        <tr>
            <td>Tollan Stargate</td>
            <td class="tick"></td>
            <td class="cross"></td>
            <td class="cross"></td>
            <td class="tick"></td>
        </tr>
    </tbody>
</table>

<details markdown="block" id="dialing-using-a-dial-home-device-dhd">
<summary>1. Dialing using a Dial Home Device (DHD)</summary>

![DHD GUI]({{ site.baseurl }}/assets/img/survival/dhd_gui.png)
{: .max-width-512 }

First, place down the gate, and then place DHD anywhere near it.
The DHD should be able to connect to the gate within a 16-block range.

Right-click the DHD and enter the address (the order of the numbers matters).
Finally, click the big red button in the middle to encode the Point of Origin and activate the gate.

</details>

<details markdown="block">
<summary id="manual-dialing-with-redstone">2. Manual dialing with redstone</summary>

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
<summary id="dialing-with-computercraft">3. Dialing using a <a href="https://tweaked.cc/" target="_blank">Computercraft</a></summary>

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
This example is meant for a **Milky Way Stargate** and a **basic interface**.
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
Welcome to Abydos, a wonderful planet full of... sand. I hope you packed some snacks.

On this planet, you are looking primarily for two things:
an ore called [Naquadah]({{ site.baseurl }}/survival/naquadah/) 
and your [way back home]({{ site.baseurl }}/survival/addresses/) to the Earth (the address of the overworld).

## [Next page: Finding addresses]({{ site.baseurl }}/survival/addresses/)
{: .no_toc }
