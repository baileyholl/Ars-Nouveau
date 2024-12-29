package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.InteractType;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.item.inv.SlotReference;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.curios.ShapersFocus;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

public class EffectBreak extends AbstractEffect {
    public static EffectBreak INSTANCE = new EffectBreak();

    private EffectBreak() {
        super(GlyphLib.EffectBreakID, "Break");
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    public ItemStack getStack(double ampMultiplier, LivingEntity shooter, InventoryManager inventory, BlockHitResult blockHitResult) {
        ItemStack stack = shooter.getMainHandItem().copy();
        BlockState state = shooter.level.getBlockState(blockHitResult.getBlockPos());

        if (!stack.isCorrectToolForDrops(state)) {
            SlotReference tool = inventory.findItem(s -> !s.isEmpty() && s.isCorrectToolForDrops(state), InteractType.EXTRACT);
            if (!tool.isEmpty() && tool.getHandler() != null) {
                return tool.getHandler().getStackInSlot(tool.getSlot()).copy();
            }
        }

        return getDefaultTool(ampMultiplier);
    }

    public ItemStack getDefaultTool(double ampMultiplier) {
        if (ampMultiplier < 1.0) {
            return new ItemStack(Items.IRON_PICKAXE);
        } else if (ampMultiplier < 2.0) {
            return new ItemStack(Items.DIAMOND_PICKAXE);
        } else {
            return new ItemStack(Items.NETHERITE_PICKAXE);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state;

        MobEffectInstance miningFatigue = shooter.getEffect(MobEffects.DIG_SLOWDOWN);
        if (miningFatigue != null)
            spellStats.setAmpMultiplier(spellStats.getAmpMultiplier() - miningFatigue.getAmplifier());

        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, pierceBuff);

        int numFortune = spellStats.getBuffCount(AugmentFortune.INSTANCE);
        int numSilkTouch = spellStats.getBuffCount(AugmentExtract.INSTANCE);
        Holder<Enchantment> fortune = HolderHelper.unwrap(world, Enchantments.FORTUNE);
        Holder<Enchantment> silkTouch = HolderHelper.unwrap(world, Enchantments.FORTUNE);

        Map<BlockState, ItemStack> toolCache = posList.size() > 1 ? new Object2ObjectOpenHashMap<>(8) : null;
        InventoryManager manager = spellContext.getCaster().getInvManager();

        Function<BlockState, ItemStack> compute = s -> {
            ItemStack stack = getStack(spellStats.getAmpMultiplier(), shooter, manager, rayTraceResult);
            stack.set(DataComponents.UNBREAKABLE, new Unbreakable(true));

            if (numFortune > 0 && stack.getEnchantmentLevel(fortune) < numFortune) {
                stack.enchant(fortune, numFortune);
            }
            if (numSilkTouch > 0 && stack.getEnchantmentLevel(silkTouch) < numSilkTouch) {
                stack.enchant(silkTouch, numSilkTouch);
            }

            return stack;
        };

        for (int i = 0; i < posList.size(); i++) {
            BlockPos pos1 = posList.get(i);

            if (world.isOutsideBuildHeight(pos1) || world.random.nextFloat() < spellStats.getBuffCount(AugmentRandomize.INSTANCE) * 0.25F) {
                continue;
            }
            state = world.getBlockState(pos1);
            if (state.is(BlockTags.AIR)) {
                continue;
            }

            ItemStack tool;
            if (spellStats.isSensitive()) {
                tool = new ItemStack(Items.SHEARS);
            } else if (toolCache != null && i < posList.size() - 1) {
                tool = toolCache.computeIfAbsent(state, compute);
            } else {
                tool = compute.apply(state);
            }

            if (world.getBlockState(pos).getDestroySpeed(world, pos) < 0 || !tool.isCorrectToolForDrops(state) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, pos1) || state.is(BlockTagProvider.BREAK_BLACKLIST)) {
                continue;
            }

            if (!BlockUtil.breakExtraBlock((ServerLevel) world, pos1, tool, shooter.getUUID(), true)) {
                continue;
            }

            ShapersFocus.tryPropagateBlockSpell(new BlockHitResult(
                    new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), rayTraceResult.getDirection(), pos1, false
            ), world, shooter, spellContext, resolver);
        }
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return true;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE, AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE, AugmentFortune.INSTANCE,
                AugmentSensitive.INSTANCE, AugmentRandomize.INSTANCE
        );
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        addBlockAoeAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Breaks blocks with Shears instead of a pickaxe.");
        map.put(AugmentDampen.INSTANCE, "Decreases the harvest level.");
        map.put(AugmentAmplify.INSTANCE, "Increases the harvest level.");
    }

    @Override
    public String getBookDescription() {
        return "Breaks blocks of an average hardness. Can be amplified to increase the harvest level. Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentFortune.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
