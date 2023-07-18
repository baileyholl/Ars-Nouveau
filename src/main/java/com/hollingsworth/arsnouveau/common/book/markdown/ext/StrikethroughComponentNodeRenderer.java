/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentNodeRendererContext;
import org.commonmark.ext.gfm.strikethrough.Strikethrough;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Collections;
import java.util.Set;

public class StrikethroughComponentNodeRenderer implements NodeRenderer {

    private final ComponentNodeRendererContext context;

    public StrikethroughComponentNodeRenderer(ComponentNodeRendererContext context) {
        this.context = context;
    }

    /**
     * Copied from StrikethroughHtmlNodeRenderer
     */
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            this.context.render(node);
            node = next;
        }
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.singleton(Strikethrough.class);
    }

    @Override
    public void render(Node node) {
        var strikethrough = this.context.getCurrentStyle().isStrikethrough();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withStrikethrough(true));
        this.visitChildren(node);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withStrikethrough(strikethrough));
    }
}
