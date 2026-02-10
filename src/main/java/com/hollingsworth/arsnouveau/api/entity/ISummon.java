package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.IntFunction;

/**
 * For entities summoned by spells.
 */
public interface ISummon extends OwnableEntity {
    // How many ticks the summon has left
    int getTicksLeft();

    void setTicksLeft(int ticks);

    default @Nullable LivingEntity getLivingEntity() {
        return this instanceof LivingEntity ? (LivingEntity) this : null;
    }

    void setOwnerID(UUID uuid);

    default void onSummonDeath(Level world, @Nullable DamageSource source, boolean didExpire) {
        NeoForge.EVENT_BUS.post(new SummonEvent.Death(world, this, source, didExpire));
    }

    default void writeOwner(CompoundTag tag) {
        if (getOwnerUUID() != null)
            tag.putUUID("owner", getOwnerUUID());
    }

    default @Nullable Entity readOwner(ServerLevel world, CompoundTag tag) {
        return tag.contains("owner") ? world.getEntity(tag.getUUID("owner")) : null;
    }

    /*
     * This is a workaround for summon entities that inherit from an entity that override LivingEntity.getOwner() to return a subclass that doesn't include players. Ex. Vex
     * By default it will redirect to the classic getOwner() method, so it's safer to use as getter for general purpose
     * */
    default LivingEntity getOwnerAlt() {
        return getOwner();
    }

    default float getManaReserve() {
        return 50f;
    }

    enum SummonBehavior implements StringRepresentable {
        AGGRESSIVE(0, "aggressive"), // Attack enemies on sight
        DEFENSIVE(1, "defensive"), // Attack only if attacked
        PASSIVE(2, "passive"); // Never attack
        private static final IntFunction<SummonBehavior> BY_ID = ByIdMap.continuous(SummonBehavior::id, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

        public static final Codec<SummonBehavior> CODEC = StringRepresentable.fromEnum(SummonBehavior::values);
        public static final StreamCodec<RegistryFriendlyByteBuf, SummonBehavior> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                SummonBehavior::id,
                SummonBehavior::fromId
        );

        private final int id;
        private final String name;

        SummonBehavior(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int id() {
            return this.id;
        }

        public static SummonBehavior fromId(int id) {
            return BY_ID.apply(id);
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }
    }

    default SummonBehavior getCurrentBehavior() {
        return SummonBehavior.DEFENSIVE;
    }

    default void setCurrentBehavior(SummonBehavior behavior){
    }

    default void reloadGoalsAndTargeting(GoalSelector goalSelector, GoalSelector targetSelector) {
        for (var goal : goalSelector.availableGoals) {
            if (goal == null) continue;
            goalSelector.removeGoal(goal.getGoal());
        }
        for (var goal : targetSelector.availableGoals) {
            if (goal == null) continue;
            targetSelector.removeGoal(goal.getGoal());
        }
    }
}
