---
title: Archeology Table
parent: Blocks
nav_order: 50
---

![Archeology table and villager]({{ site.baseurl }}/assets/img/blocks/functional/archeology_table_villager.png)
{: .max-width-512 }

## Archeology Table
{% minecraft_recipe_crafting item:"sgjourney:archeology_table" %}

Can be crafted with a [golden idol](/blocks/golden-idol/) and used as a [villager workstation](https://minecraft.wiki/w/Villager#Job_site_blocks).

## Archeologist Villager
You can trade an __archeologist map__ on the [first villager's level](https://minecraft.wiki/w/Trading#Level) (Novice)
and a [__map to Chappa'ai__]({{ '/survival/finding-gate/#the-map-to-chappaai' | absolute_url }}) on the [last level](https://minecraft.wiki/w/Trading#Level) (Master).

{: .note }
> __Archeologist map__ leads to a Goa'uld temple.  
> [__Map to Chappa'ai__]({{ '/survival/finding-gate/#the-map-to-chappaai' | absolute_url }}) leads to a buried Stargate.

[//]: # (a simple css limited only to this table to set image size)
<style>
.img-max-height-16 img {
  max-height: 16px;
}
</style>

<table class="text-center img-max-height-16">
  <thead>
    <tr><th colspan="8">Archeologist</th></tr>
    <tr>
        <th>Villager Level</th>
        <th>Item wanted</th>
        <th>Item given</th>
        <th>Trades in stock</th>
        <th>Price multiplier</th>
        <th>Villager XP</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th rowspan="3">Novice</th>
      {%- include components/survival/villager_table_td.html amount="20" item="Paper" -%}
      {%- include components/survival/villager_table_td.html amount="1" item="Emerald" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      <td>1 x <a href="{{ '/blocks/golden-idol/' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/golden_idol.png' | absolute_url }}" /> Golden idol</a></td>
      {%- include components/survival/villager_table_td.html amount="5" item="Emerald" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="8" item="Emerald" -%}
      <td>1 x <img class="vertical-text-bottom" src="https://minecraft.wiki/images/Invicon_Map.png" /> Archeologist's Map</td>
      <td>1</td>
      <td>-</td>
      <td>80</td>
    </tr>
    <tr>
      <th rowspan="3">Apprentice</th>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      <td>1 x <a href="https://minecraft.wiki/w/Compass"><img class="vertical-text-bottom" src="https://minecraft.wiki/images/Invicon_Compass.gif" /> Compass</a></td>
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      {%- include components/survival/villager_table_td.html amount="1" item="Writable Book" item_id="Book_and_Quill" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="3" item="Gold Ingot" item_id="Gold_Ingot" -%}
      {%- include components/survival/villager_table_td.html amount="1" item="Emerald" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      <td rowspan="3">Journeyman</td>
      {%- include components/survival/villager_table_td.html amount="3" item="Emerald" -%}
      <td>4 x <a href="{{ '/blocks/fire-pit/' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/fire_pit.png' | absolute_url }}" /> Fire pit</a></td>
      <td>1</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      <td>3 x <a href="{{ '/blocks/building-blocks/#sandstone-hieroglyphs' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/sandstone_hieroglyphs.png' | absolute_url }}" /> Sandstone Hieroglyphs</a></td>
      {%- include components/survival/villager_table_td.html amount="1" item="Emerald" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      <td>3 x <a href="{{ '/blocks/building-blocks/#sandstone-with-lapis' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/sandstone_with_lapis.png' | absolute_url }}" /> Sandstone with Lapis</a></td>
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      <td rowspan="4">Expert</td>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      <td>1 x <a href="{{ '/blocks/symbol-block' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/stone_symbol.png' | absolute_url }}" /> Stone Symbol</a></td>
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      <td>1 x <a href="{{ '/blocks/symbol-block' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/sandstone_symbol.png' | absolute_url }}" /> Sandstone Symbol</a></td>
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="4" item="Emerald" -%}
      <td>1 x <a href="{{ '/blocks/symbol-block' | absolute_url}}"><img class="vertical-text-bottom" src="{{ '/assets/img/items/crafting/sgjourney/red_sandstone_symbol.png' | absolute_url }}" /> Red Sandstone Symbol</a></td>
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      {%- include components/survival/villager_table_td.html amount="4" item="Bone" -%}
      {%- include components/survival/villager_table_td.html amount="1" item="Emerald" -%}
      <td>4</td>
      <td>9%</td>
      <td>12</td>
    </tr>
    <tr>
      <td>Master</td>
      {%- include components/survival/villager_table_td.html amount="8" item="Emerald" -%}
      <td>1 x <a href="{{ '/survival/finding-gate/#the-map-to-chappaai' | absolute_url }}"><img class="vertical-text-bottom" src="https://minecraft.wiki/images/Invicon_Map.png" /> Map to Chappa'ai</a></td>
      <td>1</td>
      <td>-</td>
      <td>80</td>
    </tr>
  </tbody>
</table>
