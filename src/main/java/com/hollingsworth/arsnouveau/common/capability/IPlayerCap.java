package com.hollingsworth.arsnouveau.common.capability;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IPlayerCap {
    /*Glyph data*/

    /**
     * Returns the list of known glyphs
     */
    Collection<AbstractSpellPart> getKnownGlyphs();


    void setKnownGlyphs(Collection<AbstractSpellPart> glyphs);

    /**
     * Adds the glyph to the player data.
     *
     * @return true if the glyph was unlocked, false if they already know it.
     */
    boolean unlockGlyph(AbstractSpellPart spellPart);

    /**
     * @return true if they already know this glyph.
     */
    boolean knowsGlyph(AbstractSpellPart spellPart);

    /*Familiar data*/


    boolean unlockFamiliar(AbstractFamiliarHolder holderID);

    boolean ownsFamiliar(AbstractFamiliarHolder holderID);

    Collection<FamiliarData> getUnlockedFamiliars();

    @Nullable
    FamiliarData getFamiliarData(ResourceLocation id);

    @Nullable
    FamiliarData getLastSummonedFamiliar();

    void setLastSummonedFamiliar(ResourceLocation lastSummonedFamiliar);

    void setUnlockedFamiliars(Collection<FamiliarData> familiars);

    boolean removeFamiliar(AbstractFamiliarHolder holderID);

}
