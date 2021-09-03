package com.hollingsworth.arsnouveau.api.familiar;

import java.util.Collection;

public interface IFamiliarCap {

    boolean unlockFamiliar(String holderID);

    boolean ownsFamiliar(String holderID);

    Collection<String> getUnlockedFamiliars();

    void setUnlockedFamiliars(Collection<String> familiars);

    boolean removeFamiliar(String holderID);
}
