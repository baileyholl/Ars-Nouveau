package com.hollingsworth.arsnouveau.common.loot;

import com.google.gson.JsonObject;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class ParchmentLootGenerator extends GlobalLootModifierProvider {

    public ParchmentLootGenerator(DataGenerator gen, String modid) {
        super(gen, modid);
    }
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> GLM = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, ArsNouveau.MODID);
    public static final RegistryObject<DungeonLootEnhancerModifier.Serializer> DUNGEON_LOOT = GLM.register("dungeon_loot", ParchmentLootGenerator.DungeonLootEnhancerModifier.Serializer::new);

    @Override
    protected void start() {
        add("dungeon_loot", DUNGEON_LOOT.get(), new DungeonLootEnhancerModifier(  new ILootCondition[] {
                LootTableIdCondition.builder(new ResourceLocation("chests/simple_dungeon")).build()
        }));
    }

    public static class DungeonLootEnhancerModifier extends LootModifier {

        double chance;
        public DungeonLootEnhancerModifier(final ILootCondition[] conditionsIn, double chance) {
            super(conditionsIn);
            this.chance = chance;
        }

        public DungeonLootEnhancerModifier(final ILootCondition[] conditionsIn) {
            super(conditionsIn);
            this.chance = 0.1f;
        }

        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            generatedLoot.add(new ItemStack(ItemsRegistry.EXPERIENCE_GEM));
            System.out.println("hi");
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<DungeonLootEnhancerModifier> {
            @Override
            public DungeonLootEnhancerModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
                return new DungeonLootEnhancerModifier(conditions, 0.1f);
            }

            @Override
            public JsonObject write(DungeonLootEnhancerModifier instance) {
                final JsonObject obj = this.makeConditions(instance.conditions);
                return obj;
            }
        }
    }
}
