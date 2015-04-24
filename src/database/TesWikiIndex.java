package database;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

import utils.MathUtils;

public class TesWikiIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		File indexDir = new File("F:\\Responsa\\indexes\\wikiPages");
		Directory fsDir = FSDirectory.open(indexDir);
		// Now search the index:
	    DirectoryReader dirReader = DirectoryReader.open(fsDir);
	    IndexSearcher isearcher = new IndexSearcher(dirReader);
	    IndexReader ireader = isearcher.getIndexReader();
	    
	    int totalNum = ireader.numDocs();
	    
//	    // Parse a simple query that searches for "text":
//	    Query query = new TermQuery(new Term("TITLE","המתת_חסד"));
	    ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TERM_VECTOR", new StandardAnalyzer(Version.LUCENE_48));
//	    Query query =parser.parse("\"הפניה אפילפסיה\"");
//	    ComplexPhraseQueryParser parser = new ComplexPhraseQueryParser(Version.LUCENE_48, "TITLE", new StandardAnalyzer(Version.LUCENE_48));
	    Query query =parser.parse("\"מכונת אמת\"");
	    System.out.println(query);
	    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
	    System.out.println(hits.length);
//	    // Iterate through the results:
	    for (int i = 0; i < hits.length; i++) {
		    TreeMap<Double,TreeSet<String>> map = new TreeMap<Double,TreeSet<String>>();
		    Document doc = isearcher.doc(hits[i].doc);
		    System.out.println(doc.get("TITLE"));
//		    String content = doc.get("TITLE") + " " + "הפניה אפילפסיה";
//		    if (content.split(" ").length == Integer.parseInt(doc.get("LENGTH"))){
//		    System.out.println("Title: " + doc.get("TITLE"));
//		    System.out.println(doc.get("TERM_VECTOR"));
//		    System.out.println(doc.get("LENGTH"));
//		    }
		    Terms vector = ireader.getTermVector(hits[i].doc, "TERM_VECTOR");
		    TermsEnum termsEnum = null;
		    termsEnum = vector.iterator(termsEnum);
		    BytesRef text = null;
		    while ((text = termsEnum.next()) != null) {
		        String term = text.utf8ToString();
		        int freq = (int) termsEnum.totalTermFreq();
		        Term termInstance = new Term("TERM_VECTOR", term);                              
		        long termFreq = ireader.docFreq(termInstance);
		        double tfIdf = freq*MathUtils.log2(totalNum/termFreq);
		        if (map.containsKey(tfIdf))
		        	map.get(tfIdf).add(term);
		        else{
		        	TreeSet<String> set = new TreeSet<String>();
		        	set.add(term);
		        	map.put(tfIdf, set);
		        }
		    }
		    int count = 0;
		    for(Double score:map.descendingKeySet()){
		    	System.out.println(score + "\t" + map.get(score));
		    	count++;
		    	if(count==20)
		    		break;
		    }
	    }
	    fsDir.close();
	    dirReader.close();

	}

}
