/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.commonmark.internal.renderer.text.BulletListHolder;
import org.commonmark.internal.renderer.text.OrderedListHolder;
import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CoreComponentNodeRenderer extends AbstractVisitor implements NodeRenderer {

    private final ComponentNodeRendererContext context;

    public CoreComponentNodeRenderer(ComponentNodeRendererContext context) {
        this.context = context;
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        //we need to return nodes here even if we don't do special handling, otherwise they will be skipped.
        return new HashSet<>(Arrays.asList(
                Document.class,
                Heading.class,
                Paragraph.class,
                BulletList.class,
                Link.class,
                ListItem.class,
                OrderedList.class,
                Emphasis.class,
                StrongEmphasis.class,
                Text.class,
                SoftLineBreak.class,
                HardLineBreak.class
        ));
    }

    public void render(Node node) {
        node.accept(this);
    }

    @Override
    public void visit(BulletList bulletList) {
        //create a new list holder with our (potential) current holder as parent
        this.context.setListHolder(new BulletListHolder(this.context.getListHolder(), bulletList));

        //render the list
        this.visitChildren(bulletList);

        //Now, if we have a parent, set it as current to handle nested lists
        if (this.context.getListHolder().getParent() != null) {
            this.context.setListHolder(this.context.getListHolder().getParent());
        } else {
            this.context.setListHolder(null);
        }

        if (bulletList.getParent() instanceof ListItem item && item.getNext() instanceof ListItem) {
            //do nothing - we are in a nested list so the next item will finalize before handling children.
        } else {
            this.context.finalizeCurrentComponent();
        }
    }

    @Override
    public void visit(Emphasis emphasis) {
        var italic = this.context.getCurrentStyle().isItalic();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withItalic(true));
        this.visitChildren(emphasis);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withItalic(italic));
    }

    @Override
    public void visit(HardLineBreak hardLineBreak) {
        //note: space-space-newline hard line breaks will usually not happen
        // due to java stripping trailing white spaces from text blocks
        // and data gen will use text blocks 99% of the time
        // one way to still get them is to use \s as the last space at the end of the line in the text block
        this.context.getCurrentComponent().append(Component.literal("\n"));

        //finalization is only required by lists currently, and they do it on their own
//        if (this.context.getListHolder() == null) {
//            this.context.finalizeCurrentComponent();
//        }
        this.visitChildren(hardLineBreak);
    }

    @Override
    public void visit(Link link) {
        for (var renderer : this.context.getLinkRenderers()) {
            if (renderer.visit(link, this::visitChildren, this.context)) {
                return;
            }
        }

        //if no link renderer, we just do a http link
        var currentColor = this.context.getCurrentStyle().getColor();
        var hoverComponent = Component.translatable(Gui.HOVER_HTTP_LINK, link.getDestination());
        //if we have a color we use it, otherwise we use link default.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor == null ? this.context.getLinkColor() : currentColor)
                .withClickEvent(new ClickEvent(Action.OPEN_URL, link.getDestination()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
        );

        this.visitChildren(link);

        //at the end of the link we reset to our previous color.
        this.context.setCurrentStyle(this.context.getCurrentStyle()
                .withColor(currentColor)
                .withClickEvent(null)
                .withHoverEvent(null)
        );
    }

    @Override
    public void visit(ListItem listItem) {
        //while hard newlines don't force a new component in a list, a new list item does.
        this.context.finalizeCurrentComponent();

        var listHolder = this.context.getListHolder();
        if (listHolder != null && listHolder instanceof OrderedListHolder orderedListHolder) {
            //List bullets/numbers will not be affected by current style
            this.context.getCurrentComponent().append(Component.translatable(
                    orderedListHolder.getIndent() + orderedListHolder.getCounter() + orderedListHolder.getDelimiter() + " ")
                    .withStyle(Style.EMPTY));

            this.visitChildren(listItem);
            orderedListHolder.increaseCounter();
        } else if (listHolder != null && listHolder instanceof BulletListHolder bulletListHolder) {
            //List bullets/numbers will not be affected by current style
            this.context.getCurrentComponent().append(Component.translatable(
                    bulletListHolder.getIndent() + bulletListHolder.getMarker() + " ")
                    .withStyle(Style.EMPTY));
            this.visitChildren(listItem);
        }
    }

    @Override
    public void visit(OrderedList orderedList) {
        //create a new list holder with our (potential) current holder as parent
        this.context.setListHolder(new OrderedListHolder(this.context.getListHolder(), orderedList));

        //render the list
        this.visitChildren(orderedList);

        //Now, if we have a parent, set it as current to handle nested lists
        if (this.context.getListHolder().getParent() != null) {
            this.context.setListHolder(this.context.getListHolder().getParent());
        } else {
            this.context.setListHolder(null);
        }

        if (orderedList.getParent() instanceof ListItem item && item.getNext() instanceof ListItem) {
            //do nothing - we are in a nested list so the next item will finalize before handling children.
        } else {
            this.context.finalizeCurrentComponent();
        }
    }

    @Override
    public void visit(SoftLineBreak softLineBreak) {
        if (this.context.getRenderSoftLineBreaks()) {
            this.context.getCurrentComponent().append("\n");
        } else if (this.context.getReplaceSoftLineBreaksWithSpace()) {
            this.context.getCurrentComponent().append(Component.literal(" "));
        }
        this.visitChildren(softLineBreak);
    }

    @Override
    public void visit(StrongEmphasis strongEmphasis) {
        var emphasis = this.context.getCurrentStyle().isBold();
        this.context.setCurrentStyle(this.context.getCurrentStyle().withBold(true));
        this.visitChildren(strongEmphasis);
        this.context.setCurrentStyle(this.context.getCurrentStyle().withBold(emphasis));

    }

    @Override
    public void visit(Text text) {
        this.context.getCurrentComponent().append(
                Component.translatable(text.getLiteral()).withStyle(this.context.getCurrentStyle()));
        this.visitChildren(text);
    }

    @Override
    protected void visitChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            this.context.render(node); //very important -> otherwise extensions will not render
            node = next;
        }
    }
}
