package com.hollingsworth.arsnouveau.api.item.inv;

import com.hollingsworth.arsnouveau.common.items.ItemScroll;

/**
 * Returns the preference of the given stack, and whether it is valid for a given action.
 * If Valid is false, the action should not be performed.
 */
public record InteractResult(ItemScroll.SortPref sortPref, boolean valid) {
}
