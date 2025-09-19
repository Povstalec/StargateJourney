---
title: Stargate Network
nav_order: 70
has_children: true
---

{: .note }
This content was migrated from the old wiki and is awaiting an update.

1. Table of Contents
{:toc}

# Stargate Network

# Addresses

There are 3 kinds of Addresses:
* `7-chevron Addresses` can be used to dial a Solar System inside the same galaxy. If a Solar System is a part of multiple Galaxies, it can have a different 7-chevron Address in each Galaxy.
* `8-chevron Addresses` can be used to dial any Solar System outside of the galaxy. Each Solar System has only one 8-chevron Address.
* `9-chevron Addresses` can be used to dial specific Stargates. Each Stargate has a unique 9-chevron Address, which can be used to dial it anywhere, even within the same dimension or when it's moved to another Solar System where its 7 and 8-chevron Address changes.

## Preferred Stargates
When dialing a Solar System with 7-chevron or 8-chevron Addresses, there may be more than one Stargate in said Solar System. The Stargate Network will choose a Preferred Stargate based on the following criteria in this order of importance:
1. Has DHD - It is important that the traveler can come back easily, this means the Preferred Stargate should have a functional DHD.
2. Stargate Generation - It is preferable for the Stargate to be of a newer generation. As such, the 3rd Generation Pegasus Stargates will be chosen over the 2nd Generation Milky Way Stargates and Milky Way Stargates will be chosen over the 1st Generation Universe Stargates.
3. Number of times used - If one Stargate has been used more times than another, it is very likely that travelers will wish to continue using it over the less used Stargate.

## Getting an Address
To get an Address of a Dimension, simply use one of the following commands:
* `/sgjourney stargateNetwork address` to get the 7-chevron Address of a Dimension in the current Galaxy.
* `/sgjourney stargateNetwork extragalacticAddress` to get the 8-chevron Address of a Dimension anywhere.
* Find a **Naturally Generated** Cartouche.
> Cartouches have Cartouche Tables similar to Chests with Loot Tables. You don't expect Chests you crafted yourself to have any Loot and just like that, Cartouches you place yourself won't have any interesting Addresses and will default to the Addresss of the Dimensions you've placed them in.
* Give yourself a Cartouche with a Cartouche Table through commands, for example: `/give @p sjgourney:sandstone_cartouche{BlockEntityTag:{AddressTable:"sgjourney:cartouche_buried_gate"}}`
> You can take a look at the [Cartouche Tables](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/address_table) currently in the mod

# Connections
Estabilishing different connections requires energy (currently disabled by default, can be enabled in the config) and each type of connection has a different energy cost. There are 3 kinds of Connections you can estabilish when using a Stargate:
* `System-wide connection` is a connection between two Stargates which are either in the same Dimension, or in two Dimensions which are both part of the same Solar System
* The only way to estabilish a System-wide connection is through a 9-chevron Address
* Default energy cost of the connection is 50 000 FE.
* Address will always take the form of -A-B-C-D-E-F-G-H-
> * An example of this would be using a Stargate that's on Abydos to dial another Stargate on Abydos
> * (Example Address: -32-14-5-19-6-18-31-35)

* `Interstellar connection` is a connection between two Stargates in the same Galaxy
* A connection can be estabilished with either an 7-chevron, 8-chevron or a 9-chevron Address
* Default energy cost of the connection is 100 000 FE
* Address can take the form of -A-B-C-D-E-F-G-H-,  -A-B-C-D-E-F-G- or  -A-B-C-D-E-F-
> * An example of this would be dialing Abydos from Earth
> * (Example Address: -26-6-14-31-11-29-)

* `Intergalctic connection` is a connection between two Stargates in different Galaxies, or with a Stargate located outside of any Galaxy
* A connection can be estabilished with an 8-chevron or a 9-chevron Address
* Default energy cost of the connection is 100 000 000 000 FE
* Address can take a form that looks like this -A-B-C-D-E-F-G- or this -A-B-C-D-E-F-G-H-
> * An example of this would be dialing Lantea from Earth
> * (Example Address: -18-20-1-15-14-7-19-)

# Stellar Update
Stellar updates are essentially updates to the Stargate Network which ensure the Stargate Network works properly. During a Stellar update, all Stargates are disconnected and the Stargate Network is regenerated from scratch, including any changes to the world. For instance, if you were to add a new Dimension to your World, the Dimension would not be registered by the Stargate Network until the next Stellar Update.
* A Stellar Update can be activated in multiple ways:
1. Stargate Network update (newer versions of the mod have updates to the Stargate Network)
2. Running the **/sgjourney stargateNetwork forceStellarUpdate** command
3. Outside of the game by deleting the `sgjourney-stargate_network` file from the World's `data` folder

# Versions
Stargate Network has gone through multiple versions, each with slightly different mechanics.

## Version 0
* **First used in:** `Stargate Journey 0.4.0`
* **Last used in:** `Stargate Journey 0.5.1`
> * Version 0 used a Primary Stargate system, which would designate the oldest Stargate in a given Solar System as the Primary Stargate, which would receive all incoming connections.

### Version 1
* **First used in:** `Stargate Journey 0.5.2`
* **Last used in:** `Stargate Journey 0.5.4`
* Changes:

### Version 2
* **First used in:** `Stargate Journey 0.6.0`
* **Last used in:** `Stargate Journey 0.6.0`
* Changes:
> * Removed the Primary Stargate system in favor of Preferred Stargate System.

### Version 3
* **First used in:** `Stargate Journey 0.6.1`
* **Last used in:** `Stargate Journey 0.6.2`
* Changes:
> * Nether Address in Milky Way is changed from -8-7-6-5-4-3- to -27-23-4-34-12-28-
> * End Address in Milky Way is changed from -1-2-3-4-5-6- to -13-24-2-19-3-30-
> * End Address in Pegasus is changed from -1-2-3-4-5-6- to -19-30-6-13-3-24-

### Version 4
* **First used in:** `Stargate Journey 0.6.3`
* **Last used in:** `Stargate Journey 0.6.5`

### Version 5
* **First used in:** `Stargate Journey 0.6.6`
* Changes:
> * Dialed Stargates now lock Chevrons one by one until a wormhole is formed

### Version 6

### Version 7
* **First used in:** `Stargate Journey 0.6.10`
* **Last used in:** Still used as of the newest version (Stargate Journey 0.6.10)
* Changes:
> * Changed Extragalactic Addresses of Abydos and Chulak