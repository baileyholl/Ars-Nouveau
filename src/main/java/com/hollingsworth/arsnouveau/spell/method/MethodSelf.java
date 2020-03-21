package com.hollingsworth.arsnouveau.spell.method;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class MethodSelf extends AbstractCastMethod {
    public MethodSelf() {
        super(ModConfig.MethodSelfID, "Self");
    }

    @Override
    public void onCast(ItemStack stack, PlayerEntity playerEntity, World world, ArrayList<AbstractAugment> augments) {
        System.out.println("On Cast");
        resolver.onResolveEffect(playerEntity.getEntityWorld(), playerEntity, new EntityRayTraceResult(playerEntity));
        resolver.expendMana(playerEntity);
    }

    @Override
    public void onCastOnBlock(ItemUseContext context, ArrayList<AbstractAugment> augments) {

        resolver.onResolveEffect(context.getWorld(), context.getPlayer(), new EntityRayTraceResult(context.getPlayer()));
        resolver.expendMana(context.getPlayer());
    }

    @Override
    public void onCastOnEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand, ArrayList<AbstractAugment> augments) {
//        resolver.onResolveEffect(playerIn.world, playerIn, new EntityRayTraceResult(playerIn));
//        resolver.expendMana(playerIn);
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
