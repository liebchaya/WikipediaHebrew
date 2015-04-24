package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class CheckWikiOverlap {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_orig.txt"));
		File indexDir = new File("F:\\Responsa\\indexes\\wikiPages");
		Directory fsDir = FSDirectory.open(indexDir);
		// Now search the index:
	    DirectoryReader dirReader = DirectoryReader.open(fsDir);
	    IndexSearcher isearcher = new IndexSearcher(dirReader);
	    int positiveCounter = 0;
	    int positiveCounter2 = 0;
	    String line = reader.readLine();
	    while (line!=null){
	    	String targetTerm = line.split("\t")[0].replaceAll(" ", "_");
	    	String targetTerm2 = line.split("\t")[0];
//	    // Parse a simple query that searches for "text":
	    	Query query = new TermQuery(new Term("TITLE",targetTerm));
//	    ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TERM_VECTOR", new StandardAnalyzer(Version.LUCENE_48));
//	    Query query =parser.parse("\"הפניה אפילפסיה\"");
//	    ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TITLE", new StandardAnalyzer(Version.LUCENE_48));
//	    Query query =parser.parse("\"המתת חסד\"");
//	    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
	    	TotalHitCountCollector collector = new TotalHitCountCollector();
//	        isearcher.search(query, collector);
//	        int count = collector.getTotalHits();
	        
	        Query query2 = new TermQuery(new Term("TITLE",targetTerm2));
	        isearcher.search(query2, collector);
	        int count2 = collector.getTotalHits();
	        
	        if(count2==0){
	        	System.out.println(targetTerm2);
	        	positiveCounter++;
	        }
//	        System.out.println(targetTerm+"\t"+count+"\t"+count2);
//	        if (count>0||count2>0)
//	        	positiveCounter++;
	        line = reader.readLine();
	    }
	    System.out.println(positiveCounter);
	    reader.close();

	}

}
