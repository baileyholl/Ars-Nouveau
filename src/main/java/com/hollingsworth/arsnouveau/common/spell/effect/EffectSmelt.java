package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.GenericRecipeCache;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectSmelt extends AbstractEffect {
    public static EffectSmelt INSTANCE = new EffectSmelt();

    private EffectSmelt() {
        super(GlyphLib.EffectSmeltID, "Smelt");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = (int) Math.round(4 * (1 + aoeBuff + pierceBuff));

        List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getEntity().blockPosition()).inflate(aoeBuff + 1.0));
        smeltItems(world, itemEntities, maxItemSmelt, spellStats);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        int maxItemSmelt = (int) Math.round(4 * (1 + aoeBuff + pierceBuff));
        if (spellStats.isSensitive()) {
            List<ItemEntity> itemEntities = world.getEntitiesOfClass(ItemEntity.class, new AABB(rayTraceResult.getBlockPos()).inflate(aoeBuff + 1.0));
            smeltItems(world, itemEntities, maxItemSmelt, spellStats);
        } else {
            List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, spellStats);

            for (BlockPos pos : posList) {
                smeltBlock(world, pos, shooter, rayTraceResult, spellStats, spellContext, resolver);
            }
        }
    }

    public static final GenericRecipeCache<SmokingRecipe, SingleRecipeInput> SMOKE_CACHE = new GenericRecipeCache<>(RecipeType.SMOKING, 8);
    public static final GenericRecipeCache<BlastingRecipe, SingleRecipeInput> BLAST_CACHE = new GenericRecipeCache<>(RecipeType.BLASTING, 8);
    public static final GenericRecipeCache<SmeltingRecipe, SingleRecipeInput> SMELT_CACHE = new GenericRecipeCache<>(RecipeType.SMELTING, 8);

    public void smeltBlock(Level world, BlockPos pos, LivingEntity shooter, BlockHitResult hitResult, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!canBlockBeHarvested(spellStats, world, pos)) return;
        BlockState state = world.getBlockState(pos);
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos)) return;
        RecipeHolder<? extends Recipe<SingleRecipeInput>> optional = SMELT_CACHE.get(world, new SingleRecipeInput(new ItemStack(state.getBlock().asItem(), 1)));
        if (optional != null) {
            ItemStack itemstack = optional.value().getResultItem(world.registryAccess());
            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() instanceof BlockItem) {
                    world.setBlockAndUpdate(pos, ((BlockItem) itemstack.getItem()).getBlock().defaultBlockState());
                } else {
                    BlockUtil.destroyBlockSafely(world, pos, false, shooter);
                    world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemstack.copy()));
                    BlockUtil.safelyUpdateState(world, pos);
                }
                ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), hitResult.getDirection(), pos, false), world, shooter, spellContext, resolver);
            }
        }
    }


    public void smeltItems(Level world, List<ItemEntity> itemEntities, int maxItemSmelt, SpellStats spellStats) {
        int numSmelted = 0;
        for (ItemEntity itemEntity : itemEntities) {
            if (numSmelted >= maxItemSmelt) break;
            RecipeHolder<? extends Recipe<SingleRecipeInput>> optional;

            if (spellStats.hasBuff(AugmentDampen.INSTANCE)) {
                optional = SMOKE_CACHE.get(world, new SingleRecipeInput(itemEntity.getItem()));
            } else if (spellStats.hasBuff(AugmentAmplify.INSTANCE)) {
                optional = BLAST_CACHE.get(world, new SingleRecipeInput(itemEntity.getItem()));
            } else {
                optional = SMELT_CACHE.get(world, new SingleRecipeInput(itemEntity.getItem()));
            }

            if (optional != null) {
                ItemStack result = optional.value().getResultItem(world.registryAccess()).copy();
                if (result.isEmpty()) continue;
                while (numSmelted < maxItemSmelt && !itemEntity.getItem().isEmpty()) {
                    itemEntity.getItem().shrink(1);
                    world.addFreshEntity(new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result.copy()));
                    numSmelted++;
                }
            }
        }
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSensitive.INSTANCE
        );
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        super.addDefaultAugmentLimits(defaults);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Only smelts items, not blocks.");
        map.put(AugmentDampen.INSTANCE, "Uses smoking recipes instead of smelting recipes.");
        map.put(AugmentAmplify.INSTANCE, "Allows smelting of blocks with higher hardness, or blasting recipes when targeting items.");
    }

    @Override
    public String getBookDescription() {
        return "Smelts blocks and items in the world. AOE will increase the number of items and radius of blocks that can be smelted at once, while Amplify will allow Smelt to work on blocks of higher hardness, Sensitive will make it only smelt items and not blocks. Dampen will cause Smelt to use smoking recipes.";
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_FIRE);
    }
}
