package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectFell extends AbstractEffect {
    public static EffectFell INSTANCE = new EffectFell();

    public static Tag.Named<Block> FELLABLE =  BlockTags.createOptional(new ResourceLocation(ArsNouveau.MODID, "harvest/fellable"));

    private EffectFell() {
        super(GlyphLib.EffectFellID, "Fell");
    }

    @Override
    public void onResolveBlock(BlockHitResult ray, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos blockPos = ray.getBlockPos();
        BlockState state = world.getBlockState(blockPos);
        if (isTree(state)) {
            Set<BlockPos> list = getTree(world, blockPos, GENERIC_INT.get()+ AOE_BONUS.get() * spellStats.getBuffCount(AugmentAOE.INSTANCE));
            world.levelEvent(2001, blockPos, Block.getId(state));
            list.forEach(listPos -> {
                if (!BlockUtil.destroyRespectsClaim(shooter, world, listPos))
                    return;
                if (spellStats.hasBuff(AugmentExtract.INSTANCE)) {
                    world.getBlockState(listPos).getDrops(LootUtil.getSilkContext((ServerLevel) world, listPos, shooter)).forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                } else if (spellStats.hasBuff(AugmentFortune.INSTANCE)) {
                    world.getBlockState(listPos)
                            .getDrops(LootUtil.getFortuneContext((ServerLevel) world, listPos, shooter, spellStats.getBuffCount(AugmentFortune.INSTANCE)))
                            .forEach(i -> world.addFreshEntity(new ItemEntity(world, listPos.getX(), listPos.getY(), listPos.getZ(), i)));
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, false);
                } else {
                    BlockUtil.destroyBlockSafelyWithoutSound(world, listPos, true);
                }
            });
        }
    }

    public ForgeConfigSpec.IntValue AOE_BONUS;
    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 50, "Base amount of harvested blocks", "base_harvest");
        AOE_BONUS = builder.comment("Additional max blocks per AOE").defineInRange("aoe_bonus", 50, 0, Integer.MAX_VALUE);
    }

    public boolean isTree(BlockState blockstate){
        return blockstate.is(FELLABLE);
    }

    public Set<BlockPos> getTree(Level world, BlockPos start, int maxBlocks) {
        return SpellUtil.DFSBlockstates(world, start, maxBlocks, this::isTree);
    }


    @Override
    public int getDefaultManaCost() {
        return 150;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.DIAMOND_AXE;
    }

    @Override
    public SpellTier getTier() {
        return SpellTier.TWO;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
                AugmentAOE.INSTANCE,
                AugmentExtract.INSTANCE,
                AugmentFortune.INSTANCE,
                AugmentAmplify.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
        return "Harvests entire trees, mushrooms, cactus, and other vegetation. Can be amplified with Amplify to break materials of higher hardness. AOE will increase the number of blocks that may be broken at one time.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
