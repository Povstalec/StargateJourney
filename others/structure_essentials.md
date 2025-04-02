---
nav_exclude: true
---

# Structure essentials

{: .warning }
You have to use Structure Essentials version `4.6` or newer.
([GitHub Issue](https://github.com/someaddons/structureessentials/issues/6))

If you still have problems with Stargate structure generation, you can try to use the config values below (some values from the config are omitted):
```json-doc
{
  "mapSearchRadius": {
    "desc:": "Specifies the maximum radius map items can search for structures. Lowering this value reduces the time structure searches stall the server but decreases the range in which structures are found. Vanilla: 50, Default: 40",
    "mapSearchRadius": 100
  },
  "locateSearchRadius": {
    "desc:": "Sets the search radius for the locate structure command. Vanilla: 100, Default: 110",
    "locateSearchRadius": 110
  },
  "globalSearchRadius": {
    "desc:": "Sets the global maximum structure search radius. The vanilla locate command uses 100. Lowering this value reduces the time structure searches stall the server but decreases the range in which structures are found. Default: 70",
    "globalSearchRadius": 110
  },
  "spacingSeparationModifier": {
    "desc:": "Adjusts the structure spacing (average spawn distance) and separation (minimum spawn distance). Increasing the value makes structures spawn farther apart, while decreasing it makes them spawn closer together. Vanilla Default: 1.0",
    "spacingSeparationModifier": 1.0
  },
  "minimumStructureDistance": {
    "desc:": "Set a minimum distance in blocks between structures generated which prevents structure overlaps(not 100% but close). Not recommended to use higher values, as that may strain the worldgen due to repeated structure retries and can prevent surfaces structures when there is some in a cave below. If you want structures more spaced out than this use the spacing/seperation modifier. Default: 32 blocks, range 16-512",
    "minimumStructureDistance": 32,
    "enabled": false,
    "logOverlaps": false
  },
  "autoBiomeCompat": {
    "desc:": "Automatically analyzes present biomes and adjust structure spawning to include fitting ones. Default: true",
    "autoBiomeCompat": false
  },
  "disableLegacyRandomCrashes": {
    "desc:": "Prevents crashes caused by multithreaded access to thread-specific random number generators. Default: true",
    "disableLegacyRandomCrashes": true
  }
}
```