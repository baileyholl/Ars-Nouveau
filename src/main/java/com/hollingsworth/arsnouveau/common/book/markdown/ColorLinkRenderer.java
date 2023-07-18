/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import net.minecraft.network.chat.TextColor;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;

import java.util.function.Consumer;

public class ColorLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {

        //[#](55FF55)Colorful Text![#]()
        var child = link.getFirstChild();
        if (child instanceof Text t && t.getLiteral().equals("#")) {
            if (link.getDestination().isEmpty()) {
                context.setCurrentStyle(context.getCurrentStyle().withColor((TextColor) null));
            } else {
                //we use TextColor.parseColor because it fails gracefully as a color reset.
                context.setCurrentStyle(context.getCurrentStyle()
                        .withColor(TextColor.parseColor("#" + link.getDestination())));
            }
            //we do not call visit children, because color "links" should not be rendered
            return true;
        }
        return false;
    }
}
