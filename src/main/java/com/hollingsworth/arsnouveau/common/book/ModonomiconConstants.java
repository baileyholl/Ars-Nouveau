/*
 * SPDX-FileCopyrightText: 2022 klikli-dev
 *
 * SPDX-License-Identifier: MIT
 */

package com.hollingsworth.arsnouveau.common.book;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.resources.ResourceLocation;

public class ModonomiconConstants {

    public static class Data {
        public static final String MODONOMICON_DATA_PATH = ArsNouveau.MODID + "/books";
        public static final String MULTIBLOCK_DATA_PATH = ArsNouveau.MODID + "/multiblocks";

        public static class Book {
            public static final String DEFAULT_OVERVIEW_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/book_overview.png").toString();
            public static final String DEFAULT_FRAME_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/book_frame.png").toString();

            public static final BookFrameOverlay DEFAULT_TOP_FRAME_OVERLAY = new BookFrameOverlay(
                    ArsNouveau.loc("textures/gui/book_frame_top_overlay.png"),
                    256, 256, 72, 7, 0, 4);

            public static final BookFrameOverlay DEFAULT_BOTTOM_FRAME_OVERLAY = new BookFrameOverlay(
                    ArsNouveau.loc("textures/gui/book_frame_bottom_overlay.png"),
                    256, 256, 72, 8, 0, -4);

            public static final BookFrameOverlay DEFAULT_LEFT_FRAME_OVERLAY = new BookFrameOverlay(
                    ArsNouveau.loc("textures/gui/book_frame_left_overlay.png"),
                    256, 256, 7, 70, 3, 0);

            public static final BookFrameOverlay DEFAULT_RIGHT_FRAME_OVERLAY = new BookFrameOverlay(
                    ArsNouveau.loc("textures/gui/book_frame_right_overlay.png"),
                    256, 256, 8, 70, -4, 0);

            public static final String DEFAULT_CONTENT_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/book_content.png").toString();
            public static final String DEFAULT_CRAFTING_TEXTURE = new ResourceLocation(ArsNouveau.MODID, "textures/gui/crafting_textures.png").toString();
            public static final String DEFAULT_PAGE_TURN_SOUND = new ResourceLocation(ArsNouveau.MODID, "turn_page").toString();
            public static final String DEFAULT_MODEL = new ResourceLocation(ArsNouveau.MODID, "modonomicon_purple").toString();
            public static final ResourceLocation ITEM_ID = new ResourceLocation(ArsNouveau.MODID, "modonomicon");

        }

        public static class Category {
            public static final String DEFAULT_ICON = new ResourceLocation(ArsNouveau.MODID, "modonomicon_purple").toString();
            public static final String DEFAULT_BACKGROUND = new ResourceLocation(ArsNouveau.MODID, "textures/gui/dark_slate_seamless.png").toString();
            public static final int DEFAULT_BACKGROUND_WIDTH = 512;
            public static final int DEFAULT_BACKGROUND_HEIGHT = 512;
            public static final String DEFAULT_ENTRY_TEXTURES = new ResourceLocation(ArsNouveau.MODID, "textures/gui/entry_textures.png").toString();
        }

        public static class Command {
            public static final int DEFAULT_MAX_USES = 1;
            public static final int DEFAULT_PERMISSION_LEVEL = 0;
        }

