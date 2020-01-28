package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import com.hollingsworth.craftedmagic.api.Position;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

public class MethodTouch extends CastMethod {

    public MethodTouch() {
        super(ModConfig.MethodTouchID, "Touch");
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world) {
        resolver.onResolveEffect(playerEntity.getEntityWorld(), playerEntity, null);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context) {

        World world = context.getWorld();
        if(world.isRemote) return;
        BlockRayTraceResult res = new BlockRayTraceResult(context.getHitVec(), context.getFace(), context.getPos(), false);
        resolver.onResolveEffect(world, context.getPlayer(), res);
    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        System.out.println("Cast on entity" + target);
        resolver.onResolveEffect(playerIn.getEntityWorld(), playerIn, new EntityRayTraceResult(target));
    }
}
