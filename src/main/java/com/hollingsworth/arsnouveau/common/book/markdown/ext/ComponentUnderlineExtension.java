/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown.ext;

import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer;
import com.klikli_dev.modonomicon.client.gui.book.markdown.ComponentRenderer.Builder;
import org.commonmark.Extension;
import org.commonmark.ext.ins.internal.InsDelimiterProcessor;
import org.commonmark.parser.Parser;

public class ComponentUnderlineExtension implements Parser.ParserExtension, ComponentRenderer.ComponentRendererExtension {

    public static Extension create() {
        return new ComponentUnderlineExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new InsDelimiterProcessor());
    }

    @Override
    public void extend(Builder rendererBuilder) {
        rendererBuilder.nodeRendererFactory(UnderlineComponentNodeRenderer::new);
    }
}
