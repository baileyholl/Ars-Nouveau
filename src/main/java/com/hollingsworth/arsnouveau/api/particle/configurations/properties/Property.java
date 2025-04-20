package com.hollingsworth.arsnouveau.api.particle.configurations.properties;

import java.util.ArrayList;
import java.util.List;

public abstract class Property extends BaseProperty {

    public Runnable onDependenciesChanged;

    public Property(PropertyHolder propertyHolder) {
        super(propertyHolder);
    }

    public void setChangedListener(Runnable onDependenciesChanged) {
        this.onDependenciesChanged = onDependenciesChanged;
    }

    public List<SubProperty> subProperties(){
        return new ArrayList<>();
    }


}
