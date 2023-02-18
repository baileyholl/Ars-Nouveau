/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;

import com.github.klikli_dev.occultism.Occultism;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.text.WordUtils;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class TextUtil {

    //region Fields
    private static final Map<String, String> MOD_NAME_TO_ID = new HashMap<String, String>();
    private static final String[] SYLLABLE1 = {"Kr", "Ca", "Ra", "Mrok", "Cru", "Ray", "Bre", "Zed", "Drak", "Mor", "Jag", "Mer", "Jar", "Mjol", "Zork", "Mad", "Cry", "Zur", "Creo", "Azak", "Azur", "Rei", "Cro", "Mar", "Luk", "Bar"};
    //KliKli: Obvious :)
    //Xalmas: You know why!
    //Toastbroat: You know why!
    //Ridanisaurus: Pretty things!
    //Najlitarvan: Various contributions & came up with this idea
    //TheBoo: Ambassador to E6 and many many contributions
    //Legiaseth: Tried to overload the storage system with nbt. Genius contraption using create to auto-create as much nbt as possible. Love it.
    //Vallen: Did a Bit-By-Bit Video! https://www.youtube.com/watch?v=kAKzzJ_yiC8
    //Vemerion: Sooo many new familiars! <3
    //Eqis: the long-awaited additional spirit miner tiers
    private static final String[] EASTER_EGGS = {"KliKli", "Xalmas", "Toastbroat", "Najlitarvan", "TheBoo", "Ridanisaurus", "Legiaseth", "Vallen", "Vemerion", "Eqis"};
    private static final String[] SYLLABLE2 = {"air", "ir", "mi", "sor", "mee", "clo", "red", "cra", "ark", "arc", "miri", "lori", "cres", "mur", "zer", "marac", "zoir", "slamar", "salmar", "urak", "tim"};
    private static final String[] SYLLABLE3 = {"d", "ed", "ark", "arc", "es", "er", "der", "tron", "med", "ure", "zur", "cred", "mur", "aeus"};
    private static final Random random = new Random();
    private static boolean modNamesInitialized = false;
    //endregion Fields

    //region Static Methods
    public static void initializeModNames() {
        modNamesInitialized = true;
        for (IModInfo info : ModList.get().getMods()) {
            MOD_NAME_TO_ID.put(info.getModId(), info.getDisplayName());
        }
    }

    /**
     * Gets the mod name for the given game object
     *
     * @param object the game object (item or block) to get the mod name for.
     * @return the mod name or null if invalid object type was supplied.
     */
    @SuppressWarnings("deprecation")
    public static String getModNameForGameObject(@Nonnull Object object) {

        if (modNamesInitialized)
            initializeModNames();

        ResourceLocation key;
        if (object instanceof Item) {
            key = ForgeRegistries.ITEMS.getKey((Item) object);
        } else if (object instanceof Block) {
            key = ForgeRegistries.BLOCKS.getKey((Block) object);
        } else {
            return null;
        }
        String modId = key.getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        String modName = MOD_NAME_TO_ID.get(lowercaseModId);
        if (modName == null) {
            modName = WordUtils.capitalize(modId);
            MOD_NAME_TO_ID.put(lowercaseModId, modName);
        }
        return modName;
    }

    /**
     * Formats the given spirit name in bold and gold.
     *
     * @param name the name to format.
     * @return the formatted name.
     */
    public static String formatDemonName(String name) {
        return ChatFormatting.GOLD.toString() + ChatFormatting.BOLD + name + ChatFormatting.RESET;
    }

    /**
     * Formats the given spirit name in bold and gold.
     *
     * @param name the name to format.
     * @return the formatted name.
     */
    public static MutableComponent formatDemonName(MutableComponent name) {
        return name.withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    /**
     * Formats the given number for human friendly display. Rounds high numbers.
     *
     * @param number the number to format.
     * @return a formatted string for the number.
     */
    public static String formatLargeNumber(int number) {
        if (number < Math.pow(10, 3)) {
            return number + "";
        } else if (number < Math.pow(10, 6)) {
            int rounded = Math.round(number / 1000.0F);
            return rounded + "K";
        } else if (number < Math.pow(10, 9)) {
            int rounded = Math.round(number / (float) Math.pow(10, 6));
            return rounded + "M";
        } else if (number < Math.pow(10, 12)) {
            int rounded = Math.round(number / (float) Math.pow(10, 9));
            return rounded + "B";
        }
        return Integer.toString(number);
    }

    /**
     * @return a random name from the 3 syllable variations.
     */
    public static String generateName() {
        var possibleSpiritNames = Occultism.SERVER_CONFIG.rituals.possibleSpiritNames.get();
        if (!possibleSpiritNames.isEmpty()) {
            return random.nextInt(20) == 0 ?
                    EASTER_EGGS[random.nextInt(EASTER_EGGS.length)] :
                    possibleSpiritNames.get(random.nextInt(possibleSpiritNames.size()));
        }
        return random.nextInt(20) == 0 ? EASTER_EGGS[random.nextInt(
                EASTER_EGGS.length)] : SYLLABLE1[random.nextInt(SYLLABLE1.length)] + SYLLABLE2[random.nextInt(SYLLABLE2.length)] +
                SYLLABLE3[random.nextInt(SYLLABLE3.length)];
    }
    //endregion Static Methods
}
