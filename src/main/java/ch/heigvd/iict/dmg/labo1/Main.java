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
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1.1. create an analyzer
        Analyzer analyser = getAnalyzer();

        // TODO student "Tuning the Lucene Score"
        Similarity similarity = new MySimilarity();

        Instant start = Instant.now();
        CACMIndexer indexer = new CACMIndexer(analyser, similarity);
        indexer.openIndex();
        CACMParser parser = new CACMParser("documents/cacm.txt", indexer);
        parser.startParsing();
        indexer.finalizeIndex();
        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start,end);
        System.out.println("Time taken: " + timeElapsed.toMillis() + " milliseconds");

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

        queriesPerformer.query("Information Retrieval");
        queriesPerformer.query("Information AND Retrieval");
        queriesPerformer.query("+Retrieval information -Database");
        queriesPerformer.query("Info*");
        queriesPerformer.query("\'Information Retrieval\'~5");
        queriesPerformer.query("compiler program");
    }

    private static Analyzer getAnalyzer() {

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
    }
}
