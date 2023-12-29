package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public abstract class AbstractEffect extends AbstractSpellPart {

    public AbstractEffect(String tag, String description) {
        super(tag, description);
    }

    public AbstractEffect(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public Integer getTypeIndex() {
        return 10;
    }

    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult instanceof BlockHitResult blockHitResult) {
            onResolveBlock(blockHitResult, world, shooter, spellStats, spellContext, resolver);
        } else if (rayTraceResult instanceof EntityHitResult entityHitResult) {
            onResolveEntity(entityHitResult, world, shooter, spellStats, spellContext, resolver);
        }
    }

    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
    }

    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
    }

    public boolean canSummon(LivingEntity playerEntity) {
        return isRealPlayer(playerEntity) && (playerEntity.getEffect(ModPotions.SUMMONING_SICKNESS_EFFECT.get()) == null || (playerEntity instanceof Player player && player.isCreative()));
    }

    public void applySummoningSickness(LivingEntity playerEntity, int time) {
        playerEntity.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(), time));
    }

    public void summonLivingEntity(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats augments, SpellContext spellContext, @Nullable SpellResolver resolver, ISummon summon) {
        if (isRealPlayer(shooter))
            summon.setOwnerID(shooter.getUUID());
        LivingEntity summonLivingEntity = summon.getLivingEntity();
        if (summonLivingEntity != null) {
            world.addFreshEntity(summon.getLivingEntity());
            if (resolver != null && resolver.hasFocus(ItemsRegistry.SUMMONING_FOCUS.get().getDefaultInstance())) {
                SpellContext newContext = resolver.spellContext.makeChildContext();
                EntitySpellResolver spellResolver = new EntitySpellResolver(newContext.withWrappedCaster(new LivingCaster(summonLivingEntity)));
                spellResolver.onResolveEffect(world, new EntityHitResult(summonLivingEntity));
            }
        }

        MinecraftForge.EVENT_BUS.post(new SummonEvent(rayTraceResult, world, shooter, augments, spellContext, summon));
    }

    public Player getPlayer(LivingEntity entity, ServerLevel world) {
        return entity instanceof Player player ? player : ANFakePlayer.getPlayer(world);
    }

    public int getBaseHarvestLevel(SpellStats stats) {
        return (int) (3 + stats.getAmpMultiplier());
    }

    public boolean canBlockBeHarvested(SpellStats stats, Level world, BlockPos pos) {
        return BlockUtil.canBlockBeHarvested(stats, world, pos);
    }

    public Vec3 safelyGetHitPos(HitResult result) {
        return result instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity().position() : result.getLocation();
    }

    public boolean isRealPlayer(Entity entity) {
        return entity instanceof Player && isNotFakePlayer(entity);
    }

    public boolean isNotFakePlayer(Entity entity) {
        return !(entity instanceof FakePlayer);
    }

    public void applyEnchantments(SpellStats stats, ItemStack stack) {

        if (stats.hasBuff(AugmentExtract.INSTANCE)) {
            stack.enchant(Enchantments.SILK_TOUCH, 1);
        }

        if (stats.hasBuff(AugmentFortune.INSTANCE)) {
            stack.enchant(Enchantments.BLOCK_FORTUNE, stats.getBuffCount(AugmentFortune.INSTANCE));
        }
    }

    public ForgeConfigSpec.DoubleValue DAMAGE;
    public ForgeConfigSpec.DoubleValue AMP_VALUE;
    public ForgeConfigSpec.IntValue POTION_TIME;
    public ForgeConfigSpec.IntValue EXTEND_TIME;
    public ForgeConfigSpec.IntValue GENERIC_INT;
    public ForgeConfigSpec.DoubleValue GENERIC_DOUBLE;

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        Map<ResourceLocation, Integer> defaultAugmentLimits = new HashMap<>();
        addDefaultAugmentLimits(defaultAugmentLimits);
        buildAugmentLimitsConfig(builder, defaultAugmentLimits);

        Map<ResourceLocation, Integer> defaultAugmentCosts = new HashMap<>();
        addAugmentCostOverrides(defaultAugmentCosts);
        buildAugmentCostOverrideConfig(builder, defaultAugmentCosts);
        super.buildAugmentLimitsConfig(builder, getDefaultAugmentLimits(new HashMap<>()));
        super.buildInvalidCombosConfig(builder, getDefaultInvalidCombos(new HashSet<>()));
    }

    public void addDamageConfig(ForgeConfigSpec.Builder builder, double defaultValue) {
        DAMAGE = builder.defineInRange("damage", defaultValue, 0, Integer.MAX_VALUE);
    }

    public void addAmpConfig(ForgeConfigSpec.Builder builder, double defaultValue) {
        AMP_VALUE = builder.defineInRange("amplify", defaultValue, 0, Integer.MAX_VALUE);
    }

    public void addPotionConfig(ForgeConfigSpec.Builder builder, int defaultTime) {
        POTION_TIME = builder.comment("Potion duration, in seconds").defineInRange("potion_time", defaultTime, 0, Integer.MAX_VALUE);
    }

    public void addExtendTimeConfig(ForgeConfigSpec.Builder builder, int defaultTime) {
        EXTEND_TIME = builder.comment("Extend time duration, in seconds").defineInRange("extend_time", defaultTime, 0, Integer.MAX_VALUE);
    }

    public void addGenericInt(ForgeConfigSpec.Builder builder, int val, String comment, String path) {
        GENERIC_INT = builder.comment(comment).defineInRange(path, val, 0, Integer.MAX_VALUE);
    }

    public void addGenericDouble(ForgeConfigSpec.Builder builder, double val, String comment, String path) {
        GENERIC_DOUBLE = builder.comment(comment).defineInRange(path, val, 0.0, Double.MAX_VALUE);
    }

    public void addDefaultPotionConfig(ForgeConfigSpec.Builder builder) {
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    protected Set<AbstractAugment> getPotionAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAmplify.INSTANCE);
    }

    protected Set<AbstractAugment> getSummonAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }
}
