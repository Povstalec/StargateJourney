---
title: Dialing
nav_order: 10
parent: Survival Guide
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
![Stargate interface powering the gate]({{ site.baseurl }}/assets/img/survival/stargate_interface_power.png)
{: .max-width-512 }

The stargate interface must face the gate (the black side facing away from the gate).
And there must be a power supply connected to the interface from any side.
The image shows an energy cube from Mekanism connected to the interface with a universal cable.
</details>


## Dialing

There are several ways to encode the address based on the stargate type used.
The options are: Using a **Dial Home Device (DHD)**, **manual dialing with redstone**, or using a **computer** from CC:Tweaked (Computercraft).
When a computer is used, it can either rotate the gate encoding the symbol at the top, or engage the symbols directly, like a DHD.
To engage a symbol in the same way as the DHD does, the computer requires the crystal stargate interface (or the advanced stargate interface).
For dialing using rotation, the basic stargate interface is sufficient, but not every gate can be dialed with it.

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

[//]: # (TODO: Add links to stargate interfaces in the table below)

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

<details id="dialing-using-a-dial-home-device-dhd-youtube-video">
<summary><b>[Spoiler]</b> Youtube video</summary>
{% include youtubePlayer.html id="Fv10GPshSCI" %}
</details>

![DHD GUI]({{ site.baseurl }}/assets/img/survival/dhd_gui.png)
{: .max-width-512 .m-auto .d-flex .flex-justify-around }

1. First, place down the gate, and then place DHD anywhere near it.  
The DHD with a single [communication crystal](/stargate-technology/crystals/dhd-crystals/#communication-crystal) 
is able to connect to the gate within a **32-block range**.
2. Right-click the DHD and **enter the address** (the order of the numbers matters).
3. Finally, click the **big red button** in the middle to encode the Point of Origin (symbol 0) and activate the gate.

</details>

<details markdown="block" id="manual-dialing-with-redstone">
<summary>2. Manual dialing with redstone</summary>

<details>
<summary><b>[Spoiler]</b> Youtube video</summary>
{% include youtubePlayer.html id="Ulcp8-21B6U" %}
</details>

Classic, Universe, and Milky Way stargates react to the redstone signal. 
Other stargates (Tollan and Pegasus) cannot be dialed with redstone.  
When you place the gate, note the symbol under the top chevron, that is, the Point of Origin (PoO).
You will need it later. That is not a case for the Universe Stargate.

[//]: # (TODO: provide a link to universe stargate to show the redstone dialing)

| Redstone signal strength |         Action          |
|:------------------------:|:-----------------------:|
|            0             |         Nothing         |
|    less or equal to 6    | Anti-clockwise rotation |
|    more or equal to 7    |   Clockwise rotation    |
|       equal to 15        |      Open chevron       |
|   change from 15 to 0    |      Close chevron      |

To dial the Stargate with redstone,
provide [power](#power) to the gate with a DHD or stargate interface.
Use different redstone signal strengths to **spin the ring** and position the desired **symbol** under the **top chevron**.
You can see the symbols and their order on the **cartouche**.
Once the symbol is in place, use signal strength **15** to **open the chevron** and then cut the signal (change from 15 to 0) to **close the chevron**.
No other redstone signal must be present on the gate.
This way, the symbol will be **encoded**, and the next chevron will light up.

If you accidentally encoded a **wrong symbol**, you can encode the **Point of Origin** anytime,
resetting the gate (as the encoded address will be invalid).

Once you have encoded **all the symbols** from the address, encode the **Point of Origin** to activate the Stargate.

![Manual redstone dialing setup]({{ '/assets/img/survival/redstone_dialing.png' | absolute_url }})
{: .max-width-512 .m-auto .d-flex .flex-justify-around }

The observers in the image reacts to stone buttons resulting in two pulses moving the ring together by a single symbol.
</details>

<details markdown="block" id="dialing-with-computercraft">
<summary>3. Dialing using the <a href="https://tweaked.cc/" target="_blank">Computercraft</a></summary>

<details>
<summary><b>[Spoiler]</b> Youtube video</summary>
{% include youtubePlayer.html id="VzTOlO9Ccas" %}
</details>

Stargate can be dialed using any computer (basic or advanced) from the [CC:Tweaked](https://tweaked.cc/) mod.
First, you will need a way to connect the computer to the Stargate.
"Interfaces" act as computer peripherals and allow the computer to interact with the Stargate.

{% minecraft_recipe_crafting item:"sgjourney:basic_interface" %}

![Stargate setup with computer and interface]({{ site.baseurl }}/assets/img/computercraft/connecting_interface_to_computer.png)

Place the interface **facing the gate**,
ensuring that the **black side** is facing **away** from the gate.
The interface can be placed anywhere right next to the gate.
Then, place the computer next to the interface or connect it with a cable modem.
Don't forget to activate wired modems on both sides by right-clicking them.

The last thing you need is a **program** that will dial the gate.
The minimal example follows.
You can also check this [repository](https://github.com/Povstalec/StargateJourney-ComputerCraft-Programs) for more examples.
More complex programs with advanced features are [created by the community]({{ site.baseurl }}/#computercraft-scripts) and can also be found on the discord.

Let's make a minimal example of a program dialing the gate with a hardcoded address.  
To create a script, open the computer, enter the command `edit dial.lua`, and press `Enter`,
opening the editor where you can write code.  
Text after `--` is a comment and has no effect in the code, so you don't have to write it.  
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
> Press sequentially `Ctrl` (brings up menu), `Enter` (select save), `Ctrl` (brings up menu), `right arrow →` (move to exit option), `Enter` (select exit option),  
> and enter the command `dial` (the script's name).

The gate should now start dialing the address from the script.

If you see an error, check the spelling of the script and the [common errors section]({{ site.baseurl }}/computercraft/common_errors).

[//]: # (TODO: add link to more CC:T documentation)

</details>


Dialing with DHD is the easiest way.
You push few buttons, and you are ready to go.

Manual dialing with redstone might be time-consuming,
but it is currently the only way to get the gate spinning without computers.

Computers are between the manual dialing and the DHD.
They can spin the gate ring and dial automatically.
In the later game phase, with a crystal interface, they can dial the gate without spinning (as DHD).

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
