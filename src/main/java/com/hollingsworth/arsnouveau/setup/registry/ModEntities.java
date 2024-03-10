package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.*;
import com.hollingsworth.arsnouveau.common.entity.familiar.*;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.effect.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);

    static <T extends Entity> RegistryObject<EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(MODID + ":" + name));
    }

    public static final RegistryObject<EntityType<EntityProjectileSpell>> SPELL_PROJ = registerEntity(
            LibEntityNames.SPELL_PROJ,
            EntityType.Builder.<EntityProjectileSpell>of(EntityProjectileSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave()
                    .setTrackingRange(20).fireImmune()
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntityProjectileSpell>> SPELL_PROJ_ARC = registerEntity(
            LibEntityNames.SPELL_PROJ_ARC,
            EntityType.Builder.<EntityProjectileSpell>of((entityType, world) -> new EntityProjectileSpell(entityType, world) {
                        @Override
                        public EntityType<?> getType() {
                            return SPELL_PROJ_ARC.get();
                        }

                        @Override
                        public boolean isNoGravity() {
                            return false;
                        }
                    }, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave()
                    .setTrackingRange(20).fireImmune()
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntityProjectileSpell>> SPELL_PROJ_HOM = registerEntity(
            LibEntityNames.SPELL_PROJ_HOM,
            EntityType.Builder.<EntityProjectileSpell>of(EntityHomingProjectileSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave()
                    .setTrackingRange(20).fireImmune()
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntityAllyVex>> ALLY_VEX = registerEntity(
            LibEntityNames.ALLY_VEX,
            EntityType.Builder.<EntityAllyVex>of(EntityAllyVex::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune());
    public static final RegistryObject<EntityType<EntityEvokerFangs>> ENTITY_EVOKER_FANGS_ENTITY_TYPE = registerEntity(
            LibEntityNames.FANGS,
            EntityType.Builder.<EntityEvokerFangs>of(EntityEvokerFangs::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .setUpdateInterval(60));
    public static final RegistryObject<EntityType<EntityBookwyrm>> ENTITY_BOOKWYRM_TYPE = registerEntity(LibEntityNames.BOOKWYRM, EntityType.Builder.<EntityBookwyrm>of(EntityBookwyrm::new, MobCategory.MISC)
            .sized(0.4f, 0.6f).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<Starbuncle>> STARBUNCLE_TYPE = registerEntity(LibEntityNames.STARBUNCLE, EntityType.Builder.<Starbuncle>of(Starbuncle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntityFollowProjectile>> ENTITY_FOLLOW_PROJ = registerEntity(
            LibEntityNames.FOLLOW_PROJ,
            EntityType.Builder.<EntityFollowProjectile>of(EntityFollowProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave().fireImmune()
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFollowProjectile::new));

    public static final RegistryObject<EntityType<Whirlisprig>> WHIRLISPRIG_TYPE = registerEntity(LibEntityNames.WHIRLISPRIG, EntityType.Builder.<Whirlisprig>of(Whirlisprig::new, MobCategory.CREATURE)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final RegistryObject<EntityType<EntityWixie>> ENTITY_WIXIE_TYPE = registerEntity(LibEntityNames.WIXIE, EntityType.Builder.<EntityWixie>of(EntityWixie::new, MobCategory.MISC)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final RegistryObject<EntityType<EntityFlyingItem>> ENTITY_FLYING_ITEM = registerEntity(
            LibEntityNames.FLYING_ITEM,
            EntityType.Builder.<EntityFlyingItem>of(EntityFlyingItem::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).fireImmune()
                    .setTrackingRange(10).setUpdateInterval(60)
                    .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityFlyingItem::new).noSave());
    public static final RegistryObject<EntityType<EntityRitualProjectile>> ENTITY_RITUAL = registerEntity(
            LibEntityNames.RITUAL_PROJ,
            EntityType.Builder.<EntityRitualProjectile>of(EntityRitualProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(10).setUpdateInterval(60)
                    .setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityRitualProjectile::new));

    public static final RegistryObject<EntityType<WildenHunter>> WILDEN_HUNTER = registerEntity(
            LibEntityNames.WILDEN_HUNTER,
            EntityType.Builder.<WildenHunter>of(WildenHunter::new, MobCategory.MONSTER)
                    .sized(1.2f, 1.2F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntitySpellArrow>> ENTITY_SPELL_ARROW = registerEntity(
            LibEntityNames.SPELL_ARROW,
            EntityType.Builder.<EntitySpellArrow>of(EntitySpellArrow::new, MobCategory.MISC)
                    .clientTrackingRange(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntitySpellArrow::new));
    public static final RegistryObject<EntityType<Cinder>> CINDER = registerEntity(
            LibEntityNames.CINDER,
            EntityType.Builder.<Cinder>of(Cinder::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setShouldReceiveVelocityUpdates(true)
                    .fireImmune()
                    .setTrackingRange(256));
    public static final RegistryObject<EntityType<SummonWolf>> SUMMON_WOLF = registerEntity(
            LibEntityNames.SUMMONED_WOLF,
            EntityType.Builder.of(SummonWolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

    public static final RegistryObject<EntityType<WildenStalker>> WILDEN_STALKER = registerEntity(
            LibEntityNames.WILDEN_STALKER,
            EntityType.Builder.<WildenStalker>of(WildenStalker::new, MobCategory.MONSTER)
                    .sized(0.95F, 1F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<SummonHorse>> SUMMON_HORSE = registerEntity(
            LibEntityNames.SUMMONED_HORSE,
            EntityType.Builder.of(SummonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<SummonSkeleton>> SUMMON_SKELETON = registerEntity(LibEntityNames.SUMMONED_SKELETON,
            EntityType.Builder.<SummonSkeleton>of(SummonSkeleton::new, MobCategory.CREATURE).sized(1.0F, 1.8F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<WildenGuardian>> WILDEN_GUARDIAN = registerEntity(
            LibEntityNames.WILDEN_GUARDIAN,
            EntityType.Builder.<WildenGuardian>of(WildenGuardian::new, MobCategory.MONSTER)
                    .sized(1.15F, 1.15F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<WildenChimera>> WILDEN_BOSS = registerEntity(
            LibEntityNames.WILDEN_CHIMERA,
            EntityType.Builder.<WildenChimera>of(WildenChimera::new, MobCategory.MONSTER)
                    .sized(2.5f, 2.25f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<LightningEntity>> LIGHTNING_ENTITY = registerEntity(LibEntityNames.AN_LIGHTNING, EntityType.Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
            .sized(0.0F, 0.0F)
            .clientTrackingRange(16)
            .updateInterval(Integer.MAX_VALUE
            ).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));
    public static final RegistryObject<EntityType<EntityDummy>> ENTITY_DUMMY = registerEntity(
            LibEntityNames.DUMMY,
            EntityType.Builder.<EntityDummy>of(EntityDummy::new, MobCategory.MISC)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final RegistryObject<EntityType<EntityDrygmy>> ENTITY_DRYGMY = registerEntity(
            LibEntityNames.DRYGMY,
            EntityType.Builder.<EntityDrygmy>of(EntityDrygmy::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
    public static final RegistryObject<EntityType<EntityOrbitProjectile>> ORBIT_SPELL = registerEntity(
            LibEntityNames.ORBIT_PROJECTILE,
            EntityType.Builder.<EntityOrbitProjectile>of(EntityOrbitProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).fireImmune()
                    .clientTrackingRange(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityOrbitProjectile::new));
    public static final RegistryObject<EntityType<EntityChimeraProjectile>> ENTITY_CHIMERA_SPIKE = registerEntity(
            LibEntityNames.CHIMERA_SPIKE,
            EntityType.Builder.<EntityChimeraProjectile>of(EntityChimeraProjectile::new, MobCategory.MISC)
                    .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(EntityChimeraProjectile::new));
    public static final RegistryObject<EntityType<FamiliarStarbuncle>> ENTITY_FAMILIAR_STARBUNCLE = registerEntity(LibEntityNames.FAMILIAR_STARBUNCLE, EntityType.Builder.of(FamiliarStarbuncle::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));

    public static final RegistryObject<EntityType<FamiliarWixie>> ENTITY_FAMILIAR_WIXIE = registerEntity(LibEntityNames.FAMILIAR_WIXIE, EntityType.Builder.of(FamiliarWixie::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final RegistryObject<EntityType<FamiliarBookwyrm>> ENTITY_FAMILIAR_BOOKWYRM = registerEntity(LibEntityNames.FAMILIAR_BOOKWYRM, EntityType.Builder.of(FamiliarBookwyrm::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final RegistryObject<EntityType<FamiliarDrygmy>> ENTITY_FAMILIAR_DRYGMY = registerEntity(LibEntityNames.FAMILIAR_DRYGMY, EntityType.Builder.of(FamiliarDrygmy::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final RegistryObject<EntityType<FamiliarWhirlisprig>> ENTITY_FAMILIAR_SYLPH = registerEntity(LibEntityNames.FAMILIAR_WHIRLISPRIG, EntityType.Builder.of(FamiliarWhirlisprig::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));

    public static final RegistryObject<EntityType<FamiliarAmethystGolem>> FAMILIAR_AMETHYST_GOLEM = registerEntity(LibEntityNames.FAMILIAR_AMETHYST_GOLEM, EntityType.Builder.of(FamiliarAmethystGolem::new, MobCategory.CREATURE)
            .sized(1.0f, 1.0f).setTrackingRange(10));

    public static final RegistryObject<EntityType<EntityLingeringSpell>> LINGER_SPELL = registerEntity(
            LibEntityNames.LINGER,
            EntityType.Builder.<EntityLingeringSpell>of(EntityLingeringSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .noSave()
                    .setCustomClientFactory(EntityLingeringSpell::new));
    public static final RegistryObject<EntityType<EntityWallSpell>> WALL_SPELL = registerEntity(
            LibEntityNames.WALL,
            EntityType.Builder.<EntityWallSpell>of(EntityWallSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .noSave()
                    .setCustomClientFactory(EntityWallSpell::new));
    public static final RegistryObject<EntityType<WealdWalker>> ENTITY_CASCADING_WEALD = registerEntity(LibEntityNames.CASCADING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectFreeze.INSTANCE, EffectColdSnap.INSTANCE);
                walker.color = new ParticleColor(50, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final RegistryObject<EntityType<WealdWalker>> ENTITY_FLOURISHING_WEALD = registerEntity(LibEntityNames.FLOURISHING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHarm.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE, EffectSnare.INSTANCE);
                walker.color = new ParticleColor(50, 250, 55);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final RegistryObject<EntityType<WealdWalker>> ENTITY_BLAZING_WEALD = registerEntity(LibEntityNames.BLAZING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectIgnite.INSTANCE, AugmentSensitive.INSTANCE, EffectFlare.INSTANCE);
                walker.color = new ParticleColor(250, 15, 15);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final RegistryObject<EntityType<WealdWalker>> ENTITY_VEXING_WEALD = registerEntity(LibEntityNames.VEXING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHex.INSTANCE, EffectWither.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE);
                walker.color = new ParticleColor(250, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final RegistryObject<EntityType<AmethystGolem>> AMETHYST_GOLEM = registerEntity(LibEntityNames.AMETHYST_GOLEM, EntityType.Builder.of(AmethystGolem::new, MobCategory.CREATURE)
            .sized(1.0f, 1.0f).setTrackingRange(10));
    public static final RegistryObject<EntityType<ScryerCamera>> SCRYER_CAMERA = registerEntity(LibEntityNames.SCRYER_CAMERA, EntityType.Builder.<ScryerCamera>of(ScryerCamera::new, MobCategory.MISC)
            .sized(1.0E-4F, 1.0E-4F).setTrackingRange(256).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true));

    public static final RegistryObject<EntityType<EnchantedFallingBlock>> ENCHANTED_FALLING_BLOCK = registerEntity(
            "enchanted_falling_block", EntityType.Builder.<EnchantedFallingBlock>of(EnchantedFallingBlock::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final RegistryObject<EntityType<IceShardEntity>> ICE_SHARD = registerEntity(
            "ice_shard", EntityType.Builder.<IceShardEntity>of(IceShardEntity::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final RegistryObject<EntityType<EnchantedMageblock>> ENCHANTED_MAGE_BLOCK = registerEntity(
            "enchanted_mage_block", EntityType.Builder.<EnchantedMageblock>of(EnchantedMageblock::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final RegistryObject<EntityType<EnchantedSkull>> ENCHANTED_HEAD_BLOCK = registerEntity(
            "enchanted_head_block", EntityType.Builder.<EnchantedSkull>of(EnchantedSkull::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));

    public static final RegistryObject<EntityType<GiftStarbuncle>> GIFT_STARBY = registerEntity(LibEntityNames.GIFT_STARBY, EntityType.Builder.of(GiftStarbuncle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));


    public static final RegistryObject<EntityType<AnimBlockSummon>> ANIMATED_BLOCK = registerEntity(
            "animated_block",
            EntityType.Builder.<AnimBlockSummon>of(AnimBlockSummon::new, MobCategory.MISC)
                    .sized(1.0f, 1.5f)
                    .noSave()
                    .setTrackingRange(10));

    public static final RegistryObject<EntityType<AnimHeadSummon>> ANIMATED_HEAD = registerEntity(
            "animated_head",
            EntityType.Builder.<AnimHeadSummon>of(AnimHeadSummon::new, MobCategory.MISC)
                    .sized(1.0f, 1.5f)
                    .noSave()
                    .setTrackingRange(10));
    public static final RegistryObject<EntityType<Lily>> LILY = registerEntity(
            LibEntityNames.LILY,
            EntityType.Builder.<Lily>of(Lily::new, MobCategory.MISC)
                    .sized(0.5F, 0.75F)
                    .setTrackingRange(10));

    public static void registerPlacements() {
        SpawnPlacements.register(STARBUNCLE_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(GIFT_STARBY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(WHIRLISPRIG_TYPE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(ENTITY_DRYGMY.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);

        SpawnPlacements.register(WILDEN_GUARDIAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules);
        SpawnPlacements.register(WILDEN_HUNTER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules);
        SpawnPlacements.register(WILDEN_STALKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules);

        SpawnPlacements.register(ENTITY_BLAZING_WEALD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(ENTITY_CASCADING_WEALD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(ENTITY_FLOURISHING_WEALD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);
        SpawnPlacements.register(ENTITY_VEXING_WEALD.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn);

        LightManager.init();
    }

    public static boolean canMonsterSpawnInLight(EntityType<? extends Monster> type, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return Monster.checkMonsterSpawnRules(type, worldIn, reason, pos, randomIn)
               && !Config.DIMENSION_BLACKLIST.get().contains(worldIn.getLevel().dimension().location().toString());
    }

    public static boolean wildenSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && Monster.checkMonsterSpawnRules(type, worldIn, reason, pos, randomIn)
               && !Config.DIMENSION_BLACKLIST.get().contains(worldIn.getLevel().dimension().location().toString());
    }

    public static boolean guardianSpawnRules(EntityType<Drowned> pDrowned, ServerLevelAccessor pServerLevel, MobSpawnType pMobSpawnType, BlockPos pPos, RandomSource pRandom) {
        boolean flag = pServerLevel.getDifficulty() != Difficulty.PEACEFUL && (pMobSpawnType != MobSpawnType.SPAWNER || pServerLevel.getFluidState(pPos).is(FluidTags.WATER));
        return flag;
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {
            event.put(ENTITY_BOOKWYRM_TYPE.get(), EntityBookwyrm.attributes().build());
            event.put(ALLY_VEX.get(), Vex.createAttributes().build());
            event.put(STARBUNCLE_TYPE.get(), Starbuncle.attributes().build());
            event.put(WHIRLISPRIG_TYPE.get(), Whirlisprig.attributes().build());
            event.put(ENTITY_DRYGMY.get(), Whirlisprig.attributes().build());
            event.put(ENTITY_WIXIE_TYPE.get(), EntityWixie.attributes().build());
            event.put(WILDEN_HUNTER.get(), WildenHunter.getModdedAttributes().build());
            event.put(WILDEN_STALKER.get(), WildenStalker.getModdedAttributes().build());
            event.put(SUMMON_WOLF.get(), Wolf.createAttributes().build());
            event.put(SUMMON_HORSE.get(), AbstractHorse.createBaseHorseAttributes().build());
            event.put(WILDEN_GUARDIAN.get(), WildenGuardian.getModdedAttributes().build());
            event.put(ENTITY_DUMMY.get(), Mob.createMobAttributes()
                    .add(Attributes.MAX_HEALTH, 20.0D)
                    .add(Attributes.MOVEMENT_SPEED, 0.25D).build());
            event.put(WILDEN_BOSS.get(), WildenChimera.getModdedAttributes().build());
            event.put(ENTITY_FAMILIAR_STARBUNCLE.get(), FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_BOOKWYRM.get(), FamiliarEntity.attributes().build());
//            event.put(ENTITY_FAMILIAR_JABBERWOG.get(), FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_WIXIE.get(), FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_SYLPH.get(), FamiliarEntity.attributes().build());
            event.put(ENTITY_FAMILIAR_DRYGMY.get(), FamiliarEntity.attributes().build());
            event.put(FAMILIAR_AMETHYST_GOLEM.get(), FamiliarEntity.attributes().build());
            event.put(ENTITY_CASCADING_WEALD.get(), WealdWalker.attributes().build());
            event.put(ENTITY_BLAZING_WEALD.get(), WealdWalker.attributes().build());
            event.put(ENTITY_FLOURISHING_WEALD.get(), WealdWalker.attributes().build());
            event.put(ENTITY_VEXING_WEALD.get(), WealdWalker.attributes().build());
            event.put(AMETHYST_GOLEM.get(), AmethystGolem.attributes().build());
            event.put(SUMMON_SKELETON.get(), SummonSkeleton.createAttributes().build());
            event.put(GIFT_STARBY.get(), GiftStarbuncle.attributes().build());
            event.put(ANIMATED_BLOCK.get(), AnimBlockSummon.createAttributes().build());
            event.put(ANIMATED_HEAD.get(), AnimBlockSummon.createAttributes().build());
            event.put(LILY.get(), Lily.createAttributes().build());
        }
    }

    public static boolean genericGroundSpawn(EntityType<? extends Entity> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

}
