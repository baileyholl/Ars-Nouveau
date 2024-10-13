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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID);

    static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> registerEntity(String name, EntityType.Builder<T> builder) {
        return ENTITIES.register(name, () -> builder.build(MODID + ":" + name));
    }

    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileSpell>> SPELL_PROJ = registerEntity(
            LibEntityNames.SPELL_PROJ,
            EntityType.Builder.<EntityProjectileSpell>of(EntityProjectileSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave()
                    .setTrackingRange(20).fireImmune()
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileSpell>> SPELL_PROJ_ARC = registerEntity(
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
    public static final DeferredHolder<EntityType<?>, EntityType<EntityProjectileSpell>> SPELL_PROJ_HOM = registerEntity(
            LibEntityNames.SPELL_PROJ_HOM,
            EntityType.Builder.<EntityProjectileSpell>of(EntityHomingProjectileSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave()
                    .setTrackingRange(20).fireImmune()
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityAllyVex>> ALLY_VEX = registerEntity(
            LibEntityNames.ALLY_VEX,
            EntityType.Builder.<EntityAllyVex>of(EntityAllyVex::new, MobCategory.MISC)
                    .sized(0.4F, 0.8F).fireImmune());
    public static final DeferredHolder<EntityType<?>, EntityType<EntityEvokerFangs>> ENTITY_EVOKER_FANGS_ENTITY_TYPE = registerEntity(
            LibEntityNames.FANGS,
            EntityType.Builder.<EntityEvokerFangs>of(EntityEvokerFangs::new, MobCategory.MISC)
                    .sized(0.5F, 0.8F)
                    .setUpdateInterval(60));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityBookwyrm>> ENTITY_BOOKWYRM_TYPE = registerEntity(LibEntityNames.BOOKWYRM, EntityType.Builder.<EntityBookwyrm>of(EntityBookwyrm::new, MobCategory.MISC)
            .sized(0.4f, 0.6f).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<Starbuncle>> STARBUNCLE_TYPE = registerEntity(LibEntityNames.STARBUNCLE, EntityType.Builder.<Starbuncle>of(Starbuncle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<Alakarkinos>> ALAKARKINOS_TYPE = registerEntity(LibEntityNames.ALAKARKINOS, EntityType.Builder.<Alakarkinos>of(Alakarkinos::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityFollowProjectile>> ENTITY_FOLLOW_PROJ = registerEntity(
            LibEntityNames.FOLLOW_PROJ,
            EntityType.Builder.<EntityFollowProjectile>of(EntityFollowProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).noSave().fireImmune()
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<Whirlisprig>> WHIRLISPRIG_TYPE = registerEntity(LibEntityNames.WHIRLISPRIG, EntityType.Builder.<Whirlisprig>of(Whirlisprig::new, MobCategory.CREATURE)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityWixie>> ENTITY_WIXIE_TYPE = registerEntity(LibEntityNames.WIXIE, EntityType.Builder.<EntityWixie>of(EntityWixie::new, MobCategory.MISC)
            .sized(0.6F, 0.98F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityFlyingItem>> ENTITY_FLYING_ITEM = registerEntity(
            LibEntityNames.FLYING_ITEM,
            EntityType.Builder.<EntityFlyingItem>of(EntityFlyingItem::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f).fireImmune()
                    .setTrackingRange(10).setUpdateInterval(60)
                    .setShouldReceiveVelocityUpdates(true).noSave());
    public static final DeferredHolder<EntityType<?>, EntityType<EntityRitualProjectile>> ENTITY_RITUAL = registerEntity(
            LibEntityNames.RITUAL_PROJ,
            EntityType.Builder.<EntityRitualProjectile>of(EntityRitualProjectile::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(10).setUpdateInterval(60)
                    .setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<WildenHunter>> WILDEN_HUNTER = registerEntity(
            LibEntityNames.WILDEN_HUNTER,
            EntityType.Builder.<WildenHunter>of(WildenHunter::new, MobCategory.MONSTER)
                    .sized(1.2f, 1.2F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySpellArrow>> ENTITY_SPELL_ARROW = registerEntity(
            LibEntityNames.SPELL_ARROW,
            EntityType.Builder.<EntitySpellArrow>of(EntitySpellArrow::new, MobCategory.MISC)
                    .clientTrackingRange(20).setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<Cinder>> CINDER = registerEntity(
            LibEntityNames.CINDER,
            EntityType.Builder.<Cinder>of(Cinder::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setShouldReceiveVelocityUpdates(true)
                    .fireImmune()
                    .setTrackingRange(256));
    public static final DeferredHolder<EntityType<?>, EntityType<SummonWolf>> SUMMON_WOLF = registerEntity(
            LibEntityNames.SUMMONED_WOLF,
            EntityType.Builder.of(SummonWolf::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<WildenStalker>> WILDEN_STALKER = registerEntity(
            LibEntityNames.WILDEN_STALKER,
            EntityType.Builder.<WildenStalker>of(WildenStalker::new, MobCategory.MONSTER)
                    .sized(0.95F, 1F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<SummonHorse>> SUMMON_HORSE = registerEntity(
            LibEntityNames.SUMMONED_HORSE,
            EntityType.Builder.of(SummonHorse::new, MobCategory.CREATURE).sized(1.3964844F, 1.6F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<SummonSkeleton>> SUMMON_SKELETON = registerEntity(LibEntityNames.SUMMONED_SKELETON,
            EntityType.Builder.<SummonSkeleton>of(SummonSkeleton::new, MobCategory.CREATURE).sized(1.0F, 1.8F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<WildenGuardian>> WILDEN_GUARDIAN = registerEntity(
            LibEntityNames.WILDEN_GUARDIAN,
            EntityType.Builder.<WildenGuardian>of(WildenGuardian::new, MobCategory.MONSTER)
                    .sized(1.15F, 1.15F)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<WildenChimera>> WILDEN_BOSS = registerEntity(
            LibEntityNames.WILDEN_CHIMERA,
            EntityType.Builder.<WildenChimera>of(WildenChimera::new, MobCategory.MONSTER)
                    .sized(2.5f, 2.25f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<LightningEntity>> LIGHTNING_ENTITY = registerEntity(LibEntityNames.AN_LIGHTNING, EntityType.Builder.of(LightningEntity::new, MobCategory.MISC)
            .sized(0.0F, 0.0F)
            .clientTrackingRange(16)
            .updateInterval(Integer.MAX_VALUE
            ).setShouldReceiveVelocityUpdates(true).setUpdateInterval(60));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDummy>> ENTITY_DUMMY = registerEntity(
            LibEntityNames.DUMMY,
            EntityType.Builder.<EntityDummy>of(EntityDummy::new, MobCategory.MISC)
                    .sized(1.0f, 2.0f)
                    .setTrackingRange(10)
                    .setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityDrygmy>> ENTITY_DRYGMY = registerEntity(
            LibEntityNames.DRYGMY,
            EntityType.Builder.<EntityDrygmy>of(EntityDrygmy::new, MobCategory.CREATURE).sized(0.6F, 0.85F).clientTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityOrbitProjectile>> ORBIT_SPELL = registerEntity(
            LibEntityNames.ORBIT_PROJECTILE,
            EntityType.Builder.<EntityOrbitProjectile>of(EntityOrbitProjectile::new, MobCategory.MISC).sized(0.5f, 0.5f).fireImmune()
                    .clientTrackingRange(20).setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityChimeraProjectile>> ENTITY_CHIMERA_SPIKE = registerEntity(
            LibEntityNames.CHIMERA_SPIKE,
            EntityType.Builder.<EntityChimeraProjectile>of(EntityChimeraProjectile::new, MobCategory.MISC)
                    .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true));
    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarStarbuncle>> ENTITY_FAMILIAR_STARBUNCLE = registerEntity(LibEntityNames.FAMILIAR_STARBUNCLE, EntityType.Builder.of(FamiliarStarbuncle::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarWixie>> ENTITY_FAMILIAR_WIXIE = registerEntity(LibEntityNames.FAMILIAR_WIXIE, EntityType.Builder.of(FamiliarWixie::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarBookwyrm>> ENTITY_FAMILIAR_BOOKWYRM = registerEntity(LibEntityNames.FAMILIAR_BOOKWYRM, EntityType.Builder.of(FamiliarBookwyrm::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarDrygmy>> ENTITY_FAMILIAR_DRYGMY = registerEntity(LibEntityNames.FAMILIAR_DRYGMY, EntityType.Builder.of(FamiliarDrygmy::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarWhirlisprig>> ENTITY_FAMILIAR_SYLPH = registerEntity(LibEntityNames.FAMILIAR_WHIRLISPRIG, EntityType.Builder.of(FamiliarWhirlisprig::new, MobCategory.CREATURE)
            .sized(0.5f, 0.5f).setTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<FamiliarAmethystGolem>> FAMILIAR_AMETHYST_GOLEM = registerEntity(LibEntityNames.FAMILIAR_AMETHYST_GOLEM, EntityType.Builder.of(FamiliarAmethystGolem::new, MobCategory.CREATURE)
            .sized(1.0f, 1.0f).setTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<EntityLingeringSpell>> LINGER_SPELL = registerEntity(
            LibEntityNames.LINGER,
            EntityType.Builder.<EntityLingeringSpell>of(EntityLingeringSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .noSave()
                    );
    public static final DeferredHolder<EntityType<?>, EntityType<EntityWallSpell>> WALL_SPELL = registerEntity(
            LibEntityNames.WALL,
            EntityType.Builder.<EntityWallSpell>of(EntityWallSpell::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .setTrackingRange(20)
                    .setShouldReceiveVelocityUpdates(true)
                    .noSave()
                    );
    public static final DeferredHolder<EntityType<?>, EntityType<WealdWalker>> ENTITY_CASCADING_WEALD = registerEntity(LibEntityNames.CASCADING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectFreeze.INSTANCE, EffectColdSnap.INSTANCE);
                walker.color = new ParticleColor(50, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<WealdWalker>> ENTITY_FLOURISHING_WEALD = registerEntity(LibEntityNames.FLOURISHING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHarm.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE, EffectSnare.INSTANCE);
                walker.color = new ParticleColor(50, 250, 55);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<WealdWalker>> ENTITY_BLAZING_WEALD = registerEntity(LibEntityNames.BLAZING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectIgnite.INSTANCE, AugmentSensitive.INSTANCE, EffectFlare.INSTANCE);
                walker.color = new ParticleColor(250, 15, 15);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<WealdWalker>> ENTITY_VEXING_WEALD = registerEntity(LibEntityNames.VEXING_WEALD_WALKER, EntityType.Builder.<WealdWalker>of((type, world) -> {
                WealdWalker walker = new WealdWalker(type, world);
                walker.spell = new Spell(MethodProjectile.INSTANCE, EffectHex.INSTANCE, EffectWither.INSTANCE, AugmentAmplify.INSTANCE, AugmentAmplify.INSTANCE);
                walker.color = new ParticleColor(250, 50, 250);
                return walker;
            }, MobCategory.CREATURE)
            .sized(1.4F, 3F).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<AmethystGolem>> AMETHYST_GOLEM = registerEntity(LibEntityNames.AMETHYST_GOLEM, EntityType.Builder.of(AmethystGolem::new, MobCategory.CREATURE)
            .sized(1.0f, 1.0f).setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<ScryerCamera>> SCRYER_CAMERA = registerEntity(LibEntityNames.SCRYER_CAMERA, EntityType.Builder.<ScryerCamera>of(ScryerCamera::new, MobCategory.MISC)
            .sized(1.0E-4F, 1.0E-4F).setTrackingRange(256).setUpdateInterval(20).setShouldReceiveVelocityUpdates(true));

    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedFallingBlock>> ENCHANTED_FALLING_BLOCK = registerEntity(
            "enchanted_falling_block", EntityType.Builder.<EnchantedFallingBlock>of(EnchantedFallingBlock::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final DeferredHolder<EntityType<?>, EntityType<IceShardEntity>> ICE_SHARD = registerEntity(
            "ice_shard", EntityType.Builder.<IceShardEntity>of(IceShardEntity::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedMageblock>> ENCHANTED_MAGE_BLOCK = registerEntity(
            "enchanted_mage_block", EntityType.Builder.<EnchantedMageblock>of(EnchantedMageblock::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));
    public static final DeferredHolder<EntityType<?>, EntityType<EnchantedSkull>> ENCHANTED_HEAD_BLOCK = registerEntity(
            "enchanted_head_block", EntityType.Builder.<EnchantedSkull>of(EnchantedSkull::new, MobCategory.MISC).sized(0.98F, 0.98F)
                    .setShouldReceiveVelocityUpdates(true)
                    .setTrackingRange(256));

    public static final DeferredHolder<EntityType<?>, EntityType<GiftStarbuncle>> GIFT_STARBY = registerEntity(LibEntityNames.GIFT_STARBY, EntityType.Builder.of(GiftStarbuncle::new, MobCategory.CREATURE)
            .sized(0.6F, 0.63F).setTrackingRange(10)
            .setShouldReceiveVelocityUpdates(true));


    public static final DeferredHolder<EntityType<?>, EntityType<AnimBlockSummon>> ANIMATED_BLOCK = registerEntity(
            "animated_block",
            EntityType.Builder.<AnimBlockSummon>of(AnimBlockSummon::new, MobCategory.MISC)
                    .sized(1.0f, 1.5f)
                    .noSave()
                    .setTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<AnimHeadSummon>> ANIMATED_HEAD = registerEntity(
            "animated_head",
            EntityType.Builder.<AnimHeadSummon>of(AnimHeadSummon::new, MobCategory.MISC)
                    .sized(1.0f, 1.5f)
                    .noSave()
                    .setTrackingRange(10));
    public static final DeferredHolder<EntityType<?>, EntityType<Lily>> LILY = registerEntity(
            LibEntityNames.LILY,
            EntityType.Builder.<Lily>of(Lily::new, MobCategory.MISC)
                    .sized(0.5F, 0.75F)
                    .setTrackingRange(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BubbleEntity>> BUBBLE = registerEntity(
            LibEntityNames.BUBBLE,
            EntityType.Builder.<BubbleEntity>of(BubbleEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.75F)
                    .setTrackingRange(10));


    @SubscribeEvent
    public static void registerPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(STARBUNCLE_TYPE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(GIFT_STARBY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(WHIRLISPRIG_TYPE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ENTITY_DRYGMY.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);

        event.register(WILDEN_GUARDIAN.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(WILDEN_HUNTER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(WILDEN_STALKER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::wildenSpawnRules, RegisterSpawnPlacementsEvent.Operation.AND);

        event.register(ENTITY_BLAZING_WEALD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ENTITY_CASCADING_WEALD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ENTITY_FLOURISHING_WEALD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ENTITY_VEXING_WEALD.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::genericGroundSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        event.register(ALAKARKINOS_TYPE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ModEntities::beachSpawn, RegisterSpawnPlacementsEvent.Operation.AND);
        LightManager.init();
    }

    public static boolean wildenSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource randomIn) {
        return worldIn.getDifficulty() != Difficulty.PEACEFUL && Monster.checkMonsterSpawnRules(type, worldIn, reason, pos, randomIn)
               && !Config.DIMENSION_BLACKLIST.get().contains(worldIn.getLevel().dimension().location().toString());
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
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
            event.put(ALAKARKINOS_TYPE.get(), Starbuncle.attributes().build());
        }
    }

    public static boolean genericGroundSpawn(EntityType<? extends Entity> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return worldIn.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) && worldIn.getRawBrightness(pos, 0) > 8;
    }

    public static boolean beachSpawn(EntityType<? extends Entity> animal, LevelAccessor worldIn, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return (worldIn.getBlockState(pos.below()).is(Blocks.GRASS_BLOCK) || worldIn.getBlockState(pos.below()).is(Blocks.SAND) || worldIn.getBlockState(pos.below()).is(Blocks.SANDSTONE)) && worldIn.getRawBrightness(pos, 0) > 8;
    }

}
