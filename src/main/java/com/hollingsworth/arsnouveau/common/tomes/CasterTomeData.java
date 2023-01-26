package com.hollingsworth.arsnouveau.common.tomes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.loot.DungeonLootTables;
import com.hollingsworth.arsnouveau.api.sound.ConfiguredSpellSound;
import com.hollingsworth.arsnouveau.api.sound.SpellSound;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.CasterUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.RecipeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.RegistryHelper.getRegistryName;

public class CasterTomeData implements Recipe<Container> {

    ResourceLocation id;
    String name;
    List<ResourceLocation> spell;
    ResourceLocation type;
    String flavorText;
    public int particleColor;
    ConfiguredSpellSound sound;


    public CasterTomeData(ResourceLocation id, String name,
                          List<ResourceLocation> spell,
                          ResourceLocation type,
                          String flavorText,
                          int particleColor, ConfiguredSpellSound sound) {
        this.name = name;
        this.spell = spell;
        this.id = id;
        this.type = type;
        this.flavorText = flavorText;
        this.particleColor = particleColor;
        this.sound = sound;
    }

    public static ItemStack makeTome(Item tome, String name, Spell spell, String flavorText) {
        ItemStack stack = tome.getDefaultInstance();
        ISpellCaster spellCaster = CasterUtil.getCaster(stack);
        spellCaster.setSpell(spell);
        stack.setHoverName(Component.literal(name).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_PURPLE).withItalic(true)));
        spellCaster.setFlavorText(flavorText);
        return stack;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        Item tomeType = ForgeRegistries.ITEMS.getValue(this.type);
        if (tomeType == null) tomeType = ItemsRegistry.CASTER_TOME.asItem();
        Spell spell = new Spell();
        spell.name = this.name;
        if (this.particleColor != -1)
            spell.color = ParticleColor.fromInt(this.particleColor);
        for (ResourceLocation rl : this.spell) {
            AbstractSpellPart part = ArsNouveauAPI.getInstance().getSpellpartMap().get(rl);
            if (part != null)
                spell.recipe.add(part);
        }
        spell.sound = sound;
        return makeTome(tomeType, name, spell, flavorText);
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.CASTER_TOME_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.CASTER_TOME_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    public JsonElement toJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("type", "ars_nouveau:caster_tome");
        jsonobject.addProperty("tome_type", type.toString());
        jsonobject.addProperty("name", name);
        jsonobject.addProperty("flavour_text", flavorText);
        jsonobject.addProperty("color", particleColor);
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
            int color = json.has("color") ? json.get("color").getAsInt() : -1;
            JsonArray spell = GsonHelper.getAsJsonArray(json, "spell");
            List<ResourceLocation> parsedSpell = new ArrayList<>();
            for (JsonElement e : spell) {
                ResourceLocation part = ResourceLocation.tryParse(e.getAsString());
                parsedSpell.add(part);
            }
            ConfiguredSpellSound sound = ConfiguredSpellSound.DEFAULT;
            if (json.has("sound")){
                JsonObject object = json.getAsJsonObject("sound");
                SpellSound family = ArsNouveauAPI.getInstance().getSpellSoundsRegistry().get(ResourceLocation.tryParse(object.get("family").getAsString()));
                sound = new ConfiguredSpellSound(family, object.get("volume").getAsFloat(), object.get("pitch").getAsFloat());
            }
            return new CasterTomeData(recipeId, name, parsedSpell, type, flavourText, color, sound);
        }


        @Override
        public void toNetwork(FriendlyByteBuf buf, CasterTomeData recipe) {
            buf.writeItemStack(recipe.getResultItem(), false);
        }

        @Nullable
        @Override
        public CasterTomeData fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            ItemStack itemStack = buffer.readItem();
            ISpellCaster caster = CasterUtil.getCaster(itemStack);
            return new CasterTomeData(recipeId, caster.getSpellName(), caster.getSpell().recipe.stream().map(AbstractSpellPart::getRegistryName).toList(), getRegistryName(itemStack.getItem()) ,caster.getFlavorText(), caster.getColor().getColor(), caster.getCurrentSound());
        }
    }
    public static void reloadCasterTomes(MinecraftServer event){
       var recipes = event.getRecipeManager().getAllRecipesFor(RecipeRegistry.CASTER_TOME_TYPE.get());
       DungeonLootTables.CASTER_TOMES = new ArrayList<>();
       recipes.forEach(tome -> DungeonLootTables.CASTER_TOMES.add(tome::getResultItem));
    }

}
