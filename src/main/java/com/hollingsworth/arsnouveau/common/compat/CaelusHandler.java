package com.hollingsworth.arsnouveau.common.compat;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.caelus.api.CaelusApi;

public class CaelusHandler {

    public static void setFlying(Player entity) {
        AttributeInstance attributeInstance = entity.getAttribute(CaelusApi.getInstance().getFlightAttribute());
        if (attributeInstance != null && !attributeInstance.hasModifier(CaelusApi.getInstance().getElytraModifier()))
            attributeInstance.addTransientModifier(CaelusApi.getInstance().getElytraModifier());
    }
}
