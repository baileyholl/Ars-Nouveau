package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.DamageTypesRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class DamageTypesProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, DamageTypesProvider::bootstrap);


    public static void bootstrap(BootstrapContext<DamageType> ctx) {
        ctx.register(DamageTypesRegistry.GENERIC_SPELL_DAMAGE, new DamageType("player", 0.1F));
        ctx.register(DamageTypesRegistry.COLD_SNAP, new DamageType("freeze", 0.1F));
        ctx.register(DamageTypesRegistry.FLARE, new DamageType("fire", 0.1F));
        ctx.register(DamageTypesRegistry.CRUSH, new DamageType("player", 0.1F));
        ctx.register(DamageTypesRegistry.WINDSHEAR, new DamageType("player", 0.1F));
    }

    public DamageTypesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsNouveau.MODID));
    }

    @Override
    @NotNull
    public String getName() {
        return "Ars Nouveau's Damage Type Data";
    }

    public static class DamageTypesTagsProvider extends DamageTypeTagsProvider {

        public DamageTypesTagsProvider(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
            super(pPackOutput, provider, ArsNouveau.MODID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.@NotNull Provider pProvider) {

            tag(DamageTypeTags.IS_FIRE).addOptional(DamageTypesRegistry.FLARE.location());
            tag(DamageTypeTags.IS_FREEZING).addOptional(DamageTypesRegistry.COLD_SNAP.location());
            tag(DamageTypeTags.BYPASSES_ARMOR)
                    .addOptional(DamageTypesRegistry.CRUSH.location())
                    .addOptional(DamageTypesRegistry.WINDSHEAR.location());
            tag(DamageTypeTags.IS_FALL).addOptional(DamageTypesRegistry.WINDSHEAR.location());
            tag(Tags.DamageTypes.IS_MAGIC)
                    .addOptional(DamageTypesRegistry.GENERIC_SPELL_DAMAGE.location())
                    .addOptional(DamageTypesRegistry.COLD_SNAP.location())
                    .addOptional(DamageTypesRegistry.FLARE.location())
                    .addOptional(DamageTypesRegistry.CRUSH.location())
                    .addOptional(DamageTypesRegistry.WINDSHEAR.location());
            tag(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)
                    .addOptional(DamageTypesRegistry.GENERIC_SPELL_DAMAGE.location())
                    .addOptional(DamageTypesRegistry.COLD_SNAP.location())
                    .addOptional(DamageTypesRegistry.FLARE.location())
                    .addOptional(DamageTypesRegistry.CRUSH.location())
                    .addOptional(DamageTypesRegistry.WINDSHEAR.location());

        }
    }

}
