---
title: All The Mods 9
parent: Modpacks
nav_order: 60
---

# All the Mods 9
Given the amount of questions regarding [All The Mods 9](https://www.curseforge.com/minecraft/modpacks/all-the-mods-9)
there are because Stargate Journey works slightly differently with that modpack,
it seems there is a need for a separate guide that fully explains all differences present.

# Progression
Stargate Journey progression in ATM9 is the same as normally,
except with the slight difference that you can find a Stargate Pedestal by just randomly wandering around for long enough.
You can still find the Buried Stargate through a map you get as a final trade from the Archeologist Villager, but you now have more options.

{: .highlight }
On older versions of ATM9, the Map that leads to the Buried Stargate could instead lead to one of the many Stargate Pedestals that Common Stargates Datapack generates.

# World Generation
## Stargates
All The Mods 9 uses the [Common Stargates Datapack](https://www.curseforge.com/minecraft/texture-packs/common-stargates),
which changes the world generation in such a way that Stargates generate approximately 64 chunks apart on average,
as opposed to once per Dimension (which is how Stargate Journey does it normally).

![Stargate pedestal]({{ site.baseurl }}/assets/img/blocks/technological/milkyway_pedestal.png)

## Cartouches
Cartouches with the Abydos Address will generate around the Overworld:

![Stone cartouche structure]({{ site.baseurl }}/assets/img/stone_cartouche.png)



[//]: # (# Troubleshooting)

[//]: # (## Updating from an older ATM9 version crashes, presumably due to problems caused by Stargate Journey)

[//]: # (It's probably not Stargate Journey causing it, but rather an old version of the Common Stargates Datapack. )

[//]: # (Here is a guide on fixing it created by tehgreatdoge &#40;edited a bit, since it was written in the context of Discord&#41;:  )

[//]: # (_This tutorial assumes that you have 0 knowledge about how KubeJS works._)

[//]: # ()
[//]: # (1. To get started, open your Minecraft instance’s folder. )

[//]: # (   Then go to `./kubejs/data`. )

[//]: # (   If you see a folder labeled sgjourney, this is &#40;probably&#41; the right tutorial for you.)

[//]: # ()
[//]: # (2. Now that you have the folder, you will need to verify that there aren’t any other important changes made by the modpack. )

[//]: # (   To do so, compare your file structure against the attached image. While this won’t 100% guarantee that everything will be alright, it should help prevent any issues.)

[//]: # ()
[//]: # (3. If your file structure doesn’t exactly match, please open a post in on the Discord Server bugs-and-suggestions channel with the following info: Modpack and modpack version, sgjourney version, common stargates version.)

[//]: # ()
[//]: # (4. Now that we have verified that the folders match, go ahead and delete the sgjourney folder.)

[//]: # ()
[//]: # (5. Now, download the latest version of common stargates for your Minecraft version and open it. Inside, there should be a data folder. )

[//]: # (   Copy the `common_stargates` and sgjourney folder from it and paste it into the `./kubejs/` data folder. )

[//]: # (   You did it! If this doesn’t work, please create a post in bugs-and-suggestions channel on the Discord Server with the previously mentioned info.)
