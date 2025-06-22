package com.hollingsworth.arsnouveau.client.gui.book;

import com.hollingsworth.arsnouveau.api.documentation.DocAssets;
import com.hollingsworth.arsnouveau.api.particle.configurations.properties.BaseProperty;
import com.hollingsworth.arsnouveau.client.gui.buttons.PropertyButton;
import net.minecraft.client.gui.components.AbstractWidget;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.hollingsworth.arsnouveau.client.gui.book.BaseBook.ONE_PAGE_HEIGHT;
import static com.hollingsworth.arsnouveau.client.gui.book.BaseBook.ONE_PAGE_WIDTH;

public class PropWidgetList {
    List<PropertyButton> allButtons = new ArrayList<>();
    Consumer<PropertyButton> onPropertySelected;
    Consumer<PropertyButton> onDepenciesChanged;

    int propLeftPageOffset;
    int propRightPageOffset;
    int propTopOffset;
    private int topSelectedIndex = -1;

    public PropWidgetList(int propLeftPageOffset, int propRightPageOffset, int propTopOffset, Consumer<PropertyButton> onPropertySelected, Consumer<PropertyButton> onDepenciesChanged, @Nullable PropWidgetList oldTree) {
        this.propLeftPageOffset = propLeftPageOffset;
        this.propRightPageOffset = propRightPageOffset;
        this.propTopOffset = propTopOffset;
        this.onPropertySelected = onPropertySelected;
        this.onDepenciesChanged = onDepenciesChanged;
        if (oldTree != null) {
            this.topSelectedIndex = oldTree.topSelectedIndex;
        }
    }

    public void reset() {
        this.allButtons.clear();
        this.topSelectedIndex = -1;
    }

    public void init(List<BaseProperty<?>> props) {
        this.allButtons = getPropButtons(props, 0);
        if (topSelectedIndex != -1 && topSelectedIndex < allButtons.size()) {
            PropertyButton selectedButton = allButtons.get(topSelectedIndex);
            if (selectedButton != null)
                selectedButton.setExpanded(true);
        }
    }

    public @Nullable PropertyButton getSelectedButton() {
        if (topSelectedIndex >= 0 && topSelectedIndex < allButtons.size()) {
            return allButtons.get(topSelectedIndex);
        }
        return null;
    }

    public List<PropertyButton> getPropButtons(List<BaseProperty<?>> props, int depth) {
        List<PropertyButton> buttons = new ArrayList<>();
        if (depth > 3) {
            return buttons;
        }
        for (BaseProperty<?> property : props) {
            PropertyButton propertyButton = buildPropertyButton(property, depth);
            buttons.add(propertyButton);
            List<PropertyButton> childrenButtons = getPropButtons(property.subProperties(), depth + 1);
            propertyButton.setChildren(new ArrayList<>(childrenButtons));
            buttons.addAll(childrenButtons);
        }

        for (int i = 0; i < buttons.size(); i++) {
            PropertyButton propButton = buttons.get(i);
            propButton.index = i;
            propButton.property.setChangedListener(() -> {
                propButton.setChildren(getPropButtons(propButton.property.subProperties(), propButton.nestLevel + 1));

                onDepenciesChanged.accept(propButton);

            });
        }

        return buttons;
    }

    public PropertyButton buildPropertyButton(BaseProperty<?> property, int nestLevel) {
        DocAssets.BlitInfo texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
        DocAssets.BlitInfo selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
        switch (nestLevel) {
            case 0 -> {
                texture = DocAssets.NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.NESTED_ENTRY_BUTTON_SELECTED;
            }
            case 1 -> {
                texture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.DOUBLE_NESTED_ENTRY_BUTTON_SELECTED;
            }
            case 2 -> {
                texture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON;
                selectedTexture = DocAssets.TRIPLE_NESTED_ENTRY_BUTTON_SELECTED;
            }
            default -> {
            }
        }
        var widgetProvider = property.buildWidgets(propRightPageOffset, propTopOffset, ONE_PAGE_WIDTH, ONE_PAGE_HEIGHT);
        PropertyButton propButton = new PropertyButton(propLeftPageOffset + nestLevel * 13, 0, texture, selectedTexture, property, widgetProvider, nestLevel, b -> this.onPropertySelected((PropertyButton) b));
        return propButton;
    }

    private void onPropertySelected(PropertyButton propertyButton) {
        for (AbstractWidget widget : allButtons) {
            if (widget instanceof PropertyButton button) {
                button.setExpanded(false);
            }
        }
        propertyButton.setExpanded(true);
        this.topSelectedIndex = propertyButton.index;
        onPropertySelected.accept(propertyButton);
    }
}
