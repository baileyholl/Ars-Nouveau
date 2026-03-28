package com.hollingsworth.arsnouveau.client.renderer;

import software.bernie.geckolib.constant.dataticket.DataTicket;

/**
 * Singleton DataTickets for Ars Nouveau GeckoLib render state data.
 * Must be singletons because ArsEntityRenderState uses Reference2ObjectOpenHashMap (identity keys).
 */
public class ANDataTickets {
    public static final DataTicket<String> DRYGMY_COLOR = DataTicket.create("ars_nouveau_drygmy_color", String.class);
    public static final DataTicket<String> DYE_COLOR = DataTicket.create("ars_nouveau_dye_color", String.class);
}
