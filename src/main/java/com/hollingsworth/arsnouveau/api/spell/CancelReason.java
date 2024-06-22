package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public record CancelReason(ResourceLocation id) {
    public static CancelReason FILTER_FAILED = new CancelReason(ArsNouveau.prefix("filter_failed"));
    /**
     * When a spell is canceled because it will now resolve differently. Used for cases like Orbit or Delay.
     */
    public static CancelReason NEW_CONTEXT = new CancelReason(ArsNouveau.prefix("new_context"));

    /**
     * If the spell was terminated and should not be continued. Used for magic cancellation or when an entirely new context will be substituted.
     */
    public static CancelReason TERMINATED = new CancelReason(ArsNouveau.prefix("terminated"));
}
