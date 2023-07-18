/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book.markdown;

import com.hollingsworth.arsnouveau.common.book.Book;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.commonmark.Extension;
import org.commonmark.internal.renderer.NodeRendererMap;
import org.commonmark.internal.renderer.text.ListHolder;
import org.commonmark.node.Node;
import org.commonmark.renderer.text.TextContentRenderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ComponentRenderer {

    private final List<ComponentNodeRendererFactory> nodeRendererFactories;
    private final List<LinkRenderer> linkRenderers;
    private final List<MutableComponent> components;
    private final boolean renderSoftLineBreaks;
    private final boolean replaceSoftLineBreaksWithSpace;
    private final TextColor linkColor;
    private MutableComponent currentComponent;
    private Style currentStyle;
    private ListHolder listHolder;

    private ComponentRenderer(Builder builder) {
        this.renderSoftLineBreaks = builder.renderSoftLineBreaks;
        this.replaceSoftLineBreaksWithSpace = builder.replaceSoftLineBreaksWithSpace;
        this.linkColor = builder.linkColor;
        this.currentStyle = builder.style;
        this.linkRenderers = builder.linkRenderers;

        this.components = new ArrayList<>();
        this.currentComponent = Component.translatable("");

        this.nodeRendererFactories = new ArrayList<>(builder.nodeRendererFactories.size() + 1);
        this.nodeRendererFactories.addAll(builder.nodeRendererFactories);
        // Add as last. This means clients can override the rendering of core nodes if they want.
        this.nodeRendererFactories.add(CoreComponentNodeRenderer::new);
    }

    /**
     * Create a new builder for configuring an {@link ComponentRenderer}.
     *
     * @return a builder
     */
    public static Builder builder() {
        return new Builder();
    }

    public List<MutableComponent> render(Node node, Book book) {
        RendererContext context = new RendererContext(book);
        context.render(node);
        context.cleanupPostRender();
        return context.getComponents();
    }

    /**
     * Extension for {@link ComponentRenderer}.
     */
    public interface ComponentRendererExtension extends Extension {
        void extend(Builder rendererBuilder);
    }

    /**
     * Builder for configuring an {@link TextContentRenderer}. See methods for default configuration.
     */
    public static class Builder {

        private final List<LinkRenderer> linkRenderers = new ArrayList<>();
        private final List<ComponentNodeRendererFactory> nodeRendererFactories = new ArrayList<>();
        private boolean renderSoftLineBreaks = false;
        private boolean replaceSoftLineBreaksWithSpace = true;
        private TextColor linkColor = TextColor.fromRgb(0x5555FF);
        private Style style = Style.EMPTY;

        /**
         * @return the configured {@link TextContentRenderer}
         */
        public ComponentRenderer build() {
            return new ComponentRenderer(this);
        }

        /**
         * True to render soft line breaks (deviating from MD spec). Should usually be false.
         */
        public Builder renderSoftLineBreaks(boolean renderSoftLineBreaks) {
            this.renderSoftLineBreaks = renderSoftLineBreaks;
            return this;
        }

        /**
         * True to replace soft line breaks with spaces. Should usually be true, prevents IDE line breaks from causing
         * words to be rendered without spaces inbetween.
         */
        public Builder replaceSoftLineBreaksWithSpace(boolean replaceSoftLineBreaksWithSpace) {
            this.replaceSoftLineBreaksWithSpace = replaceSoftLineBreaksWithSpace;
            return this;
        }

        /**
         * The color to use for http and book page links. Default: Blue: 0x5555FF
         */
        public Builder linkColor(TextColor linkColor) {
            this.linkColor = linkColor;
            return this;
        }

        /**
         * The style to start rendering with. Will be modified by md instructions.
         */
        public Builder style(Style style) {
            this.style = style;
            return this;
        }

        /**
         * Add a factory for instantiating a node renderer (done when rendering). This allows to override the rendering
         * of node types or define rendering for custom node types.
         * <p>
         * If multiple node renderers for the same node type are created, the one from the factory that was added first
         * "wins". (This is how the rendering for core node types can be overridden; the default rendering comes last.)
         *
         * @param nodeRendererFactory the factory for creating a node renderer
         * @return {@code this}
         */
        public Builder nodeRendererFactory(ComponentNodeRendererFactory nodeRendererFactory) {
            this.nodeRendererFactories.add(nodeRendererFactory);
            return this;
        }

        /**
         * @param extensions extensions to use on this component renderer
         * @return {@code this}
         */
        public Builder extensions(Iterable<? extends Extension> extensions) {
            for (var extension : extensions) {
                if (extension instanceof ComponentRendererExtension componentRendererExtension) {
                    componentRendererExtension.extend(this);
                }
            }
            return this;
        }

        /**
         * @param linkRenderers link renderers to use on this component renderer
         * @return {@code this}
         */
        public Builder linkRenderers(Collection<? extends LinkRenderer> linkRenderers) {
            this.linkRenderers.addAll(linkRenderers);
            return this;
        }
    }

    private class RendererContext implements ComponentNodeRendererContext {

        private final NodeRendererMap nodeRendererMap = new NodeRendererMap();
        private final Book book;

        private RendererContext(Book book) {
            this.book = book;
            // The first node renderer for a node type "wins".
            for (int i = ComponentRenderer.this.nodeRendererFactories.size() - 1; i >= 0; i--) {
                var nodeRendererFactory = ComponentRenderer.this.nodeRendererFactories.get(i);
                var nodeRenderer = nodeRendererFactory.create(this);
                this.nodeRendererMap.add(nodeRenderer);
            }
        }

        @Override
        public MutableComponent getCurrentComponent() {
            return ComponentRenderer.this.currentComponent;
        }

        @Override
        public void setCurrentComponent(MutableComponent component) {
            ComponentRenderer.this.currentComponent = component;
        }

        @Override
        public List<MutableComponent> getComponents() {
            return ComponentRenderer.this.components;
        }

        @Override
        public ListHolder getListHolder() {
            return ComponentRenderer.this.listHolder;
        }

        @Override
        public void setListHolder(ListHolder listHolder) {
            ComponentRenderer.this.listHolder = listHolder;
        }

        @Override
        public Style getCurrentStyle() {
            return ComponentRenderer.this.currentStyle;
        }

        @Override
        public void setCurrentStyle(Style style) {
            ComponentRenderer.this.currentStyle = style;
        }

        @Override
        public void render(Node node) {
            this.nodeRendererMap.render(node);
        }

        /**
         * Needs to be called after rendering to handle the last component.
         */
        @Override
        public void cleanupPostRender() {
            if (!this.isEmptyComponent()) {
                this.finalizeCurrentComponent();
            }
        }

        public boolean isEmptyComponent() {
            //translation contents have no content, they have a key (which doubles as content).
            return ((TranslatableContents) this.getCurrentComponent().getContents()).getKey().isEmpty() && this.getCurrentComponent().getSiblings().isEmpty();
        }

        public void finalizeCurrentComponent() {
            this.getComponents().add(this.getCurrentComponent());
            this.setCurrentComponent(this.getListHolder() == null ?
                    Component.translatable("") : MutableComponent.create(new ListItemContents(this.getListHolder(), "")));
        }

        @Override
        public boolean getRenderSoftLineBreaks() {
            return ComponentRenderer.this.renderSoftLineBreaks;
        }

        @Override
        public boolean getReplaceSoftLineBreaksWithSpace() {
            return ComponentRenderer.this.replaceSoftLineBreaksWithSpace;
        }

        @Override
        public TextColor getLinkColor() {
            return ComponentRenderer.this.linkColor;
        }

        @Override
        public List<LinkRenderer> getLinkRenderers() {
            return ComponentRenderer.this.linkRenderers;
        }

        @Override
        public Book getBook() {
            return this.book;
        }
    }
}
