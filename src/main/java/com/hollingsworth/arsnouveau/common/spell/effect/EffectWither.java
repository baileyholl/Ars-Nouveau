package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.ANEventBus;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectWither extends AbstractEffect implements IPotionEffect {
    public static EffectWither INSTANCE = new EffectWither();

    private EffectWither() {
        super(GlyphLib.EffectWitherID, "Wither");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {

        if (rayTraceResult.getEntity() instanceof LivingEntity living) {
            this.applyConfigPotion(living, MobEffects.WITHER, spellStats);
        }

    }

    public static Map<Block, Block> SIMPLE_CONVERSION_MAP;
    static {
        SIMPLE_CONVERSION_MAP = new Object2ObjectArrayMap<>();
        SIMPLE_CONVERSION_MAP.put(Blocks.GRASS_BLOCK, Blocks.DIRT);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSS_BLOCK, Blocks.DIRT);
        SIMPLE_CONVERSION_MAP.put(Blocks.VINE, Blocks.AIR);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_COBBLESTONE, Blocks.COBBLESTONE);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.COBBLESTONE_SLAB);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_COBBLESTONE_STAIRS, Blocks.COBBLESTONE_STAIRS);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_COBBLESTONE_WALL, Blocks.COBBLESTONE_WALL);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_STONE_BRICKS, Blocks.STONE_BRICKS);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.STONE_BRICK_SLAB);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_STONE_BRICK_STAIRS, Blocks.STONE_BRICK_STAIRS);
        SIMPLE_CONVERSION_MAP.put(Blocks.MOSSY_STONE_BRICK_WALL, Blocks.STONE_BRICK_WALL);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (!(world instanceof ServerLevel level) || !spellStats.hasBuff(AugmentSensitive.INSTANCE) || !(shooter instanceof Player player)) {
            return;
        }

        double aoeBuff = spellStats.getAoeMultiplier();
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, rayTraceResult.getBlockPos(), rayTraceResult, aoeBuff, pierceBuff);

        for (var pos : posList) {
            var state = level.getBlockState(pos);
            if (ANEventBus.post(new BlockEvent.BreakEvent(world, pos, state, player))) {
                continue;
            }

            var block = state.getBlock();

            var replacement = SIMPLE_CONVERSION_MAP.get(block);
            if (replacement != null) {
                if (replacement == Blocks.AIR) {
                    level.removeBlock(pos, false);
                } else {
                    level.setBlock(pos, replacement.defaultBlockState(), 3);
                }
            }

            switch (block) {
                case TallGrassBlock ignored -> level.removeBlock(pos, false);
                case TallFlowerBlock ignored -> level.removeBlock(pos, false);
                case DoublePlantBlock ignored -> level.removeBlock(pos, false);
                case SeagrassBlock ignored -> level.removeBlock(pos, false);
                case FlowerBlock ignored -> level.removeBlock(pos, false);
                case GrowingPlantBlock ignored -> level.removeBlock(pos, false);
                case LeavesBlock ignored -> {
                    LeavesBlock.dropResources(state, level, pos);
                    level.removeBlock(pos, false);
                }
                case CropBlock crop -> {
                    var age = crop.getAge(state);
                    if (age == 0) {
                        level.removeBlock(pos, false);
                    } else {
                        level.setBlock(pos, crop.getStateForAge(age - 1), 3);
                    }
                }
                default -> {
                }
            }
        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 4);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentSensitive.INSTANCE, "Causes targeted blocks to wither away.");
        addBlockAoeAugmentDescriptions(map);
    }

    @Override
    public int getDefaultManaCost() {
        return 100;
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.THREE;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentExtendTime.INSTANCE, AugmentDurationDown.INSTANCE,
                AugmentAmplify.INSTANCE, AugmentSensitive.INSTANCE,
                AugmentAOE.INSTANCE, AugmentPierce.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Applies the Wither debuff.";
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ABJURATION);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}

