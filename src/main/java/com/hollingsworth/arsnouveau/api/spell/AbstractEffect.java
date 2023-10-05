package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.entity.ISummon;
import com.hollingsworth.arsnouveau.api.event.SpellDamageEvent;
import com.hollingsworth.arsnouveau.api.event.SummonEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;


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

    /**
     * See IPotionEffect
     */
    @Deprecated(forRemoval = true)
    public void applyConfigPotion(LivingEntity entity, MobEffect potionEffect, SpellStats spellStats) {
        applyConfigPotion(entity, potionEffect, spellStats, true);
    }

    /**
     * See IPotionEffect
     */
    @Deprecated(forRemoval = true)
    public void applyConfigPotion(LivingEntity entity, MobEffect potionEffect, SpellStats spellStats, boolean showParticles) {
        applyPotion(entity, potionEffect, spellStats, POTION_TIME == null ? 30 : POTION_TIME.get(), EXTEND_TIME == null ? 8 : EXTEND_TIME.get(), showParticles);
    }

    /**
     * See IPotionEffect
     */
    @Deprecated(forRemoval = true)
    public void applyPotion(LivingEntity entity, MobEffect potionEffect, SpellStats stats, int baseDurationSeconds, int durationBuffSeconds, boolean showParticles) {
        if (entity == null)
            return;
        int ticks = baseDurationSeconds * 20 + durationBuffSeconds * stats.getDurationInTicks();
        int amp = (int) stats.getAmpMultiplier();
        entity.addEffect(new MobEffectInstance(potionEffect, ticks, amp, false, showParticles, true));
    }

    public boolean canSummon(LivingEntity playerEntity) {
        return isRealPlayer(playerEntity) && (playerEntity.getEffect(ModPotions.SUMMONING_SICKNESS_EFFECT.get()) == null || (playerEntity instanceof Player player && player.isCreative()));
    }

    public void applySummoningSickness(LivingEntity playerEntity, int time) {
        playerEntity.addEffect(new MobEffectInstance(ModPotions.SUMMONING_SICKNESS_EFFECT.get(), time));
    }

    @Deprecated(forRemoval = true)
    public void summonLivingEntity(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats augments, SpellContext spellContext, ISummon summon) {
        summonLivingEntity(rayTraceResult, world, shooter, augments, spellContext, null, summon);
    }

    public void summonLivingEntity(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats augments, SpellContext spellContext, @Nullable SpellResolver resolver, ISummon summon) {
        if (isRealPlayer(shooter))
            summon.setOwnerID(shooter.getUUID());
        LivingEntity summonLivingEntity = summon.getLivingEntity();
        if (summonLivingEntity != null) {
            world.addFreshEntity(summon.getLivingEntity());
            if (resolver != null && resolver.hasFocus(ItemsRegistry.SUMMONING_FOCUS.get().getDefaultInstance())) {
                EntitySpellResolver spellResolver = new EntitySpellResolver(spellContext.clone().withSpell(spellContext.getRemainingSpell()).withWrappedCaster(new LivingCaster(summonLivingEntity)));
                spellResolver.onResolveEffect(world, new EntityHitResult(summonLivingEntity));
                spellContext.setCanceled(true);
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

    /**
     * See IDamageEffect
     */
    @Deprecated(forRemoval = true)
    public void dealDamage(Level world, @NotNull LivingEntity shooter, float baseDamage, SpellStats stats, Entity entity, DamageSource source) {
        if (!(world instanceof ServerLevel server) || (entity instanceof LivingEntity living && living.getHealth() <= 0))
            return;

        float totalDamage = (float) (baseDamage + stats.getDamageModifier());

        SpellDamageEvent.Pre preDamage = new SpellDamageEvent.Pre(source, shooter, entity, totalDamage, null);
        MinecraftForge.EVENT_BUS.post(preDamage);

        source = preDamage.damageSource;
        totalDamage = preDamage.damage;
        if (totalDamage <= 0 || preDamage.isCanceled())
            return;

        if (!entity.hurt(source, totalDamage)) return;
        shooter.setLastHurtMob(entity);

        SpellDamageEvent.Post postDamage = new SpellDamageEvent.Post(source, shooter, entity, totalDamage, null);
        MinecraftForge.EVENT_BUS.post(postDamage);

        if (entity instanceof LivingEntity mob && mob.getHealth() <= 0 && !mob.isRemoved() && stats.hasBuff(AugmentFortune.INSTANCE)) {
            Player playerContext = shooter instanceof Player player ? player : ANFakePlayer.getPlayer(server);
            int looting = stats.getBuffCount(AugmentFortune.INSTANCE);
            LootContext.Builder lootContext = LootUtil.getLootingContext(server, shooter, mob, looting, DamageSource.playerAttack(playerContext));
            ResourceLocation lootTable = mob.getLootTable();
            LootTable loottable = server.getServer().getLootTables().get(lootTable);
            List<ItemStack> items = loottable.getRandomItems(lootContext.create(LootContextParamSets.ENTITY));
            items.forEach(mob::spawnAtLocation);
        }

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

    @Deprecated(forRemoval = true, since = "3.4.0")
    public ItemStack getItemFromCaster(@NotNull LivingEntity shooter, SpellContext spellContext, Predicate<ItemStack> predicate) {
        if (spellContext.castingTile instanceof IInventoryResponder iInventoryResponder) {
            return iInventoryResponder.getItem(predicate);
        } else if (shooter instanceof IInventoryResponder responder) {
            return responder.getItem(predicate);
        } else if (shooter instanceof Player playerEntity) {
            NonNullList<ItemStack> list = playerEntity.inventory.items;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = list.get(i);
                if (predicate.test(stack)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Deprecated(forRemoval = true, since = "3.4.0")
    public ItemStack getItemFromCaster(@NotNull LivingEntity shooter, SpellContext spellContext, Item item) {
        return getItemFromCaster(shooter, spellContext, (i) -> i.sameItem(new ItemStack(item)));
    }

    @Deprecated(forRemoval = true, since = "3.4.0")
    public ItemStack extractStackFromCaster(@NotNull LivingEntity shooter, SpellContext spellContext, Predicate<ItemStack> predicate, int maxExtract) {
        IInventoryResponder responder = null;
        if (spellContext.castingTile instanceof IInventoryResponder) {
            responder = (IInventoryResponder) spellContext.castingTile;
        } else if (shooter instanceof IInventoryResponder) {
            responder = (IInventoryResponder) shooter;
        }
        if (responder != null) {
            return responder.extractItem(predicate, maxExtract);
        } else if (shooter instanceof Player playerEntity) {
            NonNullList<ItemStack> list = playerEntity.inventory.items;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = list.get(i);
                if (predicate.test(stack)) {
                    return stack.split(maxExtract);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Deprecated(forRemoval = true, since = "3.4.0")
    public ItemStack insertStackToCaster(@NotNull LivingEntity shooter, SpellContext spellContext, ItemStack stack) {
        IPickupResponder responder = null;
        if (spellContext.castingTile instanceof IPickupResponder) {
            responder = (IPickupResponder) spellContext.castingTile;
        } else if (shooter instanceof IInventoryResponder) {
            responder = (IPickupResponder) shooter;
        }
        if (responder != null) {
            return responder.onPickup(stack);
        }
        if (isRealPlayer(shooter)) {
            Player player = (Player) shooter;
            VoidJar.tryVoiding(player, stack);
            if (!player.addItem(stack)) {
                ItemEntity i = new ItemEntity(shooter.level, player.getX(), player.getY(), player.getZ(), stack);
                shooter.level.addFreshEntity(i);
            }
        }
        return stack;
    }

    protected Set<AbstractAugment> getPotionAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAmplify.INSTANCE);
    }

    protected Set<AbstractAugment> getSummonAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE);
    }
}
