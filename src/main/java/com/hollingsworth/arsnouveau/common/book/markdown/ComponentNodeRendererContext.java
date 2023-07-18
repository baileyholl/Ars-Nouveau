/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import com.hollingsworth.arsnouveau.common.book.Book;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.node.Node;

import java.util.List;

public interface ComponentNodeRendererContext {

    /**
     * The component we are currently rendering to (by appending siblings). In certain well-defined cases it will be
     * replaced with a new component and the old one added to @components
     */
    MutableComponent getCurrentComponent();

    /**
     * The component we are currently rendering to (by appending siblings). In certain well-defined cases it will be
     * replaced with a new component and the old one added to @components
     */
    void setCurrentComponent(MutableComponent component);

    /**
     * The list of components we already finished rendering. Each hard newline will cause a new component to start,
     * while list items should share a component.
     */
    List<MutableComponent> getComponents();

    /**
     * List holder is used to keep track of the current markdown (ordered or unordered) list we are rendering.
     */
    ListHolder getListHolder();

    /**
     * List holder is used to keep track of the current markdown (ordered or unordered) list we are rendering.
     */
    void setListHolder(ListHolder listHolder);

    /**
     * The style applied to the next sibling. Each markdown styling instruction will replace this with a new immutable
     * style option.
     */
    Style getCurrentStyle();

    /**
     * The style applied to the next sibling. Each markdown styling instruction will replace this with a new immutable
     * style option.
     */
    void setCurrentStyle(Style style);

    /**
     * Render the specified node and its children using the configured renderers. This should be used to render child
     * nodes; be careful not to pass the node that is being rendered, that would result in an endless loop.
     * <p>
     * IMPORTANT: call cleanupPostRender after!
     *
     * @param node the node to render
     */
    void render(Node node);

    /**
     * Needs to be called after rendering to handle the last component.
     */
    void cleanupPostRender();

    /**
     * Checks if the current component is empty and has no siblings.
     */
    boolean isEmptyComponent();

    /**
     * Archives our current component on the list of components
     */
    void finalizeCurrentComponent();

    /**
     * True to render soft line breaks (deviating from MD spec). Should usually be false.
     */
    boolean getRenderSoftLineBreaks();

    /**
     * True to replace soft line breaks with spaces. Should usually be true, prevents IDE line breaks from causing words
     * to be rendered without spaces inbetween.
     */
    boolean getReplaceSoftLineBreaksWithSpace();

    /**
     * The color to use for http and book page links. Suggested: Blue: 0x5555FF
     */
    TextColor getLinkColor();

    /**
     * Gets the link renderers for the component renderer. These are used to create additional markdown functionality by
     * (ab)using the link syntax.
     */
    List<LinkRenderer> getLinkRenderers();

    /**
     * Gets the book we are rendering.
     */
    Book getBook();
}
