package com.hollingsworth.arsnouveau.client.jei;

import java.util.Collection;

public interface AliasProvider {
    record Alias(String key, String name) {
        public String toTranslationKey() {
            return "ars_nouveau.alias." + key();
        }
    }

    Collection<Alias> getAliases();
}
