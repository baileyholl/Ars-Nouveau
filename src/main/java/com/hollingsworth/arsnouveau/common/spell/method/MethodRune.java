package com.hollingsworth.arsnouveau.common.spell.method;


import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MethodRune extends AbstractCastMethod {
    public MethodRune() {
        super(ModConfig.MethodRuneID, "Rune");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        if(world.getBlockState(pos.up()).getMaterial() == Material.AIR){
            world.setBlockState(pos.up(), BlockRegistry.RUNE_BLOCK.getDefaultState());
            if(world.getTileEntity(pos.up()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getTileEntity(pos.up());
                runeTile.isTemporary = true;
                runeTile.setParsedSpell(resolver.spell_recipe);
            }
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        BlockPos pos = blockRayTraceResult.getPos();
        World world = caster.world;
        if(world.getBlockState(pos.up()).getMaterial() == Material.AIR){
            world.setBlockState(pos.up(), BlockRegistry.RUNE_BLOCK.getDefaultState());
            if(world.getTileEntity(pos.up()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getTileEntity(pos.up());
                runeTile.isTemporary = true;
                runeTile.recipe = resolver.spell_recipe;
            }
        }
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {

    }

    @Override
    public Item getCraftingReagent() {
        return Items.TRIPWIRE_HOOK;
    }


    @Override
    public int getManaCost() {
        return 30;
    }

    @Override
    protected String getBookDescription() {
        return "Places a rune on the ground that will cast the spell on targets that touch the rune. Unlike runes placed by Runic Chalk, these runes are temporary " +
                "and cannot be recharged.";
    }
}