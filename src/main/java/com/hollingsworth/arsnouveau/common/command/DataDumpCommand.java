package com.hollingsworth.arsnouveau.common.command;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.Tuple;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DataDumpCommand {
    public static final Path PATH_AUGMENT_COMPATIBILITY = Paths.get("ars_nouveau", "augment_compatibility.csv");

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("ars-data")
                .requires(sender -> sender.hasPermission(2)) // Op required
                .then(Commands.literal("dump")
                .then(Commands.literal("augment-compatibility-csv")
                .executes(DataDumpCommand::dumpAugmentCompat)))
        );
    }

    /**
     * Creates a CSV file at {@link DataDumpCommand#PATH_AUGMENT_COMPATIBILITY} all augment compatibility information
     */
    public static int dumpAugmentCompat(CommandContext<CommandSource> context) {
        Map<String, AbstractSpellPart> spells = ArsNouveauAPI.getInstance().getSpell_map();

        // Collect the Augments
        List<AbstractAugment> augments = spells.values().stream()
                .filter(p -> p instanceof AbstractAugment)
                .map(p -> (AbstractAugment) p)
                .sorted(Comparator.comparing(a -> a.tag))
                .collect(Collectors.toList());

        // Collect the augment compatibilities
        List<Tuple<AbstractSpellPart, Set<AbstractAugment>>> augmentCompat = spells.values().stream()
                .filter(part -> part instanceof AbstractCastMethod)
                .map(part -> new Tuple<>(part, part.getCompatibleAugments()))
                .sorted(Comparator.comparing(t -> t.getA().tag))
                .collect(Collectors.toList());
        // Technically can be done in one sort, but writing a comparator based on type is ugly.
        augmentCompat.addAll(spells.values().stream()
                .filter(part -> part instanceof AbstractEffect)
                .map(part -> new Tuple<>(part, part.getCompatibleAugments()))
                .sorted(Comparator.comparing(t -> t.getA().tag))
                .collect(Collectors.toList()));

        // Write the file
        File file = PATH_AUGMENT_COMPATIBILITY.toFile();
        try {
            Files.createDirectories(PATH_AUGMENT_COMPATIBILITY.getParent());
            PrintWriter w = new PrintWriter(new FileWriterWithEncoding(file, "UTF-8", false));

            // Header Line
            w.println("glyph, " + augments.stream().map(a -> a.tag).collect(Collectors.joining(", ")));

            // Rows
            for (Tuple<AbstractSpellPart, Set<AbstractAugment>> row : augmentCompat) {
                AbstractSpellPart part = row.getA();
                Set<AbstractAugment> compatibleAugments = row.getB();

                w.print(part.tag + ", ");

                // Columns
                w.print(augments.stream()
                        .map(a -> compatibleAugments.contains(a) ? "T" : "F")
                        .collect(Collectors.joining(", ")));
                w.println();
            }
            w.close();
        } catch (IOException ex) {
            LogManager.getLogger(ArsNouveau.MODID).error("Unable to dump augment compatibility chart", ex);
            context.getSource().sendFailure(new StringTextComponent("Error when trying to produce the data dump.  Check the logs."));

            // This is somewhat expected, just fail the command.  Logging took care of reporting.
            return 0;
        } catch (Exception ex) {
            LogManager.getLogger(ArsNouveau.MODID).error("Exception caught when trying to dump data", ex);
            context.getSource().sendFailure(new StringTextComponent("Error when trying to produce the data dump.  Check the logs."));

            // We really didn't expect this.  Re-throw.
            throw ex;
        }

        context.getSource().sendSuccess(new StringTextComponent("Dumped data to " + file), true);
        return 1;
    }
}
