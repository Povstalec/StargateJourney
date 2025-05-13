---
title: Transceiver
nav_order: 5
parent: Computercraft
---

{% include computercraft/doc-utils.html %}

# Transceiver
{: .no_toc }

1. Table of Contents
{:toc}

[//]: # (TODO: improve this description)
[Transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) allows to receive an IDentification Code (IDC) from a [Garage Door Opener (GDO)]({{ site.baseurl }}/items/functional/#garage-door-opener-gdo) through an active stargate on a configured frequency.
Transceiver can be configured to listen on a specific frequency and for a specific IDC.
It is also able to send an IDC on the configured frequency.
When the transceiver receives a signal on the configured frequency, it will raise a computercraft event [transceiver_transmission_received]({{ site.baseurl }}/computercraft/events/#transceiver_transmission_received).
Although the transceiver is able to validate only a single IDC, the computer can perform the validation itself by checking the received IDC from the event.
The transceiver can listen only on a single frequency.
More transceivers can be used to listen on different frequencies simultaneously.

It is a standalone peripheral (do not use the stargate interface). 
It can be placed next to the computer or connected using the wired modem.
```lua
local transceiver = peripheral.find("transceiver")
```

___

<h3 class="h-function">
    <code>setFrequency(frequency)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/TransceiverMethods.java#L12">source</a>
</h3>

Sets the frequency at which the transceiver will listen.

**Parameters**
1. `number`: The frequency to set

**Usage**
- Set the frequency to `1234`
```lua
local FREQUENCY = 1234
transceiver.setFrequency(FREQUENCY)
```

___

<h3 class="h-function">
    <code>setCurrentCode(idc)</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/TransceiverMethods.java#L31">source</a>
</h3>

Sets the IDentification Code (IDC) which the transceiver will validate or transmit.
Currently, the IDC is `string` so it can be any string.
However, note that the GDO can send only numbers.

**Parameters**
1. `string`: The IDC to set

**Usage**
- Set the IDC to `4321` (it must be a string)
```lua
local IDC = "4321"
transceiver.setCurrentCode(IDC)
```

___

<h3 class="h-function">
    <code>sendTransmission()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/TransceiverMethods.java#L50">source</a>
</h3>

Sends a transmission.
The transceiver will transmit the current IDC on the configured frequency.

**Usage**
- Send the IDC on frequency
```lua
local IDC = 4321
local FREQUENCY = 1234
transceiver.setFrequency(FREQUENCY)
transceiver.setCurrentCode(IDC)
transceiver.sendTransmission()
```

___

<h3 class="h-function">
    <code>checkConnectedShielding()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/methods/TransceiverMethods.java#L73">source</a>
</h3>

Transceiver searches for a nearest stargate and checks the state of the iris on the other side of the Stargate connection.
It does not matter if the connection is incoming or outgoing.

**Returns**
- `number`: The percentage of the iris closing on the other side of the Stargate connection
> `nil` if there is no Stargate in range or the nearest Stargate is not connected  
> `0` if the iris on the other side is fully open or there is no iris  
> `100` if the iris on the other side is fully closed  

**Usage**
- Check the iris state on the other side of the Stargate connection
```lua
local state = transceiver.checkConnectedShielding()
if state == nil then
    print("No Stargate in range or the nearest Stargate is not connected")
elseif state == 0 then
    print("The iris on the other side is fully open or there is no iris")
elseif state == 100 then
    print("The iris on the other side is fully closed")
else
    print("The iris on the other side is closed at " .. state .. "%")
end
```

___

<h3 class="h-function">
    <code>getCurrentCode()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/TransceiverPeripheral.java#L115">source</a>
</h3>

Returns the currently set IDentification Code (IDC).

**Returns**
1. `string` The current IDC, empty string if not set

**Usage**
- Print the current IDC
```lua
local idc = transceiver.getCurrentCode()
print("Current IDC: " .. idc)
```

___

<h3 class="h-function">
    <code>getFrequency()</code>
    <a class="source" target="_blank" href="https://github.com/Povstalec/StargateJourney/blob/e2419d72c2000262cd05757a30e5feda1248ff27/src/main/java/net/povstalec/sgjourney/common/compatibility/cctweaked/peripherals/TransceiverPeripheral.java#L109">source</a>
</h3>

Returns the current frequency on which the transceiver is listening.

**Returns**
1. `number` The current frequency

**Usage**
- Print the current frequency
```lua
local frequency = transceiver.getFrequency()
print("Transceiver is listening on frequency: " .. frequency)
```
