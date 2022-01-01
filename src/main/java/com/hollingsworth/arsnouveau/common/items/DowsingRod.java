package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DowsingRod extends ModItem{
    public DowsingRod(Properties properties) {
        super(properties);
    }

    public DowsingRod(Properties properties, String registryName) {
        super(properties, registryName);
    }

    public DowsingRod(String registryName) {
        super(ItemsRegistry.defaultItemProperties().durability(4), registryName);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldStack = pPlayer.getItemInHand(pUsedHand);
        heldStack.setDamageValue(pPlayer.getItemInHand(pUsedHand).getDamageValue() + 1);
        if(heldStack.getDamageValue() >= getMaxDamage(heldStack))
            heldStack.shrink(1);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return super.isDamageable(stack);
    }
}
