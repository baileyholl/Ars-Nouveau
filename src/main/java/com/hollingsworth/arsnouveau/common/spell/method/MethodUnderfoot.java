package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class MethodUnderfoot extends AbstractCastMethod {
    public MethodUnderfoot() {
        super(GlyphLib.MethodUnderfootID, "Underfoot");
    }

    @Override
    public void onCast(@Nullable ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments, SpellContext context) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockRayTraceResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, List<AbstractAugment> augments, SpellContext spellContext) {
        LivingEntity caster = context.getPlayer();
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockRayTraceResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments, SpellContext spellContext) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockRayTraceResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnEntity(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments, SpellContext spellContext) {
        resolver.onResolveEffect(caster.getCommandSenderWorld(), caster, new BlockRayTraceResult(caster.position, Direction.DOWN, caster.blockPosition().below(), true));
        resolver.expendMana(caster);
    }

    @Override
    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity playerEntity, World world, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments) {
        return false;
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public String getBookDescription() {
        return "Targets the spell on the block beneath the player.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.STONE_PRESSURE_PLATE;
    }
}
