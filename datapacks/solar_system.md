---
title: Solar System
nav_order: 20
has_children: false
parent: Datapacks and Resourcepacks
---

# solar_system
Solar Systems are essentially groups of Dimensions sharing common information, like their 7-Chevron Address.


Solar System files are located in the `data/<namespace>/sgjourney/solar_system` folder.
(<namespace> is the identificator of the given Mod/Datapack, for Stargate Journey the `<namespace>` would be `sgjourney`)
These files allow users to define information for this Solar System.


## Example Solar System
```json
{
	"name": "solar_system.sgjourney.end",
	"symbols": "sgjourney:end",
	"symbol_prefix": 1,
	"extragalactic_address": {"address": [18, 24, 8, 16, 7, 35, 30], "randomizable": true},
	"addresses":
	[
		{"galaxy": "sgjourney:milky_way", "address": {"address": [13, 24, 2, 19, 3, 30], "randomizable": true}},
		{"galaxy": "sgjourney:pegasus", "address": {"address": [14, 30, 6, 13, 17, 23], "randomizable": true}}
	],

	"point_of_origin": "sgjourney:pontem",

	"dimensions":
	[
		"minecraft:the_end"
	]
}
```
## Fields
- `name`: a translation key corresponding to the display name of the solar system,
the translation itself is defined inside [lang files](https://minecraft.wiki/w/Resource_pack#Language).
- `point_of_origin`: the Point of Origin object that defines the Point of Origin texture which will be displayed on all Stargates generated in this Solar System.
- `symbols`: a [Symbols object]({{ site.baseurl }}/datapacks/symbols.md) which specifies the Symbol textures all Stargates generated in this Solar System will display.
- `symbol_prefix`: the symbol that will be used as the first symbol of the extragalactic address if the address is randomized.
- `extragalactic_address`: a [datapack address]({{ site.baseurl }}/datapacks/datapack_address.md) an integer array that forms the Address of the Solar System as well as whether its Address can be randomized if the config allows it. Each number in the Address array must be unique. The integers forming an Address don't actually represent any physical location, they act sort of as a phone number.
- `addresses` a list of addresses the Solar System has in different Galaxies and whether they are randomizable if the config allows it.
- `dimensions`: a list of dimensions which will be considered as part of the Solar System and thus share all of the above defined attributes.