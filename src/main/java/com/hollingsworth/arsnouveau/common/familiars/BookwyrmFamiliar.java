package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.world.level.Level;

public class BookwyrmFamiliar extends AbstractFamiliarHolder {
    public BookwyrmFamiliar() {
        super("bookwyrm", (e) -> e instanceof EntityBookwyrm);
    }

    @Override
    public IFamiliar getSummonEntity(Level world) {
        return new FamiliarBookwyrm(ModEntities.ENTITY_FAMILIAR_BOOKWYRM, world);
    }

    @Override
    public String getBookName() {
        return "Bookwyrm";
    }

    @Override
    public String getBookDescription() {
        return "A Bookwyrm will reduce the cost of all spells cast, and increases any spell damage by 1. Obtained by performing the Ritual of Binding near a Bookwyrm.";
    }

    @Override
    public String getEntityKey() {
        return "whelp";
    }
}
