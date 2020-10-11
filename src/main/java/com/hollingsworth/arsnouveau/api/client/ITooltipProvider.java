package com.hollingsworth.arsnouveau.api.client;

import java.util.List;

public interface ITooltipProvider {

    /**
     * A list of tool tips to render on the screen when looking at this target.
     */
    List<String> getTooltip();

}
