package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(ArsNouveau.MODID)
public class ModEntities {


    public static EntityType<EntityProjectileSpell> SPELL_PROJ = null;


    public static EntityType<EntityAllyVex> ALLY_VEX = null;


    public static EntityType<EntityEvokerFangs> ENTITY_EVOKER_FANGS_ENTITY_TYPE = null;

    public static EntityType<EntityWhelp> ENTITY_WHELP_TYPE = null;


    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Entity} types.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerEntities(final RegistryEvent.Register<EntityType<?>> event) {
            SPELL_PROJ = build(
                    "spell_proj",
                    EntityType.Builder.<EntityProjectileSpell>create(EntityProjectileSpell::new, EntityClassification.MISC)
                            .size(0.5f, 0.5f)
                            .setTrackingRange(10)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(60).setCustomClientFactory(EntityProjectileSpell::new));
            ENTITY_EVOKER_FANGS_ENTITY_TYPE = build(
                    "fangs",
                    EntityType.Builder.<EntityEvokerFangs>create(EntityEvokerFangs::new, EntityClassification.MISC)
                    .size(0.5F, 0.8F)
                    .setUpdateInterval(60));
           ALLY_VEX = build(
                    "ally_vex",
                    EntityType.Builder.<EntityAllyVex>create(EntityAllyVex::new, EntityClassification.MISC)
                            .size(0.4F, 0.8F).immuneToFire());
           ENTITY_WHELP_TYPE = build("whelp", EntityType.Builder.<EntityWhelp>create(EntityWhelp::new, EntityClassification.MISC)
                   .size(0.6F, 0.63F).setTrackingRange(10)
                   .setShouldReceiveVelocityUpdates(true));

            event.getRegistry().registerAll(
                    SPELL_PROJ,
                    ENTITY_EVOKER_FANGS_ENTITY_TYPE,
                    ALLY_VEX, ENTITY_WHELP_TYPE
            );
            //ENT_PROJECTILE = registerEntity(EntityType.Builder.<EntityModProjectile>create(EntityClassification.MISC).setCustomClientFactory(EntityModProjectile::new).size(0.25F, 0.25F), "ent_projectile");

//            EntityRegistry.registerModEntity(new ResourceLocation(ExampleMod.MODID, "dmlightball"),
//                    EntityProjectileSpell.class, ExampleMod.MODID + ".dmlightball", lightballID, ExampleMod.instance,
//                    80, 20, true);

            //event.getRegistry().registerAll(entries);

        }
    }
    /**
     * Build an {@link EntityType} from a {@link EntityType.Builder} using the specified name.
     *
     * @param name    The entity type name
     * @param builder The entity type builder to build
     * @return The built entity type
     */
    private static <T extends Entity> EntityType<T> build(final String name, final EntityType.Builder<T> builder) {
        final ResourceLocation registryName = new ResourceLocation(ArsNouveau.MODID, name);

        final EntityType<T> entityType = builder
                .build(registryName.toString());

        entityType.setRegistryName(registryName);

        return entityType;
    }
}
