package com.hollingsworth.arsnouveau.common.light;

import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class DynamLightUtil {


    public static int getSectionCoord(double coord) {
        return getSectionCoord(Mth.floor(coord));
    }

    public static int getSectionCoord(int coord) {
        return coord >> 4;
    }

    public static int getLuminance(Entity entity){
        if(entity instanceof EntityProjectileSpell)
            return 15;

        return entity instanceof Player player && player.getMainHandItem().getItem() == Items.TORCH ? 15 : 0;
    }

}
