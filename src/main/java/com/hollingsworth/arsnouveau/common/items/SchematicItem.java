package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.events.SchematicCaptureEvents;
import com.hollingsworth.arsnouveau.common.compat.CreateHandler;
import com.hollingsworth.arsnouveau.common.items.data.SchematicData;
import com.hollingsworth.arsnouveau.common.util.Log;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.DataComponentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class SchematicItem extends ModItem {
    public static final Path SCHEMATICS_DIR = FMLPaths.GAMEDIR.get().resolve("schematics");
    public static final Path UPLOADED_SCHEMATICS_DIR = SCHEMATICS_DIR.resolve("uploaded");

    public SchematicItem() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !stack.has(DataComponentRegistry.SCHEMATIC)) {
            stack.set(DataComponentRegistry.SCHEMATIC, new SchematicData(UUID.randomUUID(), player.getUUID()));
        }
        if (level.isClientSide && !SchematicCaptureEvents.CAPTURE_HANDLER.showBoundary) {
            SchematicCaptureEvents.CAPTURE_HANDLER.startCapture();
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static ItemStack getHeldSchematic(Player player) {
        return player.getMainHandItem().getItem() instanceof SchematicItem ? player.getMainHandItem() : player.getOffhandItem();
    }
    // Yoinked from create
    public static boolean saveSchematic(Player player, BoundingBox bounds, UUID uuid) {
        Level level = player.level();
        if (!level.hasChunksAt(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ())) {
            return false;
        }
        StructureTemplate template = new StructureTemplate();
        template.fillFromWorld(level, new BlockPos(bounds.minX(), bounds.minY(), bounds.minZ()), new Vec3i(bounds.getXSpan(), bounds.getYSpan(), bounds.getZSpan()), true, Blocks.AIR);
        CompoundTag tag = template.save(new CompoundTag());
        String structureVoid = BuiltInRegistries.BLOCK.getKey(Blocks.STRUCTURE_VOID).toString();
        for (Tag state : tag.getList(StructureTemplate.PALETTE_TAG, Tag.TAG_COMPOUND)) {
            if (state instanceof CompoundTag compound && compound.getString("Name").equals(structureVoid)) {
                compound.putString("Name", BuiltInRegistries.BLOCK.getKey(Blocks.AIR).toString());
            }
        }
        if (ArsNouveau.createLoaded) {
            CreateHandler.clampGlueBoxes(level, AABB.of(bounds), tag);
        }
        Path path = getPath(level, player.getUUID(), uuid);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            NbtIo.writeCompressed(tag, out);
            int maxSize = ServerConfig.MAX_SCHEMATIC_SIZE.get();
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (out.size() > maxSize * 1000L && (server == null || !server.isSingleplayerOwner(player.getGameProfile()))) {
                player.displayClientMessage(Component.translatable("ars_nouveau.schematic.too_large", out.size() / 1000, maxSize), false);
                return false;
            }
            Files.createDirectories(path.getParent());
            if (!level.isClientSide) {
                List<Path> others;
                try (Stream<Path> files = Files.list(path.getParent())) {
                    others = files.filter(file -> !file.equals(path)).sorted(Comparator.comparingLong(file -> file.toFile().lastModified())).toList();
                }
                for (Path file : others.subList(0, Math.max(0, others.size() - ServerConfig.MAX_SCHEMATICS.get() + 1))) {
                    Files.deleteIfExists(file);
                }
            }
            Files.write(path, out.toByteArray());
            return true;
        } catch (IOException e) {
            Log.getLogger().error("Failed to save schematic {}", path, e);
            return false;
        }
    }

    public static StructureTemplate loadSchematic(Level level, SchematicData data) {
        StructureTemplate template = new StructureTemplate();
        Path path = getPath(level, data.owner(), data.uuid());
        if (Files.exists(path)) {
            try {
                byte[] header;
                try (InputStream in = Files.newInputStream(path)) {
                    header = in.readNBytes(2);
                }
                if (header.length == 2 && ((header[0] & 0xFF) | (header[1] & 0xFF) << 8) == GZIPInputStream.GZIP_MAGIC) {
                    template.load(level.holderLookup(Registries.BLOCK), NbtIo.readCompressed(path, NbtAccounter.create(0x20000000L)));
                } else {
                    Log.getLogger().warn("Schematic {} is not GZIP encoded", path);
                }
            } catch (IOException | NbtException e) {
                Log.getLogger().error("Failed to load schematic {}", path, e);
            }
        }
        return template;
    }

    private static Path getPath(Level level, UUID owner, UUID uuid) {
        return level.isClientSide ? SCHEMATICS_DIR.resolve(uuid + ".nbt") : UPLOADED_SCHEMATICS_DIR.resolve(owner.toString()).resolve(uuid + ".nbt");
    }
}
