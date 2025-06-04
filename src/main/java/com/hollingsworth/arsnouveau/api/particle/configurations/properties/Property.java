package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import java.util.ArrayList;
import java.util.List;

public abstract class Property<T extends Property<T>> extends BaseProperty<T> {

    public Property(PropMap propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public Property() {
        this.propertyHolder = new PropMap();
    }

    public List<BaseProperty<?>> subProperties(){
        return new ArrayList<>();
    }


}
