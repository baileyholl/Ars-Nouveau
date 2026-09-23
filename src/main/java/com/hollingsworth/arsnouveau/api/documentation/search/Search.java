package com.hollingsworth.arsnouveau.api.documentation.search;

import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.documentation.DocDataLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.lucene.analysis.Analyzer;
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
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.QueryBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Search {
    public static IndexSearcher searcher;
    public static PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
    public static List<ConnectedSearch> connectedSearches = new ArrayList<>();
    public static Map<Item, DocEntry> itemToEntryMap = new HashMap<>();
    public static Analyzer cjkAnalyzer = new CJKAnalyzer();

    public static void addConnectedSearch(ConnectedSearch connectedSearch) {
        connectedSearches.add(connectedSearch);
    }

    public static void initSearchIndex() {
        try {
            analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), Map.of("title", new EnglishAnalyzer(), "titleGrams", new NGramAnalyzer(2, 3), "tags", new EnglishAnalyzer(), "titleCjk", cjkAnalyzer, "tagsCjk", cjkAnalyzer));
            Files.createDirectories(Path.of(DocDataLoader.DATA_FOLDER));
            try (Directory directory = new MMapDirectory(Path.of(DocDataLoader.DATA_FOLDER + "search_index"))) {
                IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
                writer.deleteAll();
                for (DocEntry docEntry : DocumentationRegistry.getEntries()) {
                    Document document = new Document();
                    document.add(new StoredField("ID", docEntry.id().toString()));
                    document.add(new TextField("title", docEntry.entryTitle().getString(), Field.Store.YES));
                    document.add(new TextField("titleGrams", docEntry.entryTitle().getString(), Field.Store.YES));
                    document.add(new TextField("titleCjk", docEntry.entryTitle().getString(), Field.Store.NO));
                    for (Component tag : docEntry.searchTags()) {
                        document.add(new TextField("tags", tag.getString(), Field.Store.YES));
                        document.add(new TextField("tagsCjk", tag.getString(), Field.Store.NO));
                    }
                    writer.addDocument(document);
                    if (!docEntry.renderStack().isEmpty()) {
                        itemToEntryMap.put(docEntry.renderStack().getItem(), docEntry);
                    }
                }
                for (int i = 0; i < connectedSearches.size(); i++) {
                    ConnectedSearch connectedSearch = connectedSearches.get(i);
                    Document document = new Document();
                    document.add(new StoredField("ID", connectedSearch.entryId().toString()));
                    document.add(new StoredField("connectedIndex", i));
                    document.add(new TextField("title", connectedSearch.title().getString(), Field.Store.YES));
                    document.add(new TextField("titleGrams", connectedSearch.title().getString(), Field.Store.YES));
                    document.add(new TextField("titleCjk", connectedSearch.title().getString(), Field.Store.NO));
                    if (!connectedSearch.icon().isEmpty()) {
                        itemToEntryMap.put(connectedSearch.icon().getItem(), DocumentationRegistry.getEntry(connectedSearch.entryId()));
                    }
                    writer.addDocument(document);
                }
                writer.commit();
                DirectoryReader reader = DirectoryReader.open(writer);
                IndexSearcher oldSearcher = searcher;
                searcher = new IndexSearcher(reader);
                writer.close();
                if (oldSearcher != null) {
                    oldSearcher.getIndexReader().close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Result> search(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        query = query.trim().toLowerCase();
        List<Result> results = new ArrayList<>();
        try {
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "titleGrams", "tags"}, analyzer, Map.of("tags", 2.0f, "title", 4.0f, "titleGrams", 0.5f));
            parser.setDefaultOperator(QueryParser.Operator.OR);
            Query nGramQuery = parser.parse(query);
            if (nGramQuery == null) {
                return results;
            }
            BooleanQuery.Builder booleanClauses = new BooleanQuery.Builder().add(nGramQuery, BooleanClause.Occur.SHOULD);
            if (query.codePoints().anyMatch(codePoint -> {
                Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
                return script == Character.UnicodeScript.HAN || script == Character.UnicodeScript.HIRAGANA || script == Character.UnicodeScript.KATAKANA || script == Character.UnicodeScript.HANGUL;
            })) {
                QueryBuilder queryBuilder = new QueryBuilder(cjkAnalyzer);
                BooleanQuery.Builder cjkClauses = new BooleanQuery.Builder().setMinimumNumberShouldMatch(1);
                Query titleQuery = queryBuilder.createPhraseQuery("titleCjk", query);
                Query tagsQuery = queryBuilder.createPhraseQuery("tagsCjk", query);
                if (titleQuery != null) {
                    cjkClauses.add(titleQuery, BooleanClause.Occur.SHOULD);
                }
                if (tagsQuery != null) {
                    cjkClauses.add(tagsQuery, BooleanClause.Occur.SHOULD);
                }
                booleanClauses.add(cjkClauses.build(), BooleanClause.Occur.MUST);
            } else {
                booleanClauses.setMinimumNumberShouldMatch(1);
            }

            TopDocs topDocs = searcher.search(booleanClauses.build(), 100);
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc doc : topDocs.scoreDocs) {
                if (doc.score < 0.5f)
                    continue;
                Document document = storedFields.document(doc.doc);
                ResourceLocation entryId = ResourceLocation.tryParse(document.get("ID"));
                String connectedIndex = document.get("connectedIndex");
                DocEntry entry = DocumentationRegistry.getEntry(entryId);
                if (connectedIndex != null) {
                    ConnectedSearch connectedSearch = connectedSearches.get(Integer.parseInt(connectedIndex));
                    results.add(new Result(entry, connectedSearch.title(), connectedSearch.icon()));
                } else {
                    results.add(new Result(entry, entry.entryTitle(), entry.renderStack()));
                }
            }
            String finalQuery = query;
            results.sort((a, b) -> {
                int aScore = a.entry.entryTitle().getString().toLowerCase(Locale.ROOT).startsWith(finalQuery) ? 1 : 0;
                int bScore = b.entry.entryTitle().getString().toLowerCase(Locale.ROOT).startsWith(finalQuery) ? 1 : 0;
                return bScore - aScore;
            });
        } catch (Exception e) {
            if (e instanceof ParseException) {
                return results;
            }
            e.printStackTrace();
        }
        return results;
    }

    public record Result(DocEntry entry, Component displayTitle, ItemStack icon) {
    }
}
