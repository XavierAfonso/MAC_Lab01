package ch.heigvd.iict.dmg.labo1.queries;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermState;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QueriesPerformer {
	
	private Analyzer		analyzer		= null;
	private IndexReader 	indexReader 	= null;
	private IndexSearcher 	indexSearcher 	= null;

	public QueriesPerformer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		Path path = FileSystems.getDefault().getPath("index");
		Directory dir;
		try {
			dir = FSDirectory.open(path);
			this.indexReader = DirectoryReader.open(dir);
			this.indexSearcher = new IndexSearcher(indexReader);
			if(similarity != null)
				this.indexSearcher.setSimilarity(similarity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void printTopRankingTerms(String field, int numTerms) {
		// TODO student
		// This methods print the top ranking term for a field.
		// See "Reading Index".
	    System.out.println("Top ranking terms for field ["  + field +"] are: ");

        HighFreqTerms.TotalTermFreqComparator cmp = new HighFreqTerms.TotalTermFreqComparator();

        try {
            TermStats[] highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader,numTerms,field,cmp);

            List<String> terms = new ArrayList<>(highFreqTerms.length);
            for (TermStats ts : highFreqTerms) {
                System.out.println(ts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public void query(String q) {
		// TODO student
		// See "Searching" section
        System.out.println("Searching for [" + q +"]");

        Query query = null;

        QueryParser parser = new QueryParser("summary",analyzer);
        try {
            query = parser.parse(q);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            ScoreDoc[] hits = indexSearcher.search(query,10).scoreDocs;


            for(ScoreDoc hit:hits){
                Document doc = indexSearcher.doc(hit.doc);
                System.out.println((hit.doc + ": " + doc.get("title") + " (" + hit.score + ")"));
                //System.out.println(doc.get("id"));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	 
	public void close() {
		if(this.indexReader != null)
			try { this.indexReader.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
}
