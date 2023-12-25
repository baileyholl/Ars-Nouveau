package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.client.particle.ModParticles;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.DataSerializers;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.menu.MenuRegistry;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.world.Deferred;
import com.hollingsworth.arsnouveau.common.world.biome.ModBiomes;
import com.hollingsworth.arsnouveau.common.world.tree.MagicTrunkPlacer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.*;
import software.bernie.geckolib3.GeckoLib;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Objects;

import static com.hollingsworth.arsnouveau.ArsNouveau.MODID;

public class ModSetup {

    public static void sendIntercoms() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("an_focus").size(1).icon(new ResourceLocation(Curios.MODID, "slot/empty_curio_slot")).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }

    public static void initGeckolib() {
        GeckoLib.DISABLE_NETWORKING = true;
        GeckoLib.initialize();
    }

    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPE_DEFERRED_REGISTER = DeferredRegister.createOptional(Registry.TRUNK_PLACER_TYPE_REGISTRY, MODID);

    public static RegistryObject<TrunkPlacerType<MagicTrunkPlacer>> MAGIC_TRUNK_PLACER = TRUNK_PLACER_TYPE_DEFERRED_REGISTER.register("magic_trunk_placer", () -> new TrunkPlacerType<>(MagicTrunkPlacer.CODEC));

    public static void registers(IEventBus modEventBus) {
        BlockRegistry.BLOCKS.register(modEventBus);
        BlockRegistry.BLOCK_ENTITIES.register(modEventBus);
        ItemsRegistry.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModPotions.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        PerkAttributes.ATTRIBUTES.register(modEventBus);
        TRUNK_PLACER_TYPE_DEFERRED_REGISTER.register(modEventBus);
        Deferred.FEAT_REG.register(modEventBus);
        Deferred.CONFG_REG.register(modEventBus);
        Deferred.PLACED_FEAT_REG.register(modEventBus);
        LootRegistry.GLM.register(modEventBus);
        SoundRegistry.SOUND_REG.register(modEventBus);
        StructureRegistry.STRUCTURES.register(modEventBus);
        StructureRegistry.STRUCTURE_PROCESSOR.register(modEventBus);

        MenuRegistry.MENU_REG.register(modEventBus);
        VillagerRegistry.POIs.register(modEventBus);
        VillagerRegistry.VILLAGERS.register(modEventBus);
        DataSerializers.DS.register(modEventBus);

    }

    //TODO:Switch to DeferredReg where possible
    public static void registerEvents(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
            IForgeRegistry<Block> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onBlocksRegistry(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
            IForgeRegistry<Item> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onBlockItemsRegistry(registry);
            ItemsRegistry.onItemRegistry(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES)) {
            IForgeRegistry<BlockEntityType<?>> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onTileEntityRegistry(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BIOMES)) {
            IForgeRegistry<Biome> registry = Objects.requireNonNull(event.getForgeRegistry());
            ModBiomes.registerBiomes(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_STATE_PROVIDER_TYPES)) {
            IForgeRegistry<BlockStateProviderType<?>> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.registerBlockProvider(registry);
        }
    }
}
