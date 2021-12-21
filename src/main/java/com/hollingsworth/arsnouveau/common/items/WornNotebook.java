package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.common.lib.LibItemNames;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nonnull;

public class WornNotebook extends ModItem{

    public WornNotebook() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1), LibItemNames.WORN_NOTEBOOK);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        if(playerIn instanceof ServerPlayerEntity) {
            PatchouliAPI.instance.openBookGUI((ServerPlayerEntity) playerIn, Registry.ITEM.getKey(this));
        }

        return new ActionResult<>(ActionResultType.PASS, stack);
    }
}
