package com.hollingsworth.arsnouveau.api.loot;

import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.common.datagen.ItemTagProvider;
import com.hollingsworth.arsnouveau.common.items.RitualTablet;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class DungeonLootTables {

    public static List<Supplier<ItemStack>> BASIC_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> UNCOMMON_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> RARE_LOOT = new ArrayList<>();
    public static List<Supplier<ItemStack>> CASTER_TOMES = new ArrayList<>();

    // /setblock ~ ~ ~ minecraft:chest{LootTable:"minecraft:chests/simple_dungeon"}
    public static Random r = new Random();

    static {
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.SOURCE_GEM.get(), 1 + r.nextInt(5)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_HORN.get(), 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_SPIKE.get(), 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(ItemsRegistry.WILDEN_WING.get(), 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> new ItemStack(BlockRegistry.SOURCEBERRY_BUSH, 1 + r.nextInt(3)));
        BASIC_LOOT.add(() -> {
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.LONG_MANA_REGEN_POTION.get());
            return stack;
        });

        BASIC_LOOT.add(() -> {
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.STRONG_MANA_REGEN_POTION.get());
            return stack;
        });

        BASIC_LOOT.add(() -> {
            ItemStack stack = new ItemStack(Items.POTION);
            PotionUtils.setPotion(stack, ModPotions.MANA_REGEN_POTION.get());
            return stack;
        });


        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WARP_SCROLL.get(), 1 + r.nextInt(2)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.STARBUNCLE_SHARD.get()));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WHIRLISPRIG_SHARDS.get()));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.DRYGMY_SHARD.get()));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.WIXIE_SHARD.get()));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.AMPLIFY_ARROW.get(), 16 + r.nextInt(16)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.SPLIT_ARROW.get(), 16 + r.nextInt(16)));
        UNCOMMON_LOOT.add(() -> new ItemStack(ItemsRegistry.PIERCE_ARROW.get(), 16 + r.nextInt(16)));

        UNCOMMON_LOOT.add(() -> {
            List<RitualTablet> tablets = RitualRegistry.getRitualItemMap().values().stream().filter(tablet -> !(new ItemStack(tablet).is(ItemTagProvider.RITUAL_LOOT_BLACKLIST))).toList();
            if(tablets.isEmpty()){
                return ItemStack.EMPTY;
            }
            return new ItemStack(tablets.get(r.nextInt(tablets.size())));
        });

        RARE_LOOT.add(() -> new ItemStack(ItemsRegistry.FIREL_DISC.get()));
        RARE_LOOT.add(() -> new ItemStack(ItemsRegistry.SOUND_OF_GLASS.get()));
        RARE_LOOT.add(() -> new ItemStack(ItemsRegistry.WILD_HUNT.get()));

    }

    public static ItemStack getRandomItem(List<Supplier<ItemStack>> pool) {
        return pool.isEmpty() ? ItemStack.EMPTY : pool.get(r.nextInt(pool.size())).get();
    }

    public static List<ItemStack> getRandomRoll(DungeonLootEnhancerModifier modifier) {
        List<ItemStack> stacks = new ArrayList<>();

        for (int i = 0; i < modifier.commonRolls; i++) {
            if (r.nextDouble() <= modifier.commonChance)
                stacks.add(getRandomItem(BASIC_LOOT));
        }

        for (int i = 0; i < modifier.uncommonRolls; i++) {
            if (r.nextDouble() <= modifier.uncommonChance)
                stacks.add(getRandomItem(UNCOMMON_LOOT));
        }

        //TODO adjust split
        for (int i = 0; i < modifier.rareRolls; i++) {
            if (r.nextDouble() <= modifier.rareChance)
                stacks.add(getRandomItem(RARE_LOOT));
        }
        if (Config.SPAWN_TOMES.get()) {
            for (int i = 0; i < modifier.rareRolls; i++) {
                if (r.nextDouble() <= modifier.rareChance)
                    stacks.add(getRandomItem(CASTER_TOMES));
            }
        }
        return stacks;
    }

}
