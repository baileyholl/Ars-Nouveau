package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import java.util.Random;

@ObjectHolder(ArsNouveau.MODID)
public class ModEntities {

    public static EntityType<EntityProjectileSpell> SPELL_PROJ = null;
    public static EntityType<EntityAllyVex> ALLY_VEX = null;
    public static EntityType<EntityEvokerFangs> ENTITY_EVOKER_FANGS_ENTITY_TYPE = null;
    public static EntityType<EntityBookwyrm> ENTITY_BOOKWYRM_TYPE = null;
    public static EntityType<EntityCarbuncle> ENTITY_CARBUNCLE_TYPE = build("carbuncle", EntityType.Builder.<EntityCarbuncle>of(EntityCarbuncle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));
    public static EntityType<EntityFollowProjectile> ENTITY_FOLLOW_PROJ = null;
    public static EntityType<EntitySylph> ENTITY_SYLPH_TYPE = build("sylph", EntityType.Builder.<EntitySylph>of(EntitySylph::new, MobCategory.CREATURE)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static EntityType<EntityEarthElemental> ENTITY_EARTH_ELEMENTAL_TYPE = null;
    public static EntityType<EntityWixie> ENTITY_WIXIE_TYPE = null;

    public static EntityType<EntityFlyingItem> ENTITY_FLYING_ITEM = null;
    public static EntityType<EntityRitualProjectile> ENTITY_RITUAL = null;

    public static EntityType<WildenHunter> WILDEN_HUNTER = build(
            "wilden_hunter",
            EntityType.Builder.<WildenHunter>of(WildenHunter::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static EntityType<EntitySpellArrow> ENTITY_SPELL_ARROW = null;
    public static EntityType<SummonWolf> SUMMON_WOLF = null;

    public static EntityType<WildenStalker> WILDEN_STALKER = build(
            "wilden_stalker",
            EntityType.Builder.<WildenStalker>of(WildenStalker::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static EntityType<SummonHorse> SUMMON_HORSE = null;
    public static EntityType<WildenGuardian> WILDEN_GUARDIAN =  build(
            "wilden_guardian",
            EntityType.Builder.<WildenGuardian>of(WildenGuardian::new, MobCategory.MONSTER)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static EntityType<EntityChimera> WILDEN_BOSS =  build(
            "wilden_boss",
            EntityType.Builder.<EntityChimera>of(EntityChimera::new, MobCategory.MONSTER)
                    .sized(1.5f, 2.5f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static EntityType<LightningEntity> LIGHTNING_ENTITY = null;
    public static EntityType<EntityDummy> ENTITY_DUMMY = null;
    public static EntityType<EntityDrygmy> ENTITY_DRYGMY = null;
    public static EntityType<EntityOrbitProjectile> ENTITY_WARD = null;
    public static EntityType<EntityChimeraProjectile> ENTITY_CHIMERA_SPIKE = null;
    public static EntityType<FamiliarCarbuncle> ENTITY_FAMILIAR_CARBUNCLE = null;

    public static EntityType<FamiliarWixie> ENTITY_FAMILIAR_WIXIE = null;
    public static EntityType<FamiliarBookwyrm> ENTITY_FAMILIAR_BOOKWYRM = null;
    public static EntityType<FamiliarDrygmy> ENTITY_FAMILIAR_DRYGMY = null;
    public static EntityType<FamiliarSylph> ENTITY_FAMILIAR_SYLPH = null;
    public static EntityType<FamiliarJabberwog> ENTITY_FAMILIAR_JABBERWOG = null;
    public static EntityType<EntityLingeringSpell> LINGER_SPELL = null;
    public static EntityType<WealdWalker> ENTITY_CASCADING_WEALD = null;
    public static EntityType<WealdWalker> ENTITY_FLOURISHING_WEALD = null;
    public static EntityType<WealdWalker> ENTITY_BLAZING_WEALD = null;
    public static EntityType<WealdWalker> ENTITY_VEXING_WEALD = null;

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {


        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            SPELL_PROJ = build(
                    "spell_proj",
                    EntityType.Builder.<EntityProjectileSpell>of(EntityProjectileSpell::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(20)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(120).setCustomClientFactory(EntityProjectileSpell::new));
            LINGER_SPELL = build(
                    "linger",
                    EntityType.Builder.<EntityLingeringSpell>of(EntityLingeringSpell::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(20)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(120).setCustomClientFactory(EntityLingeringSpell::new));
            ENTITY_EVOKER_FANGS_ENTITY_TYPE = build(
                    "fangs",
                    EntityType.Builder.<EntityEvokerFangs>of(EntityEvokerFangs::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .setUpdateInterval(60));
           ALLY_VEX = build(
                    "ally_vex",
                    EntityType.Builder.<EntityAllyVex>of(EntityAllyVex::new, MobCategory.MISC)
                            .sized(0.4F, 0.8F).fireImmune());
           ENTITY_BOOKWYRM_TYPE = build("whelp", EntityType.Builder.<EntityBookwyrm>of(EntityBookwyrm::new, MobCategory.MISC)
                   .sized(0.7f, 0.9f).setTrackingRange(10)
                   .setShouldReceiveVelocityUpdates(true));

           ENTITY_FOLLOW_PROJ = build(
                    "follow_proj",
                    EntityType.Builder.<EntityFollowProjectile>of(EntityFollowProjectile::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFollowProjectile::new));


            ENTITY_WIXIE_TYPE = build("wixie", EntityType.Builder.<EntityWixie>of(EntityWixie::new, MobCategory.MISC)
                    .sized(0.6F, 0.98F).setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

            ENTITY_FLYING_ITEM = build(
                    "flying_item",
                    EntityType.Builder.<EntityFlyingItem>of(EntityFlyingItem::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFlyingItem::new));
            ENTITY_RITUAL = build(
                    "ritual",
                    EntityType.Builder.<EntityRitualProjectile>of(EntityRitualProjectile::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .setTrackingRange(10).setUpdateInterval(60)
                            .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityRitualProjectile::new));

            ENTITY_SPELL_ARROW = build(
                    "spell_arrow",
                    EntityType.Builder.<EntitySpellArrow>of(EntitySpellArrow::new, MobCategory.MISC)
                            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntitySpellArrow::new));

            SUMMON_WOLF = build(
                    "summon_wolf",
                    EntityType.Builder.<SummonWolf>of(SummonWolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

            SUMMON_HORSE = build(
                    "summon_horse",
                    EntityType.Builder.<SummonHorse>of(SummonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));


            ENTITY_DUMMY = build(
                    "dummy",
                    EntityType.Builder.<EntityDummy>of(EntityDummy::new, MobCategory.MISC)
                            .sized(1.0f, 2.0f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true));
            LIGHTNING_ENTITY = build("an_lightning", EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(16)
                    .updateInterval(Integer.MAX_VALUE
                    ).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));

            ENTITY_DRYGMY = build(
                    "drygmy",
                    EntityType.Builder.<EntityDrygmy>of(EntityDrygmy::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
            ENTITY_WARD = build(
                    "ward_entity",
                    EntityType.Builder.<EntityOrbitProjectile>of(EntityOrbitProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f)
                            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityOrbitProjectile::new));
            ENTITY_CHIMERA_SPIKE = build(
                    "spike",
                    EntityType.Builder.<EntityChimeraProjectile>of(EntityChimeraProjectile::new, MobCategory.MISC)
                            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityChimeraProjectile::new));

            ENTITY_FAMILIAR_CARBUNCLE =  build("familiar_carbuncle", EntityType.Builder.of(FamiliarCarbuncle::new, MobCategory.CREATURE)
                            .sized(0.7F, 0.63F).setTrackingRange(10));

            ENTITY_FAMILIAR_BOOKWYRM =  build("familiar_bookwyrm", EntityType.Builder.of(FamiliarBookwyrm::new, MobCategory.CREATURE)
                    .sized(0.7f, 0.9f).setTrackingRange(10));
            ENTITY_FAMILIAR_DRYGMY =  build("familiar_drygmy", EntityType.Builder.of(FamiliarDrygmy::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.85f).setTrackingRange(10));
            ENTITY_FAMILIAR_SYLPH =  build("familiar_sylph", EntityType.Builder.of(FamiliarSylph::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.63F).setTrackingRange(10));
            ENTITY_FAMILIAR_JABBERWOG =  build("familiar_jabberwog", EntityType.Builder.of(FamiliarJabberwog::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.63F).setTrackingRange(10));
            ENTITY_FAMILIAR_WIXIE =  build("familiar_wixie", EntityType.Builder.of(FamiliarWixie::new, MobCategory.CREATURE)
                    .sized(0.7F, 0.63F).setTrackingRange(10));
            ENTITY_CASCADING_WEALD =  build("cascading_weald_walker", EntityType.Builder.<WealdWalker>of((type, world) ->{
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectFreeze.INSTANCE, EffectColdSnap.INSTANCE);
                walker.color = new ParticleColor(50, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
                    .sized(1.4F, 3F).setTrackingRange(10));


            ENTITY_FLOURISHING_WEALD =  build("flourishing_weald_walker", EntityType.Builder.<WealdWalker>of((type, world) ->{
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHarm.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE, EffectSnare.INSTANCE);
                walker.color = new ParticleColor(50, 250, 55);
                return walker;
            }, MobCategory.CREATURE)
                    .sized(1.4F, 3F).setTrackingRange(10));
            ENTITY_BLAZING_WEALD =  build("blazing_weald_walker", EntityType.Builder.<WealdWalker>of((type, world) ->{
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectIgnite.INSTANCE, AugmentSensitive.INSTANCE, EffectFlare.INSTANCE);
                walker.color = new ParticleColor(250, 15, 15);
                return walker;
            }, MobCategory.CREATURE)
                    .sized(1.4F, 3F).setTrackingRange(10));
            ENTITY_VEXING_WEALD =  build("vexing_weald_walker", EntityType.Builder.<WealdWalker>of((type, world) ->{
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHex.INSTANCE, EffectWither.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE);
                walker.color = new ParticleColor(250, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
                    .sized(1.4F, 3F).setTrackingRange(10));
            event.getRegistry().registerAll(
                    SPELL_PROJ,
                    ENTITY_EVOKER_FANGS_ENTITY_TYPE,
                    ALLY_VEX,
                    ENTITY_BOOKWYRM_TYPE,
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
                    ENTITY_WARD,
                     WILDEN_BOSS,
                    ENTITY_CHIMERA_SPIKE,
                    ENTITY_FAMILIAR_CARBUNCLE,
                    ENTITY_FAMILIAR_BOOKWYRM,
                    ENTITY_FAMILIAR_JABBERWOG,
                    ENTITY_FAMILIAR_WIXIE,
                    ENTITY_FAMILIAR_SYLPH,
                    ENTITY_FAMILIAR_DRYGMY,
                    LINGER_SPELL,
                    ENTITY_CASCADING_WEALD,
                    ENTITY_BLAZING_WEALD,
                    ENTITY_VEXING_WEALD,
                    ENTITY_FLOURISHING_WEALD
            );



            SpawnPlacements.register(ENTITY_CARBUNCLE_TYPE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
            SpawnPlacements.register(ENTITY_SYLPH_TYPE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
            SpawnPlacements.register(ENTITY_DRYGMY, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);

            SpawnPlacements.register(WILDEN_GUARDIAN, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            SpawnPlacements.register(WILDEN_HUNTER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
            SpawnPlacements.register(WILDEN_STALKER, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::canMonsterSpawnInLight);
        }

        @SubscribeEvent
        public static void registerEntities(final EntityAttributeCreationEvent event) {
            event.put(ENTITY_BOOKWYRM_TYPE, EntityBookwyrm.attributes().build());
            event.put(ALLY_VEX, Vex.createAttributes().build());
            event.put(ENTITY_CARBUNCLE_TYPE, EntityCarbuncle.attributes().build());
            event.put(ENTITY_SYLPH_TYPE, EntitySylph.attributes().build());
            event.put(ENTITY_DRYGMY, EntitySylph.attributes().build());
            event.put(ENTITY_WIXIE_TYPE, EntityWixie.attributes().build());
            event.put(WILDEN_HUNTER, WildenHunter.getModdedAttributes().build());
            event.put(WILDEN_STALKER, WildenStalker.getModdedAttributes().build());
            event.put(SUMMON_WOLF, Wolf.createAttributes().build());
            event.put(SUMMON_HORSE, AbstractHorse.createBaseHorseAttributes().build());
            event.put(WILDEN_GUARDIAN, WildenGuardian.getModdedAttributes().build());
            event.put(ENTITY_DUMMY, Mob.createMobAttributes()
                    .add(Attributes.MAX_HEALTH, 20.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.25D).build());
            event.put(WILDEN_BOSS, EntityChimera.getModdedAttributes().build());
            event.put(ENTITY_FAMILIAR_CARBUNCLE, FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_BOOKWYRM, FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_JABBERWOG, FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_WIXIE, FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_SYLPH, FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_DRYGMY, FamiliarEntity.attributes().build());
            event.put(ENTITY_CASCADING_WEALD, WealdWalker.attributes().build());
            event.put(ENTITY_BLAZING_WEALD, WealdWalker.attributes().build());
            event.put(ENTITY_FLOURISHING_WEALD, WealdWalker.attributes().build());
            event.put(ENTITY_VEXING_WEALD, WealdWalker.attributes().build());
        }
    }
    public static boolean canMonsterSpawnInLight(EntityType<? extends Entity> type, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && isValidLightLevel(worldIn, pos, randomIn) && canSpawnOn(type, worldIn, reason, pos, randomIn)
                && !Config.DIMENSION_BLACKLIST.get().contains(worldIn.getLevel().dimension().location().toString());
    }

    public static boolean canSpawnOn(EntityType<? extends Entity> typeIn, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random randomIn) {
        BlockPos blockpos = pos.below();
        return reason == MobSpawnType.SPAWNER || worldIn.getBlockState(blockpos).isValidSpawn(worldIn, blockpos, typeIn);
    }
    public static boolean genericGroundSpawn(EntityType<? extends Entity> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, Random random) {
        return worldIn.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public static boolean isValidLightLevel(ServerLevelAccessor worldIn, BlockPos pos, Random randomIn) {
        if (worldIn.getBrightness(LightLayer.SKY, pos) > randomIn.nextInt(32)) {
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
