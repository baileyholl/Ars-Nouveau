/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import com.klikli_dev.modonomicon.api.ModonomiconConstants.I18n.Gui;
import com.klikli_dev.modonomicon.book.CommandLink;
import com.klikli_dev.modonomicon.book.error.BookErrorManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.ClickEvent.Action;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.commonmark.node.Link;
import org.commonmark.node.Node;

import java.util.function.Consumer;

public class CommandLinkRenderer implements LinkRenderer {
    @Override
    public boolean visit(Link link, Consumer<Node> visitChildren, ComponentNodeRendererContext context) {
        if (CommandLink.isCommandLink(link.getDestination())) {
            var currentColor = context.getCurrentStyle().getColor();

            BookErrorManager.get().setContext("Link: {}, \n{}",
                    link.getDestination(),
                    BookErrorManager.get().getContextHelper()
            );

            var commandLink = CommandLink.from(context.getBook(), link.getDestination()); //check parsing to throw error if needed

            var hoverComponent = Component.translatable(Gui.HOVER_COMMAND_LINK).withStyle(ChatFormatting.WHITE);

            //if we have a color we use it, otherwise we use link default.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor == null ? context.getLinkColor() : currentColor)
                    .withClickEvent(new ClickEvent(Action.RUN_COMMAND, link.getDestination()))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
            );

            visitChildren.accept(link);

            //links are not style instructions, so we reset to our previous color.
            context.setCurrentStyle(context.getCurrentStyle()
                    .withColor(currentColor)
                    .withClickEvent(null)
                    .withHoverEvent(null)
            );

            BookErrorManager.get().setContext(null);
            return true;
        }
        return false;

    }
}
