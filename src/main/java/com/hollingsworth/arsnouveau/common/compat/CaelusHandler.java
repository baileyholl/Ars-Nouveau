package com.hollingsworth.arsnouveau.common.compat;

import com.illusivesoulworks.caelus.api.CaelusApi;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;


public class CaelusHandler {

    public static void setFlying(Player entity) {
        AttributeInstance attributeInstance = entity.getAttribute(CaelusApi.getInstance().getFallFlyingAttribute());
        if (attributeInstance != null && !attributeInstance.hasModifier(CaelusApi.getInstance().getElytraModifier().id()))
            attributeInstance.addTransientModifier(CaelusApi.getInstance().getElytraModifier());
    }
}
