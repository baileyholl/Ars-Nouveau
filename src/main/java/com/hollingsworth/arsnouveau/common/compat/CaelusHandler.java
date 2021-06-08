package com.hollingsworth.arsnouveau.common.compat;

import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import top.theillusivec4.caelus.api.CaelusApi;

public class CaelusHandler {

    public static void setFlying(PlayerEntity entity){
        ModifiableAttributeInstance attributeInstance = entity.getAttribute(CaelusApi.ELYTRA_FLIGHT.get());
        if(attributeInstance != null && !attributeInstance.hasModifier(CaelusApi.ELYTRA_MODIFIER))
            attributeInstance.addTransientModifier(CaelusApi.ELYTRA_MODIFIER);
    }
}
