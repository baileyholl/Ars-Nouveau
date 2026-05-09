# Changelog

Fixes memory leak with Planarium

Planarium updates are now batched with a configurable value, preventing frequently updated planariums from causing lag.

Casting rune on an entity will now resolve the spell on it directly (Qther)

Fixes Cut not respecting player UUIDs for claims (Qther)

Improves performance for inserting items with the storage lectern (Qther)

Fixes crash with Wixie Cauldrons with misbehaving modded or custom recipes

Name will now remove custom names if used on a target with an empty spell name. (Qther)

Fixes warp portals loading the origin dimension instead of the destination when teleporting. (Qther)

Fixes rotating spell turrets using the wrong direction for glyphs (Alexthw)