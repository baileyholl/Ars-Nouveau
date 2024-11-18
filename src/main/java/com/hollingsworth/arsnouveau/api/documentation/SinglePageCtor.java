package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;

@FunctionalInterface
public interface SinglePageCtor {
    SinglePageWidget create(BaseDocScreen parent, int x, int y, int width, int height);
}
