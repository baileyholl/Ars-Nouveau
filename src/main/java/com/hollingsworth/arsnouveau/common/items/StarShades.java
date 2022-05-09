package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.ICosmeticItem;
import net.minecraft.world.phys.Vec3;

public class StarShades extends ModItem implements ICosmeticItem {

    public StarShades(String registryName) {
        super(registryName);
    }

    //translation applied to the renderer
    @Override
    public Vec3 getTranslations() {
        return new Vec3(0, 0.25, -0.155);
    }

    //scaling applied to the renderer
    @Override
    public Vec3 getScaling() {
        return new Vec3(0,0,0);
    }

}
