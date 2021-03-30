package com.hollingsworth.arsnouveau.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;

public interface IFollowingSummon {
    DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(TameableEntity.class, DataSerializers.OPTIONAL_UNIQUE_ID);
    World getWorld();

    PathNavigator getPathNav();

    LivingEntity getSummoner();

    MobEntity getSelfEntity();
}
