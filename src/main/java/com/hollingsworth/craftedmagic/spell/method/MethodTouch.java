package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.spell.CastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class MethodTouch extends CastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world) {

    }

    @Override
    public void onCastOnBlock(ItemUseContext context) {
        World world = context.getWorld();
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        resolver.onResolveEffect(world, context.getPlayer(), res);
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        resolver.onResolveEffect(playerIn.getEntityWorld(), playerIn, new EntityRayTraceResult(target));
        resolver.expendMana(playerIn);
    }
}
