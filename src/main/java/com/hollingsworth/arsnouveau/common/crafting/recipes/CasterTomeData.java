package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.particle.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.registry.GlyphRegistry;
import com.hollingsworth.arsnouveau.api.registry.SpellSoundRegistry;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.registry.RegistryHelper.getRegistryName;

public class CasterTomeData implements SpecialSingleInputRecipe {

    String name;
    List<ResourceLocation> spell;
    ResourceLocation type;
    String flavorText;
    public ParticleColor particleColor;
    ConfiguredSpellSound sound;

    public CasterTomeData(String name,
                          List<ResourceLocation> spell,
                          ResourceLocation type,
                          String flavorText,
                          ConfiguredSpellSound sound,
                          ParticleColor color) {
        this.name = name;
        this.spell = spell;
        this.type = type;
        this.flavorText = flavorText;
        this.particleColor = color;
        this.sound = sound;
    }


    public CasterTomeData(String name,
                          List<ResourceLocation> spell,
                          ResourceLocation type,
                          String flavorText,
                          CompoundTag particleColor, ConfiguredSpellSound sound) {
        this(name, spell, type, flavorText, sound, ParticleColorRegistry.from(particleColor));
    }

    public static ItemStack makeTome(Item tome, String name, Spell spell, String flavorText) {
        ItemStack stack = tome.getDefaultInstance();
        ISpellCaster spellCaster = CasterUtil.getCaster(stack);
        spellCaster.setSpell(spell);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
        spellCaster.setFlavorText(flavorText);
        return stack;
    }

    @Override
    public boolean matches(SingleRecipeInput p_346065_, Level p_345375_) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider p_267052_) {
        Item tomeType = BuiltInRegistries.ITEM.get(this.type);
        if (tomeType == Items.AIR)
            tomeType = ItemsRegistry.CASTER_TOME.asItem();
        Spell spell = new Spell();
        spell.name = this.name;
        if (this.particleColor != null)
            spell.color = this.particleColor;
        for (ResourceLocation rl : this.spell) {
            AbstractSpellPart part = GlyphRegistry.getSpellpartMap().get(rl);
            if (part != null)
                spell.recipe.add(part);
        }
        spell.sound = sound;
        return makeTome(tomeType, name, spell, flavorText);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CASTER_TOME_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CASTER_TOME_TYPE.get();
    }


    public JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:caster_tome");
        jsonobject.addProperty("tome_type", type.toString());
        jsonobject.addProperty("name", name);
        jsonobject.addProperty("flavour_text", flavorText);
        JsonObject color = new JsonObject();
        CompoundTag colorData = particleColor.serialize();
        color.addProperty("type", colorData.getString("type"));
        color.addProperty("red", colorData.getInt("r"));
        color.addProperty("green", colorData.getInt("g"));
        color.addProperty("blue", colorData.getInt("b"));
        jsonobject.add("color", color);
        JsonArray array = new JsonArray();
        for (ResourceLocation part : spell) {
            array.add(part.toString());
        }
        jsonobject.add("spell", array);
        JsonObject object = new JsonObject();
        object.addProperty("family", sound.sound == null ? "default" : sound.sound.getId().toString());
        object.addProperty("pitch", sound.pitch);
        object.addProperty("volume", sound.volume);
        jsonobject.add("sound", object);
        return jsonobject;
    }

    public static class Serializer implements RecipeSerializer<CasterTomeData> {
        @Override
        public CasterTomeData fromJson(ResourceLocation recipeId, JsonObject json) {
            ResourceLocation type = json.has("tome_type") ? ResourceLocation.tryParse(json.get("tome_type").getAsString()) : ItemsRegistry.CASTER_TOME.registryObject.getId();
            String name = json.get("name").getAsString();
            String flavourText = json.has("flavour_text") ? json.get("flavour_text").getAsString() : "";
            JsonObject color = json.has("color") ? json.get("color").getAsJsonObject() : null;
            CompoundTag colorData = new CompoundTag();
            if (color != null) {
                colorData.putString("type", color.get("type").getAsString());
                colorData.putInt("r", color.get("red").getAsInt());
                colorData.putInt("g", color.get("green").getAsInt());
                colorData.putInt("b", color.get("blue").getAsInt());
            }
            JsonArray spell = GsonHelper.getAsJsonArray(json, "spell");
            List<ResourceLocation> parsedSpell = new ArrayList<>();
            for (JsonElement e : spell) {
                ResourceLocation part = ResourceLocation.tryParse(e.getAsString());
                parsedSpell.add(part);
            }
            ConfiguredSpellSound sound = ConfiguredSpellSound.DEFAULT;
            if (json.has("sound")){
                JsonObject object = json.getAsJsonObject("sound");
                SpellSound family = SpellSoundRegistry.getSpellSoundsRegistry().get(ResourceLocation.tryParse(object.get("family").getAsString()));
                sound = new ConfiguredSpellSound(family, object.get("volume").getAsFloat(), object.get("pitch").getAsFloat());
            }
            return new CasterTomeData(recipeId, name, parsedSpell, type, flavourText, colorData, sound);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, CasterTomeData recipe) {
            buf.writeItemStack(recipe.getResultItem(null), false);
        }

        @Nullable
        @Override
        public CasterTomeData fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack itemStack = buffer.readItem();
            ISpellCaster caster = CasterUtil.getCaster(itemStack);
            return new CasterTomeData(recipeId, caster.getSpellName(), caster.getSpell().recipe.stream().map(AbstractSpellPart::getRegistryName).toList(), getRegistryName(itemStack.getItem()), caster.getFlavorText(), caster.getColor().serialize(), caster.getCurrentSound());
        }
    }
}
