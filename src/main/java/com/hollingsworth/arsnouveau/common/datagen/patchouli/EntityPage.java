package com.hollingsworth.arsnouveau.common.datagen.patchouli;

import net.minecraft.resources.ResourceLocation;

public class EntityPage extends AbstractPage {

    public EntityPage(String entity) {
        object.addProperty("entity", entity);
    }

    public EntityPage withScale(float scale) {
        object.addProperty("scale", scale);
        return this;
    }

    public EntityPage withOffset(float offset) {
        object.addProperty("offset", offset);
        return this;
    }

    public EntityPage withRotate(boolean rotate) {
        object.addProperty("rotate", rotate);
        return this;
    }

    public EntityPage withDefaultRotation(float rotation) {
        object.addProperty("default_rotation", rotation);
        return this;
    }

    public EntityPage withText(String text) {
        object.addProperty("text", text);
        return this;
    }

    @Override
    public ResourceLocation getType() {
        return new ResourceLocation("patchouli:entity");
    }
}
