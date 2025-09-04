package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarStarbuncle;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class StarbuncleFamiliarHolder extends AbstractFamiliarHolder {

    public StarbuncleFamiliarHolder() {
        super(LibEntityNames.FAMILIAR_STARBUNCLE, (e) -> e instanceof Starbuncle);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarStarbuncle familiarStarbuncle = new FamiliarStarbuncle(ModEntities.ENTITY_FAMILIAR_STARBUNCLE.get(), world);
        familiarStarbuncle.setTagData(tag);
        return familiarStarbuncle;
    }

    @Override
    public String getBookName() {
        return "Starbuncle";
    }

    @Override
    public String getBookDescription() {
        return "A Starbuncle familiar that will grant you Speed 2. Additionally, using a Golden Nugget on the starbuncle will consume it and grant the owner a short duration of Scrying for Gold Ore. Obtained by performing the Ritual of Binding near a Starbuncle.";
    }
}