        public static class Page {
            public static final ResourceLocation TEXT = new ResourceLocation(ArsNouveau.MODID, "text");
            public static final ResourceLocation MULTIBLOCK = new ResourceLocation(ArsNouveau.MODID, "multiblock");
            public static final ResourceLocation CRAFTING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "crafting_recipe");
            public static final ResourceLocation SMOKING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "smoking_recipe");
            public static final ResourceLocation SMELTING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "smelting_recipe");
            public static final ResourceLocation BLASTING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "blasting_recipe");
            public static final ResourceLocation CAMPFIRE_COOKING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "campfire_cooking_recipe");
            public static final ResourceLocation STONECUTTING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "stonecutting_recipe");
            public static final ResourceLocation SMITHING_RECIPE = new ResourceLocation(ArsNouveau.MODID, "smithing_recipe");
            public static final ResourceLocation SPOTLIGHT = new ResourceLocation(ArsNouveau.MODID, "spotlight");
            public static final ResourceLocation EMPTY = new ResourceLocation(ArsNouveau.MODID, "empty");
            public static final ResourceLocation ENTITY = new ResourceLocation(ArsNouveau.MODID, "entity");
            public static final ResourceLocation IMAGE = new ResourceLocation(ArsNouveau.MODID, "image");
        }

        public static class Condition {

            public static final ResourceLocation NONE = new ResourceLocation(ArsNouveau.MODID, "none");
            public static final ResourceLocation ADVANCEMENT = new ResourceLocation(ArsNouveau.MODID, "advancement");
            public static final ResourceLocation MOD_LOADED = new ResourceLocation(ArsNouveau.MODID, "mod_loaded");
            public static final ResourceLocation OR = new ResourceLocation(ArsNouveau.MODID, "or");
            public static final ResourceLocation AND = new ResourceLocation(ArsNouveau.MODID, "and");

            public static final ResourceLocation TRUE = new ResourceLocation(ArsNouveau.MODID, "true");

            public static final ResourceLocation FALSE = new ResourceLocation(ArsNouveau.MODID, "false");

            public static final ResourceLocation ENTRY_UNLOCKED = new ResourceLocation(ArsNouveau.MODID, "entry_unlocked");

            public static final ResourceLocation ENTRY_READ = new ResourceLocation(ArsNouveau.MODID, "entry_read");
        }
    }

    public static class Nbt {

        public static final String VERSION_1_0 = "1.0.0";

        public static final String CURRENT_VERSION = VERSION_1_0;
        public static final String PREFIX = ArsNouveau.MODID + ":";

        public static final String VERSION_TAG = PREFIX + "nbt_version";

        public static final String ITEM_BOOK_ID_TAG = PREFIX + "book_id";
    }

    public static class I18n {
        public static final String BOOK_PREFIX = "book." + ArsNouveau.MODID + ".";
        public static final String ITEM_GROUP = "itemGroup." + ArsNouveau.MODID;

        public static class Gui {
            public static final String PREFIX = ArsNouveau.MODID + ".gui.";
            public static final String BUTTON_NEXT = PREFIX + "button.next_page";
            public static final String BUTTON_PREVIOUS = PREFIX + "button.previous_page";
            public static final String BUTTON_BACK = PREFIX + "button.back";
            public static final String BUTTON_BACK_TOOLTIP = PREFIX + "button.back.tooltip";
            public static final String BUTTON_EXIT = PREFIX + "button.exit";

            public static final String BUTTON_VISUALIZE = PREFIX + "button.visualize";
            public static final String BUTTON_VISUALIZE_TOOLTIP = PREFIX + "button.visualize.tooltip";

            public static final String BUTTON_READ_ALL = PREFIX + "button.read_all";
            public static final String BUTTON_READ_ALL_TOOLTIP_READ_UNLOCKED = PREFIX + "button.read_all.tooltip.read_unlocked";
            public static final String BUTTON_READ_ALL_TOOLTIP_READ_ALL = PREFIX + "button.read_all.tooltip.read_all";
            public static final String BUTTON_READ_ALL_TOOLTIP_NONE = PREFIX + "button.read_all.tooltip.none";
            public static final String BUTTON_READ_ALL_TOOLTIP_SHIFT_INSTRUCTIONS = PREFIX + "button.read_all.tooltip.shift";
            public static final String BUTTON_READ_ALL_TOOLTIP_SHIFT_WARNING = PREFIX + "button.read_all.tooltip.shift_warning";

            public static final String HOVER_BOOK_LINK = PREFIX + "hover.book_link";
            public static final String HOVER_BOOK_LINK_LOCKED = PREFIX + "hover.book_link_locked";
            public static final String HOVER_BOOK_LINK_LOCKED_INFO = PREFIX + "hover.book_link_locked_info";
            public static final String HOVER_HTTP_LINK = PREFIX + "hover.http_link";
            public static final String HOVER_ITEM_LINK_INFO = PREFIX + "hover.item_link_info";
            public static final String HOVER_ITEM_LINK_INFO_LINE2 = PREFIX + "hover.item_link_info_line2";
            public static final String HOVER_ITEM_LINK_INFO_NO_JEI = PREFIX + "hover.item_link_info.no_jei";
            public static final String HOVER_COMMAND_LINK = PREFIX + "hover.command_link";
            public static final String HOVER_COMMAND_LINK_UNAVAILABLE = PREFIX + "hover.command_link.unavailable";
            public static final String NO_ERRORS_FOUND = PREFIX + "no_errors_found";
            public static final String PAGE_ENTITY_LOADING_ERROR = PREFIX + ".page.entity.loading_error";

            public static final String SEARCH_SCREEN_TITLE = PREFIX + "search.screen.title";
            public static final String SEARCH_ENTRY_LOCKED = PREFIX + "search.entry.locked";
            public static final String SEARCH_NO_RESULTS = PREFIX + "search.no_results";
            public static final String SEARCH_NO_RESULTS_SAD = PREFIX + "search.sad";
            public static final String SEARCH_INFO_TEXT = PREFIX + "search.info";
            public static final String SEARCH_ENTRY_LIST_TITLE= PREFIX + "search.entry_list_title";

            public static final String OPEN_SEARCH= PREFIX + "open_search";

            public static final String RECIPE_PAGE_RECIPE_MISSING = PREFIX + "recipe_page.recipe_missing";
        }

        public static class Multiblock {
            public static final String PREFIX = ArsNouveau.MODID + ".multiblock.";
            public static final String COMPLETE = PREFIX + "complete";
            public static final String NOT_ANCHORED = PREFIX + "not_anchored";
            public static final String REMOVE_BLOCKS = PREFIX + "remove_blocks";
        }

        public static class Subtitles {
            public static final String PREFIX = ArsNouveau.MODID + ".subtitle.";
            public static final String TURN_PAGE = PREFIX + "turn_page";
        }

        public static class Tooltips {
            public static final String PREFIX = "tooltip." + ArsNouveau.MODID + ".";
            public static final String CONDITION_PREFIX =PREFIX + ".condition.";
            public static final String RECIPE_PREFIX =PREFIX + ".recipe.";
            public static final String CONDITION_ADVANCEMENT = CONDITION_PREFIX + "advancement";

            public static final String CONDITION_MOD_LOADED = CONDITION_PREFIX + "mod_loaded";
            public static final String CONDITION_ENTRY_UNLOCKED = CONDITION_PREFIX + "entry_unlocked";
            public static final String CONDITION_ENTRY_READ = CONDITION_PREFIX + "entry_read";

            public static final String ITEM_NO_BOOK_FOUND_FOR_STACK = PREFIX + "no_book_found_for_stack";
            public static final String RECIPE_CRAFTING_SHAPELESS = RECIPE_PREFIX + "crafting_shapeless";
        }

        public static class Command {
            public static final String PREFIX = ArsNouveau.MODID + ".command.";

            public static final String ERROR_PREFIX = PREFIX + "error.";
            public static final String SUCCESS_PREFIX = PREFIX + "success.";
            public static final String ERROR_UNKNOWN_BOOK = ERROR_PREFIX + "unknown_book";
            public static final String ERROR_LOAD_PROGRESS = ERROR_PREFIX + "load_progress";
            public static final String ERROR_LOAD_PROGRESS_CLIENT = ERROR_PREFIX + "load_progress_client";
            public static final String SUCCESS_RESET_BOOK = SUCCESS_PREFIX + "reset_book";
            public static final String SUCCESS_SAVE_PROGRESS = SUCCESS_PREFIX + "save_progress";
            public static final String SUCCESS_LOAD_PROGRESS = SUCCESS_PREFIX + "load_progress";

            public static final String DEFAULT_FAILURE_MESSAGE = PREFIX + "failure";
        }
    }
}
