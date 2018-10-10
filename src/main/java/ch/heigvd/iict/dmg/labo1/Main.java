package ch.heigvd.iict.dmg.labo1;

import ch.heigvd.iict.dmg.labo1.indexer.CACMIndexer;
import ch.heigvd.iict.dmg.labo1.parsers.CACMParser;
import ch.heigvd.iict.dmg.labo1.queries.QueriesPerformer;
import ch.heigvd.iict.dmg.labo1.similarities.MySimilarity;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1.1. create an analyzer
        Analyzer analyser = getAnalyzer();

        // TODO student "Tuning the Lucene Score"
        //Similarity similarity = null;//new MySimilarity();
        Similarity similarity = new MySimilarity();

        CACMIndexer indexer = new CACMIndexer(analyser, similarity);
        indexer.openIndex();
        CACMParser parser = new CACMParser("documents/cacm.txt", indexer);
        parser.startParsing();
        indexer.finalizeIndex();

        QueriesPerformer queriesPerformer = new QueriesPerformer(analyser, similarity);

        // Section "Reading Index"
        readingIndex(queriesPerformer);

        // Section "Searching"
        searching(queriesPerformer);

        queriesPerformer.close();

    }

    private static void readingIndex(QueriesPerformer queriesPerformer) {
        queriesPerformer.printTopRankingTerms("authors", 10);
        queriesPerformer.printTopRankingTerms("title", 10);
    }

    private static void searching(QueriesPerformer queriesPerformer) {
        // Example
        queriesPerformer.query("compiler program");

        // TODO student
        // queriesPerformer.query(<containing the term Information Retrieval>);
        // queriesPerformer.query(<containing both Information and Retrieval>);
        // and so on for all the queries asked on the instructions...
        //
        // Reminder: it must print the total number of results and
        // the top 10 results.
    }

    private static Analyzer getAnalyzer() {

        // TODO student... For the part "Indexing and Searching CACM collection

        System.out.println("Choose Analyzer : ");
        System.out.println("1 -> StandardAnalyzer");
        System.out.println("2 -> EnglishAnalyzer");
        System.out.println("3 -> WhitespaceAnalyser");
        System.out.println("4 -> ShingleAnalyser");
        System.out.println("5 -> StopAnalyser");

        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();

        switch (i) {
            case 1:
                return new StandardAnalyzer();
            case 2:
                return new EnglishAnalyzer();
            case 3:
                return new WhitespaceAnalyzer();
            case 4:
                System.out.println("Choose Shingle value (2 or 3):");
                int value = sc.nextInt();
                if (value == 2) {
                    return new ShingleAnalyzerWrapper(2, 2);
                } else if (value == 3) {
                    return new ShingleAnalyzerWrapper(3, 3);
                } else {
                    System.err.println("Incorrect value given");
                }
            case 5:
                try {
                    return new StopAnalyzer(Paths.get("./common_words.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            default:
                System.err.println("Incorrect value given, StandardAnalyser chosen");
                return new StandardAnalyzer();
        }
        // - Indexing" use, as indicated in the instructions,
        // the StandardAnalyzer class.
        //
        // For the next part "Using different Analyzers" modify this method
        // and return the appropriate Analyzers asked.

        //return null;
    }

}
