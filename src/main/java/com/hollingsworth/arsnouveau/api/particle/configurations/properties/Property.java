package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import java.util.ArrayList;
import java.util.List;

public abstract class Property<T extends Property<T>> extends BaseProperty<T> {

    public Runnable onDependenciesChanged;

    public Property(PropMap propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public Property() {
        this.propertyHolder = new PropMap();
    }

    public void setChangedListener(Runnable onDependenciesChanged) {
        this.onDependenciesChanged = onDependenciesChanged;
    }

    public List<SubProperty> subProperties(){
        return new ArrayList<>();
    }


}
