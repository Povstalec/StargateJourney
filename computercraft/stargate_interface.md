---
title: Stargate Interface
nav_order: 0
has_children: false
parent: Computercraft
---

{% include computercraft_doc.html %}

# Stargate Interface
{: .no_toc }

1. Table of Contents
{:toc}

Technology from Stargate Journey (mainly Stargates) can be controlled with computers from [ComputerCraft](https://tweaked.cc/). 
This is achieved through the use of Interfaces, 
which, as the name suggests, 
interface with alien technology and enable you to control it.

They can read information from a Stargate and provide a [redstone signal with a comparator]({{ site.baseurl }}/stargate_network/interface/).
And they can also act as computercraft peripherals.

There are three available Stargate Interfaces -
[Basic Interface]({{ site.baseurl }}/blocks/technological_blocks/#basic-interface),
[Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#crystal-interface),
[Advanced Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#advanced-crystal-interface).

<blockquote class="warning"> 
<p>
    Unless there is a label with interface name at the function, it can be used by any interface.<br> 
    If there is a label, the function is only available for the specified interface.
</p> 
<p>
    A similar applies to return values. 
    Some return values might only be available for crystal or advanced crystal interface.
</p>
<span class="label label-green">Crystal Interface</span>
<span class="label label-purple">Advanced Crystal Interface</span>
</blockquote>

# Connecting the interface
{: .no_toc }
The interface needs to face the stargate (the blank black side must face **away from the gate**).

The computer needs to be either placed right next to the interface, the side does not matter.
Or you need to connect the interface using a cable modem. 
Note that the cable modems on both sides need to be activated by right-clicking, lighting them red.

![Connecting the interface to the computer]({{ site.baseurl }}/assets/img/computercraft/connecting_interface_to_computer.png)

In the program, interface can be acquired using the find function.
```lua
local interface = peripheral.find("basic_interface")
if interface == nil then
    printError("The interface is not connected")
    return
end
```
You can also instruct the program to use any available interface type,
but keep in mind that not all features are available for all interface types.
```lua
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
```

# Functions
{: .no_toc }

## Common functions
These are the functions every Interface has available at all times.

___

<h3 class="h-function">
    <code>addressToString(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L35">source</a>
</h3>

Converts the array specified by address to a form used elsewhere in the mod (`-1-2-3-4-5-6-`).

**Parameters**
1. `address`: `number[]` The array of numbers representing an address.

**Returns**
1. `string` The address in text form used elsewhere in the mod. Returns `"-"` when the address is empty or has more than 8 symbols.

**Usage**
- Convert the abydos address to text `-26-6-14-31-11-29-`
```lua
local stringAddress = interface.addressToString({ 26, 6, 14, 31, 11, 29 }) 
print(stringAddress) -- prints -26-6-14-31-11-29-
```

<details markdown="block">
<summary>Lua equivalent</summary>
```lua
function addressToString(address)
    if #address == 0 or #address > 8 then
        return "-"
    end
    return "-" .. table.concat(address, "-") .. "-"
end
```
</details>

___

<h3 class="h-function">
    <code>getEnergy()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L96">source</a>
</h3>

Returns the current amount of energy [FE (Forge Energy)] stored in the interface.

**Returns**
1. `number` The energy [FE] stored within the interface

**See also**
- [getStargateEnergy()](#getStargateEnergy)

**Usage**
- Print the current amount of energy in the interface
```lua
local energy = interface.getEnergy()
print("There is "..energy.." FE in the interface")
```

___

<h3 class="h-function">
    <code>getEnergyCapacity()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L102">source</a>
</h3>

Returns the maximal amount of energy [FE] that can be stored in the interface.

**Returns**
1. `number` The interface capacity

**Usage**
- Print the energy capacity of the interface
```lua
local capacity = interface.getEnergyCapacity()
print("The interface can store up to "..capacity.." FE")
```

___

<h3 class="h-function">
    <code>getEnergyTarget()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L108">source</a>
</h3>

Returns the current energy target that is set for the interface.

**Returns**
1. `number` The current energy target [FE]

**See also**
- [Energy Target](/stargate_network/interface/#energy-target)
- [setEnergyTarget(energyTarget)](#setEnergyTarget)

**Usage**
- Print the current energy target
```lua
local energyTarget = interface.getEnergyTarget()
print("The current energy target: "..energyTarget.." FE")
```

___

<h3 class="h-function">
    <code>setEnergyTarget(energyTarget)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L17">source</a>
</h3>

Sets the energy target to the amount specified by `energyTarget` parameter.

**Parameters**
1. `energyTarget`: `number` The new energy target

**See also**
- [Energy Target](/stargate_network/interface/#energy-target)
- [getEnergyTarget()](#getEnergyTarget)

**Usage**
- Set a new energy target
```lua
-- the amount of energy [FE] required to reach another galaxy by default (100 000 000 000)
local energyTarget = 100000000000 
interface.setEnergyTarget(energyTarget)
```

___

## Milky Way Stargate functions
Functions available for an interface connected to the **Milky Way Stargate**.

___

<h3 class="h-function">
    <code>closeChevron()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L180">source</a>
</h3>

Closes the upper chevron if it is open, encoding the current symbol.  
If the symbol is already encoded, returns `-2` (`symbol_in_address`).

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [openChevron()](#openChevron)
- [encodeChevron()](#encodeChevron)
- [isChevronOpen()](#isChevronOpen)

**Usage**
- Close chevron
```lua
local feedback = interface.closeChevron()
print(feedback) 
```

___

<h3 class="h-function">
    <code>encodeChevron()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L159">source</a>
</h3>

Encodes the current symbol under the top chevron.
Requires the chevron to be **open**, otherwise returns `-35` (`chevron_not_raised`).

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [openChevron()](#openChevron)
- [closeChevron()](#closeChevron)
- [isChevronOpen()](#isChevronOpen)

**Usage**
- Encode chevron
```lua
local feedback = interface.encodeChevron()
print(feedback) 
```

___

<h3 class="h-function">
    <code>endRotation()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L117">source</a>
</h3>

Stops the inner ring rotation if it was started by a computer.  
Does nothing if the ring rotates due to a redstone signal.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [getRotation()](#getRotation)
- [rotateAntiClockwise()](#rotateAntiClockwise)
- [rotateClockwise()](#rotateClockwise)

**Usage**
- End the ring rotation
```lua
local feedback = interface.endRotation()
print(feedback) 
```

___

<h3 class="h-function">
    <code>getCurrentSymbol()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L14">source</a>
</h3>

Returns the current symbol under the top chevron.

**Returns**
1. `number` The symbol under the top chevron

**Usage**
- Print the current symbol
```lua
local symbol = interface.getCurrentSymbol()
print(symbol) 
```

___

<h3 class="h-function">
    <code>getRotation()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L46">source</a>
</h3>

Returns the current inner ring rotation from `0` up to `155` (inclusive).  
> `0` when the Point of Origin is centered under the top chevron  
> plus `4` for each symbol to the right centered under the top chevron  
> `152` for the last symbol (`38`) centered under the top chevron.

**Returns**
1. `number` The current ring rotation from `0` to `155`

**See also**
- [endRotation()](#endRotation)
- [rotateAntiClockwise()](#rotateAntiClockwise)
- [rotateClockwise()](#rotateClockwise)

**Usage**
- Check current ring rotation
```lua
while true do
    local rotation = interface.getRotation()
    print(rotation) 
    sleep(0)
end
```

___

<h3 class="h-function">
    <code>isChevronOpen()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L201">source</a>
</h3>

Returns `true` when the top chevron is open, `false` otherwise.

**Returns**
1. `boolean` Whether the top chevron is open

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [encodeChevron()](#encodeChevron)
- [openChevron()](#openChevron)
- [closeChevron()](#closeChevron)

**Usage**
- Check whether the top chevron is open
```lua
local isOpen = interface.isChevronOpen()
if isOpen then
    print("The chevron is open")
else
    print("The chevron is closed")
end 
```

___

<h3 class="h-function">
    <code>isCurrentSymbol(symbol)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L29">source</a>
</h3>

Returns `true` when the current symbol is **centered** under the top chevron, 
and it is the desired symbol specified as parameter.
Returns `false` otherwise.

**Parameters**
1. `symbol`: `number` The desired symbol

**Returns**
1. `boolean` Whether the current symbol is centered under the top chevron and matches the desired symbol.

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [rotateAntiClockwise()](#rotateAntiClockwise)
- [rotateClockwise()](#rotateClockwise)

**Usage**
- Await the rotation completion
```lua
local symbol = 15
interface.rotateClockwise(symbol)
while not interface.isCurrentSymbol(symbol) do
    sleep(0)
end
-- rotation complete
print("The current symbol is "..symbol)
```

___

<h3 class="h-function">
    <code>openChevron()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L138">source</a>
</h3>

Opens the top chevron in preparation for encoding the current symbol.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [closeChevron()](#closeChevron)
- [encodeChevron()](#encodeChevron)
- [isChevronOpen()](#isChevronOpen)

**Usage**
- Open the top chevron
```lua
local feedback interface.openChevron()
print(feedback)
```

___

<h3 class="h-function">
    <code>rotateAntiClockwise(symbol)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L89">source</a>
</h3>

Rotates the inner ring anticlockwise, positioning the specified symbol centered under the top chevron.  
The method does not block the execution for the whole rotation.  
The rotation is stopped when the interface is destroyed.

**Parameters**
1. `symbol`: `number` The desired symbol (from `0` to `38` inclusive), or `-1` for infinite rotation.

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**Throws**
- When the chevron is open or the symbol is out of range (lower than `-1` or higher than `38`).

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [endRotation()](#endRotation)
- [isChevronOpen()](#isChevronOpen)
- [rotateClockwise()](#rotateClockwise)

**Usage**
- Rotate the ring anticlockwise to the symbol
```lua
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

<h3 class="h-function">
    <code>rotateClockwise(symbol)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/MilkyWayStargateMethods.java#L61">source</a>
</h3>

Rotates the inner ring clockwise, positioning the specified symbol centered under the top chevron.  
The method does not block the execution for the whole rotation.  
The rotation is stopped when the interface is destroyed.

**Parameters**
1. `symbol`: `number` The desired symbol (from `0` to `38` inclusive), or `-1` for infinite rotation

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**Throws**
- When the chevron is open or the symbol is out of range (lower than `-1` or higher than `38`).

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [endRotation()](#endRotation)
- [isChevronOpen()](#isChevronOpen)
- [rotateAntiClockwise()](#rotateAntiClockwise)

**Usage**
- Rotate the ring clockwise to the symbol
```lua
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

## Stargate functions
Functions available for an interface connected to a Stargate.

___

<h3 class="h-function">
    <code>disconnectStargate()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L101">source</a>
</h3>

Disconnects the Stargate if there is an active connection.
The Stargate will be reset if it isn't connected (encoded chevrons will be deactivated).

The Stargate won't disconnect/reset if the connection is currently forming (before the kawoosh finishes).

**Returns**
1. `boolean` `true` if the connection was closed, `false` if there was no connection or the Stargate failed to disconnect (e.g. function was called during kawoosh).

**Usage**
- Disconnect the Stargate
```lua
local result = interface.disconnectStargate()
if result then
    print("Stargate disconnected")
else
    print("Stargate is not open / Can not disconnect")
end
```

___

<h3 class="h-function">
    <code>getChevronsEngaged()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L89">source</a>
</h3>

Returns a number from `0` to `9` which represents a number of chevrons that are engaged on the Stargate.

**Returns**
1. `number` The number of chevrons that have been engaged (`0 - 9`).

**Usage**
- Print the number of engaged chevrons
```lua
local engaged = interface.getChevronsEngaged()
print("Stargate has "..engaged.."/9 chevrons engaged")
```

___

<h3 class="h-function">
    <code>getOpenTime()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L95">source</a>
</h3>

Returns a number of ticks for which Stargate has been active.

**Returns**
1. `number` The number of ticks the Stargate has been active for, returns `0` if it's inactive.

**See also**
- [Minecraft tick](https://minecraft.fandom.com/wiki/Tick)

**Usage**
- Print a number of seconds for which the Stargate has been active
```lua
local openTimeInTicks = interface.getOpenTime()
-- each second has 20 ticks
local openTimeInSeconds = math.floor(openTimeInTicks / 20)
print("Stargate has been open for "..openTimeInSeconds.." seconds")
```

___

<h3 class="h-function">
    <code>getRecentFeedback()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L28">source</a>
</h3>

Returns information about the Stargate state.  
For Advanced Crystal interface also returns a second string value with a status description.

**Returns**
1. `number` The most recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- Because the wiki can quickly become outdated,   
you can check the [feedback codes in the mod source code](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/stargate/Stargate.java#L396).

**Usage**
- Print the recent feedback
```lua
local feedbackCode, feedbackMessage = interface.getRecentFeedback()
print("Feedback code: "..feedbackCode)
if feedbackMessage then
    print(feedbackMessage)
else
    print("No description - advanced crystal interface required")
end
```

___

<h3 class="h-function">
    <code>getStargateEnergy()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L83">source</a>
</h3>

Returns the amount of energy currently stored in the Stargate.

**Returns**
1. `number` The energy [FE] stored within the Stargate

**See also**
- [getEnergy()](#getEnergy)
- [setEnergyTarget()](#setEnergyTarget)

**Usage**
- Print the current amount of energy in the Stargate
```lua
local energy = interface.getEnergy()
print("There is "..energy.." FE in the Stargate")
```

___

<h3 class="h-function">
    <code>getStargateGeneration()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L53">source</a>
</h3>

Returns the Stargate generation identifier.

> `0` - Classic Stargate  
> `1` - Universe Stargate  
> `2` - Milky Way Stargate, Tollan Stargate  
> `3` - Pegasus

<!-- TODO: link to stargate generations -->

**Returns**
1. `number` The generation `[int]` of the Stargate

**See also**
- [getStargateType()](#getStargateType)
- [getStargateVariant()](#getStargateVariant)

**Usage**
- Print the Stargate generation
```lua
local generation = interface.getStargateGeneration()
print("The Stargate is "..generation.." generation")
```

___

<h3 class="h-function">
    <code>getStargateType()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L59">source</a>
</h3>

Returns the minecraft resource identifier for the Stargate.

> `sgjourney:classic_stargate`  
> `sgjourney:universe_stargate`  
> `sgjourney:milky_way_stargate`  
> `sgjourney:tollan_stargate`  
> `sgjourney:pegasus_stargate`  

**Returns**
1. `string` The resource identifier of the Stargate

**See also**
- [Stargate types]({{ site.baseurl }}/blocks/technological_blocks/#stargates)
- [Minecraft resource identifier](https://minecraft.fandom.com/wiki/Resource_location)
- [getStargateGeneration()](#getStargateGeneration)
- [getStargateVariant()](#getStargateVariant)

**Usage**
- Print the Stargate type
```lua
local type = interface.getStargateType()
print("The stargate identifier: "..type)
```

___

<h3 class="h-function">
    <code>getStargateVariant()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L74">source</a>
</h3>

Returns the minecraft resource identifier for the Stargate variant.

**Returns**
1. `string` The Stargate variant resource identifier (e.g. `sgjourney:milky_way_movie`)  
or `sgjourney:empty` for the default Stargate variant

**See also**
- [Stargate variants]({{ site.baseurl }}/blocks/stargate_variants/)
- [Minecraft resource identifier](https://minecraft.fandom.com/wiki/Resource_location)
- [getStargateGeneration()](#getStargateGeneration)
- [getStargateType()](#getStargateType)

**Usage**
- Print the Stargate variant
```lua
local variant = interface.getStargateVariant()
print("The stargate variant: "..variant)
```

___

<h3 class="h-function">
    <code>isStargateConnected()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L65">source</a>
</h3>

Check whether the Stargate is connected to another gate.

{: .note }
The function returns `true` even before kawoosh.  
The Stargate is connected when it establishes a connection.  
Once the Point of Origin is successfully encoded or the first chevron is being locked for an incoming connection.

**Returns**
1. `boolean` Whether the Stargate has an active connection

**See also**
- [isWormholeOpen()](#isWormholeOpen)
- [isStargateDialingOut()](#isStargateDialingOut)

**Usage**
- Check whether the Stargate is connected
```lua
local isConnected = interface.isStargateConnected()
if isConnected then
    print("Stargate is connected")
else
    print("Stargate is not connected")
end
```

___

<h3 class="h-function">
    <code>isStargateDialingOut()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L71">source</a>
</h3>

Returns `true` when there is an active **outgoing** connection (this Stargate dialed the other gate).

**Returns**
1. `boolean` Whether the Stargate is currently connected and the connection is outgoing. Returns `false` otherwise (the Stargate is not connected or the connection is incoming).

**See also**
- [isWormholeOpen()](#isWormholeOpen)
- [isStargateConnected()](#isStargateConnected)

**Usage**
- Check whether the active connection is outgoing
```lua
local isDialingOut = interface.isStargateDialingOut()
if isDialingOut then
    print("Stargate is dialing out")
else
    print("The connection is incoming, or the gate is not active")
end
```

___

<h3 class="h-function" id="isWormholeOpen">
    <code>isWormholeOpen()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/StargatePeripheral.java#L77">source</a>
</h3>

Returns `true` if there is an active wormhole. 
After the kawoosh finishes, and it is safe to enter the wormhole,
`false` otherwise.

**Returns**
1. `boolean` Whether the wormhole has formed

**See also**
- [isStargateConnected()](#isStargateConnected)
- [isStargateDialingOut()](#isStargateDialingOut)

**Usage**
- Check whether the wormhole has formed
```lua
local isOpen = interface.isWormholeOpen()
if isOpen then
    print("Wormhole is open")
else
    print("Wormhole is not open")
end
```

- Check whether the wormhole is active and it is safe to enter
```lua
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

<h3 class="h-function">
    <code>sendStargateMessage(message)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L49">source</a>
</h3>
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
local message = os.pullEvent("stargate_message_received")
print("Received a message from the Stargate:")
print(message)
```

___

<h3 class="h-function">
    <code>engageSymbol(symbol)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L95">source</a>
</h3>
Crystal Interface
{: .label .label-green }
Advanced Crystal Interface
{: .label .label-purple }

Directly encodes the symbol.
This method can encode symbols on any Stargate.

Using this method matches dialing with DHD.  
For example, the Milky Way Stargate does not need to spin the ring; it just encodes chevrons directly.

**Parameters**
1. `symbol`: `number` A symbol to encode. The symbol must be in a supported range by the Stargate type. 
<!-- TODO: add link to supported symbol range -->

**Returns**
1. `number` The recent Stargate Feedback `[int]`
2. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

**See also**
- [getRecentFeedback()](#getRecentFeedback)
- [openChevron()](#openChevron)
- [closeChevron()](#closeChevron)

**Usage**
- Dial the address
```lua
local address = { 26, 6, 14, 31, 11, 29, 0 } -- Abydos
-- don't forgot the zero (Point of Origin) at the end!
for _, symbol in pairs(address) do
    interface.engageSymbol(symbol)
    sleep(1)
end
```

___

<h3 class="h-function">
    <code>getDialedAddress()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L118">source</a>
</h3>
Crystal Interface
{: .label .label-green }
Advanced Crystal Interface
{: .label .label-purple }

Returns the address dialed by the gate.  
If the currently active connection is incoming or there is no active connection, the address will be empty.

**Returns**
1. `number[]`: The dialed address

**See also**
- [getConnectedAddress()](#getConnectedAddress)
- [getLocalAddress()](#getLocalAddress)

**Usage**
- Print the dialed address
```lua
local address = interface.getDialedAddress()
print("The dialed address: " .. interface.addressToString(address))
```

___

<h3 class="h-function">
    <code>setChevronConfiguration(configuration)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L141">source</a>
</h3>
Crystal Interface
{: .label .label-green }
Advanced Crystal Interface
{: .label .label-purple }

Causes the chevrons to encode in the order specified by configuration.
This configuration resets every time a Stargate is reset.
<!-- TODO: explain when stargate resets -->

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
- [disconnectStargate()](#disconnectStargate) Resets the Stargate

**Usage**
 - Set the default chevron order
```lua
interface.setChevronConfiguration({1, 2, 3, 6, 7, 8, 4, 5})
```
 - Set clockwise chevron order (e.g. when encoding 9-chevron address).
```lua
interface.setChevronConfiguration({1, 2, 3, 4, 5, 6, 7, 8})
```

___

<h3 class="h-function">
    <code>addToBlacklist(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L160">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Adds the address to the blacklist.
When the [filter is set](#setFilterType) to the blacklist type,
the Stargate will not be able to form a connection with the address on the blacklist.
That being said, the Stargate can't dial the address or accept a connection from the blacklisted address.

{: .note }
Blacklisting a 9-chevron address will block all 9-chevron address connections from/to that specific Stargate.
However, a connection using a 7/8-chevron address
could still be made from/to the Stargate with a blacklisted 9-chevron address.
Similarly, blacklisting a 7/8-chevron address
will block all 7/8-chevron connections from/to the Stargate.
However, it will not block 9-chevron connections from/to such Stargates.

<!-- TODO: move this to mechanics and provide a link -->

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the blacklist (without the trailing zero - Point of Origin).

**Returns**
1. `string` A message describing the result of the action  
`"Address blacklisted successfully"` or `"Address is already blacklisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L190)

**Throws**  
- When the specified address is invalid (the only allowed lengths are 6, 7 and 8).

**See also**
- [clearBlacklist()](#clearBlacklist)
- [removeFromBlacklist(address)](#removeFromBlacklist)
- [setFilterType(type)](#setFilterType)

**Usage**
- Blacklist a 9-chevron address
```lua
local address = { 16, 25, 4, 21, 6, 19, 33, 22 }
interface.clearBlacklist()
interface.setFilterType(-1) -- set filter to blacklist mode
interface.addToBlacklist(address)
-- now the Stargate will not be able to dial the specified address 
-- or accept a 9-chevron connection from the other gate.
```

___

<h3 class="h-function">
    <code>addToWhitelist(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L56">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Adds the address to the whitelist.
When the [filter is set](#setFilterType) to the whitelist type,
the Stargate will not be able to form a connection with the address that is not on the whitelist.
That being said, the Stargate can't dial the address or accept a connection from an address that is not on the whitelist.

{: .note }
Whitelisting a 9-chevron address will allow all 9-chevron address connections from/to that specific Stargate. 
However, connections using a 7/8-chevron address would not be possible from/to the Stargate with a whitelisted 9-chevron address 
unless those addresses are also specifically whitelisted.

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the whitelist.

**Returns**
1. `string`: A message describing the result of the action  
`"Address whitelisted successfully"` or `"Address is already whitelisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L86)

**Throws**
- When the specified address is invalid (the only allowed lengths are 6, 7 and 8).

**See also**
- [clearWhitelist()](#clearWhitelist)
- [removeFromWhitelist(address)](#removeFromWhitelist)
- [setFilterType(type)](#setFilterType)

**Usage**
- Whitelist the Abydos 7-chevron address
```lua
local address = { 26, 6, 14, 31, 11, 29 }
interface.clearWhitelist()
interface.setFilterType(1) -- set filter to whitelist mode
interface.addToWhitelist(address)
-- now the Stargate can only estabilish a connection with a Stargate 
-- on Abydos using the 7-chevron address
```

___

<h3 class="h-function">
    <code>clearBlacklist()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L240">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes all addresses from the blacklist.

**Returns**
1. `string`: A message `"Blacklist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L255C26-L255C45)

**See also**
- [addToBlacklist()](#addToBlacklist)
- [removeFromBlacklist(address)](#removeFromBlacklist)
- [setFilterType(type)](#setFilterType)

**Usage**
- Remove all addresses from the blacklist
```lua
interface.clearBlacklist()
-- blacklist is now empty
```

___

<h3 class="h-function">
    <code>clearWhitelist()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L136">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes all addresses from the whitelist.

**Returns**
1. `string`: A message `"Whitelist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L151).

**See also**
- [addToWhitelist()](#addToWhitelist)
- [removeFromWhitelist(address)](#removeFromWhitelist)
- [setFilterType(type)](#setFilterType)

**Usage**
- Remove all addresses from the whitelist
```lua
interface.clearWhitelist()
-- whitelist is now empty
```
___

<h3 class="h-function">
    <code>getConnectedAddress()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L268">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

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
- [getDialedAddress()](#getDialedAddress)
- [getLocalAddress()](#getLocalAddress)

**Usage**
- Print the remote address
```lua
if interface.isWormholeOpen() then
    local address = interface.getConnectedAddress()
    print("The remote address is "..interface.addressToString(address))
else
    print("Wormhole not formed")
end
```

___

<h3 class="h-function">
    <code>getFilterType()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L17">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the numeric identifier of the filter type.  
> 0 None  
> 1 Whitelist  
> -1 Blacklist  

**Returns**
1. `number`: The filter type identifier

**See also**
- [addToBlacklist(address)](#addToBlacklist)
- [addToWhitelist(address)](#addToWhitelist)

**Usage**
- Print the current filter type
```lua
local type = interface.getFilterType()
if type == 0 then
    print("Filter is disabled")
elif type == 1 then
    print("Filter is in whitelist mode")
elif type == -1 then
    print("Filter is in blacklist mode")
end 
```

___

<h3 class="h-function">
    <code>getLocalAddress()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L289">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the 9-chevron address of this stargate.

**Returns**
1. `number[]`: The address

**See also**
- [getConnectedAddress()](#getConnectedAddress)
- [getDialedAddress()](#getDialedAddress)

**Usage**
- Print the local address
```lua
local localAddress = interface.getLocalAddress()
print(interface.addressToString(localAddress))
```

___

<h3 class="h-function">
    <code>getNetwork()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L310">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the numeric identifier of the Stargate network of which the Stargate is a part.
<!-- TODO a link to the stargate network explanation -->

**Returns**
1. `number`: The network ID

<details markdown="block">
<summary>Default network IDs</summary>
0 Classic Stargate  
1 Universe Stargate  
2 Milky Way Stargate and Tollan Stargate  
3 Pegasus Stargate  
</details>

**See also**
- [isNetworkRestricted()](#isNetworkRestricted)
- [setNetwork(network)](#setNetwork)
- [restrictNetwork(network)](#restrictNetwork)

**Usage**
- Print the network ID
```lua
local network = interface.getNetwork()
print("The network ID is: "..network)
```
___

<h3 class="h-function">
    <code>isNetworkRestricted()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L366">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Checks for the network restriction of the Stargate.

**Returns**
1. `boolean`: Whether the Stargate is network restricted

<!-- TODO: add see also links to other relevant methods -->

**See also**
- [getNetwork()](#getNetwork)
- [setNetwork(network)](#setNetwork)
- [restrictNetwork(network)](#restrictNetwork)

**Usage**
- Print whether the Stargate is network restricted
```lua
local isRestricted = interface.isNetworkRestricted()
if isRestricted then
    print("Network restriction is active")
else
    print("Network restriction is not active")
end
```

___

<h3 class="h-function">
    <code>removeFromBlacklist(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L200">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes the specified address from the blacklist.

**Parameters**
1. `address`: `number[]` The address to remove from blacklist

**Returns**
1. `string`: A message describing the result of the action  
`"Address removed from blacklist successfully"` or `"Address is not blacklisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L230)

**Throws**
- When the specified address is invalid.

**See also**
- [addToBlacklist(address)](#addToBlacklist)

**Usage**
- Remove the address from the blacklist
```lua
local address = { 16, 25, 4, 21, 6, 19, 33, 22 }
interface.removeFromBlacklist(address)
```

___

<h3 class="h-function">
    <code>removeFromWhitelist(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L96">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes the specified address from the whitelist.

**Parameters**
1. `address`: `number[]` The address to remove from whitelist

**Returns**
1. `string`: A message describing the result of the action  
   `"Address removed from whitelist successfully"` or `"Address is not whitelisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L127)

**Throws**
- When the specified address is invalid.

**See also**
- [addToWhitelist(address)](#addToWhitelist)

**Usage**
- Remove the address from the whitelist
```lua
local address = { 26, 6, 14, 31, 11, 29 }
interface.removeFromWhitelist(address)
```

___

<h3 class="h-function">
    <code>restrictNetwork(enable)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L348">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Sets the Stargate to enable or disable declining connections from foreign networks.

When the network restriction is enabled, 
only Stargates with matching Stargate network will be able to establish a connection to this Stargate.
Outgoing connections are not affected.

{: .warning }
9-chevron address connections bypasses the network restrictions.

**Parameters**
1. `enable`: `boolean` Whether the network restriction should be enabled.

**See also**
- [isNetworkRestricted()](#isNetworkRestricted)
- [setNetwork(network)](#setNetwork)
- [getNetwork()](#getNetwork)

**Usage**
- Enable network restriction
```lua
interface.restrictNetwork(true)
```

___

<h3 class="h-function">
    <code>setFilterType(type)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

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
- [getFilterType()](#getFilterType)
- [addToBlacklist(address)](#addToBlacklist)
- [addToWhitelist(address)](#addToWhitelist)


**Usage**
- Set the filter type to blacklist
```lua
local FilterType = { 
    None = 0,
    Whitelist = 1,
    Blacklist = -1
}
interface.setFilterType(FilterType.Blacklist)
```

___

<h3 class="h-function">
    <code>setNetwork(network)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateMethods.java#L330">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Sets the network identifier for the Stargate.

**Parameters**
1. `network`: `number` The identifier of the Stargate network (any number)

**See also**
- [isNetworkRestricted()](#isNetworkRestricted)
- [restrictNetwork(network)](#restrictNetwork)
- [getNetwork()](#getNetwork)

**Usage**
- Set the network of the Stargate
```lua
local network = 415252 -- could be any number
interface.setNetwork(network) 
```

___

### Iris control
Iris related methods are available even when the Stargate does not have an iris installed.
However, they are not available for the Tollan Stargate which can't have an iris.

___

<h3 class="h-function">
    <code>getIris()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L16">source</a>
</h3>
Retrieves the identifier of the currently installed iris on the Stargate.

**Returns**
1. `string`: The identifier of the iris (e.g. `sgjourney:naquadah_alloy_iris`)<br>
   Returns `nil` if there is no iris installed

**See also**
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

**Usage**
- Check the installed iris
```lua
local iris = interface.getIris()
if iris then
    print("The Stargate has an iris installed: "..iris)
else
    print("The Stargate does not have an iris installed")
end
```

___

<h3 class="h-function">
    <code>closeIris()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L39">source</a>
</h3>
Instruct the Iris to start closing.
The function does not wait for the iris to close.

**Returns**
1. `boolean`: `false` when the iris is already being closed (in motion) by a computer, `true` otherwise.
Can return `true` even when there is no iris installed.

**See also**
- [`getIris()`](#getIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

**Usage**
- Close the iris
```lua
local closing = interface.closeIris()
if closing then
    print("Closing the iris...")
else
    print("The iris is already being closed by a computer...")
end
```

___

<h3 class="h-function">
    <code>openIris()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L59">source</a>
</h3>
Instruct the Iris to start opening.
The function does not wait for the iris to open.

**Returns**
1. `boolean`: `false` when the iris is already being opened (in motion) by a computer, `true` otherwise.
Can return `true` even when there is no iris installed.

**See also**
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

**Usage**
- Open the iris
```lua
local opening = interface.openIris()
if opening then
    print("Opening the iris...")
else
    print("The iris is already being opened by a computer...")
end
```

___

<h3 class="h-function">
    <code>stopIris()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L79">source</a>
</h3>
Instruct the Iris to stop.
The function does not wait for the iris to stop.

**Returns**
1. `boolean`: `false` when the iris is already being stopped by a computer, `true` otherwise.
Can return `true` even when there is no iris installed.

**See also**
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

**Usage**
- Stop the iris
```lua
local stopped = interface.stopIris()
if stopped then
    print("Stopped the iris...")
else
    print("The iris is already being stopped by a computer...")
end
```

___

<h3 class="h-function">
    <code>getIrisProgress()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L99">source</a>
</h3>
Retrieves the internal iris closing progress.  
This progress is internally used for [blocking the gate by the iris](https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/client/models/WormholeModel.java#L51).

**Returns**
1. `number`: The internal iris closing progress<br>
> `0` when the iris is fully opened or not installed on the gate<br> 
> `58` when the iris is fully closed.

**See also**
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

___

<h3 class="h-function">
    <code>getIrisProgressPercentage()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L119">source</a>
</h3>
Retrieves the percentage of the iris closing progress.

**Returns**
1. `number`: The percentage (decimal) of the iris closing progress<br>
> `0` when the iris is fully opened or not installed on the gate<br>
> `100` when the iris is fully closed

**See also**
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisDurability()`](#getIrisDurability)
- [`getIrisMaxDurability()`](#getIrisMaxDurability)

**Usage**
- Get the iris closing percentage
```lua
local progress = interface.getIrisProgressPercentage()
if progress == 0 then
    print("Iris is open")
elif progress == 100 then
    print("The iris is fully closed")
else
    print("The iris is "..math.floor(progress).."% closed")
end
```

___

<h3 class="h-function">
    <code>getIrisDurability()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L139">source</a>
</h3>
Retrieves the iris remaining durability.

**Returns**
1. `number`: The remaining durability of the iris

**See also**
- [`getIrisMaxDurability()`](#getIrisMaxDurability)
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)

**Usage**
- Get the iris durability
```lua
local durability = interface.getIrisDurability()
local maxDurability = interface.getIrisMaxDurability()
print("The iris durability: "..durability.."/"..maxDurability.." "..math.floor(durability/maxDurability*100).."%")
```

___

<h3 class="h-function">
    <code>getIrisMaxDurability()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/ShieldingMethods.java#L159">source</a>
</h3>
Retrieves the iris maximum durability.

**Returns**
1. `number`: The maximum iris durability

**See also**
- [`getIris()`](#getIris)
- [`closeIris()`](#closeIris)
- [`openIris()`](#openIris)
- [`stopIris()`](#stopIris)
- [`getIrisProgress()`](#getIrisProgress)
- [`getIrisProgressPercentage()`](#getIrisProgressPercentage)
- [`getIrisDurability()`](#getIrisDurability)

**Usage**
- Get the iris durability
```lua
local durability = interface.getIrisDurability()
local maxDurability = interface.getIrisMaxDurability()
print("The iris durability: "..durability.."/"..maxDurability.." "..math.floor(durability/maxDurability*100).."%")
```

