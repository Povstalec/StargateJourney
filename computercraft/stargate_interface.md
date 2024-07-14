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

They can read information from a Stargate and provide a [redstone signal with a comparator]({{ site.baseurl }}/mechanics/stargate_network/interface/).
And they can also act as computercraft peripherals.

There are three available Stargate Interfaces -
[Basic Interface]({{ site.baseurl }}/blocks/technological_blocks/#basic-interface),
[Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#crystal-interface),
[Advanced Crystal Interface]({{ site.baseurl }}/blocks/technological_blocks/#advanced-crystal-interface).

# Functions
{: .no_toc }

## Common functions
These are the functions every Interface has available at all times.

___

<h3 class="h-function">
    <code>addressToString(address)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L35">source</a>
</h3>

Converts the array specified by address to a form used elsewhere in the mod (-1-2-3-4-5-6-).

**Parameters**
1. `address`: `int[]` The array of numbers representing an address

**Returns**
1. `string` The address in text form used elsewhere in the mod

**Usage**
- Converts the abydos address to text `-26-6-14-31-11-29-`

```lua
interface.addressToString({ 26, 6, 14, 31, 11, 29 })
```

<details markdown="block">
<summary>Lua equivalent</summary>
```lua
function addressToString(address)
    if #address == 0 then
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

**Returns**
1. `number` The energy stored [FE] within the interface.

___

<h3 class="h-function">
    <code>getEnergyCapacity()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L102">source</a>
</h3>

**Returns**
1. `number` The energy capacity [FE] of the interface.

___

<h3 class="h-function">
    <code>getEnergyTarget()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L108">source</a>
</h3>

**Returns**
1. `number` The current energy target.

___

<h3 class="h-function">
    <code>setEnergyTarget(energyTarget)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L26">source</a>
</h3>

Sets the energy target to the amount specified by `energyTarget` parameter.

**Parameters**
1. `energyTarget`: `number` The new energy target.

___

## Stargate functions
Functions available for any interface connected to a Stargate.

<h3 class="h-function">
    <code>disconnectStargate()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

If the Stargate is connected, the command disconnects it. 
If it isn't connected, the Stargate will be reset (encoded chevrons will be deactivated).

**Returns**
1. `boolean` - `true` if the connection was closed, `false` if there was no connection or the Stargate failed to disconnect (e.g. function was called during kawoosh).

___

<h3 class="h-function">
    <code>getChevronsEngaged()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number` The number of chevrons that have been engaged (`0 - 9`).

___

<h3 class="h-function">
    <code>getOpenTime()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number` The number of Ticks the Stargate has been active for, returns 0 if it's inactive.

___

<h3 class="h-function">
    <code>getRecentFeedback()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number` The most recent Stargate Feedback [int].
2. `string` **Only (Advanced) Crystal interface**: description of the feedback.

{: .note }
Because the wiki can quickly become outdated, you can check the feedback codes of the latest version [here](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/stargate/Stargate.java#L396).

___

<h3 class="h-function">
    <code>getStargateEnergy()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number` The energy [FE] stored within the Stargate.

___

<h3 class="h-function">
    <code>getStargateGeneration()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number` The generation [int] of the Stargate.  
> `0` - Classic Stargate  
> `1` - Universe Stargate  
> `2` - Milky Way Stargate, Tollan Stargate  
> `3` - Pegasus  

___

<h3 class="h-function">
    <code>getStargateType()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `string` The registry ID (string) of the Stargate (e.g. `sgjourney:milky_way_stargate`).

___

<h3 class="h-function">
    <code>getStargateVariant()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `string` The registry ID (string) of the Stargate variant (e.g. `sgjourney:milky_way_movie`)
or `sgjourney:empty` for the default Stargate variant.

___

<h3 class="h-function">
    <code>isStargateConnected()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean` - `true` if the Stargate is currently connected, otherwise returns `false`.

{: .note }
The Stargate is connected when it establishes a connection once the Point of Origin is successfully encoded.
The function returns `true` even before kawoosh.

___

<h3 class="h-function">
    <code>isStargateDialingOut()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean` - `true` if the Stargate is currently connected and the connection is outgoing (this stargate dialed the connection), `false` otherwise (the Stargate is not connected or the connection is incoming).

___

<h3 class="h-function" id="isWormholeOpen">
    <code>isWormholeOpen()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean` - `true` if the wormhole has formed (kawoosh finished), `false` otherwise.

___

<h3 class="h-function">
    <code>sendStargateMessage(message)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Sends a `message` through the current Stargate connection, which can be received by a computer on the other side (in `stargate_message_received_event`). 
<!-- TODO: add link to stargate_message_received_event -->

Basic and Crystal interfaces can send messages after the wormhole ha fully formed (`isWormholeOpen` returns true)

**Parameters**
1. `message`: `string` The message to send.

**Returns**
1. `boolean` - `true` if the message was sent, `false` otherwise

___


