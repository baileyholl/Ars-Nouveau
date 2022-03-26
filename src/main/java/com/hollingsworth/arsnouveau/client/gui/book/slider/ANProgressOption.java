package com.hollingsworth.arsnouveau.client.gui.book.slider;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.client.gui.BookSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.ProgressOption;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ANProgressOption extends ProgressOption {
    private final Function<Minecraft, List<FormattedCharSequence>> pubTooltipSupplier; // expose as public
    public ANProgressOption(String pCaptionKey, double pMinValue, double pMaxValue, float pSteps, Function<Options, Double> pGetter, BiConsumer<Options, Double> pSetter, BiFunction<Options, ProgressOption, Component> pToString, Function<Minecraft, List<FormattedCharSequence>> pTooltipSupplier) {
        super(pCaptionKey, pMinValue, pMaxValue, pSteps, pGetter, pSetter, pToString, pTooltipSupplier);
        pubTooltipSupplier = pTooltipSupplier;
    }

    public ANProgressOption(String pCaptionKey, double pMinValue, double pMaxValue, float pSteps, Function<Options, Double> pGetter, BiConsumer<Options, Double> pSetter, BiFunction<Options, ProgressOption, Component> pToString) {
        this(pCaptionKey, pMinValue, pMaxValue, pSteps, pGetter, pSetter, pToString, (p_168549_) -> {
            return ImmutableList.of();
        });
    }

    @Override
    public BookSlider createButton(Options pOptions, int pX, int pY, int pWidth) {
        List<FormattedCharSequence> list = this.pubTooltipSupplier.apply(Minecraft.getInstance());
        return new BookSlider(pOptions, pX, pY, pWidth, 20, this, list);
    }
}
