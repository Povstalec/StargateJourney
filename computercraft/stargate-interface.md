---
title: Stargate Interface
nav_order: 10
parent: Computercraft
custom_css: "/assets/css/computercraft.css"
---

# Stargate Interface
{: .no_toc }

1. Table of Contents
{:toc}

[//]: # (TODO: add introduction pointing to general description of interface)

# Functions
{: .no_toc }


<blockquote class="warning"> 
<p>
    Unless there is a label with interface name at the function, it can be used by <b>any interface</b>.<br> 
    If there is a label, the function **is only available** for the specified interface.
</p> 
<p>
    A similar applies to return values. 
    Some return values might be <b>only available</b> for crystal or advanced crystal interface.
</p>

<!-- %- include components/interface_label.html type="basic" -% -->

{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" -%}

</blockquote>

## Rotating Stargate functions
Functions available for an interface connected to a stargate can be rotated --
[Classic]({{ '/stargate-technology/stargate/#classic-stargate' | absolute_url }}),
[Universe]({{ '/stargate-technology/stargate/#universe-stargate' | absolute_url }})
and [Milky Way]({{ '/stargate-technology/stargate/#milky-way-stargate' | absolute_url }})
stargates.

<div class="flex-row flex-wrap" markdown="block" style="gap: 1em">

![Classic Stargate]({{ '/assets/img/blocks/technological/classic_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&3Classic Stargate" }
![Milky Way Stargate]({{ '/assets/img/blocks/technological/milkyway_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&eMilky Way Stargate" }
![Universe Stargate]({{ '/assets/img/blocks/technological/universe_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&7Universe Stargate" }

</div>


{% include components/computercraft_function.html
    name="encodeChevron"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L45"
%}

Encodes the current symbol under the top chevron.

[Milky Way]({{ '/stargate-technology/stargate/#milky-way-stargate' | absolute_url }})
stargate requires the chevron to be **open**, otherwise returns `-35` (`chevron_not_raised`).

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [openChevron](#openChevron)
- [closeChevron](#closeChevron)
- [engageSymbol](#engageSymbol)

**Usage**
- Encode chevron
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local feedbackCode, message = interface.encodeChevron()
-- The message is not available for basic interface
print(feedbackCode, message)
```

___

{% include components/computercraft_function.html
    name="endRotation"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L136"
%}

Stops the inner ring rotation if it was started by a computer.  
Does nothing if the ring rotates due to a redstone signal.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [rotateAntiClockwise](#rotateAntiClockwise)
- [rotateClockwise](#rotateClockwise)

**Usage**
- End the ring rotation
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local feedbackCode, message = interface.endRotation()
-- The message is not available for basic interface
print(feedbackCode, message) 
```

___

{% include components/computercraft_function.html
    name="getCurrentSymbol"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L14"
%}

Returns the current symbol under the top chevron.
The symbol may not be exactly centered.

**Returns**
1. `number` The symbol under the top chevron or `-1`
   (e.g. when no symbol is at the top on the universe stargate)

**See also**
- [isCurrentSymbol](#isCurrentSymbol)
- [rotateClockwise](#rotateClockwise)
- [rotateAntiClockwise](#rotateAntiClockwise)

**Usage**
- Print the current symbol
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbol = interface.getCurrentSymbol()
print(symbol) 
```

___

{% include components/computercraft_function.html
    name="getRotation"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L60"
%}

Returns the current gate rotation.
> For Classic and Milky Way stargates: `0 - 155`  
> `0` when the Point of Origin is centered under the top chevron.   
> Plus `4` for each symbol to the right centered under the top chevron,  
> `152` for the last symbol (`38`) centered under the top chevron.  
> 
> For Universe stargate: `0 - 322`  
> `0` when the chevron to the left from the Point of Origin is centered at the top,  
> `9` when the Point of Origin is centered at the top.

**Returns**
1. `number` The current ring rotation based on the stargate type.

**See also**
- [getRotationDegrees](#getRotationDegrees)

**Usage**
- Check current ring rotation
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
while true do
    local rotation = interface.getRotation()
    print(rotation, "Hold Ctrl+T to stop the loop") 
    sleep(0)
end
```

___

{% include components/computercraft_function.html
    name="getRotationDegrees"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L75"
%}

Returns the current gate rotation in degrees.

**Returns**
1. `number` The current ring rotation in degrees [`0 - 360`]

**See also**
- [getRotation](#getRotation)

**Usage**
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
while true do
    local rotationDegrees = interface.getRotationDegrees()
    print(rotationDegrees, "Hold Ctrl+T to stop the loop") 
    sleep(0)
end
```

___

{% include components/computercraft_function.html
    name="isCurrentSymbol"
    arguments="symbol"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/RotationMethods.java#L29"
%}

Returns `true` when the current symbol is **centered** under the top chevron,
and it is the desired symbol specified as parameter.
Returns `false` otherwise.

**Parameters**
1. `symbol`: `number` The desired symbol

**Returns**
1. `boolean` Whether the current symbol is centered under the top chevron and matches the desired symbol.

**See also**
- [getCurrentSymbol](#getCurrentSymbol)
- [rotateAntiClockwise](#rotateAntiClockwise)
- [rotateClockwise](#rotateClockwise)

**Usage**
- Await the rotation completion
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbol = 15
interface.rotateClockwise(symbol)
-- wait for the rotation until the desired symbol is reached
while not interface.isCurrentSymbol(symbol) do
    sleep(0)
end
-- rotation complete
print("The current symbol is "..symbol)
```

___


{% include components/computercraft_function.html
    name="rotateAntiClockwise"
    arguments="symbol"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L89"
%}

Rotates the stargate ring **anticlockwise**, positioning the specified symbol centered under the top chevron.  
The method **does not block** the execution for the whole rotation.  
The rotation is stopped when the interface is destroyed.

**Parameters**
1. `symbol`: `number` The desired symbol, or `-1` for infinite rotation.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**Throws**
- When the chevron is open or the symbol is not available on the stargate.

**See also**
- [rotateClockwise](#rotateClockwise)
- [endRotation](#endRotation)

**Usage**
- Rotate the ring anticlockwise to the symbol
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbol = 15
-- start the rotation
interface.rotateAntiClockwise(symbol)
-- await the completion
while not interface.isCurrentSymbol(symbol) do
    sleep(0)
end
-- rotation complete
print("The current symbol is "..symbol)
```

___


{% include components/computercraft_function.html
    name="rotateClockwise"
    arguments="symbol"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L61"
%}

Rotates the inner ring clockwise, positioning the specified symbol centered under the top chevron.  
The method does not block the execution for the whole rotation.  
The rotation is stopped when the interface is destroyed.

**Parameters**
1. `symbol`: `number` The desired symbol (from `0` to `38` inclusive), or `-1` for infinite rotation

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**Throws**
- When the chevron is open or the symbol is out of range (lower than `-1` or higher than `38`).

**See also**
- [rotateAntiClockwise](#rotateAntiClockwise)
- [endRotation](#endRotation)

**Usage**
- Rotate the ring clockwise to the symbol
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbol = 15
-- start the rotation
interface.rotateClockwise(symbol)
-- await the completion
while not interface.isCurrentSymbol(symbol) do
    sleep(0)
end
-- rotation complete
print("The current symbol is "..symbol)
```

___

## Milky Way Stargate functions
Functions exclusive to
[Milky Way]({{ '/stargate-technology/stargate/#milky-way-stargate' | absolute_url }})
stargate.

![Milky Way Stargate]({{ '/assets/img/blocks/technological/milkyway_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&eMilky Way Stargate" }


{% include components/computercraft_function.html
    name="closeChevron"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L180"
%}

Closes the upper chevron if it is open, encoding the current symbol.  
If the symbol is already encoded, returns `-2` (`symbol_in_address`).

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [openChevron](#openChevron)
- [encodeChevron](#encodeChevron)
- [isChevronOpen](#isChevronOpen)

**Usage**
- Close chevron
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local feedbackCode, message = interface.closeChevron()
-- The message is not available for basic interface
print(feedbackCode, message) 
```

___


{% include components/computercraft_function.html
    name="isChevronOpen"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L201"
%}

Returns `true` when the top chevron is open, `false` otherwise.

**Returns**
1. `boolean` Whether the top chevron is open

**See also**
- [encodeChevron](#encodeChevron)
- [openChevron](#openChevron)
- [closeChevron](#closeChevron)

**Usage**
- Check whether the top chevron is open
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local isOpen = interface.isChevronOpen()
if isOpen then
    print("The chevron is open")
else
    print("The chevron is closed")
end 
```

___


{% include components/computercraft_function.html
    name="openChevron"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L138"
%}

Opens the top chevron in preparation for encoding the current symbol.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [closeChevron](#closeChevron)
- [encodeChevron](#encodeChevron)
- [isChevronOpen](#isChevronOpen)

**Usage**
- Open the top chevron
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local feedbackCode, message = interface.openChevron()
-- The message is not available for basic interface
print(feedbackCode, message)
```

___

## Pegasus Stargate functions
Functions exclusive for an interface connected to a
[Pegasus Stargate]({{ '/stargate-technology/stargate/#pegasus-stargate' | absolute_url }}).

![Pegasus Stargate]({{ '/assets/img/blocks/technological/pegasus_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&bPegasus Stargate" }


{% include components/computercraft_function.html
    name="dynamicSymbols"
    arguments="enabled"
    source="https://github.com/Povstalec/StargateJourney/blob/3e4ecd319aacab568b40e18b80a049d034120f3f/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/PegasusStargateMethods.java#L24"
%}

Controls the gate ability to dynamically switch symbols based on its location.
The gate will keep the value when picked up.

When disabled, the symbols and the Point of Origin used by the gate can be overriden.

**Parameters**
1. `enabled`: `boolean` Whether the gate should dynamically change symbols.

**See also**
- [overrideSymbols](#overrideSymbols)
- [overridePointOfOrigin](#overridePointOfOrigin)

**Usage**
- Disables dynamically changing symbols
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.dynamicSymbols(false)
```

___

{% include components/computercraft_function.html
    name="overrideSymbols"
    arguments="symbols"
    source="https://github.com/Povstalec/StargateJourney/blob/3e4ecd319aacab568b40e18b80a049d034120f3f/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/PegasusStargateMethods.java#L47"
%}

Overrides the symbols used by the gate.
This change has only visual effect.
The gate will reset its symbols based on its location when the chunk is unloaded.
To keep the overridden symbols, disable dynamic symbols with [`dynamicSymbols(false)`](#dynamicSymbols).

All symbols options in the base mod are available on [GitHub / data directory](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/symbols).
Their textures are available in a [different folder](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/assets/sgjourney/textures/symbol).
More symbols can be added with datapacks and resourcepacks.

Note that unique symbols for solar systems can be seen only by players with `unique_symbols` config option enabled.
By default, the rendered symbols will fall back to respective symbol set.
See [Stargate Technology / Stargate / Symbols / Symbols and Symbol sets]({{ '/stargate-technology/stargate/#symbols-and-symbol-sets' | absolute_url }}) for explanation.

[//]: # (TODO link to the config option docs)

**Parameters**
1. `symbols`: `string` The resource location of the symbols.  
Examples: `"sgjourney:galaxy_pegasus"`, `"sgjourney:galaxy_milky_way"`

**See also**
- [dynamicSymbols](#dynamicSymbols)
- [overridePointOfOrigin](#overridePointOfOrigin)

**Usage**
- Overrides symbols to Milky Way galaxy
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.overrideSymbols("sgjourney:galaxy_milky_way")
```

___

{% include components/computercraft_function.html
    name="overridePointOfOrigin"
    arguments="pointOfOrigin"
    source="https://github.com/Povstalec/StargateJourney/blob/3e4ecd319aacab568b40e18b80a049d034120f3f/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/PegasusStargateMethods.java#L69"
%}

Overrides the Point Of Origin used by the gate.
This change has only visual effect.
The gate will reset its Point Of Origin based on its location when the chunk is unloaded.
To keep the overridden symbols, disable dynamic symbols with [`dynamicSymbols(false)`](#dynamicSymbols).

All Point Of Origin options in the base mod (more can be added by datapacks) are available in [GitHub / data directory](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/point_of_origin).
Their textures are available in a [different folder, look for subfolders with points of origins](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/assets/sgjourney/textures/symbol).

**Parameters**
1. `pointOfOrigin`: `string` The resource location of the Point Of Origin.
   Examples: `"sgjourney:terra"`, `"sgjourney:tauri"`, `"sgjourney:subido"`

**See also**
- [dynamicSymbols](#dynamicSymbols)
- [overrideSymbols](#overrideSymbols)

**Usage**
- Overrides the Point Of Origin to universal
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.overridePointOfOrigin("sgjourney:universal")
```

___

## Stargate functions
Functions available for an interface connected to **any** Stargate.

<div class="flex-row flex-wrap" markdown="block" style="gap: 1em">

![Classic Stargate]({{ '/assets/img/blocks/technological/classic_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&3Classic Stargate" }
![Universe Stargate]({{ '/assets/img/blocks/technological/universe_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&7Universe Stargate" }
![Milky Way Stargate]({{ '/assets/img/blocks/technological/milkyway_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&eMilky Way Stargate" }
![Tollan Stargate]({{ '/assets/img/blocks/technological/tollan_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&fTollan Stargate" }
![Pegasus Stargate]({{ '/assets/img/blocks/technological/pegasus_stargate.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&bPegasus Stargate" }

</div>


{% include components/computercraft_function.html
    name="disconnectStargate"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L101"
%}

Disconnects the Stargate if there is an active connection.
The Stargate will be reset if it isn't connected (encoded chevrons will be deactivated).

The Stargate won't disconnect/reset if the connection is currently forming (before the kawoosh finishes).

**Returns**
1. `boolean` `true` if the connection was closed, `false` if there was no connection or the Stargate failed to disconnect (e.g. function was called during kawoosh).

**See also**
- [engageStargate](#engageStargate)
- [isWormholeOpen](#isWormholeOpen)
- [isStargateConnected](#isStargateConnected)

**Usage**
- Disconnect the Stargate
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local result = interface.disconnectStargate()
if result then
    print("Stargate disconnected")
else
    print("Stargate is not open / Can not disconnect")
end
```

___

{% include components/computercraft_function.html
    name="engageStargate"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L106"
%}

Confirms the encoded address and instructs the gate to initiate the connection.
If the address is correct and the gate was able to connect to the destination gate, wormhole will be established.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [getRecentFeedback](#getRecentFeedback)
- [engageSymbol](#engageSymbol)
- [encodeChevron](#encodeChevron)

**Usage**  
The following example encodes address for Abydos, waits until the gate encodes all the symbols and then engages the stargate with a 3 second delay.
```lua
-- find any interface connected to the computer
-- this example requires at least crystal interface to work with engageSymbol method and any stargate type
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
-- encode an address
local address = {26, 6, 14, 31, 11, 29, 0}
local engageDirectly = false
local canEngageStargate = false
for index, symbol in pairs(address) do
    print(interface.engageSymbol(symbol, engageDirectly, canEngageStargate))
end
-- requested engaging all symbols waiting for gate to finish
while #interface.getDialedAddress() < #address do
    sleep(1)
end
-- because canEngageStargate is false, the gate will not activate by engaging point of origin
print("Address encoded, sleeping for 3s")
sleep(3)
print("Engaging stargate now")
local feedbackCode, message = interface.engageStargate()
-- The message is not available for basic interface
print(feedbackCode, message)

```

___

{% include components/computercraft_function.html
    name="getChevronsEngaged"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L89"
%}

Returns a number from `0` to `9` which represents a number of chevrons that are engaged on the Stargate.

**Returns**
1. `number` The number of chevrons that have been engaged (`0 - 9`).

**Usage**
- Print the number of engaged chevrons
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local engaged = interface.getChevronsEngaged()
print("Stargate has "..engaged.."/9 chevrons engaged")
```

___

{% include components/computercraft_function.html
    name="getOpenTime"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L95"
%}

Returns a number of **ticks** for which Stargate has been active.

**Returns**
1. `number` The number of ticks the Stargate has been active for, returns `0` if it's inactive.

**See also**
- [Minecraft tick](https://minecraft.fandom.com/wiki/Tick)

**Usage**
- Print a number of seconds for which the Stargate has been active
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local openTimeInTicks = interface.getOpenTime()
-- each second has 20 ticks if the gate is not lagging
local openTimeInSeconds = math.floor(openTimeInTicks / 20)
print("Stargate has been open for "..openTimeInSeconds.." seconds")
```

___

{% include components/computercraft_function.html
    name="getPointOfOrigin"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/b5d8f1b0c5817ba96f363f1bf926c580f740b299/src/main/java/net/povstalec/sgjourney/common/compatibility/computer_functions/GenericStargateFunctions.java#L85"
%}

Returns a string of the resource location of the Point Of Origin.

**Returns**
1. `string` The resource location of the Point Of Origin.

**See also**
- [overridePointOfOrigin](#overridePointOfOrigin)
- [getSymbols](#getSymbols)

**Usage**
- Print the Point of Origin
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local PoO = interface.getPointOfOrigin()
-- print the Point of Origin
print("Stargate has Point of Origin: "..PoO)
```

___

{% include components/computercraft_function.html
    name="getPublicBlacklist"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/b5d8f1b0c5817ba96f363f1bf926c580f740b299/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L207"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns a table with publicly visible addresses on the blacklist.
Only addresses that were added to blacklist as **public** will be listed.
**Private blacklist cannot be listed.**

**Returns**
1. `number[][]` The table (array) of blacklisted addresses, each address is a table (array) of numbers (symbols).  
For example:
```lua
{
    { 1, 2, 3, 4, 5, 6 },
    { 8, 7, 6, 5, 4, 3, 2, 1 }
}
```

**See also**
- [addToBlacklist](#addToBlacklist)
- [clearBlacklist](#clearBlacklist)
- [removeFromBlacklist](#removeFromBlacklist)
- [setFilterType](#setFilterType)

**Usage**
- Print public address on blacklist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local publicBlacklist = interface.getPublicBlacklist()
-- Print each address
for _,address in pairs(publicBlacklist) do
    local text = interface.addressToString(address)
    print(text)
end
```

___

{% include components/computercraft_function.html
    name="getPublicWhitelist"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L127"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns a table with publicly visible addresses on the whitelist.
Only addresses that were added to whitelist as **public** will be listed.
**Private whitelist cannot be listed.**

**Returns**
1. `number[][]` The table (array) of whitelisted addresses, each address is a table (array) of numbers (symbols).  
   For example:
```lua
{
    { 1, 2, 3, 4, 5, 6 },
    { 8, 7, 6, 5, 4, 3, 2, 1 }
}
```

**See also**
- [addToWhitelist](#addToWhitelist)
- [clearWhitelist](#clearWhitelist)
- [removeFromWhitelist](#removeFromWhitelist)
- [setFilterType](#setFilterType)

**Usage**
- Print public addresses on the whitelist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local publicWhitelist = interface.getPublicWhitelist()
-- Print each address
for _,address in pairs(publicWhitelist) do
    local text = interface.addressToString(address)
    print(text)
end
```

___

{% include components/computercraft_function.html
    name="getRecentFeedback"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L28"
%}

Returns information about the Stargate state.  
For Crystal interfaces also returns a second string value with a status description.

**Returns**
1. `number` The most recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- Because the wiki can quickly become outdated,   
you can check the [feedback codes in the mod source code](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/stargate/Stargate.java#L396).

**Usage**
- Print the recent feedback
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local feedbackCode, feedbackMessage = interface.getRecentFeedback()
print("Feedback code: "..feedbackCode)
if feedbackMessage then
    print(feedbackMessage)
else
    print("No description - advanced crystal interface required")
end
```

___

{% include components/computercraft_function.html
    name="getStargateEnergy"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L83"
%}

Returns the amount of energy currently stored in the Stargate.

**Returns**
1. `number` The energy [FE] stored within the Stargate

**See also**
- [Energy Target]({{ '/stargate-technology/energy-target/' | absolute_url }})
- [getEnergy]({{ '/computercraft/interface/#getEnergy' | absolute_url }})
- [setEnergyTarget]({{ '/computercraft/interface/#setEnergyTarget' | absolute_url }})

**Usage**
- Print the current amount of energy in the Stargate
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local energy = interface.getStargateEnergy()
print("There is "..energy.." FE in the Stargate")
```

___

{% include components/computercraft_function.html
    name="getStargateGeneration"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L53"
%}

Returns the [Stargate generation]({{ '/stargate-technology/stargate/#stargate-generations' | absolute_url }}) identifier.

> `0` - Classic Stargate  
> `1` - Universe Stargate  
> `2` - Milky Way Stargate, Tollan Stargate  
> `3` - Pegasus

**Returns**
1. `number` The generation of the Stargate

**See also**
- [getStargateType](#getStargateType)
- [getStargateVariant](#getStargateVariant)

**Usage**
- Print the Stargate generation
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local generation = interface.getStargateGeneration()
print("The Stargate is "..generation..". generation")
```

___

{% include components/computercraft_function.html
    name="getStargateType"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L59"
%}

Returns the Minecraft resource identifier for the Stargate.

**Returns**
1. `string` The resource identifier of the Stargate  
Possible values:
> `sgjourney:classic_stargate`  
> `sgjourney:universe_stargate`  
> `sgjourney:milky_way_stargate`  
> `sgjourney:tollan_stargate`  
> `sgjourney:pegasus_stargate`

**See also**
- [Stargate types]({{ '/stargate-technology/stargate/#stargate-generations' | absolute_url }})
- [Minecraft resource identifier](https://minecraft.fandom.com/wiki/Resource_location)
- [getStargateGeneration](#getStargateGeneration)
- [getStargateVariant](#getStargateVariant)

**Usage**
- Print the Stargate type
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local type = interface.getStargateType()
print("The stargate identifier: "..type)
```

___

{% include components/computercraft_function.html
    name="getStargateVariant"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L74"
%}

Returns the Minecraft resource identifier for the Stargate variant.

**Returns**
1. `string` The Stargate variant resource identifier (e.g. `sgjourney:milky_way_movie`)  
or `sgjourney:empty` for the default Stargate variant

**See also**
- [Stargate variants]({{ '/stargate-technology/crystals/stargate-variant-crystals/' | absolute_url }})
- [Minecraft resource identifier](https://minecraft.fandom.com/wiki/Resource_location)
- [getStargateGeneration](#getStargateGeneration)
- [getStargateType](#getStargateType)

**Usage**
- Print the Stargate variant
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local variant = interface.getStargateVariant()
print("The stargate variant: "..variant)
```

___

{% include components/computercraft_function.html
    name="getSymbols"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/b5d8f1b0c5817ba96f363f1bf926c580f740b299/src/main/java/net/povstalec/sgjourney/common/compatibility/computer_functions/GenericStargateFunctions.java#L90"
%}

Returns a string of the resource location of the Symbols.

**Returns**
1. `string` The resource location of the Symbols, e.g. `sgjourney:terra`

**See also**
- [getPointOfOrigin](#getPointOfOrigin)
- [overrideSymbols](#overrideSymbols)

**Usage**
- Print the Symbols
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbols = interface.getSymbols()
-- print the Symbols
print("Stargate has Symbols: "..symbols)
```

___

{% include components/computercraft_function.html
    name="isStargateConnected"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L65"
%}

Check whether the Stargate is connected to another gate.

{: .note }
The function returns `true` even before kawoosh.  
The Stargate is connected when it establishes a connection.  
Once the Point of Origin is successfully encoded or the first chevron is being locked for an incoming connection.

**Returns**
1. `boolean` Whether the Stargate has an active connection

**See also**
- [isWormholeOpen](#isWormholeOpen)
- [isStargateDialingOut](#isStargateDialingOut)

**Usage**
- Check whether the Stargate is connected
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local isConnected = interface.isStargateConnected()
if isConnected then
    print("Stargate is connected")
else
    print("Stargate is not connected")
end
```

___

{% include components/computercraft_function.html
    name="isStargateDialingOut"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L71"
%}

Returns `true` when there is an active **outgoing** connection (this Stargate dialed the other gate).

**Returns**
1. `boolean` Whether the Stargate is currently connected and the connection is outgoing. Returns `false` otherwise (the Stargate is not connected or the connection is incoming).

**See also**
- [isWormholeOpen](#isWormholeOpen)
- [isStargateConnected](#isStargateConnected)

**Usage**
- Check whether the active connection is outgoing
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local isDialingOut = interface.isStargateDialingOut()
if isDialingOut then
    print("Stargate is dialing out")
else
    print("The connection is incoming, or the gate is not active")
end
```

___

{% include components/computercraft_function.html
    name="isWormholeOpen"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L77"
%}

Returns `true` if there is an active wormhole,
after the kawoosh finishes, and it is safe to enter the wormhole,
`false` otherwise.

**Returns**
1. `boolean` Whether the wormhole has formed

**See also**
- [isStargateConnected](#isStargateConnected)
- [isStargateDialingOut](#isStargateDialingOut)

**Usage**
- Check whether the wormhole has formed
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local isOpen = interface.isWormholeOpen()
if isOpen then
    print("Wormhole is open")
else
    print("Wormhole is not open")
end
```

- Check whether the wormhole is active and it is safe to enter
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
-- assuming the config uses default values (the reverse wormhole kills)
local isConnected = interface.isStargateConnected()
local isOpen = interface.isWormholeOpen()
local isOutgoing = interface.isStargateDialingOut()
if not isConnected then
    print("The Stargate is not connected")
elseif not isOpen then
    -- The Stargate is connected, but the wormhole has not yet formed.
    print("The wormhole is forming")
elseif isOutgoing then
    print("The wormhole is safe to enter")
else
    print("The connection is incoming, do not enter the wormhole!")
end
```

___

{% include components/computercraft_function.html
    name="sendStargateMessage"
    arguments="message"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L49"
%}
Sends the `message` through the current Stargate connection,
which can be received by a computer on the other side as event `stargate_message_received`.

**Basic** and **Crystal interfaces** can only send messages **after the wormhole has fully formed**  
(`isWormholeOpen` returns `true`).

The Advanced Crystal interface can send a message once the Stargate **is connected**  
(`isStargateConnected` returns `true`).
Any interface can receive the message.

**Parameters**
1. `message`: `string` The message to send

**Returns**
1. `boolean` Whether the message was sent successfully

**See also**
- [`stargate_message_received` event]({{ site.baseurl }}/computercraft/events/#stargate_message_received)

**Usage**
- Send a message
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local message = "Hello from the other side"
local wasSent = interface.sendStargateMessage(message)
if wasSent then
    print("Message sent successfully")
else
    print("The message could not be sent")
end
```
- Receive a message from the stargate
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local message = os.pullEvent("stargate_message_received")
print("Received a message from the Stargate:")
print(message)
```

___

{% include components/computercraft_function.html
    name="engageSymbol"
    arguments="symbol, engageDirectly, canEngageStargate"
    source="https://github.com/Povstalec/StargateJourney/blob/77e5a6efd596a5b3e7df99fda6c58ea6f3093bee/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L115"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Directly encodes the symbol.
This method can encode symbols on any Stargate.

Using this method matches dialing with DHD.  
For example, the Milky Way Stargate does not need to spin the ring; it just encodes chevrons directly.

**Parameters**
1. `symbol`: `number` A symbol to encode. The symbol must be in a supported range by the Stargate type.
2. `engageDirectly`: `boolean` (Optional, default: `false`) Whether to engage the symbol directly. Skips the rotation on Pegasus or Universe Stargates.
3. `canEngageStargate`: `boolean` (Optional, default: `true`) Whether engaging the symbol can engage the Stargate and initiate a new connection.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string` {% include components/interface_label.html type="crystal" -%}{%- include components/interface_label.html type="advanced_crystal" -%} A description of the feedback

**See also**
- [getRecentFeedback](#getRecentFeedback)
- [openChevron](#openChevron)
- [closeChevron](#closeChevron)

**Usage**
- Dial the address
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = { 26, 6, 14, 31, 11, 29, 0 } -- Abydos
-- don't forgot the zero (Point of Origin) at the end!
for _, symbol in pairs(address) do
    interface.engageSymbol(symbol)
    sleep(1)
end
```

___

{% include components/computercraft_function.html
    name="getDialedAddress"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L118"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns the address dialed by the gate.  
If the currently active connection is incoming or there is no active connection, the address will be empty.

**Returns**
1. `number[]`: The dialed address

**See also**
- [getConnectedAddress](#getConnectedAddress)
- [getLocalAddress](#getLocalAddress)

**Usage**
- Print the dialed address
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = interface.getDialedAddress()
print("The dialed address: " .. interface.addressToString(address))
```

___

{% include components/computercraft_function.html
    name="getMappedSymbol"
    arguments="symbol"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L209"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns the mapped symbol for the given symbol.
If the symbol was not mapped, returns the same symbol.

**Parameters**
1. `symbol`: `number` The symbol to lookup.

**Returns**
1. `number`: The mapped symbol, or the requested symbol if not mapped.

**See also**
- [Symbol remapping]({{ '/stargate-technology/stargate/#symbol-remapping' | absolute_url }})
- [remapSymbol](#remapSymbol)

**Usage**
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local symbol = 1
local mappedSymbol = interface.getMappedSymbol(symbol)
if symbol == mappedSymbol then
    print("Symbol "..symbol.." is not mapped")
else
    print("Symbol "..symbol.." is mapped to "..mappedSymbol)
end
```

___

{% include components/computercraft_function.html
    name="hasDHD"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L225"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Checks whether there is a DHD connected to the stargate.

**Returns**
1. `boolean`: `true` if there is a DHD connected to the stargate, `false` otherwise.

**Usage**
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
-- TODO: example usage
local hasDHD = interface.hasDHD()
print(hasDHD)
```

___

{% include components/computercraft_function.html
    name="remapSymbol"
    arguments="originalSymbol, newSymbol"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L192"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Remaps a symbol so that the `newSymbol` is represented by the `originalSymbol`.  
After mapping, encoding `originalSymbol` physically encodes `originalSymbol` on the Stargate,
but adds the `newSymbol` to the dialed address.

The point of origin (symbol `0`) cannot be used in remapping.

`originalSymbol → newSymbol`

**Parameters**
1. `originalSymbol`: `number` The original symbol that will represent the `newSymbol`.  
Must not be `0` or symbol already physically encoded on the stargate.
2. `newSymbol`: `number` The new symbol that will be added to the dialed address instead of the `originalSymbol`  
Must not be `0` or symbol already in the dialed address.

**Returns**
1. `boolean`: `true` if the symbol was remapped successfully, `false` otherwise.

**See also**
- [Symbol Remapping]({{ '/stargate-technology/stargate/#symbol-remapping' | absolute_url '}})
- [getMappedSymbol](#getMappedSymbol)
- [getDialedAddress](#getDialedAddress)

**Usage**
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local originalSymbol = 1
local newSymbol = 38
interface.remapSymbol(originalSymbol, newSymbol)
interface.engageSymbol(originalSymbol)
-- original symbol physically encoded on the stargate
local address = interface.getDialedAddress()
print(interface.addressToString(address)) 
-- newSymbol was appended to the address
```

___

{% include components/computercraft_function.html
    name="setChevronConfiguration"
    arguments="configuration"
    source="https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L141"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Causes the chevrons to encode in the order specified by configuration.
This configuration resets every time a Stargate is reset.

[//]: # (TODO: explain when stargate resets)

**Parameters**
1. `configuration`: `number[]` An array of length 8 representing the order of chevrons.   
Possible chevron numbers are `1, 2, 3, 4, 5, 6, 7, 8`.
The top chevron is always encoded as the last one, this can't be changed.
<details markdown="block">
<summary>Chevron numbers</summary>
![Chevron numbers on the Stargate]({{ site.baseurl }}/assets/img/computercraft/milkyway_stargate_chevron_numbers.png)
</details>

**Returns**
1. `string` The message `"Chevron configuration set successfully"`

**Throws**
- When specified configuration is invalid.
The configuration must be an array of exact length 8 with numbers from 1 to 8 without duplicates.

**See also**
- [disconnectStargate](#disconnectStargate) Resets the Stargate

**Usage**
- Set the default chevron order
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.setChevronConfiguration({1, 2, 3, 6, 7, 8, 4, 5})
```
- Set clockwise chevron order (e.g. when encoding 9-chevron address).
```lua
-- find crystal or advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.setChevronConfiguration({1, 2, 3, 4, 5, 6, 7, 8})
```

___

{% include components/computercraft_function.html
    name="addNetwork"
    arguments="network"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L255"
%}

{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Adds the stargate to the given network.

**Parameters**
1. `network`: `number` The number of the network to which the gate should be added.

**Returns**
1. `boolean`: `true` if the stargate was added to the network and wasn't in it already, `false` otherwise.

**See also**
- [getNetworks](#getNetworks)
- [removeNetwork](#removeNetwork)
- [restrictNetwork](#restrictNetwork)

**Usage**
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local network = 415252 -- could be any number
local added = interface.addNetwork(network)
if added then
    print("Stargate was added to network "..network)
else
    print("Failed to add stargate to network")
end
```

___

{% include components/computercraft_function.html
    name="addToBlacklist"
    arguments="address, isPublic"
    source="https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L160"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Adds the address to the blacklist.
When the [filter is set](#setFilterType) to the blacklist type,
the Stargate will not be able to form a connection with the address on the blacklist.
That being said, the Stargate can't dial the address or accept a connection from the blacklisted address.

The added address can be either private or public.
By default, the address is added as private.
Private addresses cannot be listed, but they are still being blacklisted and can be removed with [removeFromBlacklist(address)](#removeFromBlacklist).
If an address is already listed and an attempt to add it again is made, with a different visibility,
the specified visibility is applied and updated in the blacklist.
The method returns `false` in such case, since the address was already on the blacklist.

{: .note }
Blacklisting a 9-chevron address will block all 9-chevron address connections from/to that specific Stargate.
However, a connection using a 7/8-chevron address
could still be made from/to the Stargate with a blacklisted 9-chevron address.
Similarly, blacklisting a 7/8-chevron address
will block all 7/8-chevron connections from/to the Stargate.
However, it will not block 9-chevron connections from/to such Stargates.

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the blacklist (without the trailing zero - Point of Origin).
2. `isPublic`: `boolean` (Optional, default: `false`) Whether the address should be added as public.
   The public blacklist contents are accessible with [getPublicBlacklist()](#getPublicBlacklist).
   The contents of the private blacklist cannot be listed.

**Returns**
1. `boolean`: `true` if the address was added to the blacklist and was not present before, `false` if the address was already on the blacklist.

**Throws**
- When the specified address is invalid (the only allowed lengths are 6, 7 and 8).

**See also**
- [getPublicBlacklist](#getPublicBlacklist)
- [clearBlacklist](#clearBlacklist)
- [removeFromBlacklist](#removeFromBlacklist)
- [setFilterType](#setFilterType)

**Usage**
- Blacklist a 9-chevron address
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = { 16, 25, 4, 21, 6, 19, 33, 22 }
interface.clearBlacklist()
interface.setFilterType(-1) -- set filter to blacklist mode
interface.addToBlacklist(address)
-- now the Stargate will not be able to dial the specified address 
-- or accept a 9-chevron connection from the other gate.
```

___

{% include components/computercraft_function.html
    name="addToWhitelist"
    arguments="address, isPublic"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L56"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Adds the address to the whitelist.
When the [filter is set](#setFilterType) to the whitelist type,
the Stargate will not be able to form a connection with the address that is not on the whitelist.
That being said, the Stargate can't dial the address or accept a connection from an address that is not on the whitelist.

The added address can be either private or public.
By default, the address is added as private.
Private addresses cannot be listed, but they are still being whitelisted and can be removed with [removeFromWhitelist(address)](#removeFromWhitelist).
If an address is already listed and an attempt to add it again is made, with a different visibility,
the specified visibility is applied and updated in the whitelist.
The method returns `false` since the address was already on the whitelist.

{: .note }
Whitelisting a 9-chevron address will allow all 9-chevron address connections from/to that specific Stargate.
However, a connection using a 7/8-chevron address
will not be possible from/to the Stargate with a blacklisted 9-chevron address.
Similarly, whitelisting a 7/8-chevron address
will allow all 7/8-chevron connections from/to the Stargate.
However, it will not allow 9-chevron connections from/to such Stargates.

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the whitelist.
2. `isPublic`: `boolean` (Optional, default: `false`) Whether the address should be added to the public whitelist.

**Returns**
1. `boolean`: `true` if the address was added to the whitelist and was not present before, `false` if the address was already on the whitelist.

**Throws**
- When the specified address is invalid (the only allowed lengths are 6, 7 and 8).

**See also**
- [clearWhitelist](#clearWhitelist)
- [removeFromWhitelist](#removeFromWhitelist)
- [setFilterType](#setFilterType)

**Usage**
- Whitelist the Abydos 7-chevron address
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = { 26, 6, 14, 31, 11, 29 }
interface.clearWhitelist()
interface.setFilterType(1) -- set filter to whitelist mode
interface.addToWhitelist(address)
-- now the Stargate can only estabilish a connection with a Stargate 
-- on Abydos using the 7-chevron address
-- all 8-chevron and 9-chevron connections are blocked
-- including such connections with stargates on Abydos
-- only 7-chevron connection from/to Abydos is possible
```

___

{% include components/computercraft_function.html
    name="clearBlacklist"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L240"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Removes all addresses from the blacklist.
Including all public and all private addresses.

**Returns**
1. `string`: A message `"Blacklist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L255C26-L255C45)

**See also**
- [addToBlacklist](#addToBlacklist)
- [removeFromBlacklist](#removeFromBlacklist)
- [getPublicBlacklist](#getPublicBlacklist)
- [setFilterType](#setFilterType)

**Usage**
- Remove all addresses from the blacklist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.clearBlacklist()
-- blacklist is now empty
```

___

{% include components/computercraft_function.html
    name="clearWhitelist"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L136"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Removes all addresses from the whitelist.
Including all public and all private addresses.

**Returns**
1. `string`: A message `"Whitelist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L151).

**See also**
- [addToWhitelist](#addToWhitelist)
- [removeFromWhitelist](#removeFromWhitelist)
- [getPublicWhitelist](#getPublicWhitelist)
- [setFilterType](#setFilterType)

**Usage**
- Remove all addresses from the whitelist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.clearWhitelist()
-- whitelist is now empty
```
___

{% include components/computercraft_function.html
    name="getConnectedAddress"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L268"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns the address to which the Stargate is connected (the address on the other side of the connection).

**Returns**
1. `number[]`: The remote 7, 8 or 9-chevron address of the connection

{: .note }
> The address is partially filled when a connection is forming 
> and the Stargate is locking the chevrons for an incoming connection.
> 
> To ensure the address has full length, the `isWormholeOpen()` must return true.
> 
> For an outgoing connection, the address is always either empty or full-length.

**See also**
- [getDialedAddress](#getDialedAddress)
- [getLocalAddress](#getLocalAddress)

**Usage**
- Print the remote address
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
if interface.isWormholeOpen() then
    local address = interface.getConnectedAddress()
    print("The remote address is "..interface.addressToString(address))
else
    print("Wormhole not formed")
end
```

___

{% include components/computercraft_function.html
    name="getFilterType"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L17"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns the numeric identifier of the filter type.  
> 0 None  
> 1 Whitelist  
> -1 Blacklist

**Returns**
1. `number`: The filter type identifier

**See also**
- [addToBlacklist](#addToBlacklist)
- [addToWhitelist](#addToWhitelist)

**Usage**
- Print the current filter type
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local type = interface.getFilterType()
if type == 0 then
    print("Filter is disabled")
elseif type == 1 then
    print("Filter is in whitelist mode")
elseif type == -1 then
    print("Filter is in blacklist mode")
end 
```

___

{% include components/computercraft_function.html
    name="getLocalAddress"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L289"
%}
{%- include components/interface_label.html type="advanced_crystal" -%}

Returns the 9-chevron address of this (local) stargate.

**Returns**
1. `number[]`: The 9-chevron address

**See also**
- [getConnectedAddress](#getConnectedAddress)
- [getDialedAddress](#getDialedAddress)

**Usage**
- Print the local address
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local localAddress = interface.getLocalAddress()
print(interface.addressToString(localAddress))
```

___

{% include components/computercraft_function.html
    name="getNetworks"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L240"
%}

{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Returns the list of networks in which the stargate is.

**Returns**
1. `number[]` The list of Stargate's networks

**See also**
- [addNetwork](#addNetwork)
- [removeNetwork](#removeNetwork)
- [isNetworkRestricted](#isNetworkRestricted)
- [restrictNetwork](#restrictNetwork)

**Usage**
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
-- TODO: example usage
local networks = interface.getNetworks()
for _,network in pairs(networks) do
    print(network)
end
```

___

{% include components/computercraft_function.html
    name="isNetworkRestricted"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L366"
%}
{%- include components/interface_label.html type="crystal" -%}
{%- include components/interface_label.html type="advanced_crystal" %}

Checks for the [network restrictions]({{ '/stargate-technology/stargate/#network-restrictions' | absolute_url }}) of the Stargate.

**Returns**
1. `boolean`: Whether are the network restrictions active

**See also**
- [Network restrictions]({{ '/stargate-technology/stargate/#network-restrictions' | absolute_url }})
- [getNetworks](#getNetworks)
- [restrictNetwork](#restrictNetwork)

**Usage**
- Print whether the Stargate is network restricted
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local isRestricted = interface.isNetworkRestricted()
if isRestricted then
    print("Network restriction is active")
else
    print("Network restriction is not active")
end
```

___

{% include components/computercraft_function.html
    name="removeFromBlacklist"
    arguments="address"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L200"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Removes the specified address from the blacklist.

**Parameters**
1. `address`: `number[]` The address to remove from blacklist

**Returns**
1. `boolean`: `true` if the address was on blacklist and was successfully removed, `false` otherwise.

**Throws**
- When the specified address is invalid.

**See also**
- [addToBlacklist](#addToBlacklist)
- [getPublicBlacklist](#getPublicBlacklist)

**Usage**
- Remove the address from the blacklist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = { 16, 25, 4, 21, 6, 19, 33, 22 }
interface.removeFromBlacklist(address)
```

___

{% include components/computercraft_function.html
    name="removeFromWhitelist"
    arguments="address"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L96"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Removes the specified address from the whitelist.

**Parameters**
1. `address`: `number[]` The address to remove from whitelist

**Returns**
1. `boolean`: `true` if the address was on whitelist and was successfully removed, `false` otherwise.

**Throws**
- When the specified address is invalid.

**See also**
- [addToWhitelist](#addToWhitelist)
- [getPublicWhitelist](#getPublicWhitelist)

**Usage**
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local address = { 26, 6, 14, 31, 11, 29 }
interface.removeFromWhitelist(address)
```

___

{% include components/computercraft_function.html
    name="removeNetwork"
    arguments="network"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L271"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Removes the stargate from the given network.

Each stargate has its default network based on its generation.
If the stargate is not in any other network, it is always in its default network from which it can not be removed.

**Parameters**
1. `network`: `number` The network from which the stargate should be removed.

**Returns**
1. `boolean`: `true` if the stargate was in the network and was successfully removed, `false` otherwise.

**See also**
- [Network restrictions]({{ '/stargate-technology/stargate/#network-restrictions' | absolute_url }})
- [getNetworks](#getNetworks)
- [addNetwork](#addNetwork)
- [restrictNetwork](#restrictNetwork)

**Usage**
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local network = 415252 -- could be any number
interface.removeNetwork(network)
```

___

{% include components/computercraft_function.html
    name="restrictNetwork"
    arguments="restrict"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L348"
%}
{%- include components/interface_label.html type="advanced_crystal" %}

Sets the status of [Network Restrictions]({{ '/stargate-technology/stargate/#network-restrictions' | absolute_url }}) of the Stargate.

**Parameters**
1. `restrict`: `number` Whether the network restriction should be enabled, disabled or respect the DHD.
> `number < 0` Network restrictions will be disabled  
> `number = 0` Network restrictions will respect the DHD  
> `number > 0` Network restrictions will be enabled

**See also**
- [Network restrictions]({{ '/stargate-technology/stargate/#network-restrictions' | absolute_url }})
- [isNetworkRestricted](#isNetworkRestricted)
- [getNetworks](#getNetworks)

**Usage**
- Enable network restriction
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
interface.restrictNetwork(1)
```

___

{% include components/computercraft_function.html
    name="setFilterType"
    arguments="type"
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L61"
%}
{%- include components/interface_label.html type="advanced_crystal" -%}

Sets the filter type for the Stargate.

Only one filter type can be active, either whitelist, or blacklist (or none to disable the filter).

**Parameters**
1. `type`: `number` The identifier of the filter type
> 0 None  
> 1 Whitelist  
> -1 Blacklist

**Returns**
1. `number`: the filter type identifier that was set

**See also**
- [getFilterType](#getFilterType)
- [addToBlacklist](#addToBlacklist)
- [addToWhitelist](#addToWhitelist)

**Usage**
- Set the filter type to blacklist
```lua
-- find an advanced crystal interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local FilterType = { 
    None = 0,
    Whitelist = 1,
    Blacklist = -1
}
interface.setFilterType(FilterType.Blacklist)
```

___

## Iris control
Iris related methods are available even when the Stargate does not have an iris installed.
However, they are not available for the Tollan Stargate which can't have an iris.

![Milky Way Stargate with Iris]({{ '/assets/img/stargate-technology/iris/types/naquadah_iris.png' | absolute_url }})
{: .max-width-128 data-minetip-title="&eMilky Way Stargate with &rNaquadah Iris" }


{% include components/computercraft_function.html
    name="getIris"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L14"
%}

Retrieves the identifier of the currently installed iris on the Stargate.

**Returns**
1. `string`: The identifier of the iris (e.g. `sgjourney:naquadah_alloy_iris`)  
Returns `nil` if there is no iris installed

**Usage**
- Check the installed iris
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local iris = interface.getIris()
if iris then
    print("The Stargate has an iris installed: "..iris)
else
    print("The Stargate does not have an iris installed")
end
```

___

{% include components/computercraft_function.html
    name="closeIris"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L29"
%}

Instruct the Iris to start closing.
The function does not wait for the iris to close.

**Returns**
1. `boolean`: `false` when the iris is already being closed (in motion) by a computer, `true` otherwise.
`true` if no iris is installed on the gate.

**See also**
- [`openIris`](#openIris)
- [`stopIris`](#stopIris)
- [`getIrisProgressPercentage`](#getIrisProgressPercentage)

**Usage**
- Close the iris
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local closing = interface.closeIris()
if closing then
    print("Closing the iris...")
else
    print("The iris is already being closed by a computer...")
end
```

___

{% include components/computercraft_function.html
    name="openIris"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L44"
%}

Instruct the Iris to start opening.
The function does not wait for the iris to open.

**Returns**
1. `boolean`: `false` when the iris is already being opened (in motion) by a computer, `true` otherwise.
`true` if no iris is installed on the gate.

**See also**
- [`closeIris`](#closeIris)
- [`stopIris`](#stopIris)
- [`getIrisProgressPercentage`](#getIrisProgressPercentage)

**Usage**
- Open the iris
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local opening = interface.openIris()
if opening then
    print("Opening the iris...")
else
    print("The iris is already being opened by a computer...")
end
```

___

{% include components/computercraft_function.html
    name="stopIris"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L59"
%}

Instruct the Iris to stop.
The function does not wait for the iris to stop.

**Returns**
1. `boolean`: `false` when the iris is already being stopped by a computer, `true` otherwise.
`true` if no iris is installed on the gate.

**See also**
- [`closeIris`](#closeIris)
- [`openIris`](#openIris)
- [`getIrisProgressPercentage`](#getIrisProgressPercentage)

**Usage**
- Stop the iris
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local stopped = interface.stopIris()
if stopped then
    print("Stopped the iris...")
else
    print("The iris is already being stopped by a computer...")
end
```

___

{% include components/computercraft_function.html
    name="getIrisProgress"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L74"
%}

Retrieves the internal iris closing progress.  
This progress is internally used for [blocking the gate by the iris](https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/client/models/WormholeModel.java#L51).

**Returns**
1. `number`: The internal iris closing progress<br>
> `0` when the iris is fully opened or not installed on the gate  
> `58` when the iris is fully closed.

**See also**
- [`getIrisProgressPercentage`](#getIrisProgressPercentage)
- [`closeIris`](#closeIris)
- [`openIris`](#openIris)

___

{% include components/computercraft_function.html
    name="getIrisProgressPercentage"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L89"
%}

Retrieves the percentage of the iris closing progress.

**Returns**
1. `number`: The percentage (decimal) of the iris closing progress<br>
> `0` when the iris is fully opened or not installed on the gate<br>
> `100` when the iris is fully closed

**See also**
- [`getIrisProgress`](#getIrisProgress)
- [`closeIris`](#closeIris)
- [`openIris`](#openIris)

**Usage**
- Get the iris closing percentage
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local progress = interface.getIrisProgressPercentage()
if progress == 0 then
    print("Iris is open")
elseif progress == 100 then
    print("The iris is fully closed")
else
    print("The iris is "..math.floor(progress).."% closed")
end
```

___

{% include components/computercraft_function.html
    name="getIrisDurability"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L104"
%}

Retrieves the iris remaining durability.

**Returns**
1. `number`: The remaining durability of the iris

**See also**
- [`getIrisMaxDurability`](#getIrisMaxDurability)

**Usage**
- Get the iris durability
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local durability = interface.getIrisDurability()
local maxDurability = interface.getIrisMaxDurability()
print("The iris durability: "..durability.."/"..maxDurability.." "..math.floor(durability/maxDurability*100).."%")
```

___

{% include components/computercraft_function.html
    name="getIrisMaxDurability"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/7de36d93323131cb4b71d3ae902791b6ea6c7596/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/IrisMethods.java#L119"
%}

Retrieves the iris maximum durability.

**Returns**
1. `number`: The maximum iris durability

**See also**
- [`getIrisDurability`](#getIrisDurability)

**Usage**
- Get the iris durability
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local durability = interface.getIrisDurability()
local maxDurability = interface.getIrisMaxDurability()
print("The iris durability: "..durability.."/"..maxDurability.." "..math.floor(durability/maxDurability*100).."%")
```

___