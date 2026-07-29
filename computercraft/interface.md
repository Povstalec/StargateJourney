---
title: Interface
nav_order: 0
parent: Computercraft
custom_css: "/assets/css/computercraft.css"
---

# Interface
{: .no_toc }

1. Table of Contents
{:toc}

Technology from Stargate Journey (mainly Stargates) can be controlled with computers from [ComputerCraft: Tweaked](https://tweaked.cc/).
This is achieved through the use of Interfaces,
which, as the name suggests,
interface with alien technology and enable you to control it.

They can read information from a Stargate and provide a [redstone signal with a comparator]({{ '/stargate-technology/interface' | absolute_url }}).
And they can also act as **computercraft peripherals**.  
This page will only cover the integration of interfaces with [CC: Tweaked](https://tweaked.cc/).

There are three available Interfaces -
Basic Interface,
Crystal Interface,
and Advanced Crystal Interface.  
[See the interface page for general description and redstone interaction.]({{ '/stargate-technology/interface' | absolute_url }})


<div class="flex-row flex-wrap" markdown="block">

![Basic Interface]({{ '/assets/img/items/crafting/sgjourney/static/basic_interface.png' | absolute_url }})
![Crystal Interface]({{ '/assets/img/items/crafting/sgjourney/static/crystal_interface.png' | absolute_url }})
![Advanced Crystal Interface]({{ '/assets/img/items/crafting/sgjourney/static/advanced_crystal_interface.png' | absolute_url }})

</div>

___

## Available functions

**Functions available** on the interface depends on the technology to which the **interface is connected**.
- [Interface connected to a Stargate]({{ '/computercraft/stargate-interface/' | absolute_url }})
- [Interface connected to a Ring Transporter]({{ '/computercraft/ring-transporter-interface/' | absolute_url }})

___

## Connecting the interface

The interface **needs to face** the technology it should connect to.  
The correct orientation can be seen on the image below where the basic interface is **facing the glass block**.

![Basic Interface facing a glass block]({{ '/assets/img/computercraft/interface_orientation.png' | absolute_url }})
{: .max-width-256 }

In the same style, the interface can be oriented up or down as well.
It will connect to any supported block **right in front of it**.
There must be no other block between the interface and the block, they must be **directly touching** each other.

To connect a **computer** to the interface, it needs to either be placed **right next to the interface**, 
or connected with a **cable modem**.
The wireless modem can **not** be used.
Note that the cable modems on both sides need to be **activated by right-clicking**, lighting them red.

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
but keep in mind that **not all features** are available for **every interface type**.
```lua
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
```

**See also about CC:Tweaked peripherals**  
- [peripheral](https://tweaked.cc/module/peripheral.html)
- [peripheral.find](https://tweaked.cc/module/peripheral.html#v:find)

___

## Common Interface Functions
The following functions are available to **any interface** at all times.
Even when the interface is not connected ty any other technology.

{% include components/computercraft_function.html
    name="addressToString"
    arguments="address"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L35"
%}

Converts the array specified by address to a form used elsewhere in the mod: `-1-2-3-4-5-6-7-8-`.

**Parameters**
1. `address`: `number[]` The array of numbers representing an address.

**Returns**
1. `string` The address in text form used elsewhere in the mod. Returns `"-"` when the address is empty or has more than 8 symbols.

**Usage**
- Convert the abydos address to text `-26-6-14-31-11-29-`
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local stringAddress = interface.addressToString({ 26, 6, 14, 31, 11, 29 }) 
print(stringAddress) -- prints -26-6-14-31-11-29-
```

<details markdown="block">
<summary>Lua equivalent</summary>
A function having the same effect implemented in Lua without the need of an interface.
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

{% include components/computercraft_function.html
    name="getEnergy"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L96"
%}

Returns the current amount of energy stored in the interface.
Uses FE (Forge Energy).

**Returns**
1. `number` The energy [FE] stored within the interface

**See also**
- [getStargateEnergy()](#getStargateEnergy)

**Usage**
- Print the current amount of energy in the interface
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local energy = interface.getEnergy()
print("There is "..energy.." FE in the interface")
```

___

{% include components/computercraft_function.html
    name="getEnergyCapacity"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L102"
%}

Returns the maximal amount of energy [FE] that can be stored in the interface.

**Returns**
1. `number` The interface capacity

**Usage**
- Print the energy capacity of the interface
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local capacity = interface.getEnergyCapacity()
print("The interface can store up to "..capacity.." FE")
```

___

{% include components/computercraft_function.html
    name="getEnergyTarget"
    arguments=""
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/InterfacePeripheral.java#L108"
%}

Returns the current energy target that is set for the interface.

**Returns**
1. `number` The current energy target [FE]

**See also**
- [Energy Target]({{ '/stargate-technology/energy-target/' | absolute_url }})
- [setEnergyTarget(energyTarget)](#setEnergyTarget)

**Usage**
- Print the current energy target
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
local energyTarget = interface.getEnergyTarget()
print("The current energy target: "..energyTarget.." FE")
```

___


{% include components/computercraft_function.html
    name="setEnergyTarget"
    arguments="energyTarget"
    source="https://github.com/Povstalec/StargateJourney/blob/6a4c5800c8f3ef88c352accfd76306db9db1325c/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/InterfaceMethods.java#L17"
%}


Sets the energy target to the amount specified by `energyTarget` parameter.

**Parameters**
1. `energyTarget`: `number` The new energy target

**See also**
- [Energy Target]({{ '/stargate-technology/energy-target/' | absolute_url }})
- [getEnergyTarget()](#getEnergyTarget)

**Usage**
- Set a new energy target
```lua
-- find any interface connected to the computer
local interface = peripheral.find("advanced_crystal_interface") or peripheral.find("crystal_interface") or peripheral.find("basic_interface")
if interface == nil then
    error("The interface is not connected")
end
-- use the interface:
-- the amount of energy [FE] required to reach another galaxy by default (100 000 000 000)
local energyTarget = 100000000000 
interface.setEnergyTarget(energyTarget)
```

___