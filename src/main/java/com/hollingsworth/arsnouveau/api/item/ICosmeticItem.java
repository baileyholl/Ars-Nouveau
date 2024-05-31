package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface ICosmeticItem {

    //bone model where the item is renderer, all animations that include the bone will be synced with the item
    default String getBone() {
        return "head";
    }

    //translate relative to bone pivot
    Vec3 getTranslations();

    Vec3 getScaling();

    //Entity Sensitive
    //translate relative to bone pivot
    default Vec3 getTranslations(LivingEntity entity) {
        return getTranslations();
    }

    default Vec3 getScaling(LivingEntity entity) {
        return getScaling();
    }

    /**
     * @param entity check if is compatible with the cosmetic item
     */
    default boolean canWear(LivingEntity entity) {
        return true;
    }

    /**
     * select the camera transform, default is GROUND. You can change this with HEAD and tweak that display setting
     * (with slider or forge separate prospective) to make the item scale/translations not influence how
     * the items look in inventory and when it's dropped.
     */
    @OnlyIn(Dist.CLIENT)
    default ItemDisplayContext getTransformType() {
        return ItemDisplayContext.GROUND;
    }

}
