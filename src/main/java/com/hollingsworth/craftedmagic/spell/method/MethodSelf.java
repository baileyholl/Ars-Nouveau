package com.hollingsworth.craftedmagic.spell.method;

import com.hollingsworth.craftedmagic.ModConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

public class MethodSelf extends CastMethod{
    public MethodSelf() {
        super(ModConfig.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world) {
        resolver.onResolveEffect(playerEntity.getEntityWorld(), playerEntity, new EntityRayTraceResult(playerEntity));
    }

    @Override
    public void onCastOnBlock(ItemUseContext context) {

    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {

    }

    @Override
    public int getManaCost() {
        return 0;
    }
}
