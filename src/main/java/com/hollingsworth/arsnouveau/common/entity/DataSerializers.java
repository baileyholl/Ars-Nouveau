package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

public class DataSerializers {
    public static final EntityDataSerializer<Vec3> VEC3 = EntityDataSerializer.simple((buffer, vec) -> {
        buffer.writeDouble(vec.x);
        buffer.writeDouble(vec.y);
        buffer.writeDouble(vec.z);
    }, buffer -> new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
}
