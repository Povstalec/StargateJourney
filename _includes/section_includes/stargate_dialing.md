[//]: # (This section is being included into survival / dialing and stargate technology / stargate)

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
{% include youtubePlayer.html id="NRQBZ53qCYM" %}
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
{% include youtubePlayer.html id="Pu3V4u2MGJs" %}
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
{% include youtubePlayer.html id="t6SBpAXBaUg" %}
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
    -- wrapped in print() function to get the feedback number from the gate
    -- you can use advanced crystal interface to get feedback also as code names
    print(interface.openChevron())
    sleep(1)
    -- you can either explicitly call encodeChevron as follows
    -- or skip it and the encoding will take place automatically on closeChevron
    -- that's the difference between three-phase encoding and two-phase encoding
    -- it's really just aesthetics
    -- print(interface.encodeChevron())
    sleep(1)
    print(interface.closeChevron())
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
