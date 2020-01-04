package com.hollingsworth.craftedmagic.spell;

import net.minecraft.util.math.RayTraceResult;

public interface ISpellCallback {
    void resolveEffect(RayTraceResult result);
}
