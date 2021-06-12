package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.RedstoneAir;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtendTime;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectRedstone extends AbstractEffect {
    public static EffectRedstone INSTANCE = new EffectRedstone();

    private EffectRedstone() {
        super(GlyphLib.EffectRedstoneID, "Redstone Signal");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            BlockState state = BlockRegistry.REDSTONE_AIR.defaultBlockState();
            int signalModifier = getAmplificationBonus(augments) + 10;
            if(signalModifier < 1)
                signalModifier = 1;
            if(signalModifier > 15)
                signalModifier = 15;
            state = state.setValue(RedstoneAir.POWER, signalModifier);
            BlockPos pos = ((BlockRayTraceResult) rayTraceResult).getBlockPos().relative(((BlockRayTraceResult) rayTraceResult).getDirection());
            if(!(world.getBlockState(pos).getMaterial() == Material.AIR && world.getBlockState(pos).getBlock() != BlockRegistry.REDSTONE_AIR)){
                return;
            }
            int timeBonus = getBuffCount(augments, AugmentExtendTime.class);
            world.setBlockAndUpdate(pos, state);
            world.getBlockTicks().scheduleTick(pos, state.getBlock(), GENERIC_INT.get() + timeBonus * BONUS_TIME.get());

            BlockPos hitPos = pos.relative(((BlockRayTraceResult) rayTraceResult).getDirection().getOpposite());

            BlockUtil.safelyUpdateState(world, pos);
            world.updateNeighborsAt(pos, state.getBlock());
            world.updateNeighborsAt(hitPos, state.getBlock());
        }
    }


    public ForgeConfigSpec.IntValue BONUS_TIME;
    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 5, "Base time in ticks", "base_duration");
        BONUS_TIME = builder.comment("Extend time bonus, in ticks").defineInRange("extend_time", 10, 0, Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.REDSTONE_BLOCK;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Creates a brief redstone signal on a block, like a button. The signal starts at strength 10, and may be increased with Amplify, or decreased with Dampen. The duration may be extended with Extend Time.";
    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
