package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarDrygmy;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class DrygmyFamiliarHolder extends AbstractFamiliarHolder {
    public DrygmyFamiliarHolder() {
        super(LibEntityNames.FAMILIAR_DRYGMY, (e) -> e instanceof EntityDrygmy);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarDrygmy familiarDrygmy = new FamiliarDrygmy(ModEntities.ENTITY_FAMILIAR_DRYGMY.get(), world);
        familiarDrygmy.setTagData(tag);
        return familiarDrygmy;
    }

    @Override
    public String getBookName() {
        return "Drygmy";
    }

    @Override
    public String getBookDescription() {
        return "A Drygmy familiar will increase the damage of Earth spells by 2, and has a chance to increase the amount of looting when slaying enemies. Obtained by performing the Ritual of Binding near a Drygmy.";
    }
}
