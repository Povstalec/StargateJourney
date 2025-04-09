---
title: Transceiver & GDO
parent: Stargate Technology
nav_order: 50
---

## Garage Door Opener (GDO)

This device can send an IDC (IDentification Code) on the specified frequency through the nearest Stargate
and report the state of the iris or the shield on the other side of the connection.
On the other side, the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver)
can receive and validate the identification code.

See [Stargate Network / Stargate Iris / Controlling / Remote iris control]({{ site.baseurl }}/stargate_network/stargate_iris/#remote-iris-control)
for redstone example using the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver).

**Usage**
- `Right-click` to check the iris state of the Stargate on **the other side** of the connection.  
  The state is displayed in the action bar.  
  GDO will only report the state of the iris on the other side of the connection.
- `Shift + right-click` to open the GUI

**GUI usage**
- The **code** is displayed on the first line of the display.
- The **editing mode** is displayed as **#** when you are modifying the frequency.  
  When the **#** is absent, you are modifying the identification code.  
  The editing mode can be switched with the **f** button.
- The **transmit button** on the left sends the entered IDC (IDentification Code) on the specified frequency.

To properly use the GDO, you must place the [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver)
and set the frequency and the IDC.
Then, you can open the GDO, enter the same frequency and the IDC as set in the transceiver,
and press the transmit button.
The [transceiver]({{ site.baseurl }}/blocks/technological_blocks/#transceiver) will provide a redstone signal
through a comparator when the received code is correct and will notify connected computers with an event.

Simply right-clicking with the GDO will check the iris/shield state on the other side of the Stargate connection.

![GDO item texture]({{ site.baseurl }}/assets/img/items/functional/gdo.png)
![GDO GUI]({{ site.baseurl }}/assets/img/items/functional/gdo_gui.png)
{: .d-flex .flex-justify-between .flex-align-items-center}
