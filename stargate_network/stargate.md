---
title: Stargate
nav_order: 0
parent: Stargate Network
---

{: .note }
This content was migrated from the old wiki and is awaiting an update.

1. Table of Contents
{:toc}

# Stargate

## Generation
By default, two Stargates generate in the Overworld and then one Stargate generates in each of the Dimensions added by Stargate Journey. Other Dimensions like Nether and End are still reachable through the Stargate Network, but they do not have their own Stargates by default.

## Symbols
Stargates use Addresses to dial other Stargates. These Addresses are formed by Symbols and while there dozens upon dozens of Symbols in the mod, all of them fit under one of these two categories:

### Regular Symbols
Regular Symbols are Symbols numbered 1, 2, 3,... which actually form an Address. Different Solar Systems will generaly share regular Symbols if they're located in the same Galaxy. Even though different Galaxies may use different sets of Symbols, those Symbols still represent numbers starting from 1, 2, 3,... and as such, they will work the exact same way in different Galaxies.

### Point Of Origin
The Point of Origin is a special kind of symbol with the number 0. Each Solar System usually has a unique Point Of Origin representing it (Though they may look different, all of them share the number 0).

## Natural Generation
Only one Stargate will generate per dimension by default(the only exception to this is Overworld, which will generate 2 Stargates in the form of a Buried Stargate and a Stargate Pedestal), however, this can be changed with the use of datapacks.

## Variants

### Universe Stargate
* Generation: 1
* Only has 36 Symbols (Can't dial Symbols above 35)
* Always has the same set of Symbols, that being the Universal Symbols.

### Milky Way Stargate
* Generation: 2
* Only has 39 Symbols (Can't dial Symbols above 38)
* Can be dialed Manually: [Manual Dialing Tutorial]({{ site.baseurl }}/survival/dialing/#manual-dialing-with-redstone)
* When first placed, the Stargate will adapt the set of Symbols belonging to the Solar System it was placed in uses. It will remember those Symbols when broken.

### Pegasus Stargate
* Generation: 3
* Only displays 36 Symbols at a time, but can actually dial any Symbol
* When placed, it will use the set of Symbols belonging to the Solar System it is currently in. When broken and placed in another Solar System, it will switch to that Solar System's Symbols.

## Feedback Codes
Whenever Stargate performs any action that may succeed or fail, it will return a Feedback Code, which may be used to better understand what caused the success or failure. The codes are return in the form of an integer, with error codes being negative integers and success codes being positive.

* `0` NONE

### Success Codes
* `1` SYMBOL_ENCODED
* `2` CONNECTION_ESTABILISHED_SYSTEM_WIDE
* `3` CONNECTION_ESTABILISHED_INTERSTELLAR
* `4` CONNECTION_ESTABILISHED_INTERGALACTIC
* `7` CONNECTION_ENDED_BY_DISCONNECT
* `8` CONNECTION_ENDED_BY_POINT_OF_ORIGIN
* `9` CONNECTION_ENDED_BY_NETWORK
* `10` CONNECTION_ENDED_BY_AUTOCLOSE
* `11` CHEVRON_RAISED

### Error Codes
* `-1` UNKNOWN_ERROR
* `-2` SYMBOL_IN_ADDRESS
* `-3` SYMBOL_OUT_OF_BOUNDS
* `-4` INCOPLETE_ADDRESS
* `-5` INVALID_ADDRESS
* `-6` NOT_ENOUGH_POWER
* `-7` SELF_OBSTRUCTED
* `-8` TARGET_OBSTRUCTED
* `-9` SELF_DIAL
* `-10` SAME_SYSTEM_DIAL
* `-11` ALREADY_CONNECTED
* `-12` NO_GALAXY
* `-13` NO_DIMENSIONS
* `-14` NO_STARGATES
* `-15` EXCEEDED_CONNECTION_TIME
* `-16` RAN_OUT_OF_POWER
* `-17` CONNECTION_REROUTED
* `-18` WRONG_DISCONNECT_SIDE
* `-19` STARGATE_DESTROYED
* `-20` TARGET_STARGATE_DOES_NOT_EXIST
* `-21` CHEVRON_ALREADY_RAISED
* `-22` CHEVRON_ALREADY_LOWERED