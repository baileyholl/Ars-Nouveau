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
}
