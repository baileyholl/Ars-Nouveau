package com.hollingsworth.arsnouveau.setup.registry;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Util;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.EnumMap;

public class MaterialRegistry {

    /**
     * Equipment asset keys for our custom armor models.
     * These are referenced in the armor material for rendering.
     * The actual asset data must be provided via data pack JSON.
     */
    public static final ResourceKey<EquipmentAsset> LIGHT_ASSET = EquipmentAssets.createId(ArsNouveau.MODID + "/light_armor");
    public static final ResourceKey<EquipmentAsset> MEDIUM_ASSET = EquipmentAssets.createId(ArsNouveau.MODID + "/medium_armor");
    public static final ResourceKey<EquipmentAsset> HEAVY_ASSET = EquipmentAssets.createId(ArsNouveau.MODID + "/heavy_armor");

    /**
     * ArmorMaterial is no longer a registry in 1.21.11 - it's a plain record.
     * Repair tag uses WOOL as a placeholder; define a proper mage_fiber tag for production.
     * Defense values: boots, leggings, chestplate, helmet, body
     */
    public static final ArmorMaterial LIGHT = new ArmorMaterial(
            20,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 1);
                map.put(ArmorType.LEGGINGS, 3);
                map.put(ArmorType.CHESTPLATE, 5);
                map.put(ArmorType.HELMET, 2);
                map.put(ArmorType.BODY, 4);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0f,
            0.0f,
            ItemTags.WOOL,  // TODO: replace with a mage_fiber item tag
            LIGHT_ASSET
    );

    public static final ArmorMaterial MEDIUM = new ArmorMaterial(
            25,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 2);
                map.put(ArmorType.LEGGINGS, 5);
                map.put(ArmorType.CHESTPLATE, 6);
                map.put(ArmorType.HELMET, 2);
                map.put(ArmorType.BODY, 4);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0f,
            0.0f,
            ItemTags.WOOL,  // TODO: replace with a mage_fiber item tag
            MEDIUM_ASSET
    );

    public static final ArmorMaterial HEAVY = new ArmorMaterial(
            35,
            Util.make(new EnumMap<>(ArmorType.class), map -> {
                map.put(ArmorType.BOOTS, 3);
                map.put(ArmorType.LEGGINGS, 6);
                map.put(ArmorType.CHESTPLATE, 8);
                map.put(ArmorType.HELMET, 3);
                map.put(ArmorType.BODY, 4);
            }),
            30,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0f,
            0.0f,
            ItemTags.WOOL,  // TODO: replace with a mage_fiber item tag
            HEAVY_ASSET
    );
}
