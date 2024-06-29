package com.hollingsworth.arsnouveau.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.phys.Vec2;

public class ANCodecs {

    public static Codec<Vec2> VEC2 = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("x").forGetter(v -> v.x),
            Codec.FLOAT.fieldOf("y").forGetter(v -> v.y)
    ).apply(instance, Vec2::new));
}
