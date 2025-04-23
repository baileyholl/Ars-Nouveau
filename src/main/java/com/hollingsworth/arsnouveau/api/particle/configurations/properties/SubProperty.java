package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

public abstract class SubProperty<T extends SubProperty<T>> extends BaseProperty<T> {

    public SubProperty(PropMap propertyHolder) {
        super(propertyHolder);
    }

    public SubProperty() {
        super();
    }
}
