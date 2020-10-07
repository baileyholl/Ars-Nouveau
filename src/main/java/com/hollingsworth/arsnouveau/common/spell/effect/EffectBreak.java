package com.hollingsworth.arsnouveau.common.spell.effect;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LootUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;

public class EffectBreak extends AbstractEffect {

    public EffectBreak() {
        super(ModConfig.EffectBreakID, "Break");
    }

    @Override
    public int getManaCost() {
        return 10;
    }

    public float getHardness(List<AbstractAugment> augments){
        // Iron block or lower unpowered
        float maxHardness = 5.0f + 25 * getAmplificationBonus(augments);
        int buff = getAmplificationBonus(augments);
        if(buff == -1){
            maxHardness = 2.5f;
        }else if(buff == -2){
            maxHardness = 1.0f;
        }else if(buff < -2){
            maxHardness = 0.5f;
        }
        return maxHardness;
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        if(!world.isRemote && rayTraceResult instanceof BlockRayTraceResult){
            BlockPos pos = new BlockPos(((BlockRayTraceResult) rayTraceResult).getPos());
            BlockState state;
            float maxHardness = getHardness(augments);

            int aoeBuff = getBuffCount(augments, AugmentAOE.class);
            ImmutableList<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, (BlockRayTraceResult)rayTraceResult,1 + aoeBuff, 1 + aoeBuff, 1, -1);
            for(BlockPos pos1 : posList) {
                state = world.getBlockState(pos1);

                if(!(state.getBlockHardness(world, pos1) <= maxHardness && state.getBlockHardness(world, pos1) >= 0)){
                    continue;
                }

                if (hasBuff(augments, AugmentExtract.class)) {
                    ItemStack stack = LootUtil.getDefaultFakeTool();
                    stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
                    Block.spawnDrops(world.getBlockState(pos1), world, pos1, world.getTileEntity(pos1), shooter,stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                } else if (hasBuff(augments, AugmentFortune.class)) {
                    ItemStack stack = LootUtil.getDefaultFakeTool();
                    stack.addEnchantment(Enchantments.FORTUNE, getBuffCount(augments, AugmentFortune.class));
                    Block.spawnDrops(world.getBlockState(pos1), world, pos1, world.getTileEntity(pos1), shooter,stack);
                    destroyBlockSafely(world, pos1, false, shooter);
                } else {
                    destroyBlockSafely(world, pos1, true, shooter);
                }
                BlockUtil.safelyUpdateState(world, pos);
            }
        }
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_PICKAXE;
    }

    @Override
    protected String getBookDescription() {
        return "Breaks blocks of an average hardness. Can be amplified twice to break harder blocks or can break multiple blocks with the AOE augment.";
    }
}
