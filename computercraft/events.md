---
title: Events
nav_order: 50
has_children: false
parent: Computercraft
---

{% include computercraft_doc.html %}

# Events
{: .no_toc }

1. Table of Contents
{:toc}

<details markdown="block">
<summary>How to listen to events</summary>
You can listen to any event by calling a function `os.pullEvent()`,
which will block execution until some event occurs.
You can specify a single event name if you are not interested in other events `os.pullEvent("name_of_the_event")`.

Wrapping the `os.pullEvent()` call to `{}` allows taking all the event's return values as a single table.
```lua
local event = {os.pullEvent()}
```

Using the `table.unpack(table, fromIndex)`, you can unpack a table to multiple values
and pass them as separated parameters to a function.
By specifying the `fromIndex` parameter, you tell which index the unpack should start from 
so it will skip all values before that index.
That is, for example, useful when you want to avoid passing event names to each event function.
```lua
function takeParameters(a, b)
    print(a, b)
end

local myTable = {"valueA", "valueB"}
takeParameters(table.unpack(myTable))
-- prints valueA valueB

takeParameters(table.unpack(myTable, 2))
-- prints nil valueB
```


Now you can process events, for example, like this:
```lua
function onDisconnect(feedback, description)
    -- note that description may be nil when basic interface is used!
    print("Stargate disconnected", feedback, description)
end

while true do
    local event = {os.pullEvent()}
    local eventName = event[1]
    if eventName == "stargate_disconnected" then
        -- start from index 2, skipping the event name on index 1
        onDisconnect(table.unpack(event, 2))
--[[
    elif eventName == "other_event" then
        ...
]]--
    end
end
```
</details>

## Stargate interface
The computer will receive these events whenever an interface is connected to a Stargate and the computer.

<h3 class="h-function">
    <code>stargate_chevron_engaged</code>
</h3>

Fired whenever a **chevron** is engaged.

**Return values**
1. `string` The event name (`stargate_chevron_engaged`)
2. `string` The peripheral name
3. `number` Count of engaged symbols (from `1` to `9`)
4. `number` Engaged chevron (chevron identifier from `0` to `8`)
5. `boolean` `true` if the chevron was engaged for incoming connection, 
`false` if the chevron was locked by dialing this gate
6. `number` Encoded symbol (from `0` to `38` - or `35` for the Universe gate)  

<span class="label label-blue ml-0">Basic Interface</span><span class="label label-green ml-0">Crystal Interface</span>The symbol is present only when engaged for outgoing connection.  
<span class="label label-purple ml-0">Advanced Crystal Interface</span> The symbol is present even for incoming connection.

___

<h3 class="h-function">
    <code>stargate_incoming_wormhole</code>
</h3>

Fired whenever an **incoming** wormhole forms.
The event is fired right **after** the kawoosh end.

**Return values**
1. `string` The event name (`stargate_incoming_wormhole`)
2. `string` The peripheral name
3. `number[]`<span class="label label-purple">Advanced Crystal Interface</span> The connected address

___

<h3 class="h-function">
    <code>stargate_outgoing_wormhole</code>
</h3>

Fired whenever an outgoing Wormhole forms.
The event is fired right **before** the kawoosh start.

**Return values**
1. `string` The event name (`stargate_outgoing_wormhole`)
2. `string` The peripheral name
3. `number[]` The dialed address

___

<h3 class="h-function">
    <code>stargate_disconnected</code>
</h3>

Fired whenever a connection is ended.

**Return values**
1. `string` The event name (`stargate_disconnected`)
2. `string` The peripheral name
3. `number` The recent feedback code <!-- TODO: add feedback code link -->
4. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

___

<h3 class="h-function">
    <code>stargate_reset</code>
</h3>

Fired whenever a Stargate resets.
<!-- TODO: link explaining when a stargate resets -->

**Return values**
1. `string` The event name (`stargate_reset`)
2. `string` The peripheral name
3. `number` The recent feedback code <!-- TODO: add feedback code link -->
4. `string`<span class="label label-green">Crystal Interface</span><span class="label label-purple ml-0">Advanced Crystal Interface</span>A description of the feedback

___

<h3 class="h-function">
    <code>stargate_deconstructing_entity</code>
</h3>

Fired whenever an entity enters the wormhole.

**Return values**
1. `string` The event name (`stargate_deconstructing_entity`)
2. `string` The peripheral name
3. `string` The type of the entity (e.g. `minecraft:pig`)
4. `string` The display name of the entity (e.g. player's name, name set by a nametag or a default mob name)
5. `string` UUID of the entity
6. `boolean` `true` when the entity was destroyed by stepping through the wrong end of the wormhole, `false` otherwise.

___

<h3 class="h-function">
    <code>stargate_reconstructing_entity</code>
</h3>

Fired whenever an entity exits the wormhole.

**Return values**
1. `string` The event name (`stargate_reconstructing_entity`)
2. `string` The peripheral name
3. `string` The type of the entity (e.g. `minecraft:pig`)
4. `string` The display name of the entity (e.g. player's name, name set by a nametag or a default mob name)
5. `string` UUID of the entity

___

<h3 class="h-function">
    <code>stargate_message_received</code>
</h3>

Fired whenever a Stargate receives a message sent by the `sendStargateMessage(message)` function

**Return values**
1. `string` The event name (`stargate_message_received`)
2. `string` The peripheral name
3. `string` The message that was sent from an interface connected to the Stargate on the other end of the connection.

**See also**
- [sendStargateMessage(message)]({{ site.baseurl }}/computercraft/stargate_interface/#sendStargateMessage)

___

## Transceiver
The computer will receiver these events whenever a [transceiver]({{ site.baseurl }}/computercraft/transceiver/) is connected as a peripheral.

___

<h3 class="h-function">
    <code>transceiver_transmission_received</code>
</h3>

Fired whenever the [transceiver]({{ site.baseurl }}/computercraft/transceiver/) receives a transmission on the configured frequency.

**Return values**
1. `string` The event name (`transceiver_transmission_received`)
2. `string` The peripheral name
3. `number` The configured frequency
4. `number` The received identification code (IDC)
5. `boolean` Whether the code matches the configured IDC on the transceiver
