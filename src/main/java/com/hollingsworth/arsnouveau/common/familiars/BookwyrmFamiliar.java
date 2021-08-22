package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import net.minecraft.world.World;

public class BookwyrmFamiliar extends AbstractFamiliarHolder {
    public BookwyrmFamiliar() {
        super("bookwyrm", (e) -> e instanceof EntityWhelp);
    }

    @Override
    public IFamiliar getSummonEntity(World world) {
        return new FamiliarBookwyrm(ModEntities.ENTITY_FAMILIAR_BOOKWYRM, world);
    }
}
