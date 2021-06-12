package com.hollingsworth.arsnouveau.common.spell.method;


import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class MethodRune extends AbstractCastMethod {
    public static MethodRune INSTANCE = new MethodRune();

    private MethodRune() {
        super(GlyphLib.MethodRuneID, "Rune");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellContext context, SpellResolver resolver) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = context.getClickedPos();
        World world = context.getLevel();
        if(world.getBlockState(pos.above()).getMaterial() == Material.AIR){
            world.setBlockAndUpdate(pos.above(), BlockRegistry.RUNE_BLOCK.defaultBlockState());
            if(world.getBlockEntity(pos.above()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getBlockEntity(pos.above());
                runeTile.isTemporary = true;
                runeTile.uuid = context.getPlayer().getUUID();
                runeTile.setParsedSpell(resolver.spell.recipe);
            }
            resolver.expendMana(context.getPlayer());
        }
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = blockRayTraceResult.getBlockPos();
        World world = caster.level;
        if(world.getBlockState(pos.above()).getMaterial() == Material.AIR){
            world.setBlockAndUpdate(pos.above(), BlockRegistry.RUNE_BLOCK.defaultBlockState());
            if(world.getBlockEntity(pos.above()) instanceof RuneTile){
                RuneTile runeTile = (RuneTile) world.getBlockEntity(pos.above());
                if(caster instanceof PlayerEntity){
                    runeTile.uuid = caster.getUUID();
                }
                runeTile.isTemporary = true;
                runeTile.recipe = resolver.spell.recipe;
            }
            resolver.expendMana(caster);
        }
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext, SpellResolver resolver) {

    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments, SpellResolver resolver) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellResolver resolver) {
        return true;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellResolver resolver) {
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

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Places a rune on the ground that will cast the spell on targets that touch the rune. Unlike runes placed by Runic Chalk, these runes are temporary " +
                "and cannot be recharged. When using Item Pickup, items are deposited into adjacent inventories.";
    }
}