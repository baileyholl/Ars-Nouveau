package com.hollingsworth.arsnouveau.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWilden extends PatrollingMonster implements GeoEntity {

    protected AbstractWilden(EntityType<? extends PatrollingMonster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static List<EntityType<? extends AbstractWilden>> WILDEN_TYPES = new CopyOnWriteArrayList<>();

    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public static EntityType<? extends PatrollingMonster> getRandomWildenType(Holder<Biome> biome, RandomSource randomSource) {
        return WILDEN_TYPES.get(randomSource.nextInt(WILDEN_TYPES.size()));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

    }

    @Override
    protected int getBaseExperienceReward() {
        return 8;
    }

    /**
     * Returns the volume for the sounds this mob makes.
     */
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public @org.jetbrains.annotations.Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @org.jetbrains.annotations.Nullable SpawnGroupData spawnGroupData) {
        SpawnGroupData ret = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
        if (this.isPatrolLeader()) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            this.setDropChance(EquipmentSlot.HEAD, 0F);
        }
        return ret;
    }

    public static boolean checkPatrollingMonsterSpawnRules(@NotNull EntityType<? extends PatrollingMonster> pPatrollingMonster, LevelAccessor pLevel, @NotNull MobSpawnType pSpawnType, @NotNull BlockPos pPos, @NotNull RandomSource pRandom) {
        return pLevel.getBrightness(LightLayer.BLOCK, pPos) <= 8 && checkAnyLightMonsterSpawnRules(pPatrollingMonster, pLevel, pSpawnType, pPos, pRandom);
    }

}
