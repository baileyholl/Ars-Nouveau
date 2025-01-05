package com.hollingsworth.arsnouveau.api.documentation.search;

import com.hollingsworth.arsnouveau.api.documentation.entry.DocEntry;
import com.hollingsworth.arsnouveau.api.registry.DocumentationRegistry;
import com.hollingsworth.arsnouveau.client.documentation.DocDataLoader;
import net.minecraft.resources.ResourceLocation;
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
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Search {
    public static IndexSearcher searcher;
    public static PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer());

    public static void initSearchIndex(){
        try {
            Map<String, Analyzer> perFieldAnalyzer = Map.of("title", new EnglishAnalyzer(), "titleGrams", new NGramAnalyzer(2, 3));
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

    public static List<ResourceLocation> search(String query)
    {
        if(query == null || query.isEmpty()){
            return new ArrayList<>();
        }
        List<ResourceLocation> results = new ArrayList<>();
        try{
//            QueryParser parser = new QueryParser("title", new EnglishAnalyzer());
//            parser.
//            Query query1 = new QueryBuilder(nGramAnalyzer).createPhraseQuery("title", query);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"title", "titleGrams"}, analyzer);
//            QueryParser parser = new QueryParser("title", analyzer);
            Query nGramQuery = parser.parse(query);
            if(nGramQuery == null){
                return results;
            }
            BooleanQuery booleanClauses = new BooleanQuery.Builder().add(nGramQuery, BooleanClause.Occur.SHOULD).build();
            TopDocs topDocs = searcher.search(booleanClauses, 10);
            StoredFields storedFields = searcher.storedFields();
            System.out.println("Search Results:");
            for(ScoreDoc doc : topDocs.scoreDocs){
                Document document = storedFields.document(doc.doc);
                results.add(ResourceLocation.tryParse(document.get("ID")));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }
}
