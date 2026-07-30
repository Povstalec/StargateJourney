---
title: Energy Target
parent: Stargate Technology
nav_order: 35
description: "Documentation for Energy Target from the Stargate Journey Minecraft mod."
---

# Energy Target

Energy Target is a specific value that represents an amount of energy up to which a device should be **charged**.

## DHD and Control Panel

Energy Target in **DHD** and **Control Panel** specifies how much energy should be sent to the connected Stargate or Transporter.

As long as the DHD or Control Panel will have **available energy**, they will keep sending it to the Stargate, 
or Transporter respectively, until they are charged **at least to** the specified Energy Target.
Once the Energy Target is reached, **no more** energy will be sent.

The Energy Target in DHD and Control Panels is set using **Energy Crystals**.

![Energy Target in DHD]({{ '/assets/img/stargate-technology/energy_target_dhd.png' | absolute_url }})
![Energy Target in Control Panel]({{ '/assets/img/stargate-technology/energy_target_control_panel.png' | absolute_url }})

## Interface

Energy Target in an **interface** (Basic Interface, Crystal Interface, Advanced Crystal Interface)
specifies up to which amount of energy the **connected device** should be charged.
Once the Energy Target is reached, **no more** energy will be pushed.

The Energy Target in the interface can be set in the **GUI** opened by **right-clicking** the interface with empty hands.  
Alternatively can be set by [computers]({{ '/computercraft/interface/#setEnergyTarget' | absolute_url }}).

![Energy Target in Interface GUI]({{ '/assets/img/stargate-technology/energy_target_interface.png' | absolute_url }})