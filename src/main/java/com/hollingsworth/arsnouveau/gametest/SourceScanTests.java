package com.hollingsworth.arsnouveau.gametest;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceAccess;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SourceScanTests {
    private static final BlockPos scanPos = new BlockPos(4, 1, 4);
    private static final BlockPos nearPos = new BlockPos(5, 1, 4);
    private static final BlockPos farPos = new BlockPos(6, 1, 4);
    private static final BlockPos splitterPos = new BlockPos(5, 1, 5);
    private static final int range = 3;

    @GameTest(template = "empty10")
    public static void sourceAccessReachesSplitter(GameTestHelper helper) {
        helper.setBlock(splitterPos, BlockRegistry.RELAY_SPLITTER.get());
        BlockPos splitter = helper.absolutePos(splitterPos);
        helper.assertTrue(helper.getLevel().getCapability(CapabilityRegistry.SOURCE_CAPABILITY, splitter, null) != null, "Expected the splitter to register the source capability");
        helper.assertTrue(SourceAccess.at(helper.getLevel(), splitter, null) != null, "Expected the splitter to be found as valid source access");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void findProvidersSkipsSplitter(GameTestHelper helper) {
        placeStore(helper, nearPos, BlockRegistry.SOURCE_JAR.get(), 1000);
        placeStore(helper, splitterPos, BlockRegistry.RELAY_SPLITTER.get(), 500);

        List<SourceAccess> providers = SourceUtil.findProviders(helper.absolutePos(scanPos), helper.getLevel(), range);
        helper.assertTrue(providers.size() == 1, "Expected '1' provider, found '" + providers.size() + "'");
        helper.assertTrue(providers.getFirst().pos().equals(helper.absolutePos(nearPos)), "Expected the source jar as first provider, found '" + providers.getFirst().pos() + "' instead");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void takeNearbyChargesJar(GameTestHelper helper) {
        AbstractSourceMachine jar = placeStore(helper, nearPos, BlockRegistry.SOURCE_JAR.get(), 1000);

        List<SourceAccess> filled = SourceUtil.takeNearby(helper.absolutePos(scanPos), helper.getLevel(), range, 300);
        helper.assertTrue(filled != null && filled.size() == 1, "Expected '1' filled source storage, found '" + filled + "'");
        helper.assertTrue(filled.getFirst().pos().equals(helper.absolutePos(nearPos)), "Expected the source jar to be filled, found '" + filled.getFirst().pos() + "'");
        helper.assertTrue(jar.getSource() == 700, "Expected '700' source left in the source jar, found '" + jar.getSource() + "'");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void takeNearbyTakesNothingWhenShort(GameTestHelper helper) {
        AbstractSourceMachine near = placeStore(helper, nearPos, BlockRegistry.SOURCE_JAR.get(), 100);
        AbstractSourceMachine far = placeStore(helper, farPos, BlockRegistry.SOURCE_JAR.get(), 100);

        List<SourceAccess> filled = SourceUtil.takeNearby(helper.absolutePos(scanPos), helper.getLevel(), range, 300);
        helper.assertTrue(filled == null, "Expected no storages with any source, found '" + filled + "'");
        helper.assertTrue(near.getSource() == 100 && far.getSource() == 100, "Expected both source jars to keep '100' source each exactly, found '" + near.getSource() + "' and '" + far.getSource() + "'");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void takeNearbyChargesOnlyCreativeJar(GameTestHelper helper) {
        AbstractSourceMachine jar = placeStore(helper, nearPos, BlockRegistry.SOURCE_JAR.get(), 1000);
        helper.setBlock(farPos, BlockRegistry.CREATIVE_SOURCE_JAR.get());

        List<SourceAccess> filled = SourceUtil.takeNearby(helper.absolutePos(scanPos), helper.getLevel(), range, 300);
        helper.assertTrue(filled != null && filled.size() == 1, "Expected '1' filled storage, found '" + filled + "'");
        helper.assertTrue(filled.getFirst().pos().equals(helper.absolutePos(farPos)), "Expected the creative jar to be filled, found '" + filled.getFirst().pos() + "'");
        helper.assertTrue(jar.getSource() == 1000, "Expected the jar to keep '1000' source, found '" + jar.getSource() + "'");
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void canTakeSourceReturnsJarTile(GameTestHelper helper) {
        AbstractSourceMachine jar = placeStore(helper, nearPos, BlockRegistry.SOURCE_JAR.get(), 1000);

        List<ISpecialSourceProvider> providers = SourceUtil.canTakeSource(helper.absolutePos(scanPos), helper.getLevel(), range);
        helper.assertTrue(providers.size() == 1, "Expected '1' provider, found '" + providers.size() + "'");
        helper.assertTrue(providers.getFirst().getSource() == jar, "Expected the jar block entity as the provider's tile, found '" + providers.getFirst().getSource() + "'");
        helper.succeed();
    }

    private static AbstractSourceMachine placeStore(GameTestHelper helper, BlockPos pos, Block block, int source) {
        helper.setBlock(pos, block);
        AbstractSourceMachine store = helper.getBlockEntity(pos);
        store.setSource(source);
        return store;
    }
}
