package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarDrygmy;
import net.minecraft.world.World;

public class DrygmyFamiliar extends AbstractFamiliarHolder {
    public DrygmyFamiliar() {
        super("drygmy", (e) -> e instanceof EntityDrygmy);
    }

    @Override
    public IFamiliar getSummonEntity(World world) {
        return new FamiliarDrygmy(ModEntities.ENTITY_FAMILIAR_DRYGMY, world);
    }

    @Override
    public String getBookName() {
        return "Drygmy";
    }

    @Override
    public String getBookDescription() {
        return "A Drygmy familiar will increase the damage of Earth spells by 2, and has a chance to increase the amount of looting when slaying enemies.";
    }
}
