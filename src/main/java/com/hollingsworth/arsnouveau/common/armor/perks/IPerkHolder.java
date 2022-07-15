package com.hollingsworth.arsnouveau.common.armor.perks;

import com.sun.jna.platform.unix.solaris.LibKstat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface IPerkHolder {

    default Perks getPerks(ItemStack stack)
    {
        if(!stack.hasTag() || stack.getTag().contains("arsPerks"))
            return null;

        return Perks.fromNBT(stack.getTag().getCompound("arsPerks"));
    }

    default void updatePerks(ItemStack stack, Perks perks)
    {

    }

    @OnlyIn(Dist.CLIENT)
    static void appendPerkTooltip(List<Component> tooltip, ItemStack stack, Perks perks){
        if(perks != null) {
            tooltip.add(new TranslatableComponent("tooltip.ars_nouveau.armor.perks", perks.getPerkCount(), perks.getMaxPerks(stack.getEquipmentSlot())).withStyle(ChatFormatting.LIGHT_PURPLE));

            perks.getPerks().forEach((k,v) -> {

            });
        }
    }

}
