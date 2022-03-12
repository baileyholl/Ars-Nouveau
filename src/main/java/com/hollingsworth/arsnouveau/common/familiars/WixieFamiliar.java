package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWixie;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class WixieFamiliar extends AbstractFamiliarHolder {
    public WixieFamiliar() {
        super("wixie", (e) -> e instanceof EntityWixie);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarWixie wixie = new FamiliarWixie(ModEntities.ENTITY_FAMILIAR_WIXIE, world);
        wixie.setTagData(tag);
        return wixie;
    }

    @Override
    public String getBookName() {
        return "Wixie";
    }

    @Override
    public String getBookDescription() {
        return "Wixies will increases the duration of potions that you consume. Additionally, the Wixie will apply harmful potions to enemies during combat. Obtained by performing the Ritual of Binding near a Wixie.";
    }
}
