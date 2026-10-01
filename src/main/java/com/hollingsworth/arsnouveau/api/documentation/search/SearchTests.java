package com.hollingsworth.arsnouveau.api.documentation.search;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.util.QueryBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@GameTestHolder(ArsNouveau.MODID)
@PrefixGameTestTemplate(false)
public class SearchTests {

    @GameTest(template = "empty10")
    public static void cjkSearchTagsDoNotMatchAcrossValues(GameTestHelper helper) throws Exception {
        try (var analyzer = new CJKAnalyzer();
             var directory = new ByteBuffersDirectory();
             var writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            Document separateTags = new Document();
            separateTags.add(new StoredField("id", "separate"));
            separateTags.add(new TextField("tagsCjk", "掘", Field.Store.NO));
            separateTags.add(new TextField("tagsCjk", "穴", Field.Store.NO));
            writer.addDocument(separateTags);

            Document contiguousTag = new Document();
            contiguousTag.add(new StoredField("id", "contiguous"));
            contiguousTag.add(new TextField("tagsCjk", "掘穴", Field.Store.NO));
            writer.addDocument(contiguousTag);
            writer.commit();

            try (var reader = DirectoryReader.open(writer)) {
                var searcher = new IndexSearcher(reader);
                var hits = searcher.search(new QueryBuilder(analyzer).createPhraseQuery("tagsCjk", "掘穴"), 10);
                Set<String> actualIds = new HashSet<>();
                for (var hit : hits.scoreDocs) {
                    actualIds.add(searcher.storedFields().document(hit.doc).get("id"));
                }
                helper.assertTrue(actualIds.equals(Set.of("contiguous")), "Expected [contiguous], got " + actualIds);
            }
        }
        helper.succeed();
    }

    @GameTest(template = "empty10")
    public static void englishSearchMatchesTitlesAndTags(GameTestHelper helper) throws Exception {
        try (var analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), Map.of("title", new EnglishAnalyzer(), "titleGrams", new NGramAnalyzer(2, 3), "tags", new EnglishAnalyzer()));
             var directory = new ByteBuffersDirectory();
             var writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            Document burrowing = new Document();
            burrowing.add(new StoredField("id", "burrowing"));
            burrowing.add(new TextField("title", "Burrowing", Field.Store.YES));
            burrowing.add(new TextField("titleGrams", "Burrowing", Field.Store.YES));
            burrowing.add(new TextField("tags", "ritual", Field.Store.YES));
            burrowing.add(new TextField("tags", "earth", Field.Store.YES));
            writer.addDocument(burrowing);

            Document wilden = new Document();
            wilden.add(new StoredField("id", "wilden"));
            wilden.add(new TextField("title", "Wilden Summon", Field.Store.YES));
            wilden.add(new TextField("titleGrams", "Wilden Summon", Field.Store.YES));
            wilden.add(new TextField("tags", "ritual", Field.Store.YES));
            wilden.add(new TextField("tags", "summoning", Field.Store.YES));
            writer.addDocument(wilden);
            writer.commit();

            try (var reader = DirectoryReader.open(writer)) {
                var searcher = new IndexSearcher(reader);
                var parser = new MultiFieldQueryParser(new String[]{"title", "titleGrams", "tags"}, analyzer, Map.of("tags", 2.0f, "title", 4.0f, "titleGrams", 0.5f));
                parser.setDefaultOperator(QueryParser.Operator.OR);
                assertMatches(helper, searcher, parser.parse("burrowing"), "burrowing");
                assertMatches(helper, searcher, parser.parse("burrow"), "burrowing");
                assertMatches(helper, searcher, parser.parse("earth"), "burrowing");
                assertMatches(helper, searcher, parser.parse("fire"));
            }
        }
        helper.succeed();
    }

    private static void assertMatches(GameTestHelper helper, IndexSearcher searcher, Query query, String... expectedIds) throws Exception {
        var hits = searcher.search(query, 10);
        Set<String> actualIds = new HashSet<>();
        for (var hit : hits.scoreDocs) {
            if (hit.score >= 0.5f) {
                actualIds.add(searcher.storedFields().document(hit.doc).get("id"));
            }
        }
        Set<String> expected = Set.copyOf(Arrays.asList(expectedIds));
        helper.assertTrue(actualIds.equals(expected), "Expected " + expected + ", got " + actualIds);
    }
}
