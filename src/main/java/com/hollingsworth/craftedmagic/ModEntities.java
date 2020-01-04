package com.hollingsworth.craftedmagic;

import com.hollingsworth.craftedmagic.entity.EntityProjectileSpell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@GameRegistry.ObjectHolder(ExampleMod.MODID)
public class ModEntities {

//    public static void init() {
//        // Every entity in our mod has an ID (local to this mod)
//        int id = 1;
//         EntityRegistry.registerModEntity(new ResourceLocation(""), EntityProjectileSpell.class, "ProjectileSpell", id++, ExampleMod.instance, 64, 3, true);
////        EntityRegistry.registerModEntity(EntityWeirdZombie.class, "WeirdZombie", id++, ModTut.instance, 64, 3, true, 0x996600, 0x00ff00);
////
////        // We want our mob to spawn in Plains and ice plains biomes. If you don't add this then it will not spawn automatically
////        // but you can of course still make it spawn manually
////        EntityRegistry.addSpawn(EntityWeirdZombie.class, 100, 3, 5, EnumCreatureType.MONSTER, Biomes.PLAINS, Biomes.ICE_PLAINS);
////
////        // This is the loot table for our mob
////        LootTableList.register(EntityWeirdZombie.LOOT);
//    }
    @Mod.EventBusSubscriber(modid = ExampleMod.MODID)
    public static class RegistrationHandler {
    public static final int lightballID = 29;

        /**
         * Register this mod's {@link Entity} types.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityEntry> event) {
            System.out.println("Registered entitites");
//            final EntityEntry[] entries = {
//                    createBuilder("mod_projectile_spell")
//                            .entity(EntityProjectileSpell.class)
//                            .tracker(64, 20, false)
//                            .build(),
//
//
//            };
            EntityRegistry.registerModEntity(new ResourceLocation(ExampleMod.MODID, "dmlightball"),
                    EntityProjectileSpell.class, ExampleMod.MODID + ".dmlightball", lightballID, ExampleMod.instance,
                    80, 20, true);

            //event.getRegistry().registerAll(entries);

        }

        private static int entityID = 0;

        /**
         * Create an {@link EntityEntryBuilder} with the specified registry name/translation key and an automatically-assigned network ID.
         *
         * @param name The name
         * @param <E>  The entity type
         * @return The builder
         */
        private static <E extends Entity> EntityEntryBuilder<E> createBuilder(final String name) {
            final EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
            final ResourceLocation registryName = new ResourceLocation(ExampleMod.MODID, name);
            return builder.id(registryName, entityID++).name(registryName.toString());
        }
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        //RenderingRegistry.registerEntityRenderingHandler(EntityWeirdZombie.class, RenderWeirdZombie.FACTORY);
    }
}
