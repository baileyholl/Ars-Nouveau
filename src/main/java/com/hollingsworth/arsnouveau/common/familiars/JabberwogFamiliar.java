package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarCarbuncle;
import net.minecraft.world.level.Level;

public class JabberwogFamiliar extends AbstractFamiliarHolder {

    public JabberwogFamiliar(){
        super("jabberwog", (e) -> e instanceof Starbuncle);
    }

    @Override
    public IFamiliar getSummonEntity(Level world) {
        return new FamiliarCarbuncle(ModEntities.ENTITY_FAMILIAR_CARBUNCLE, world);
    }

    @Override
    public String getBookName() {
        return "Jabberwog";
    }

    @Override
    public String getBookDescription() {
        return "";
    }
}
