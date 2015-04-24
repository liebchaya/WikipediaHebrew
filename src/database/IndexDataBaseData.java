package database;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

public class IndexDataBaseData {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws IndexerException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IndexerException, IOException {
		long start = new Date().getTime();
		long end = new Date().getTime();
		
		System.out.println("start :"+start);
//		String indexFolder = "F:\\Responsa\\indexes\\wikiPages";
		String indexFolder = "I:\\Prediction\\Responsa\\indexes\\fourgramWikiDirichletAccurate";
		DocReader reader = new WikipediaDocReader();

		System.out.println("Indexing wikipedia with lucene version: " + Version.LUCENE_48);
		File indexDir = new File(indexFolder);
		Indexer manager = new Indexer();
		Set<Indexer.DocField> fields = new HashSet<Indexer.DocField>();
		fields.add(Indexer.DocField.ID);
		fields.add(Indexer.DocField.TITLE);
		fields.add(Indexer.DocField.TERM_VECTOR);
		fields.add(Indexer.DocField.CATEGORY_ID);
		fields.add(Indexer.DocField.CATEGORY_NAME);
		fields.add(Indexer.DocField.LENGTH);
//		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
		
		Analyzer anal = new WhitespaceAnalyzer(Version.LUCENE_48);
		String tokenSeparator = " ";
		String fillerToken = "?";
		int minShingleSize = 4, maxShingleSize = 4;
		boolean outputUnigrams = false, outputUnigramsIfNoShingles = false;
		ShingleAnalyzerWrapper wrapper = new ShingleAnalyzerWrapper(
			anal, 
		    minShingleSize, maxShingleSize, tokenSeparator,
		    outputUnigrams, outputUnigramsIfNoShingles, fillerToken);
		System.out.println(wrapper.isOutputUnigrams());
		
		manager.index(wrapper, reader, indexDir , fields, false, true);
		wrapper.close();
		
//		manager.index(analyzer, reader, indexDir , fields, false, true);
//		analyzer.close();
		
		end = new Date().getTime();
		System.out.println("total run time : "+(end-start)/1000+" seconds"+"("+(end-start)/60000+" minutes)");

	}
	
	
}
