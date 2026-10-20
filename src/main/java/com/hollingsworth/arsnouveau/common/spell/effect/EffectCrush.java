package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IDamageEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellSchool;
import com.hollingsworth.arsnouveau.api.spell.SpellSchools;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.spell.SpellTier;
import com.hollingsworth.arsnouveau.api.util.DamageUtil;
import com.hollingsworth.arsnouveau.api.util.GenericRecipeCache;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.CrushRecipe;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.ItemCollection;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectCrush extends AbstractEffect implements IDamageEffect {

    public static EffectCrush INSTANCE = new EffectCrush();

    private static GenericRecipeCache<CrushRecipe, SingleRecipeInput> CACHE;

    private EffectCrush() {
        super(GlyphLib.EffectCrushID, "Crush");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (spellStats.isSensitive()) {
            double aoeBuff = spellStats.getAoeMultiplier();
            int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
            int maxItemCrush = (int) (4 + (4 * aoeBuff) + (4 * pierceBuff));
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(BlockPos.containing(rayTraceResult.getLocation())).inflate(aoeBuff + 1.0));
            if (!itemEntities.isEmpty()) {
                crushItems(world, itemEntities, maxItemCrush);
            }
        } else {
            super.onResolve(rayTraceResult, world, shooter, spellStats, spellContext, resolver);
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level level, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        float damage = (float) ((rayTraceResult.getEntity().isUnderWater() ? DAMAGE.get() * 3.0 : DAMAGE.get()) + AMP_VALUE.get() * spellStats.getAmpMultiplier());
        attemptDamage(level, shooter, spellStats, spellContext, resolver, rayTraceResult.getEntity(), buildDamageSource(level, shooter), damage);
    }

    @Override
    public DamageSource buildDamageSource(Level world, LivingEntity shooter) {
        return DamageUtil.source(world, DamageTypesRegistry.CRUSH, shooter);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        CrushRecipe lastHit = null; // Cache this for AOE hits
        for (BlockPos p : SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats.getAoeMultiplier(), spellStats.getBuffCount(AugmentPierce.INSTANCE))) {
            BlockState state = world.getBlockState(p);
            Item item = state.getBlock().asItem();
            var defaultInstance = lastHit == null ? item.getDefaultInstance() : null;
            if (lastHit == null || !lastHit.matches(defaultInstance, world)) {
                var holder = getCache().get(world, new SingleRecipeInput(defaultInstance));
                lastHit = holder == null ? null : holder.value();
            }

            if (lastHit == null)
                continue;

            List<ItemStack> outputs = lastHit.getRolledOutputs(world.random);
            boolean placedBlock = false;
            for (ItemStack i : outputs) {
                if (!placedBlock && i.getItem() instanceof BlockItem blockItem && !lastHit.shouldSkipBlockPlace()) {
                    world.setBlockAndUpdate(p, blockItem.getBlock().defaultBlockState());
                    placedBlock = true;
                    i.shrink(1);
                    ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                            new Vec3(p.getX(), p.getY(), p.getZ()), rayTraceResult.getDirection(), p, false
                    ), world, shooter, spellContext, resolver);
                }
                if (!i.isEmpty()) {
                    world.addFreshEntity(new ItemEntity(world, p.getX() + 0.5, p.getY(), p.getZ() + 0.5, i));
                }
            }
            if (!placedBlock) {
                world.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                        new Vec3(p.getX(), p.getY(), p.getZ()), rayTraceResult.getDirection(), p, false
                ), world, shooter, spellContext, resolver);
            }
        }
    }


    public static void crushItems(Level world, List<ItemEntity> itemEntities, int maxItemCrush) {
        CrushRecipe lastHit = null; // Cache this for AOE hits
        int itemsCrushed = 0;
        var outputs = new ItemCollection();
        for (ItemEntity IE : itemEntities) {
            if (itemsCrushed >= maxItemCrush) {
                break;
            }

            ItemStack stack = IE.getItem();
            Item item = stack.getItem();

            var defaultInstance = lastHit == null ? item.getDefaultInstance() : null;
            if (lastHit == null || !lastHit.matches(defaultInstance, world)) {
                var holder = getCache().get(world, new SingleRecipeInput(defaultInstance));
                lastHit = holder == null ? null : holder.value();
            }

            if (lastHit == null) continue;

            if (!stack.isEmpty()) {
                var rolls = maxItemCrush - itemsCrushed;
                if (lastHit.isOutputDeterministic()) {
                    lastHit.rollOutputs(s -> outputs.add(s, s.getCount() * rolls), world.random);
                } else {
                    for (int roll = 0; roll < rolls; roll++) {
                        lastHit.rollOutputs(outputs::add, world.random);
                    }
                }
                stack.shrink(rolls);
                itemsCrushed += rolls;
            }

            var resultIter = outputs.iterator();
            while (resultIter.hasNext()) {
                world.addFreshEntity(new ItemEntity(world, IE.getX(), IE.getY(), IE.getZ(), resultIter.next()));
            }
            outputs.clear();

            // Remove item entity to prevent further actions (e.g. Pickup) in the same tick from doing unnecessary work
            if (stack.isEmpty()) {
                IE.discard();
            }
        }
    }

    private static GenericRecipeCache<CrushRecipe, SingleRecipeInput> getCache() {
        if (CACHE == null) {
            CACHE = new GenericRecipeCache<>(RecipeRegistry.CRUSH_TYPE.get(), 8);
        }

        return CACHE;
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addDamageConfig(builder, 3.0);
        addAmpConfig(builder, 1.0);
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE,
                AugmentFortune.INSTANCE, AugmentSensitive.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addDamageAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentAmplify.INSTANCE, "Increases damage dealt and the harvest level.");
        map.put(AugmentDampen.INSTANCE, "Decreases damage dealt and the harvest level.");
        map.put(AugmentSensitive.INSTANCE, "Crush will also target items nearby.");
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 2);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public String getBookDescription() {
        return "Turns stone into gravel, and gravel into sand. Will also crush flowers into bonus dye. Need Sensitive to work on items. For full recipe support, see JEI. Will also harm entities and deals bonus damage to entities that are swimming.";
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
