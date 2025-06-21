package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectCut extends AbstractEffect implements IDamageEffect {

    public static EffectCut INSTANCE = new EffectCut();

    private EffectCut() {
        super(GlyphLib.EffectCutID, "Cut");
    }

    @Override
    public boolean canDamage(LivingEntity shooter, SpellStats stats, SpellContext spellContext, SpellResolver resolver, @NotNull Entity entity) {
        return IDamageEffect.super.canDamage(shooter, stats, spellContext, resolver, entity) && !(entity instanceof IShearable);
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity entity = rayTraceResult.getEntity();
        if (entity instanceof IShearable shearable) {
            ItemStack shears = new ItemStack(Items.SHEARS);
            applyEnchantments(world, spellStats, shears);
            if (shearable.isShearable(getPlayer(shooter, (ServerLevel) world), shears, world, entity.blockPosition())) {
                // TODO: restore fortune bonus on augment
                List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, entity.blockPosition());
                items.forEach(i -> world.addFreshEntity(new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), i)));
            }
        } else {
            float damage = (float) (DAMAGE.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier());
            attemptDamage(world, shooter, spellStats, spellContext, resolver, entity, buildDamageSource(world, shooter), damage);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            if (spellStats.getBuffCount(AugmentAmplify.INSTANCE) > 0) {
                doStrip(p, rayTraceResult, world, shooter, spellStats, spellContext, resolver);
            } else {
                doShear(p, rayTraceResult, world, shooter, spellStats, spellContext, resolver);
            }
        }
    }

    private boolean dupeCheck(Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        return be != null && (world.getCapability(Capabilities.ItemHandler.BLOCK, pos, null) != null || be instanceof Container);
    }

    public void doStrip(BlockPos p, BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ItemStack axe = new ItemStack(Items.DIAMOND_AXE);
        applyEnchantments(world, spellStats, axe);
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, axe);
        // TODO Replace with AN shears
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).useItemOn(axe, world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        axe.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }

    public void doShear(BlockPos p, BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        ItemStack shears = new ItemStack(Items.SHEARS);
        applyEnchantments(world, spellStats, shears);
        if (world.getBlockState(p).getBlock() instanceof IShearable shearable && shearable.isShearable(getPlayer(shooter, (ServerLevel) world), shears, world, p)) {
            // TODO: restore fortune bonus on augment
            List<ItemStack> items = shearable.onSheared(getPlayer(shooter, (ServerLevel) world), shears, world, p);
            items.forEach(i -> world.addFreshEntity(new ItemEntity(world, p.getX(), p.getY(), p.getZ(), i)));
        }
        Player entity = ANFakePlayer.getPlayer((ServerLevel) world);
        entity.setItemInHand(InteractionHand.MAIN_HAND, shears);
        // TODO Replace with AN shears
        if (dupeCheck(world, p)) return;
        entity.setPos(p.getX(), p.getY(), p.getZ());
        world.getBlockState(p).useItemOn(shears, world, entity, InteractionHand.MAIN_HAND, rayTraceResult);
        shears.useOn(new UseOnContext(entity, InteractionHand.MAIN_HAND, rayTraceResult));
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 1.0);
        addAmpConfig(builder, 1.0);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtract.INSTANCE, AugmentFortune.INSTANCE,
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        map.put(AugmentAmplify.INSTANCE, "Simulates using an Axe instead of Shears.");
        map.put(AugmentDampen.INSTANCE, "Reduces the damage dealt.");
        map.put(AugmentExtract.INSTANCE, "Applies Silk Touch when breaking blocks.");
        map.put(AugmentFortune.INSTANCE, "Applies Fortune when breaking blocks or killing mobs.");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
    }

    @Override
    public String getBookDescription() {
        return "Simulates using shears on entities and blocks, or damages non-shearable entities for a small amount. Amplify will simulate using an Axe instead of Shears. For simulating breaking with shears, see Break and Sensitive. Costs nothing.";
    }

    @Override
    public int getDefaultManaCost() {
        return 0;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
