/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;

public class BookTextRenderer {

//    private final Parser markdownParser;
//
//    private final List<Extension> extensions;

    private final Book book;

    public BookTextRenderer(Book book) {
        //TODO: make parser configurable for modders
//        this.extensions = List.of(ComponentStrikethroughExtension.create(), ComponentUnderlineExtension.create());
//        this.markdownParser = Parser.builder()
//                .extensions(this.extensions)
//                .build();
        this.book = book;
    }

    public List<MutableComponent> render(String markdown) {
        return this.render(markdown, Style.EMPTY);
    }

    public List<MutableComponent> render(String markdown, Style defaultStyle) {
        //TODO: make renderer configurable for modders

//        //renderer needs to be instantiated every time, because it caches the results
//        var renderer = new ComponentRenderer.Builder()
//                .renderSoftLineBreaks(false)
//                .replaceSoftLineBreaksWithSpace(true)
//                .linkColor(TextColor.fromRgb(0x5555FF))
//                .linkRenderers(List.of(new ColorLinkRenderer(), new BookLinkRenderer(), new ItemLinkRenderer(), new PatchouliLinkRenderer(), new CommandLinkRenderer()))
//                .style(defaultStyle)
//                .extensions(this.extensions)
//                .build();
//
//        var document = this.markdownParser.parse(markdown);
//
//        return renderer.render(document, this.book);
        return new ArrayList<>();
    }
}
