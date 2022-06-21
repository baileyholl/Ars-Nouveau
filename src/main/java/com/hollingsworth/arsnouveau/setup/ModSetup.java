package com.hollingsworth.arsnouveau.setup;

import com.hollingsworth.arsnouveau.client.particle.ModParticles;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.entity.ModEntities;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.common.world.biome.ModBiomes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import software.bernie.geckolib3.GeckoLib;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Objects;

public class ModSetup {

    public static void sendIntercoms(){
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.RING.getMessageBuilder().size(2).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BELT.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("an_focus").size(1).build());
//        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.BODY.getMessageBuilder().build());
//        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.HEAD.getMessageBuilder().build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }

    public static void initGeckolib() {
        GeckoLib.initialize();
    }

    //some things need to be deferred to maintain sanity
    public static void registers(IEventBus modEventBus) {
        ItemsRegistry.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModPotions.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENTS.register(modEventBus);
        RecipeRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        RecipeRegistry.RECIPE_TYPES.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
    }

    public static void registerEvents(RegisterEvent event){
        if(event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)){
            IForgeRegistry<Block> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onBlocksRegistry(registry);
        }
        if(event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)){
            IForgeRegistry<Item> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onBlockItemsRegistry(registry);
            ItemsRegistry.onItemRegistry(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES)) {
            IForgeRegistry<BlockEntityType<?>> registry = Objects.requireNonNull(event.getForgeRegistry());
            BlockRegistry.onTileEntityRegistry(registry);
        }
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS)) {
            IForgeRegistry<SoundEvent> registry = Objects.requireNonNull(event.getForgeRegistry());
            SoundRegistry.onSoundRegistry(registry);
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
