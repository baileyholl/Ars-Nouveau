package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.registry.ANRegistries;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractCaster;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record CasterTomeData(String name, List<ResourceLocation> spell, ResourceLocation tomeType, String flavorText,
                             ParticleColor particleColor,
                             ConfiguredSpellSound sound) implements SpecialSingleInputRecipe {


    public CasterTomeData(String name,
                          List<ResourceLocation> spell,
                          ResourceLocation type,
                          String flavorText,
                          CompoundTag particleColor, ConfiguredSpellSound sound) {
        this(name, spell, type, flavorText, ParticleColorRegistry.from(particleColor), sound);
    }

    public static ItemStack makeTome(Item tome, String name, Spell spell, String flavorText) {
        ItemStack stack = tome.getDefaultInstance();
        AbstractCaster<?> spellCaster = SpellCasterRegistry.fromOrCreate(stack);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
        spellCaster.setSpell(spell).setFlavorText(flavorText).saveToStack(stack);
        return stack;
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        Item tomeType = BuiltInRegistries.ITEM.get(this.tomeType);
        if (tomeType == Items.AIR)
            tomeType = ItemsRegistry.CASTER_TOME.asItem();
        var spell = new Spell().mutable();
        spell.name = this.name;
        if (this.particleColor != null)
            spell.color = this.particleColor;
        for (ResourceLocation rl : this.spell) {
            AbstractSpellPart part = ANRegistries.GLYPH_TYPES.get(rl);
            if (part != null)
                spell.recipe.add(part);
        }
        spell.sound = sound;
        return makeTome(tomeType, name, spell.immutable(), flavorText);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CASTER_TOME_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CASTER_TOME_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<CasterTomeData> {
        public static MapCodec<CasterTomeData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(CasterTomeData::name),
                Codec.list(ResourceLocation.CODEC).fieldOf("spell").forGetter(CasterTomeData::spell),
                ResourceLocation.CODEC.fieldOf("tome_type").forGetter(CasterTomeData::tomeType),
                Codec.STRING.fieldOf("flavour_text").forGetter(CasterTomeData::flavorText),
                ParticleColor.CODEC.fieldOf("color").forGetter(CasterTomeData::particleColor),
                ConfiguredSpellSound.CODEC.fieldOf("sound").forGetter(CasterTomeData::sound)
        ).apply(instance, CasterTomeData::new));

        public static StreamCodec<RegistryFriendlyByteBuf, CasterTomeData> STREAM = CheatSerializer.create(CODEC);

        @Override
        public MapCodec<CasterTomeData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CasterTomeData> streamCodec() {
            return STREAM;
        }
    }
}
