package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.FamiliarCarbuncle;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import net.minecraft.world.World;

public class CarbuncleFamiliar extends AbstractFamiliarHolder {

    public CarbuncleFamiliar(){
        super("carbuncle", (e) -> e instanceof EntityCarbuncle);
    }

    @Override
    public IFamiliar getSummonEntity(World world) {
        return new FamiliarCarbuncle(ModEntities.ENTITY_FAMILIAR_CARBUNCLE, world);
    }

    @Override
    public String getBookName() {
        return "Carbuncle";
    }

    @Override
    public String getBookDescription() {
        return "A carbuncle familiar that will passively grant you speed. Can be obtained by performing the Ritual of Binding with a Carbuncle nearby.";
    }
}
