package com.hollingsworth.arsnouveau.api.registry;

import com.hollingsworth.arsnouveau.api.potion.IPotionProvider;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PotionProviderRegistry {

    private static final ConcurrentHashMap<Item, Function<ItemStack, IPotionProvider>> MAP = new ConcurrentHashMap<>();

    private static final IPotionProvider DEFAULT = new IPotionProvider() {

        @Override
        @NotNull
        public PotionContents getPotionData(ItemStack stack) {
            return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        }

        @Override
        public int usesRemaining(ItemStack stack) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null) {
                return 0;
            }
            return contents == PotionContents.EMPTY ? 0 : 1;
        }

        @Override
        public int maxUses(ItemStack stack) {
            return 1;
        }

        @Override
        public void consumeUses(ItemStack stack, int amount, @Nullable LivingEntity entity) {
            if (stack.getItem() instanceof PotionItem potionItem) {
                if (entity instanceof Player player) {
                    if (!player.hasInfiniteMaterials()) {
                        stack.shrink(1);
                    }
                    player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    stack.shrink(1);
                }
            }
        }

        @Override
        public void addUse(ItemStack stack, int amount, @Nullable LivingEntity entity) {
            if (stack.getItem() instanceof BottleItem bottleItem) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                bottle.set(DataComponents.POTION_CONTENTS, stack.get(DataComponents.POTION_CONTENTS));
                if (entity instanceof Player player) {
                    if (!player.hasInfiniteMaterials()) {
                        stack.shrink(1);
                    }
                    player.inventory.add(bottle);
                }
            }
        }

        @Override
        public void setData(PotionContents contents, int usesRemaining, int maxUses, ItemStack stack) {
            stack.set(DataComponents.POTION_CONTENTS, contents);
        }
    };

    static {
        MAP.put(ItemsRegistry.POTION_FLASK.asItem(), (stack) -> stack.get(DataComponentRegistry.MULTI_POTION));
        MAP.put(ItemsRegistry.POTION_FLASK_AMPLIFY.asItem(), (stack) -> stack.get(DataComponentRegistry.MULTI_POTION));
        MAP.put(ItemsRegistry.POTION_FLASK_EXTEND_TIME.asItem(), (stack) -> stack.get(DataComponentRegistry.MULTI_POTION));
        MAP.put(Items.POTION, (stack) -> DEFAULT);
    }

    public static @Nullable IPotionProvider from(ItemStack stack) {
        return MAP.getOrDefault(stack.getItem(), (item) -> null).apply(stack);
    }
}
