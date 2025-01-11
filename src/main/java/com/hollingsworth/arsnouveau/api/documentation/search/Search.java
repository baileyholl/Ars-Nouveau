package com.hollingsworth.arsnouveau.api.documentation.search;

import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.documentation.DocDataLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.apache.lucene.analysis.Analyzer;
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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Search {
    public static IndexSearcher searcher;
    public static PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());
    public static List<ConnectedSearch> connectedSearches = new ArrayList<>();

    public static void addConnectedSearch(ConnectedSearch connectedSearch){
        connectedSearches.add(connectedSearch);
    }

    public static void initSearchIndex(){
        try {
            Map<String, Analyzer> perFieldAnalyzer = Map.of("title", new EnglishAnalyzer(), "titleGrams", new NGramAnalyzer(2, 3), "tags", new EnglishAnalyzer());
            analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), perFieldAnalyzer);
            Files.createDirectories(Path.of(DocDataLoader.DATA_FOLDER));
            try(Directory directory = new MMapDirectory(Path.of(DocDataLoader.DATA_FOLDER + "search_index"))){
                IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer));
                writer.deleteAll();
                for(DocEntry docEntry : DocumentationRegistry.getEntries()) {
                    Document document = new Document();
                    document.add(new StoredField("ID", docEntry.id().toString()));
                    document.add(new TextField("title", docEntry.entryTitle().getString(), Field.Store.YES));
                    document.add(new TextField("titleGrams", docEntry.entryTitle().getString(), Field.Store.YES));
                    for(Component tag : docEntry.searchTags()){
                        document.add(new TextField("tags", tag.getString(), Field.Store.YES));
                    }
                    writer.addDocument(document);
                }
                for(int i = 0; i < connectedSearches.size(); i++){
                    ConnectedSearch connectedSearch = connectedSearches.get(i);
                    Document document = new Document();
                    document.add(new StoredField("ID", connectedSearch.entryId().toString()));
                    document.add(new StoredField("connectedIndex", i));
                    document.add(new TextField("title", connectedSearch.title().getString(), Field.Store.YES));
                    document.add(new TextField("titleGrams", connectedSearch.title().getString(), Field.Store.YES));
                    writer.addDocument(document);
                }
                writer.commit();
                DirectoryReader reader = DirectoryReader.open(writer);
                searcher = new IndexSearcher(reader);
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<Result> search(String query)
    {
        if(query == null || query.isEmpty()){
            return new ArrayList<>();
        }
        query = query.toLowerCase();
        List<Result> results = new ArrayList<>();
        try{
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "titleGrams", "tags"}, analyzer, Map.of("tags", 2.0f, "title", 4.0f, "titleGrams",  0.5f));
            parser.setDefaultOperator(QueryParser.Operator.OR);
            Query nGramQuery = parser.parse(query);
            if(nGramQuery == null){
                return results;
            }
            BooleanQuery booleanClauses = new BooleanQuery.Builder().setMinimumNumberShouldMatch(1).add(nGramQuery, BooleanClause.Occur.SHOULD).build();

            TopDocs topDocs = searcher.search(booleanClauses, 100);
            StoredFields storedFields = searcher.storedFields();
            for(ScoreDoc doc : topDocs.scoreDocs){
                if(doc.score < 0.5f)
                    continue;
                Document document = storedFields.document(doc.doc);
                ResourceLocation entryId = ResourceLocation.tryParse(document.get("ID"));
                String connectedIndex = document.get("connectedIndex");
                DocEntry entry = DocumentationRegistry.getEntry(entryId);
                if(connectedIndex != null){
                    ConnectedSearch connectedSearch = connectedSearches.get(Integer.parseInt(connectedIndex));
                    results.add(new Result(entry, connectedSearch.title(), connectedSearch.icon()));
                }else {
                    results.add(new Result(entry, entry.entryTitle(), entry.renderStack()));
                }
            }
            String finalQuery = query;
            results.sort((a, b) -> {
                int aScore = a.entry.entryTitle().getString().toLowerCase(Locale.ROOT).startsWith(finalQuery) ? 1 : 0;
                int bScore = b.entry.entryTitle().getString().toLowerCase(Locale.ROOT).startsWith(finalQuery) ? 1 : 0;
                return bScore - aScore;
            });
        }catch (Exception e) {
            if(e instanceof ParseException){
                return results;
            }
            e.printStackTrace();
        }
        return results;
    }

    public record Result(DocEntry entry, Component displayTitle, ItemStack icon) {
    }
}
