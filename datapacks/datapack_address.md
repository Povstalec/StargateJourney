---
title: Addresses
nav_order: 5
parent: Datapacks and Resourcepacks
---

# Datapack Address
Datapack Addresses are the form of addresses used by [galaxies](galaxy.md) 
and [solar_systems](solar_system.md) to represent addresses.
## Example
```json
{
  "randomizable": true,
  "address": [18, 20, 1, 15, 14, 7, 19]
}
```
## Fields
- `randomizable`: whether the address will be randomized when 
CommonStargateNetworkConfig.random_addresses_from_seed is enabled.
- `address`: an array of integers for the value of the address. It does not
include the Point of Origin.