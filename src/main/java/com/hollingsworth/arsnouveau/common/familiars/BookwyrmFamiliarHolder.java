package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class BookwyrmFamiliarHolder extends AbstractFamiliarHolder {

    public BookwyrmFamiliarHolder() {
        super(LibEntityNames.FAMILIAR_BOOKWYRM, (e) -> e instanceof EntityBookwyrm);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarBookwyrm bookwyrm = new FamiliarBookwyrm(ModEntities.ENTITY_FAMILIAR_BOOKWYRM.get(), world);
        bookwyrm.setTagData(tag);
        return bookwyrm;
    }

    @Override
    public String getBookName() {
        return "Bookwyrm";
    }

    @Override
    public String getBookDescription() {
        return "A Bookwyrm will reduce the cost of all spells cast, and increases any spell damage by 1. Obtained by performing the Ritual of Binding near a Bookwyrm.";
    }
}
