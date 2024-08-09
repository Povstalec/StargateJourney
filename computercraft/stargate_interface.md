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

<blockquote class="warning"> 
<p>
    Unless there is a label with interface name at the function, it can be used by any interface.<br> 
    If there is a label, the function is only available for the specified interface.
</p> 
<span class="label label-green">Crystal Interface</span>
<span class="label label-purple">Advanced Crystal Interface</span>
</blockquote>


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
1. `address`: `number[]` The array of numbers representing an address

**Returns**
1. `string`: The address in text form used elsewhere in the mod

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
1. `number`: The energy stored [FE] within the interface.

___

<h3 class="h-function">
    <code>getEnergyCapacity()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L102">source</a>
</h3>

**Returns**
1. `number`: The energy capacity [FE] of the interface.

___

<h3 class="h-function">
    <code>getEnergyTarget()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L108">source</a>
</h3>

**Returns**
1. `number`: The current energy target.

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
1. `boolean`: `true` if the connection was closed, `false` if there was no connection or the Stargate failed to disconnect (e.g. function was called during kawoosh).

___

<h3 class="h-function">
    <code>getChevronsEngaged()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number`: The number of chevrons that have been engaged (`0 - 9`).

___

<h3 class="h-function">
    <code>getOpenTime()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number`: The number of Ticks the Stargate has been active for, returns 0 if it's inactive.

___

<h3 class="h-function">
    <code>getRecentFeedback()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number`: The most recent Stargate Feedback [int].
2. `string`: **Only (Advanced) Crystal interface**: description of the feedback.

{: .note }
Because the wiki can quickly become outdated, you can check the feedback codes of the latest version [here](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/stargate/Stargate.java#L396).

___

<h3 class="h-function">
    <code>getStargateEnergy()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number`: The energy [FE] stored within the Stargate.

___

<h3 class="h-function">
    <code>getStargateGeneration()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `number`: The generation [int] of the Stargate.  
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
1. `string`: The registry ID (string) of the Stargate (e.g. `sgjourney:milky_way_stargate`).

___

<h3 class="h-function">
    <code>getStargateVariant()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `string`: The registry ID (string) of the Stargate variant (e.g. `sgjourney:milky_way_movie`)
or `sgjourney:empty` for the default Stargate variant.

___

<h3 class="h-function">
    <code>isStargateConnected()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean`: `true` if the Stargate is currently connected, otherwise returns `false`.

{: .note }
The function returns `true` even before kawoosh.  
The Stargate is connected when it establishes a connection.  
Once the Point of Origin is successfully encoded or the first chevron is being locked for an incoming connection.

___

<h3 class="h-function">
    <code>isStargateDialingOut()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean`: `true` if the Stargate is currently connected and the connection is outgoing (this stargate dialed the connection), `false` otherwise (the Stargate is not connected or the connection is incoming).

___

<h3 class="h-function" id="isWormholeOpen">
    <code>isWormholeOpen()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>

**Returns**
1. `boolean`: `true` if the wormhole has formed (kawoosh finished), `false` otherwise.

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
1. `boolean`: `true` if the message was sent, `false` otherwise

___

<h3 class="h-function">
    <code>engageSymbol(symbol)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Crystal Interface
{: .label .label-green }
Advanced Crystal Interface
{: .label .label-purple }

Directly encodes the symbol.
This method can encode symbols on any Stargate (this method matches dialing using a DHD).

**Parameters**
1. `symbol`: `number` A symbol to encode. The symbol must be in a supported range by the Stargate type. <!-- TODO: add link to supported symbol range -->

___

<h3 class="h-function">
    <code>getDialedAddress()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Crystal Interface
{: .label .label-green }
Advanced Crystal Interface
{: .label .label-purple }

**Returns**
1. `number[]`: The dialed address. The address this stargate has dialed. 
If the currently active connection is incoming or there is no active connection, the address will be empty.

___

<h3 class="h-function">
    <code>setChevronConfiguration(configuration)</code>
    <a class="source" target="_blank" href="">source</a>
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


**Usage**
 - Sets the default chevron order
```lua
interface.setChevronConfiguration({1, 2, 3, 6, 7, 8, 4, 5})
 ```
 - Sets clockwise chevron order (e.g. when encoding 9-chevron address).
```lua
interface.setChevronConfiguration({1, 2, 3, 4, 5, 6, 7, 8})
```

___

<h3 class="h-function">
    <code>addToBlacklist(address)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Adds the address to the blacklist.
When the [filter is set](#setFilterType) to the blacklist type,
the Stargate will not be able to form a connection with the address on the blacklist.
That being said, the Stargate can't dial the address or accept a connection from the blacklisted address.

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the blacklist.

**Returns**
1. `string` A message describing the result of the action.  
`"Address blacklisted successfully"` or `"Address is already blacklisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L190)

**Throws**  
- When the specified address is invalid.


___

<h3 class="h-function">
    <code>addToWhitelist(address)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Adds the address to the whitelist.
When the [filter is set](#setFilterType) to the whitelist type,
the Stargate will not be able to form a connection with the address that is not on the whitelist.
That being said, the Stargate can't dial the address or accept a connection from an address that is not on the whitelist.

**Parameters**
1. `address`: `number[]` The 7, 8 or 9-chevron address to be added to the whitelist.

**Returns**
1. `string`: A message describing the result of the action.  
`"Address whitelisted successfully"` or `"Address is already whitelisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L86)

**Throws**
- When the specified address is invalid.

___

<h3 class="h-function">
    <code>clearBlacklist()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes all addresses from the blacklist.

**Returns**
1. `string`: A message `"Blacklist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L255C26-L255C45).

___

<h3 class="h-function">
    <code>clearWhitelist()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes all addresses from the whitelist.

**Returns**
1. `string`: A message `"Whitelist cleared"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L151).

