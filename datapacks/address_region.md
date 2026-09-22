---
title: Address Region
nav_order: 2
parent: Datapacks and Resourcepacks
---

# address_region
Address Regions are essentially a way to group Space Locations together, allowing them to share information, like their 7 and 8-Chevron Address.
You can imagine an Address Region to be a star system and the Space Locations it contains to be the planets inside this star system.


Solar System files are located in the `data/<namespace>/sgjourney/address_region` folder.  
`<namespace>` is the identificator of the given Mod/Datapack. For example, for Stargate Journey the `<namespace>` would be `sgjourney`,
making the whole path `data/sgjourney/sgjourney/address_region`.

# Example Address Region

Address Regions for [Stargate Journey](https://github.com/Povstalec/StargateJourney/tree/main/src/main/resources/data/sgjourney/sgjourney/address_region)

```json
{
	"name": "solar_system.sgjourney.end",
	"point_of_origin": "sgjourney:pontem",
	"symbols": "sgjourney:end",
	"symbol_prefix": 1,
	"extragalactic_address": {"symbols": [18, 24, 8, 16, 7, 35, 30], "randomizable": true},
	"galactic_addresses":
	{
		"sgjourney:pegasus": {"address": {"symbols": [14, 30, 6, 13, 17, 23], "randomizable": true}},
		"sgjourney:milky_way": {"address": {"symbols": [13, 24, 2, 19, 3, 30], "randomizable": true}}
	}
}
```
## Fields
- `name`: a translation key serving as the display name of the Address Region,
the translation itself is defined inside [lang files](https://minecraft.wiki/w/Resource_pack#Language).
- `point_of_origin`: Local [Point of Origin]({{ site.baseurl }}datapacks/point_of_origin) of this Address Region, any placed Stargates with the "Local Point of Origin" tooltip will default to it.
- `symbols`: Local [Symbols]({{ site.baseurl }}datapacks/symbols) of this Address Region, any placed Stargates will default to it.
- `symbol_prefix`: the symbol that will be used as the first symbol of the extragalactic address if the address is randomized.
- `extragalactic_address`: a [datapack address]({{ site.baseurl }}datapacks/datapack_address) an integer array that forms the Address of the Address Region as well as whether its Address can be randomized (if the config allows it). Each number in the Address array must be unique. The integers forming an Address don't actually represent any physical location, they act sort of as a phone number.
- `galactic_addresses` a map of Addresses the Address Region has in different Galaxies and whether they are randomizable (if the config allows it).