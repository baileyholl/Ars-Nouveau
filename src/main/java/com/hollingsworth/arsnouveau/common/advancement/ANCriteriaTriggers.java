package com.hollingsworth.arsnouveau.common.advancement;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class ANCriteriaTriggers {
    public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, MODID);
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> POOF_MOB = register("poof_mob");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> FAMILIAR = register("familiar");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CHIMERA_EXPLOSION = register("chimera_explosion");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CREATE_PORTAL = register("portals");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> PRISMATIC = register("prismatic");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> SHRUNK_STARBY = register("shrunk_starby");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> CAUGHT_LIGHTNING = register("catch_lightning");
    public static final DeferredHolder<CriterionTrigger<?>, PlayerTrigger> TIME_IN_BOTTLE = register("time_in_bottle");

    public static void rewardNearbyPlayers(PlayerTrigger criteria, ServerLevel level, BlockPos pos, int radius) {
        AABB aabb = new AABB(pos).inflate(radius);
        for (ServerPlayer player : level.players()) {
            if (aabb.contains(player.getX(), player.getY(), player.getZ())) {
                criteria.trigger(player);
            }
        }
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, PlayerTrigger> register(String pName) {
        return register(pName, new PlayerTrigger());
    }

    public static <T extends CriterionTrigger<?>> DeferredHolder<CriterionTrigger<?>, T> register(String pName, T pTrigger) {
        return TRIGGERS.register(pName, () -> pTrigger);
    }

    public static void init() {
    }

    public static Criterion<?> createCriterion(DeferredHolder<CriterionTrigger<?>, PlayerTrigger> holder) {
        return holder.get().createCriterion(new PlayerTrigger.TriggerInstance(Optional.empty()));
    }
}
