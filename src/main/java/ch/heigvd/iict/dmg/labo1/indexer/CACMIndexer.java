package ch.heigvd.iict.dmg.labo1.indexer;

import ch.heigvd.iict.dmg.labo1.parsers.ParserListener;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CACMIndexer implements ParserListener {

	private Directory 	dir 			= null;
	private IndexWriter indexWriter 	= null;

	private Analyzer 	analyzer 		= null;
	private Similarity 	similarity 		= null;

	public CACMIndexer(Analyzer analyzer, Similarity similarity) {
		this.analyzer = analyzer;
		this.similarity = similarity;
	}

	public void openIndex() {
		// 1.2. create an index writer config
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE); // create and replace existing index
		iwc.setUseCompoundFile(false); // not pack newly written segments in a compound file:
		//keep all segments of index separately on disk
		if(similarity != null)
			iwc.setSimilarity(similarity);
		// 1.3. create index writer
		Path path = FileSystems.getDefault().getPath("index");
		try {
			this.dir = FSDirectory.open(path);
			this.indexWriter = new IndexWriter(dir, iwc);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onNewDocument(Long id, String authors, String title, String summary) {

		Document doc = new Document();

		/* Response to 'What should be added to the code to have access to the term vector
		in the index?' TV = Term Vector

		FieldType fieldType = new FieldType();
		fieldType.setIndexOptions(IndexOptions.DOCS);
		fieldType.setTokenized(true);
		fieldType.setStored(true);
		fieldType.setStoreTermVectors(true);
		fieldType.setStoreTermVectorOffsets(true);
		fieldType.setStoreTermVectorPositions(true);
		fieldType.freeze();*/

		// Id
		Field fieldId = new LongPoint("id", id);
		StoredField storedId = new StoredField("id", id);

		// Authors
		String[] authorsArray = authors.split(";"); // authors might be "author1; author2; ..."
		for (String authorName : authorsArray) {
			Field author = new TextField("authors", authorName, Field.Store.YES);
			//Field author = new Field("authors", authorName, fieldType); //TV
			doc.add(author);
		}

		// Title
		Field fieldTitle = new TextField("title", title, Field.Store.YES);
		//Field fieldTitle = new Field("title",title, fieldType); //TV

		// Summary
		Field fieldSummary = new TextField("summary", summary, Field.Store.YES);
		//Field fieldSummary = new Field("summary",summary,fieldType); //TV

		doc.add(fieldId);
		doc.add(storedId);
		doc.add(fieldTitle);
		doc.add(fieldSummary);

		try {
			this.indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void finalizeIndex() {
		if(this.indexWriter != null)
			try { this.indexWriter.close(); } catch(IOException e) { /* BEST EFFORT */ }
		if(this.dir != null)
			try { this.dir.close(); } catch(IOException e) { /* BEST EFFORT */ }
	}
}