___

<h3 class="h-function">
    <code>getConnectedAddress()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the address to which the Stargate is connected (the address on the other side of the connection).

**Returns**
1. `number[]`: The remote 7, 8 or 9-chevron address of the connection.

{: .note }
> The address is partially filled when a connection is forming, 
> and the Stargate is locking the chevrons for an incoming connection.
> 
> To ensure the address has full length, the `isWormholeOpen()` must return true.
> 
> For an outgoing connection, the address is always either empty or full-length.

___

<h3 class="h-function">
    <code>getFilterType()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns a numeric identifier of the filter type.  
> 0 None  
> 1 Whitelist  
> -1 Blacklist  

**Returns**
1. `number`: The filter type identifier.

___

<h3 class="h-function">
    <code>getLocalAddress()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the 9-chevron address of this stargate.

**Returns**
1. `number[]`: The address

___

<h3 class="h-function">
    <code>getNetwork()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Returns the numeric identifier of the Stargate network of which the Stargate is a part.
<!-- TODO a link to the stargate network explanation -->

**Returns**
1. `number`: The network ID.

<details markdown="block">
<summary>Default network IDs</summary>
0 Classic Stargate
1 Universe Stargate
2 Milky Way Stargate and Tollan Stargate
3 Pegasus Stargate
</details>

___

<h3 class="h-function">
    <code>isNetworkRestricted()</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Checks for the network restriction of the Stargate.

**Returns**
1. `boolean`: Whether the Stargate is network restricted.

<!-- TODO: add see also links to other relevant methods -->

___

<h3 class="h-function">
    <code>removeFromBlacklist(address)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes the specified address from the blacklist.

**Parameters**
1. `address`: `number[]` The address to remove from blacklist.

**Returns**
1. `string`: A message describing the result of the action.  
`"Address removed from blacklist successfully"` or `"Address is not blacklisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L230)

**Throws**
- When the specified address is invalid.

___

<h3 class="h-function">
    <code>removeFromWhitelist(address)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Removes the specified address from the whitelist.

**Parameters**
1. `address`: `number[]` The address to remove from whitelist.

**Returns**
1. `string`: A message describing the result of the action.  
   `"Address removed from whitelist successfully"` or `"Address is not whitelisted"` [source](https://github.com/Povstalec/StargateJourney/blob/main/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/StargateFilterMethods.java#L127)

**Throws**
- When the specified address is invalid.

___

<h3 class="h-function">
    <code>restrictNetwork(enable)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Sets the Stargate to enable or disable declining connections from foreign networks.

When the network restriction is enabled, 
only Stargates with matching Stargate network will be able to establish a connection to this Stargate.

{: .warning }
9-chevron address connections bypasses the network restrictions.

**Parameters**
1. `enable`: `boolean` Whether the network restriction should be enabled.

___

<h3 class="h-function">
    <code>setFilterType(type)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Sets the filter type for the Stargate.

Only one filter type can be active, either whitelist, or blacklist (or none).

**Parameters**
1. `type`: `number` The identifier of the filter type.
> 0 None  
> 1 Whitelist  
> -1 Blacklist

**Returns**
1. `number`: the filter type identifier that was set

___

<h3 class="h-function">
    <code>setNetwork(network)</code>
    <a class="source" target="_blank" href="">source</a>
</h3>
Advanced Crystal Interface
{: .label .label-purple }

Sets the network identifier for the Stargate.

**Parameters**
1. `network`: `number` The identifier of the Stargate network.

<script>
/**
 * Adds ids to the head elements and fixes the links to them
 */
function functionHeadingLink(h) {
    const func = h.querySelector("code").innerText;
    const funcName = func.replaceAll(/[()]+$/g, "");
    const link = h.querySelector(".anchor-heading");
    if(link?.attributes) {
        link.attributes.getNamedItem("href").value = `#${funcName}`;
        link.attributes.getNamedItem("aria-labelledby").value = funcName;
    }
    if(!h.id) {
        h.id = funcName;
    }
}

/**
 * Moves labels that are right after the function heading into the heading element
 */
function headingLabels(h) {
    const wrapper = document.createElement("span");
    const funcName = h.querySelector("code");
    funcName.remove();
    wrapper.appendChild(funcName);
    while (h.nextElementSibling && h.nextElementSibling.tagName === "P" && h.nextElementSibling.classList.contains("label")) {
        const label = h.nextElementSibling;
        label.remove();
        wrapper.appendChild(label);
    }
    h.prepend(wrapper);
}

document.querySelectorAll(".h-function")?.forEach(h => {
    headingLabels(h);
    functionHeadingLink(h);
});
</script>