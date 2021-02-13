package com.hollingsworth.arsnouveau.common.spell.method;


import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.block.tile.RuneTile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
        super(GlyphLib.MethodRuneID, "Rune");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellContext context) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext) {
        BlockPos pos = context.getPos();
        World world = context.getWorld();
        if(world.getBlockState(pos.up()).getMaterial() == Material.AIR){
            world.setBlockState(pos.up(), BlockRegistry.RUNE_BLOCK.getDefaultState());
            if(world.getTileEntity(pos.up()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getTileEntity(pos.up());
                runeTile.isTemporary = true;
                runeTile.uuid = context.getPlayer().getUniqueID();
                runeTile.setParsedSpell(resolver.spell.recipe);
            }
            resolver.expendMana(context.getPlayer());
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext) {
        BlockPos pos = blockRayTraceResult.getPos();
        World world = caster.world;
        if(world.getBlockState(pos.up()).getMaterial() == Material.AIR){
            world.setBlockState(pos.up(), BlockRegistry.RUNE_BLOCK.getDefaultState());
            if(world.getTileEntity(pos.up()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getTileEntity(pos.up());
                if(caster instanceof PlayerEntity){
                    runeTile.uuid = caster.getUniqueID();
                }
                runeTile.isTemporary = true;
                runeTile.recipe = resolver.spell.recipe;
            }
            resolver.expendMana(caster);
        }
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext) {

    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        return true;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        return false;
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
    public String getBookDescription() {
        return "Places a rune on the ground that will cast the spell on targets that touch the rune. Unlike runes placed by Runic Chalk, these runes are temporary " +
                "and cannot be recharged. When using Item Pickup, items are deposited into adjacent inventories.";
    }
}