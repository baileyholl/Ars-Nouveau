package com.hollingsworth.arsnouveau.common.util;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IRandomOutputRecipe;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemUtil {

    public static boolean canStack(ItemStack a, ItemStack b) {
        ItemStack singleA = a.copyWithCount(1);
        ItemStack singleB = b.copyWithCount(1);
        return ItemEntity.areMergable(singleA, singleB);
    }

    public static boolean shrinkHandAndAddStack(ItemStack stack, InteractionHand hand, Player player) {
        ItemStack heldStack = player.getItemInHand(hand);
        if (heldStack.isEmpty() || heldStack.getCount() == 1) {
            player.setItemInHand(hand, stack);
            return true;
        }
        if (player.inventory.add(stack)) {
            if (!player.hasInfiniteMaterials()) {
                heldStack.shrink(1);
            }
            return true;
        }
        return false;
    }

    public static void replaceItemEntity(ItemEntity entity, ItemStack stack) {
        //noinspection resource
        var level = entity.level();
        entity.setItem(stack);
        entity.setDeltaMovement(level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        entity.lifespan = entity.getAge() + 6000;
        entity.setPickUpDelay(0);
    }

    public static <R extends Recipe<I>, I extends RecipeInput> void processItems(ServerLevel level, Function<ItemStack, I> toInput, List<ItemEntity> itemEntities, int limit, GenericRecipeCache<R, I> recipeCache, @Nullable SpellResolver resolver, @Nullable SpellStats spellStats) {
        if (itemEntities.isEmpty()) {
            return;
        }

        RecipeHolder<R> recipe = null;
        int itemsProcessed = 0;

        var output = new ItemCollection();

        for (var entity : itemEntities) {
            if (itemsProcessed >= limit) {
                break;
            }

            var stack = entity.getItem();
            if (stack.isEmpty()) {
                continue;
            }

            if (recipe == null || !recipe.value().matches(toInput.apply(stack), level)) {
                recipe = recipeCache.get(level, toInput.apply(stack));
                if (recipe == null) {
                    continue;
                }
            }

            var rolls = Math.min(stack.getCount(), limit - itemsProcessed);
            if (recipe.value() instanceof IRandomOutputRecipe rand) {
                if (rand.isOutputDeterministic()) {
                    rand.rollOutputs(s -> output.add(s, s.getCount() * rolls), resolver, spellStats, level.random);
                } else {
                    for (int roll = 0; roll < rolls; roll++) {
                        rand.rollOutputs(output::add, resolver, spellStats, level.random);
                    }
                }
            } else {
                var singleOutput = recipe.value().assemble(toInput.apply(stack), level.registryAccess());
                output.add(singleOutput, singleOutput.getCount() * rolls);
            }

            stack.shrink(rolls);
            itemsProcessed += rolls;

            var outputIter = output.iterator();
            if (stack.isEmpty() && outputIter.hasNext())  {
                stack = outputIter.next();
                replaceItemEntity(entity, stack);
            }

            while (outputIter.hasNext()) {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), outputIter.next()));
            }
            output.clear();

            // Remove item entity to prevent further actions (e.g. Pickup) in the same tick from doing unnecessary work
            if (stack.isEmpty()) {
                entity.discard();
            }
        }
    }
}
