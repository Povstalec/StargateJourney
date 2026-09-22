---
title: Randomizable Addresses
nav_order: 4
parent: Datapacks and Resourcepacks
---

# Randomizable Address
Randomizable Addresses are the form of Address used by Stargate Journey json files,
most notably [Address Regions]({{ site.baseurl }}datapacks/address_region), to represent addresses.
## Example
```json
{
  "symbols": [18, 20, 1, 15, 14, 7, 19],
  "randomizable": true
}
```
## Fields
- `symbols`: an array of integers for the value of the address. It does not
include the Point of Origin.
- `randomizable`: whether the address will be randomized when 
CommonStargateNetworkConfig.random_addresses_from_seed is enabled.