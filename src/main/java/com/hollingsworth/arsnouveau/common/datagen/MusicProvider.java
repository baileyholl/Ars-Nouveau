package com.hollingsworth.arsnouveau.common.datagen;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.setup.registry.JukeboxRegistry;
import com.hollingsworth.arsnouveau.setup.registry.SoundRegistry;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MusicProvider extends DatapackBuiltinEntriesProvider {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.JUKEBOX_SONG, MusicProvider::bootstrap);


    public static void bootstrap(BootstrapContext<JukeboxSong> ctx) {
        register(ctx, JukeboxRegistry.ARIA_BIBLIO, SoundRegistry.ARIA_BIBLIO, 20*240, 1);
        register(ctx, JukeboxRegistry.WILD_HUNT, SoundRegistry.WILD_HUNT, 20 * 121, 2);
        register(ctx, JukeboxRegistry.SOUND_OF_GLASS, SoundRegistry.SOUND_OF_GLASS, 20 * 182, 3);
    }

    public static void register(
            BootstrapContext<JukeboxSong> p_350719_, ResourceKey<JukeboxSong> p_350460_, Holder<SoundEvent> p_350456_, int p_350314_, int p_350919_
    ) {
        p_350719_.register(
                p_350460_,
                new JukeboxSong(p_350456_, Component.translatable(Util.makeDescriptionId("jukebox_song", p_350460_.location())), (float)p_350314_, p_350919_)
        );


    }

    public MusicProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(ArsNouveau.MODID));
    }

    @Override
    @NotNull
    public String getName() {
        return "Ars Nouveau's Jukebox Data";
    }

}
