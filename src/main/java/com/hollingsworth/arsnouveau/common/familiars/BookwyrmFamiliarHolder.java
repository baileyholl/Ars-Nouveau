package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityBookwyrm;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarBookwyrm;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
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
        return "A Bookwyrm will automatically pickup nearby items and exp orbs within 5 blocks of the player. Thrown items will not be picked up. Use of the familiar hotkey is recommended to turn the magnet on and off as needed. Obtained by performing the Ritual of Binding near a Bookwyrm.";
    }
}
