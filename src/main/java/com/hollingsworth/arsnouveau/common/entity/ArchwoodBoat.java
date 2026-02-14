package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ArchwoodBoat extends Boat {
    private static final EntityDataAccessor<Integer> MOD_BOAT_TYPE =
        SynchedEntityData.defineId(ArchwoodBoat.class, EntityDataSerializers.INT);

    public ArchwoodBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level);
    }

    public ArchwoodBoat(Level level, double x, double y, double z) {
        this(ModEntities.ARCHWOOD_BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setArchwoodVariant(ArchwoodBoat.Type.ARCHWOOD);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOD_BOAT_TYPE, Type.ARCHWOOD.ordinal());
    }

    @Override
    public @NotNull Item getDropItem() {
        return ItemsRegistry.ARCHWOOD_BOAT.get();
    }

    @Override
    public net.minecraft.world.entity.vehicle.Boat.@NotNull Type getVariant() {
        return net.minecraft.world.entity.vehicle.Boat.Type.OAK;
    }

    public void setArchwoodVariant(ArchwoodBoat.Type type) {
        this.entityData.set(MOD_BOAT_TYPE, type.ordinal());
    }

    public ArchwoodBoat.Type getArchwoodVariant() {
        return ArchwoodBoat.Type.byId(this.entityData.get(MOD_BOAT_TYPE));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("ArchwoodType", this.getArchwoodVariant().getName());
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("ArchwoodType", 8)) {
            this.setArchwoodVariant(ArchwoodBoat.Type.getByName(compound.getString("ArchwoodType")));
        }
    }

    public enum Type {
        ARCHWOOD(0, "archwood");

        private final String name;
        private final int id;

        Type(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public String toString() {
            return this.name;
        }

        public static ArchwoodBoat.Type byId(int id) {
            ArchwoodBoat.Type[] types = values();
            if (id < 0 || id >= types.length) {
                id = 0;
            }
            return types[id];
        }

        public static ArchwoodBoat.Type getByName(String name) {
            ArchwoodBoat.Type[] types = values();
            for (ArchwoodBoat.Type type : types) {
                if (type.getName().equals(name)) {
                    return type;
                }
            }
            return types[0];
        }
    }
}
