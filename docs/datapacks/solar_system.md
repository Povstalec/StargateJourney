# solar_system
Solar systems are in the data/sgjourney/solar_system folder and are used to tell
sgjourney what dimensions should be in a given solar system.
## Example Solar System
```json
{
    "name": "solar_system.sgjourney.lantea",
    "symbols": "sgjourney:lantea",
    "symbol_prefix": 18,
    "extragalactic_address": {
        "randomizable": true,
        "address": [18, 20, 1, 15, 14, 7, 19]
    },
    "point_of_origin": "sgjourney:subido",
    "dimensions": [
        "sgjourney:lantea"
    ]
}
```
## Fields
- `name`: a translation key corresponding to the display name of the solar system.
- `symbols`: a [symbol_set](/docs/datapacks/symbol_set.md) which will be used
by gates in this solar system for their symbol rings.
- `symbol_prefix`: the symbol that will be used as the first chevron of the 
extragalactic address if the address is randomized.
- `extragalactic_address`: a [datapack address](/docs/datapacks/datapack_address.md)
corresponding to the address of the solar system as well as whether its address 
can be randomized.
- `point_of_origin`: the symbol that will be used as the point of origin for any
gates generated in the dimension.
- `dimensions`: an array of dimensions that will be part of the solar system.