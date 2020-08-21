package com.hollingsworth.arsnouveau.common.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodSelf extends AbstractCastMethod {
    public MethodSelf() {
        super(ModConfig.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, LivingEntity caster, World world, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(caster.getEntityWorld(), caster, new EntityRayTraceResult(caster));
        resolver.expendMana(caster);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {

        resolver.onResolveEffect(context.getWorld(), context.getPlayer(), new EntityRayTraceResult(context.getPlayer()));
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public int getManaCost() {
        return 10;
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells on the caster.";
    }
}
