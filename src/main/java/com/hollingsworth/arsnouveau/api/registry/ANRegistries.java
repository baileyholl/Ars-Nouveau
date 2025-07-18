package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.api.particle.IParticleProvider;
import com.hollingsworth.arsnouveau.api.perk.IPerk;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ItemCasterProvider;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.FamiliarScript;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.PerkItem;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ANRegistries {
    public static final Registry<AbstractSpellPart> GLYPH_TYPES = create(Keys.GLYPH_TYPES).sync(true).create();
    public static final Registry<Supplier<Glyph>> GLYPH_ITEMS = create(Keys.GLYPH_ITEMS).sync(true).create();
    public static final Registry<IParticleProvider> PARTICLE_PROVIDERS = create(Keys.PARTICLE_PROVIDERS).defaultKey(ParticleColor.ID).sync(true).create();
    public static final Registry<IPerk> PERK_TYPES = create(Keys.PERK_TYPES).sync(true).create();
    public static final Registry<PerkItem> PERK_ITEMS = create(Keys.PERK_ITEMS).sync(true).create();
    public static final Registry<AbstractRitual> RITUAL_TYPES = create(Keys.RITUAL_TYPES).sync(true).create();
    public static final Registry<RitualTablet> RITUAL_TABLETS = create(Keys.RITUAL_TABLETS).sync(true).create();
    public static final Registry<SpellSound> SPELL_SOUNDS = create(Keys.SPELL_SOUNDS).sync(true).create();
    public static final Registry<ItemCasterProvider> SPELL_CASTER_TYPES = create(Keys.SPELL_CASTER_TYPES).sync(true).create();
    public static final Registry<AbstractFamiliarHolder> FAMILIAR_TYPES = create(Keys.FAMILIAR_TYPES).sync(true).create();
    public static final Registry<FamiliarScript> FAMILIAR_SCRIPTS = create(Keys.FAMILIAR_SCRIPTS).sync(true).create();

    private static <T> RegistryBuilder<T> create(ResourceKey<Registry<T>> key) {
        return new RegistryBuilder<>(key);
    }

    public static class Keys {
        public static final ResourceKey<Registry<AbstractSpellPart>> GLYPH_TYPES = key("glyph_type");
        public static final ResourceKey<Registry<Supplier<Glyph>>> GLYPH_ITEMS = key("glyph_item");
        public static final ResourceKey<Registry<IParticleProvider>> PARTICLE_PROVIDERS = key("particle_provider");
        public static final ResourceKey<Registry<IPerk>> PERK_TYPES = key("perk_type");
        public static final ResourceKey<Registry<PerkItem>> PERK_ITEMS = key("perk_item");
        public static final ResourceKey<Registry<AbstractRitual>> RITUAL_TYPES = key("ritual_type");
        public static final ResourceKey<Registry<RitualTablet>> RITUAL_TABLETS = key("ritual_tablet");
        public static final ResourceKey<Registry<SpellSound>> SPELL_SOUNDS = key("spell_sound");
        public static final ResourceKey<Registry<ItemCasterProvider>> SPELL_CASTER_TYPES = key("spell_caster_type");
        public static final ResourceKey<Registry<AbstractFamiliarHolder>> FAMILIAR_TYPES = key("familiar_type");
        public static final ResourceKey<Registry<FamiliarScript>> FAMILIAR_SCRIPTS = key("familiar_script");

        private static <T> ResourceKey<Registry<T>> key(String name) {
            return ResourceKey.createRegistryKey(ArsNouveau.prefix(name));
        }
    }
}
