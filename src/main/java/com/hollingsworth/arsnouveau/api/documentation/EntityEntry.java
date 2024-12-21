package com.hollingsworth.arsnouveau.api.documentation;

import com.hollingsworth.arsnouveau.client.gui.documentation.BaseDocScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;

public class EntityEntry extends SinglePageWidget{
    EntityType<?> entityType;
    Component description;
    float scale;
    public EntityEntry(EntityType<?> entityType, Component description, float scale, BaseDocScreen parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
        this.entityType = entityType;
        this.description = description;
        this.scale = scale;
    }

    public static SinglePageCtor create(EntityType<?> entityType, Component description){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, description, 1.0f, parent, x, y, width, height);
    }

    public static SinglePageCtor create(EntityType<?> entityType, Component description, float scale){
        return (parent, x, y, width, height) -> new EntityEntry(entityType, description, scale, parent, x, y, width, height);
    }
}
