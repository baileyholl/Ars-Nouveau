package com.hollingsworth.arsnouveau.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public interface ICosmeticItem {

    Vec3 defaultScaling = new Vec3(1.0, 1.0, 1.0);

    //bone model where the item is renderer, all animations that include the bone will be synced with the item
    @NotNull
    default String getBone(LivingEntity entity) {
        return "head";
    }

    /**
     * translate relative to bone pivot specified by {@link ICosmeticItem#getBone}
     */
    default Vec3 getTranslations() {
        return Vec3.ZERO;
    }

    default Vec3 getScaling() {
        return defaultScaling;
    }

    /**
     * Entity Sensitive
     * translate relative to bone pivot specified by {@link ICosmeticItem#getBone}
     */
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
