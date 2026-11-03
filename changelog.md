# Changelog

Fixes lectern inserting into full storage causing massive TPS loss

Generally improves lectern performance for inserting items into all storage types

Fixes repositories who previously had an item in them getting priority over repositories that had filter scrolls,
causing filters to be ignored

Fixes edge cases that could cause item loss when inserting into a full lectern via crafter or other modded blocks

Fixes lectern UI components not properly refreshing when resizing your screen

Fixes crash when opening spell book while it was on the spell styles menu and the spellbook was removed and readded to
your inventory

Fixes containment rituals capture radius to be 3 blocks instead of 5

Fixes planarium not properly removing render data when logging out or leaving a world

Fixes evaporate checking if a block CAN be waterlogged instead of IS waterlogged (alexthw)