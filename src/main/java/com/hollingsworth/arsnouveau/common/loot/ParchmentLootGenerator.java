package com.hollingsworth.arsnouveau.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.RegistryObject;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.loot.LootProviderEvent.GLM;

public class ParchmentLootGenerator extends GlobalLootModifierProvider {
    private static final RegistryObject<DungeonLootEnhancerModifier.Serializer> DUNGEON_LOOT = GLM.register("dungeon_loot", DungeonLootEnhancerModifier.Serializer::new);
    public ParchmentLootGenerator(DataGenerator gen, String modid) {
        super(gen, modid);
    }

    @Override
    protected void start() {

    }

    private static class DungeonLootEnhancerModifier extends LootModifier {

        double chance;
        public DungeonLootEnhancerModifier(final ILootCondition[] conditionsIn, double chance) {
            super(conditionsIn);
            this.chance = chance;
        }

        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            return generatedLoot;
        }

        private static class Serializer extends GlobalLootModifierSerializer<DungeonLootEnhancerModifier> {
            @Override
            public DungeonLootEnhancerModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
                final double chance = JSONUtils.getAsFloat(object, "chance", 0.1f);
                return new DungeonLootEnhancerModifier(conditions, chance);
            }

            @Override
            public JsonObject write(DungeonLootEnhancerModifier instance) {
                final JsonObject obj = this.makeConditions(instance.conditions);
                obj.addProperty("chance", instance.chance);
                return obj;
            }
        }
    }
}
