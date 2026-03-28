package net.minecraft.world.level.storage.loot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;

public class BuiltInLootTables {
    private static final Set<ResourceKey<LootTable>> LOCATIONS = new HashSet<>();
    private static final Set<ResourceKey<LootTable>> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);
    public static final ResourceKey<LootTable> SPAWN_BONUS_CHEST = register("chests/spawn_bonus_chest");
    public static final ResourceKey<LootTable> END_CITY_TREASURE = register("chests/end_city_treasure");
    public static final ResourceKey<LootTable> SIMPLE_DUNGEON = register("chests/simple_dungeon");
    public static final ResourceKey<LootTable> VILLAGE_WEAPONSMITH = register("chests/village/village_weaponsmith");
    public static final ResourceKey<LootTable> VILLAGE_TOOLSMITH = register("chests/village/village_toolsmith");
    public static final ResourceKey<LootTable> VILLAGE_ARMORER = register("chests/village/village_armorer");
    public static final ResourceKey<LootTable> VILLAGE_CARTOGRAPHER = register("chests/village/village_cartographer");
    public static final ResourceKey<LootTable> VILLAGE_MASON = register("chests/village/village_mason");
    public static final ResourceKey<LootTable> VILLAGE_SHEPHERD = register("chests/village/village_shepherd");
    public static final ResourceKey<LootTable> VILLAGE_BUTCHER = register("chests/village/village_butcher");
    public static final ResourceKey<LootTable> VILLAGE_FLETCHER = register("chests/village/village_fletcher");
    public static final ResourceKey<LootTable> VILLAGE_FISHER = register("chests/village/village_fisher");
    public static final ResourceKey<LootTable> VILLAGE_TANNERY = register("chests/village/village_tannery");
    public static final ResourceKey<LootTable> VILLAGE_TEMPLE = register("chests/village/village_temple");
    public static final ResourceKey<LootTable> VILLAGE_DESERT_HOUSE = register("chests/village/village_desert_house");
    public static final ResourceKey<LootTable> VILLAGE_PLAINS_HOUSE = register("chests/village/village_plains_house");
    public static final ResourceKey<LootTable> VILLAGE_TAIGA_HOUSE = register("chests/village/village_taiga_house");
    public static final ResourceKey<LootTable> VILLAGE_SNOWY_HOUSE = register("chests/village/village_snowy_house");
    public static final ResourceKey<LootTable> VILLAGE_SAVANNA_HOUSE = register("chests/village/village_savanna_house");
    public static final ResourceKey<LootTable> ABANDONED_MINESHAFT = register("chests/abandoned_mineshaft");
    public static final ResourceKey<LootTable> NETHER_BRIDGE = register("chests/nether_bridge");
    public static final ResourceKey<LootTable> STRONGHOLD_LIBRARY = register("chests/stronghold_library");
    public static final ResourceKey<LootTable> STRONGHOLD_CROSSING = register("chests/stronghold_crossing");
    public static final ResourceKey<LootTable> STRONGHOLD_CORRIDOR = register("chests/stronghold_corridor");
    public static final ResourceKey<LootTable> DESERT_PYRAMID = register("chests/desert_pyramid");
    public static final ResourceKey<LootTable> JUNGLE_TEMPLE = register("chests/jungle_temple");
    public static final ResourceKey<LootTable> JUNGLE_TEMPLE_DISPENSER = register("chests/jungle_temple_dispenser");
    public static final ResourceKey<LootTable> IGLOO_CHEST = register("chests/igloo_chest");
    public static final ResourceKey<LootTable> WOODLAND_MANSION = register("chests/woodland_mansion");
    public static final ResourceKey<LootTable> UNDERWATER_RUIN_SMALL = register("chests/underwater_ruin_small");
    public static final ResourceKey<LootTable> UNDERWATER_RUIN_BIG = register("chests/underwater_ruin_big");
    public static final ResourceKey<LootTable> BURIED_TREASURE = register("chests/buried_treasure");
    public static final ResourceKey<LootTable> SHIPWRECK_MAP = register("chests/shipwreck_map");
    public static final ResourceKey<LootTable> SHIPWRECK_SUPPLY = register("chests/shipwreck_supply");
    public static final ResourceKey<LootTable> SHIPWRECK_TREASURE = register("chests/shipwreck_treasure");
    public static final ResourceKey<LootTable> PILLAGER_OUTPOST = register("chests/pillager_outpost");
    public static final ResourceKey<LootTable> BASTION_TREASURE = register("chests/bastion_treasure");
    public static final ResourceKey<LootTable> BASTION_OTHER = register("chests/bastion_other");
    public static final ResourceKey<LootTable> BASTION_BRIDGE = register("chests/bastion_bridge");
    public static final ResourceKey<LootTable> BASTION_HOGLIN_STABLE = register("chests/bastion_hoglin_stable");
    public static final ResourceKey<LootTable> ANCIENT_CITY = register("chests/ancient_city");
    public static final ResourceKey<LootTable> ANCIENT_CITY_ICE_BOX = register("chests/ancient_city_ice_box");
    public static final ResourceKey<LootTable> RUINED_PORTAL = register("chests/ruined_portal");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD = register("chests/trial_chambers/reward");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_COMMON = register("chests/trial_chambers/reward_common");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_RARE = register("chests/trial_chambers/reward_rare");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_UNIQUE = register("chests/trial_chambers/reward_unique");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS = register("chests/trial_chambers/reward_ominous");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_COMMON = register("chests/trial_chambers/reward_ominous_common");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_RARE = register("chests/trial_chambers/reward_ominous_rare");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_REWARD_OMINOUS_UNIQUE = register("chests/trial_chambers/reward_ominous_unique");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_SUPPLY = register("chests/trial_chambers/supply");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR = register("chests/trial_chambers/corridor");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_INTERSECTION = register("chests/trial_chambers/intersection");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_INTERSECTION_BARREL = register("chests/trial_chambers/intersection_barrel");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_ENTRANCE = register("chests/trial_chambers/entrance");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_DISPENSER = register("dispensers/trial_chambers/corridor");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CHAMBER_DISPENSER = register("dispensers/trial_chambers/chamber");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_WATER_DISPENSER = register("dispensers/trial_chambers/water");
    public static final ResourceKey<LootTable> TRIAL_CHAMBERS_CORRIDOR_POT = register("pots/trial_chambers/corridor");
    public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER = register("equipment/trial_chamber");
    public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER_RANGED = register("equipment/trial_chamber_ranged");
    public static final ResourceKey<LootTable> EQUIPMENT_TRIAL_CHAMBER_MELEE = register("equipment/trial_chamber_melee");
    public static final Map<DyeColor, ResourceKey<LootTable>> SHEEP_BY_DYE = makeDyeKeyMap("entities/sheep");
    public static final ResourceKey<LootTable> FISHING = register("gameplay/fishing");
    public static final ResourceKey<LootTable> FISHING_JUNK = register("gameplay/fishing/junk");
    public static final ResourceKey<LootTable> FISHING_TREASURE = register("gameplay/fishing/treasure");
    public static final ResourceKey<LootTable> FISHING_FISH = register("gameplay/fishing/fish");
    public static final ResourceKey<LootTable> CAT_MORNING_GIFT = register("gameplay/cat_morning_gift");
    public static final ResourceKey<LootTable> ARMORER_GIFT = register("gameplay/hero_of_the_village/armorer_gift");
    public static final ResourceKey<LootTable> BUTCHER_GIFT = register("gameplay/hero_of_the_village/butcher_gift");
    public static final ResourceKey<LootTable> CARTOGRAPHER_GIFT = register("gameplay/hero_of_the_village/cartographer_gift");
    public static final ResourceKey<LootTable> CLERIC_GIFT = register("gameplay/hero_of_the_village/cleric_gift");
    public static final ResourceKey<LootTable> FARMER_GIFT = register("gameplay/hero_of_the_village/farmer_gift");
    public static final ResourceKey<LootTable> FISHERMAN_GIFT = register("gameplay/hero_of_the_village/fisherman_gift");
    public static final ResourceKey<LootTable> FLETCHER_GIFT = register("gameplay/hero_of_the_village/fletcher_gift");
    public static final ResourceKey<LootTable> LEATHERWORKER_GIFT = register("gameplay/hero_of_the_village/leatherworker_gift");
    public static final ResourceKey<LootTable> LIBRARIAN_GIFT = register("gameplay/hero_of_the_village/librarian_gift");
    public static final ResourceKey<LootTable> MASON_GIFT = register("gameplay/hero_of_the_village/mason_gift");
    public static final ResourceKey<LootTable> SHEPHERD_GIFT = register("gameplay/hero_of_the_village/shepherd_gift");
    public static final ResourceKey<LootTable> TOOLSMITH_GIFT = register("gameplay/hero_of_the_village/toolsmith_gift");
    public static final ResourceKey<LootTable> WEAPONSMITH_GIFT = register("gameplay/hero_of_the_village/weaponsmith_gift");
    public static final ResourceKey<LootTable> UNEMPLOYED_GIFT = register("gameplay/hero_of_the_village/unemployed_gift");
    public static final ResourceKey<LootTable> BABY_VILLAGER_GIFT = register("gameplay/hero_of_the_village/baby_gift");
    public static final ResourceKey<LootTable> SNIFFER_DIGGING = register("gameplay/sniffer_digging");
    public static final ResourceKey<LootTable> PANDA_SNEEZE = register("gameplay/panda_sneeze");
    public static final ResourceKey<LootTable> CHICKEN_LAY = register("gameplay/chicken_lay");
    public static final ResourceKey<LootTable> ARMADILLO_SHED = register("gameplay/armadillo_shed");
    public static final ResourceKey<LootTable> TURTLE_GROW = register("gameplay/turtle_grow");
    public static final ResourceKey<LootTable> HARVEST_CAVE_VINE = register("harvest/cave_vine");
    public static final ResourceKey<LootTable> HARVEST_SWEET_BERRY_BUSH = register("harvest/sweet_berry_bush");
    public static final ResourceKey<LootTable> HARVEST_BEEHIVE = register("harvest/beehive");
    public static final ResourceKey<LootTable> CARVE_PUMPKIN = register("carve/pumpkin");
    public static final ResourceKey<LootTable> PIGLIN_BARTERING = register("gameplay/piglin_bartering");
    public static final ResourceKey<LootTable> SPAWNER_TRIAL_CHAMBER_KEY = register("spawners/trial_chamber/key");
    public static final ResourceKey<LootTable> SPAWNER_TRIAL_CHAMBER_CONSUMABLES = register("spawners/trial_chamber/consumables");
    public static final ResourceKey<LootTable> SPAWNER_OMINOUS_TRIAL_CHAMBER_KEY = register("spawners/ominous/trial_chamber/key");
    public static final ResourceKey<LootTable> SPAWNER_OMINOUS_TRIAL_CHAMBER_CONSUMABLES = register("spawners/ominous/trial_chamber/consumables");
    public static final ResourceKey<LootTable> SPAWNER_TRIAL_ITEMS_TO_DROP_WHEN_OMINOUS = register("spawners/trial_chamber/items_to_drop_when_ominous");
    public static final ResourceKey<LootTable> ARMADILLO_BRUSH = register("brush/armadillo");
    public static final ResourceKey<LootTable> BOGGED_SHEAR = register("shearing/bogged");
    public static final ResourceKey<LootTable> SHEAR_MOOSHROOM = register("shearing/mooshroom");
    public static final ResourceKey<LootTable> SHEAR_RED_MOOSHROOM = register("shearing/mooshroom/red");
    public static final ResourceKey<LootTable> SHEAR_BROWN_MOOSHROOM = register("shearing/mooshroom/brown");
    public static final ResourceKey<LootTable> SHEAR_SNOW_GOLEM = register("shearing/snow_golem");
    public static final ResourceKey<LootTable> SHEAR_SHEEP = register("shearing/sheep");
    public static final Map<DyeColor, ResourceKey<LootTable>> SHEAR_SHEEP_BY_DYE = makeDyeKeyMap("shearing/sheep");
    public static final ResourceKey<LootTable> CHARGED_CREEPER = register("charged_creeper/root");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_PIGLIN = register("charged_creeper/piglin");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_CREEPER = register("charged_creeper/creeper");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_SKELETON = register("charged_creeper/skeleton");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_WITHER_SKELETON = register("charged_creeper/wither_skeleton");
    public static final ResourceKey<LootTable> CHARGED_CREEPER_ZOMBIE = register("charged_creeper/zombie");
    public static final ResourceKey<LootTable> DESERT_WELL_ARCHAEOLOGY = register("archaeology/desert_well");
    public static final ResourceKey<LootTable> DESERT_PYRAMID_ARCHAEOLOGY = register("archaeology/desert_pyramid");
    public static final ResourceKey<LootTable> TRAIL_RUINS_ARCHAEOLOGY_COMMON = register("archaeology/trail_ruins_common");
    public static final ResourceKey<LootTable> TRAIL_RUINS_ARCHAEOLOGY_RARE = register("archaeology/trail_ruins_rare");
    public static final ResourceKey<LootTable> OCEAN_RUIN_WARM_ARCHAEOLOGY = register("archaeology/ocean_ruin_warm");
    public static final ResourceKey<LootTable> OCEAN_RUIN_COLD_ARCHAEOLOGY = register("archaeology/ocean_ruin_cold");

    private static Map<DyeColor, ResourceKey<LootTable>> makeDyeKeyMap(String p_363301_) {
        return Util.makeEnumMap(DyeColor.class, p_393454_ -> register(p_363301_ + "/" + p_393454_.getName()));
    }

    private static ResourceKey<LootTable> register(String p_78768_) {
        return register(ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace(p_78768_)));
    }

    private static ResourceKey<LootTable> register(ResourceKey<LootTable> p_335977_) {
        if (LOCATIONS.add(p_335977_)) {
            return p_335977_;
        } else {
            throw new IllegalArgumentException(p_335977_.identifier() + " is already a registered built-in loot table");
        }
    }

    public static Set<ResourceKey<LootTable>> all() {
        return IMMUTABLE_LOCATIONS;
    }
}
