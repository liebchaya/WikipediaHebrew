package database;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.LMDirichletSimilarityAccurateDocLength;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 * Lucene indexing
 * 
 * @author HZ
 * 
 */
public class Indexer {

	/**
	 * Possible document fields
	 * 
	 */
	public enum DocField {
		ID, TITLE, TERM_VECTOR, CATEGORY_ID, CATEGORY_NAME, LENGTH
	}

	/**
	 * Indexes the contents of the DocReader into the dir indexDir. Each doc
	 * will be indexed with the fields specified in combo
	 * 
	 * @param analyzer
	 * @param docReader
	 * @param indexDir
	 * @param fieldsToIndex
	 * @param dontModifyExistingIndex
	 *            if true, this method will require indexDir to be empty, and
	 *            will otherwise throw an exception
	 * @param overwriteNotApped
	 *            in case there already is an index there, true means overwrite
	 *            it; false means append
	 * @return the number of docs indexed
	 * @throws IndexerException
	 */
	public int index(Analyzer analyzer, DocReader docReader, File indexDir,
			Set<DocField> fieldsToIndex, boolean dontModifyExistingIndex,
			boolean overwriteNotApped) throws IndexerException {
		if (analyzer == null)
			throw new IndexerException("null analyzer");

		try {
			Directory fsDir = FSDirectory.open(indexDir);
			if (dontModifyExistingIndex && fsDir.listAll().length > 0)
				throw new IndexerException(
						"Failed creating index "
								+ indexDir
								+ ": Index already exists and write protection is turned on");

			// in case the index dir doesn't have a working index in it,
			// overwriteNotApped must be lit. otherwise, the indexWriter fails
			if (!indexDir.exists() || indexDir.listFiles().length == 0)
				overwriteNotApped = true;

			IndexWriterConfig conf = new IndexWriterConfig(
					Version.LUCENE_48, analyzer);
			conf.setOpenMode(overwriteNotApped ? OpenMode.CREATE
					: OpenMode.CREATE_OR_APPEND);
			
			Similarity sim = new LMDirichletSimilarityAccurateDocLength(1000);
//			Similarity sim = new LMDirichletSimilarity();
			conf.setSimilarity(sim);
			
			IndexWriter indexWriter = new IndexWriter(fsDir, conf);
			int docCounter = 0;

			final FieldType BodyOptions = new FieldType();
			BodyOptions.setIndexed(true);
			BodyOptions
					.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			BodyOptions.setStored(true);
			BodyOptions.setStoreTermVectors(true);
			try {
				while (docReader.next()) {
					Document doc = new Document();

					if (fieldsToIndex.contains(DocField.ID))
						doc.add(new IntField(DocField.ID.name(), docReader
								.docId(), Store.YES));
					
					if (fieldsToIndex.contains(DocField.TITLE))
						doc.add(new StringField(DocField.TITLE.name(), docReader
								.title(), Store.YES));
					
					if (fieldsToIndex.contains(DocField.TERM_VECTOR))
						doc.add(new Field(DocField.TERM_VECTOR.name(), docReader.doc(),
								BodyOptions));

					if (fieldsToIndex.contains(DocField.CATEGORY_ID))
						doc.add(new StringField(DocField.CATEGORY_ID.name(), docReader
								.categoryId(), Store.YES));

					if (fieldsToIndex.contains(DocField.CATEGORY_NAME))
						doc.add(new StringField(DocField.CATEGORY_NAME.name(), docReader
								.categoryName(), Store.YES));
					
					if (fieldsToIndex.contains(DocField.LENGTH))
						doc.add(new IntField(DocField.LENGTH.name(),
								docReader.length(), Store.YES));

					docCounter++;
					indexWriter.addDocument(doc);
					if (docCounter % 1000 == 0)
						System.out.println("Doc number: " + docCounter);
				}

				int numDocs = indexWriter.numDocs();
				indexWriter.close();

				return numDocs;
			} catch (Exception e) {
				throw new IndexerException(
						"Problem with docReader. Might have returned a null field value",
						e);
			}
		} catch (CorruptIndexException e) {
			throw new IndexerException("corrupt index " + indexDir, e);
		} catch (LockObtainFailedException e) {
			throw new IndexerException("Error obtaining lock on index "
					+ indexDir, e);
		} catch (IOException e) {
			throw new IndexerException("IO exception occured with index "
					+ indexDir, e);
		}

	}

	
}
