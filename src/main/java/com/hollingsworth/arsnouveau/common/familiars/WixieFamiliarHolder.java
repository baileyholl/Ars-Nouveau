package com.hollingsworth.arsnouveau.common.familiars;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.familiar.IFamiliar;
import com.hollingsworth.arsnouveau.common.entity.EntityWixie;
import com.hollingsworth.arsnouveau.common.entity.familiar.FamiliarWixie;
import com.hollingsworth.arsnouveau.common.lib.LibEntityNames;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class WixieFamiliarHolder extends AbstractFamiliarHolder {
    public WixieFamiliarHolder() {
        super(LibEntityNames.FAMILIAR_WIXIE, (e) -> e instanceof EntityWixie);
    }

    @Override
    public IFamiliar getSummonEntity(Level world, CompoundTag tag) {
        FamiliarWixie wixie = new FamiliarWixie(ModEntities.ENTITY_FAMILIAR_WIXIE.get(), world);
        wixie.setTagData(tag);
        return wixie;
    }

    @Override
    public String getBookName() {
        return "Wixie";
    }

    @Override
    public String getBookDescription() {
        return "Wixies will increase the duration of potions that you apply, and wixies will accept basic potion reagents in exchange for applying the tier 1 potion result. Additionally, the Wixie will apply harmful potions to enemies during combat. Obtained by performing the Ritual of Binding near a Wixie.";
    }
}
