package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
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
    public static EntityType<EntityCarbuncle> ENTITY_CARBUNCLE_TYPE = build("carbuncle", EntityType.Builder.<EntityCarbuncle>of(EntityCarbuncle::new, EntityClassification.CREATURE)
            .sized(0.7F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));;
    public static EntityType<EntityFollowProjectile> ENTITY_FOLLOW_PROJ = null;
    public static EntityType<EntitySylph> ENTITY_SYLPH_TYPE = build("sylph", EntityType.Builder.<EntitySylph>of(EntitySylph::new, EntityClassification.CREATURE)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));;

    public static EntityType<EntityEarthElemental> ENTITY_EARTH_ELEMENTAL_TYPE = null;
    public static EntityType<EntityWixie> ENTITY_WIXIE_TYPE = null;

    public static EntityType<EntityFlyingItem> ENTITY_FLYING_ITEM = null;
    public static EntityType<EntityRitualProjectile> ENTITY_RITUAL = null;

    public static EntityType<WildenHunter> WILDEN_HUNTER = build(
            "wilden_hunter",
            EntityType.Builder.<WildenHunter>of(WildenHunter::new, EntityClassification.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));;
    public static EntityType<EntitySpellArrow> ENTITY_SPELL_ARROW = null;
    public static EntityType<SummonWolf> SUMMON_WOLF = null;

    public static EntityType<WildenStalker> WILDEN_STALKER = build(
            "wilden_stalker",
            EntityType.Builder.<WildenStalker>of(WildenStalker::new, EntityClassification.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));;
    public static EntityType<SummonHorse> SUMMON_HORSE = null;
    public static EntityType<WildenGuardian> WILDEN_GUARDIAN =  build(
            "wilden_guardian",
            EntityType.Builder.<WildenGuardian>of(WildenGuardian::new, EntityClassification.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

    public static EntityType<LightningEntity> LIGHTNING_ENTITY = null;
    public static EntityType<EntityDummy> ENTITY_DUMMY = null;
    public static EntityType<DrygmyEntity> ENTITY_DRYGMY = null;
    public static EntityType<EntityWardProjectile> ENTITY_WARD = null;


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
                    EntityType.Builder.<EntityProjectileSpell>of(EntityProjectileSpell::new, EntityClassification.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(20)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(120).setCustomClientFactory(EntityProjectileSpell::new));
            ENTITY_EVOKER_FANGS_ENTITY_TYPE = build(
                    "fangs",
                    EntityType.Builder.<EntityEvokerFangs>of(EntityEvokerFangs::new, EntityClassification.MISC)
                    .sized(0.5F, 0.8F)
                    .setUpdateInterval(60));
           ALLY_VEX = build(
                    "ally_vex",
                    EntityType.Builder.<EntityAllyVex>of(EntityAllyVex::new, EntityClassification.MISC)
                            .sized(0.4F, 0.8F).fireImmune());
           ENTITY_WHELP_TYPE = build("whelp", EntityType.Builder.<EntityWhelp>of(EntityWhelp::new, EntityClassification.MISC)
                   .sized(0.6F, 0.63F).setTrackingRange(10)
                   .setShouldReceiveVelocityUpdates(true));

           ENTITY_FOLLOW_PROJ = build(
                    "follow_proj",
                    EntityType.Builder.<EntityFollowProjectile>of(EntityFollowProjectile::new, EntityClassification.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFollowProjectile::new));


            ENTITY_WIXIE_TYPE = build("wixie", EntityType.Builder.<EntityWixie>of(EntityWixie::new, EntityClassification.MISC)
                    .sized(0.6F, 0.98F).setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

            ENTITY_FLYING_ITEM = build(
                    "flying_item",
                    EntityType.Builder.<EntityFlyingItem>of(EntityFlyingItem::new, EntityClassification.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFlyingItem::new));
            ENTITY_RITUAL = build(
                    "ritual",
                    EntityType.Builder.<EntityRitualProjectile>of(EntityRitualProjectile::new, EntityClassification.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityRitualProjectile::new));

            ENTITY_SPELL_ARROW = build(
                    "spell_arrow",
                    EntityType.Builder.<EntitySpellArrow>of(EntitySpellArrow::new, EntityClassification.MISC)
                            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntitySpellArrow::new));

            SUMMON_WOLF = build(
                    "summon_wolf",
                    EntityType.Builder.<SummonWolf>of(SummonWolf::new, EntityClassification.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

            SUMMON_HORSE = build(
                    "summon_horse",
                    EntityType.Builder.<SummonHorse>of(SummonHorse::new, EntityClassification.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));


            ENTITY_DUMMY = build(
                    "dummy",
                    EntityType.Builder.<EntityDummy>of(EntityDummy::new, EntityClassification.MISC)
                            .sized(1.0f, 2.0f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true));
            LIGHTNING_ENTITY = build("an_lightning", EntityType.Builder.<LightningEntity>of(LightningEntity::new, EntityClassification.MISC).sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));

            ENTITY_DRYGMY = build(
                    "drygmy",
                    EntityType.Builder.<DrygmyEntity>of(DrygmyEntity::new, EntityClassification.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
            ENTITY_WARD = build(
                    "ward_entity",
                    EntityType.Builder.<EntityWardProjectile>of(EntityWardProjectile::new, EntityClassification.MISC)
                            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityWardProjectile::new));

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
                    LIGHTNING_ENTITY,
                    ENTITY_DUMMY,
                    ENTITY_DRYGMY,
                    ENTITY_WARD
            );

            GlobalEntityTypeAttributes.put(ENTITY_WHELP_TYPE, EntityWhelp.attributes().build());
            GlobalEntityTypeAttributes.put(ALLY_VEX, VexEntity.createAttributes().build());
            GlobalEntityTypeAttributes.put(ENTITY_CARBUNCLE_TYPE, EntityCarbuncle.attributes().build());
            GlobalEntityTypeAttributes.put(ENTITY_SYLPH_TYPE, EntitySylph.attributes().build());
            GlobalEntityTypeAttributes.put(ENTITY_DRYGMY, EntitySylph.attributes().build());
            GlobalEntityTypeAttributes.put(ENTITY_WIXIE_TYPE, EntityWixie.attributes().build());
            GlobalEntityTypeAttributes.put(WILDEN_HUNTER, WildenHunter.getModdedAttributes().build());
            GlobalEntityTypeAttributes.put(WILDEN_STALKER, WildenStalker.getModdedAttributes().build());
            GlobalEntityTypeAttributes.put(SUMMON_WOLF, WolfEntity.createAttributes().build());
            GlobalEntityTypeAttributes.put(SUMMON_HORSE, AbstractHorseEntity.createBaseHorseAttributes().build());
            GlobalEntityTypeAttributes.put(WILDEN_GUARDIAN, WildenGuardian.getModdedAttributes().build());
            GlobalEntityTypeAttributes.put(ENTITY_DUMMY, MobEntity.createMobAttributes()
                    .add(Attributes.MAX_HEALTH, 20.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.25D).build());

            EntitySpawnPlacementRegistry.register(ENTITY_CARBUNCLE_TYPE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
            EntitySpawnPlacementRegistry.register(ENTITY_SYLPH_TYPE, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);

            EntitySpawnPlacementRegistry.register(WILDEN_GUARDIAN, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            EntitySpawnPlacementRegistry.register(WILDEN_HUNTER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            EntitySpawnPlacementRegistry.register(WILDEN_STALKER, EntitySpawnPlacementRegistry.PlacementType.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
        }
    }
    public static boolean canMonsterSpawnInLight(EntityType<? extends Entity> type, IServerWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isValidLightLevel(worldIn, pos, randomIn) && canSpawnOn(type, worldIn, reason, pos, randomIn)
                && !Config.DIMENSION_BLACKLIST.get().contains(worldIn.getLevel().dimension().location().toString());
    }

    public static boolean canSpawnOn(EntityType<? extends Entity> typeIn, IWorld worldIn, SpawnReason reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.below();
        return reason == SpawnReason.SPAWNER || worldIn.getBlockState(blockpos).isValidSpawn(worldIn, blockpos, typeIn);
    }
    public static boolean genericGroundSpawn(EntityType<? extends Entity> animal, IWorld worldIn, SpawnReason reason, BlockPos pos, Random random) {
        return worldIn.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public static boolean isValidLightLevel(IServerWorld worldIn, BlockPos pos, Random randomIn) {
        if (worldIn.getBrightness(LightType.SKY, pos) > randomIn.nextInt(32)) {
            return false;
        } else {
            int i = worldIn.getLevel().isThundering() ? worldIn.getMaxLocalRawBrightness(pos, 10) : worldIn.getMaxLocalRawBrightness(pos);
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
