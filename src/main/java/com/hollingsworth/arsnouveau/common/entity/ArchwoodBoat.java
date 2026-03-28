package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ArchwoodBoat extends Boat {
    private static final EntityDataAccessor<Integer> MOD_BOAT_TYPE =
        SynchedEntityData.defineId(ArchwoodBoat.class, EntityDataSerializers.INT);

    public ArchwoodBoat(EntityType<? extends Boat> entityType, Level level) {
        super(entityType, level, () -> ItemsRegistry.ARCHWOOD_BOAT.get());
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



    public void setArchwoodVariant(ArchwoodBoat.Type type) {
        this.entityData.set(MOD_BOAT_TYPE, type.ordinal());
    }

    public ArchwoodBoat.Type getArchwoodVariant() {
        return ArchwoodBoat.Type.byId(this.entityData.get(MOD_BOAT_TYPE));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("ArchwoodType", this.getArchwoodVariant().getName());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        if (compound.keySet().contains("ArchwoodType")) {
            this.setArchwoodVariant(ArchwoodBoat.Type.getByName(compound.getStringOr("ArchwoodType", "")));
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
