package com.hollingsworth.arsnouveau.common.crafting.recipes;

import com.hollingsworth.arsnouveau.api.registry.SpellCasterRegistry;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.common.spell.casters.ReactiveCaster;
import com.hollingsworth.arsnouveau.common.util.HolderHelper;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.RecipeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.crafting.recipes.ReactiveEnchantmentRecipe.getParchment;

public class SpellWriteRecipe extends EnchantingApparatusRecipe implements ITextOutput {

    public SpellWriteRecipe(List<Ingredient> pedestalItems, int cost) {
        super(Ingredient.EMPTY, ItemStack.EMPTY, pedestalItems, cost, true);
    }

    @Override
    public boolean excludeJei() {
        return true;
    }

    @Override
    public boolean doesReagentMatch(ApparatusRecipeInput input, Level level, @org.jetbrains.annotations.Nullable Player player) {
        return true;
    }

    @Override
    public boolean matches(ApparatusRecipeInput input, Level level, @org.jetbrains.annotations.Nullable Player player) {
        ItemEnchantments enchantments = input.catalyst().get(DataComponents.ENCHANTMENTS);
        int level1 = enchantments.getLevel(HolderHelper.unwrap(level, EnchantmentRegistry.REACTIVE_ENCHANTMENT));
        ItemStack parchment = getParchment(input.pedestals());
        return !parchment.isEmpty() && !SpellCasterRegistry.from(parchment).getSpell().isEmpty() && level1 > 0 && super.matches(input, level, player);

    }

    @Override
    public ItemStack assemble(ApparatusRecipeInput input, HolderLookup.Provider p_346030_) {
        ItemStack parchment = getParchment(input.pedestals());
        ISpellCaster caster = SpellCasterRegistry.from(parchment);
        ItemStack result = input.catalyst().copy();
        ReactiveCaster reactiveCaster = new ReactiveCaster(result);
        reactiveCaster.setSpell(caster.getSpell());
        reactiveCaster.setColor(caster.getColor());
        return result;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeRegistry.SPELL_WRITE_TYPE.get();
    }

    @Override
    public Component getOutputComponent() {
        return Component.translatable("ars_nouveau.spell_write.book_desc");
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeRegistry.SPELL_WRITE_RECIPE.get();
    }


    public static class Serializer implements RecipeSerializer<SpellWriteRecipe> {

        public static final MapCodec<SpellWriteRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("pedestalItems").forGetter(SpellWriteRecipe::pedestalItems),
                Codec.INT.fieldOf("sourceCost").forGetter(SpellWriteRecipe::sourceCost)
        ).apply(instance, SpellWriteRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SpellWriteRecipe> STREAM = CheatSerializer.create(CODEC);

        @Override
        public MapCodec<SpellWriteRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SpellWriteRecipe> streamCodec() {
            return STREAM;
        }
    }
}
