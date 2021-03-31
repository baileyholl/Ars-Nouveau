package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

@ObjectHolder(ArsNouveau.MODID)
public class ModEntities {


    public static EntityType<EntityProjectileSpell> SPELL_PROJ = null;
    public static EntityType<EntityAllyVex> ALLY_VEX = null;
    public static EntityType<EntityEvokerFangs> ENTITY_EVOKER_FANGS_ENTITY_TYPE = null;
    public static EntityType<EntityWhelp> ENTITY_WHELP_TYPE = null;
    public static EntityType<EntityCarbuncle> ENTITY_CARBUNCLE_TYPE = null;
    public static EntityType<EntityFollowProjectile> ENTITY_FOLLOW_PROJ = null;
    public static EntityType<EntitySylph> ENTITY_SYLPH_TYPE = null;

    public static EntityType<EntityEarthElemental> ENTITY_EARTH_ELEMENTAL_TYPE = null;
    public static EntityType<EntityWixie> ENTITY_WIXIE_TYPE = null;

    public static EntityType<EntityFlyingItem> ENTITY_FLYING_ITEM = null;
    public static EntityType<EntityRitualProjectile> ENTITY_RITUAL = null;

    public static EntityType<WildenHunter> WILDEN_HUNTER = null;
    public static EntityType<EntitySpellArrow> ENTITY_SPELL_ARROW = null;
    public static EntityType<SummonWolf> SUMMON_WOLF = null;
    public static EntityType<WildenStalker> WILDEN_STALKER = null;
    public static EntityType<SummonHorse> SUMMON_HORSE = null;
    public static EntityType<WildenGuardian> WILDEN_GUARDIAN = null;
    public static EntityType<LightningEntity> LIGHTNING_ENTITY = null;


    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Entity} types.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            SPELL_PROJ = build(
                    "spell_proj",
                    EntityType.Builder.<EntityProjectileSpell>create(EntityProjectileSpell::new, EntityClassification.MISC)
                            .size(0.5f, 0.5f)
                            .setTrackingRange(20)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(120).setCustomClientFactory(EntityProjectileSpell::new));
            ENTITY_EVOKER_FANGS_ENTITY_TYPE = build(
                    "fangs",
                    EntityType.Builder.<EntityEvokerFangs>create(EntityEvokerFangs::new, EntityClassification.MISC)
                    .size(0.5F, 0.8F)
                    .setUpdateInterval(60));
           ALLY_VEX = build(
                    "ally_vex",
                    EntityType.Builder.<EntityAllyVex>create(EntityAllyVex::new, EntityClassification.MISC)
                            .size(0.4F, 0.8F).immuneToFire());
           ENTITY_WHELP_TYPE = build("whelp", EntityType.Builder.<EntityWhelp>create(EntityWhelp::new, EntityClassification.MISC)
                   .size(0.6F, 0.63F).setTrackingRange(10)
                   .setShouldReceiveVelocityUpdates(true));

            ENTITY_CARBUNCLE_TYPE = build("carbuncle", EntityType.Builder.<EntityCarbuncle>create(EntityCarbuncle::new, EntityClassification.CREATURE)
                    .size(0.7F, 0.63F).setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
            ENTITY_FOLLOW_PROJ = build(
                    "follow_proj",
                    EntityType.Builder.<EntityFollowProjectile>create(EntityFollowProjectile::new, EntityClassification.MISC)
                            .size(0.5f, 0.5f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFollowProjectile::new));


            ENTITY_SYLPH_TYPE = build("sylph", EntityType.Builder.<EntitySylph>create(EntitySylph::new, EntityClassification.CREATURE)
                    .size(0.6F, 0.98F).setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

            ENTITY_WIXIE_TYPE = build("wixie", EntityType.Builder.<EntityWixie>create(EntityWixie::new, EntityClassification.MISC)
                    .size(0.6F, 0.98F).setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

            ENTITY_FLYING_ITEM = build(
                    "flying_item",
                    EntityType.Builder.<EntityFlyingItem>create(EntityFlyingItem::new, EntityClassification.MISC)
                            .size(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFlyingItem::new));
            ENTITY_RITUAL = build(
                    "ritual",
                    EntityType.Builder.<EntityRitualProjectile>create(EntityRitualProjectile::new, EntityClassification.MISC)
                            .size(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityRitualProjectile::new));
            WILDEN_HUNTER = build(
                    "wilden_hunter",
                    EntityType.Builder.<WildenHunter>create(WildenHunter::new, EntityClassification.MONSTER)
                            .size(1.0f, 2.0f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true));
            ENTITY_SPELL_ARROW = build(
                    "spell_arrow",
                    EntityType.Builder.<EntitySpellArrow>create(EntitySpellArrow::new, EntityClassification.MISC)
                            .trackingRange(20).func_233608_b_(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntitySpellArrow::new));

            SUMMON_WOLF = build(
                    "summon_wolf",
                    EntityType.Builder.<SummonWolf>create(SummonWolf::new, EntityClassification.CREATURE).size(0.6F, 0.85F).trackingRange(10));

            WILDEN_STALKER = build(
                    "wilden_stalker",
                    EntityType.Builder.<WildenStalker>create(WildenStalker::new, EntityClassification.MONSTER)
                            .size(1.0f, 2.0f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true));

            SUMMON_HORSE = build(
                    "summon_horse",
                    EntityType.Builder.<SummonHorse>create(SummonHorse::new, EntityClassification.CREATURE).size(1.3964844F, 1.6F).trackingRange(10));

            WILDEN_GUARDIAN = build(
                    "wilden_guardian",
                    EntityType.Builder.<WildenGuardian>create(WildenGuardian::new, EntityClassification.MONSTER)
                            .size(1.0f, 2.0f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true));
            LIGHTNING_ENTITY = build("an_lightning", EntityType.Builder.<LightningEntity>create(LightningEntity::new, EntityClassification.MISC).size(0.0F, 0.0F).trackingRange(16).func_233608_b_(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));
            event.getRegistry().registerAll(
                    SPELL_PROJ,
                    ENTITY_EVOKER_FANGS_ENTITY_TYPE,
                    ALLY_VEX,
                    ENTITY_WHELP_TYPE,
                    ENTITY_CARBUNCLE_TYPE,
                    ENTITY_SYLPH_TYPE,
                    ENTITY_FOLLOW_PROJ,
                    ENTITY_WIXIE_TYPE,
                    ENTITY_FLYING_ITEM,
                    ENTITY_RITUAL,
                    WILDEN_HUNTER,
                    ENTITY_SPELL_ARROW,
                    SUMMON_WOLF,
                    WILDEN_STALKER,
                    SUMMON_HORSE,
                    WILDEN_GUARDIAN,
                    LIGHTNING_ENTITY
            );

            GlobalEntityTypeAttributes.put(ENTITY_WHELP_TYPE, EntityWhelp.attributes().create());
            GlobalEntityTypeAttributes.put(ALLY_VEX, VexEntity.func_234321_m_().create());
            GlobalEntityTypeAttributes.put(ENTITY_CARBUNCLE_TYPE, EntityCarbuncle.attributes().create());
            GlobalEntityTypeAttributes.put(ENTITY_SYLPH_TYPE, EntitySylph.attributes().create());
            GlobalEntityTypeAttributes.put(ENTITY_WIXIE_TYPE, EntityWixie.attributes().create());
            GlobalEntityTypeAttributes.put(WILDEN_HUNTER, WildenHunter.getAttributes().create());
            GlobalEntityTypeAttributes.put(WILDEN_STALKER, WildenStalker.getAttributes().create());
            GlobalEntityTypeAttributes.put(SUMMON_WOLF, WolfEntity.func_234233_eS_().create());
            GlobalEntityTypeAttributes.put(SUMMON_HORSE, AbstractHorseEntity.func_234237_fg_().create());
            GlobalEntityTypeAttributes.put(WILDEN_GUARDIAN, WildenGuardian.getAttributes().create());

            EntitySpawnPlacementRegistry.register(ENTITY_CARBUNCLE_TYPE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
            EntitySpawnPlacementRegistry.register(ENTITY_SYLPH_TYPE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);

            EntitySpawnPlacementRegistry.register(WILDEN_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            EntitySpawnPlacementRegistry.register(WILDEN_HUNTER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            EntitySpawnPlacementRegistry.register(WILDEN_STALKER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
        }
    }
    public static boolean canMonsterSpawnInLight(EntityType<? extends Entity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isValidLightLevel(worldIn, pos, randomIn) && canSpawnOn(type, worldIn, reason, pos, randomIn);
    }

    public static boolean canSpawnOn(EntityType<? extends Entity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.down();
        return reason == SpawnReason.SPAWNER || worldIn.getBlockState(blockpos).canEntitySpawn(worldIn, blockpos, typeIn);
    }
    public static boolean genericGroundSpawn(EntityType<? extends Entity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getBlockState(pos.down()).isIn(Blocks.GRASS_BLOCK) && worldIn.getLightSubtracted(pos, 0) > 8;
    }

    public static boolean isValidLightLevel(IServerWorld worldIn, BlockPos pos, Random randomIn) {
        if (worldIn.getLightFor(LightType.SKY, pos) > randomIn.nextInt(32)) {
            return false;
        } else {
            int i = worldIn.getWorld().isThundering() ? worldIn.getNeighborAwareLightSubtracted(pos, 10) : worldIn.getLight(pos);
            return i <= randomIn.nextInt(8);
        }
    }
    /**
     * Build an {@link EntityType} from a {@link EntityType.Builder} using the specified name.
     *
     * @param name    The entity type name
     * @param builder The entity type builder to build
     * @return The built entity type
     */
    private static <T extends Entity> EntityType<T> build(final String name, final EntityType.Builder<T> builder) {
        final ResourceLocation registryName = new ResourceLocation(ArsNouveau.MODID, name);

        final EntityType<T> entityType = builder
                .build(registryName.toString());

        entityType.setRegistryName(registryName);

        return entityType;
    }
}
