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

public class MethodTouch extends AbstractCastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world, ArrayList<AbstractAugment> augments) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {
        World world = context.getWorld();

        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);

        resolver.onResolveEffect(world, context.getPlayer(), res);
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {
        resolver.onResolveEffect(playerIn.getEntityWorld(), playerIn, new EntityRayTraceResult(target));
        resolver.expendMana(playerIn);
    }

    @Override
    protected String getBookDescription() {
        return "Applies spells at the block or entity that is targeted.";
    }
}
