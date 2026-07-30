---
title: Galaxy
nav_order: 10
parent: Datapacks and Resourcepacks
---

# galaxy
Galaxies are in the data/sgjourney/galaxy folder and tell sgjourney 
various properties of a given galaxy as well as what [solar_systems](solar_system.md)
should be in the galaxy.
## Example Galaxy
```json
{
  "name": "galaxy.example.andromeda",
  "type": "sgjourney:medium_galaxy",
  "default_symbols": "sgjourney:pegasus",
  "solar_systems": [
    {
      "solar_system": "sgjourney:lantea",
      "address": {
        "randomizable": true,
        "address": [29, 5, 17, 34, 6, 12]
      }
    },
    {
      "solar_system": "dimensioncompat:twilight_forest",
      "address": {
        "randomizable": true,
        "address": [12, 6, 32, 17, 23, 2]
      }
    }
  ]
}
```
## Fields
- `name`: a translation key corresponding to the display name of the galaxy
- `type`: a [galaxy_type](galaxy_type.md)
- `default_symbols`: a [symbol_set](symbol_set.md) corresponding to the default 
symbols of solar_systems generated in the galaxy.
- `solar_systems`: an array of solar_systems and their intragalactic addresses.
  - `solar_system`: a solar_system
  - `address`: a [datapack address](datapack_address.md)