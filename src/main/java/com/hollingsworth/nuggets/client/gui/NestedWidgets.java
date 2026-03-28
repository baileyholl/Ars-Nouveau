package com.hollingsworth.nuggets.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public interface NestedWidgets {

    default void addBeforeParent(List<AbstractWidget> widgets){
        widgets.addAll(getExtras());
    }

    @Deprecated(forRemoval = true)
    default List<AbstractWidget> getExtras(){
        return List.of();
    }

    default void addAfterParent(List<AbstractWidget> widgets){

    }
}

