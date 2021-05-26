package com.hollingsworth.arsnouveau.common.spell.validation;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

/**
 * Base class for common logic when constructing a {@link SpellValidationError}.
 *
 * Provides logic around finding the exact localization keys and for building text components that translate both
 * the core message and the glyphs being passed as arguments.
 *
 * Subclasses of this base class will seek localization keys under
 * <code>"ars_nouveau.spell.validation.exists."</code>
 * or <code>"ars_nouveau.spell.validation.adding."</code>.
 */
public class BaseSpellValidationError implements SpellValidationError {
    private static final ITextComponent[] NO_ARGS = new ITextComponent[0];
    private final int position;
    private final AbstractSpellPart spellPart;
    private final String localizationCode;
    private final Object[] translationArguments;

    /**
     * Creates a new Spell Validation Error.
     *
     * @param position the glyph position in the overall spell that was validated.
     * @param spellPart the actual spell part in the offending position
     * @param localizationCode the localization code
     * @param translationArguments arguments to be passed to the i18n system when constructing text components for this
     *                             error.
     * @see TranslationTextComponent
     */
    public BaseSpellValidationError(int position,
                                    AbstractSpellPart spellPart,
                                    String localizationCode,
                                    ITextComponent... translationArguments) {
        this.position = position;
        this.spellPart = spellPart;
        this.localizationCode = localizationCode;
        this.translationArguments = translationArguments;
    }

    /**
     * Creates a new Spell Validation Error.
     *
     * @param position the glyph position in the overall spell that was validated.
     * @param spellPart the actual spell part in the offending position
     * @param localizationCode the localization code
     */
    public BaseSpellValidationError(int position,
                                    AbstractSpellPart spellPart,
                                    String localizationCode) {
        this(position, spellPart, localizationCode, NO_ARGS);
    }

    /**
     * Creates a new Spell Validation Error.
     *
     * @param position the glyph position in the overall spell that was validated.
     * @param spellPart the actual spell part in the offending position
     * @param localizationCode the localization code
     * @param spellParts arguments to be passed to the i18n system when constructing text components for this error.
     */
    public BaseSpellValidationError(int position,
                                    AbstractSpellPart spellPart,
                                    String localizationCode,
                                    AbstractSpellPart... spellParts) {
        this(position, spellPart, localizationCode, translateGlyphs(spellParts));
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Nullable
    @Override
    public AbstractSpellPart getSpellPart() {
        return spellPart;
    }

    @Override
    public IFormattableTextComponent makeTextComponentExisting() {
        return makeTextComponent("ars_nouveau.spell.validation.exists.");
    }
    @Override
    public IFormattableTextComponent makeTextComponentAdding() {
        return makeTextComponent("ars_nouveau.spell.validation.adding.");
    }

    private IFormattableTextComponent makeTextComponent(String keyPrefix) {
        return new TranslationTextComponent(keyPrefix + localizationCode, translationArguments);
    }

    /**
     * Translates a number of spell parts into an array of ITextComponents suitable for use as the arguments
     * parameter of the constructor.
     */
    private static ITextComponent[] translateGlyphs(AbstractSpellPart... parts) {
        ITextComponent[] textComponents = new ITextComponent[parts.length];
        for (int i = 0; i < parts.length; i++) {
            textComponents[i] = new TranslationTextComponent(parts[i].getLocalizationKey());
        }
        return textComponents;
    }
}
