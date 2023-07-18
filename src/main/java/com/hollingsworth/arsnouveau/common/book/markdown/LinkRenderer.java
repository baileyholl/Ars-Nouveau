/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.function.Consumer;

public interface LinkRenderer {
    /**
     * Renders a link node - used for custom functionality
     *
     * @param Link          the link node
     * @param visitChildren callback to visit children (if link text should be rendered)
     * @param context       the renderer context
     * @return true if handled, false if next link renderer (or default if none) should be called.
     */
    boolean visit(Link Link, Consumer<Node> visitChildren, ComponentNodeRendererContext context);
}
