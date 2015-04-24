package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import obj.TermResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.search.similarities.LMDirichletSimilarityAccurateDocLength;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

import statistics.TermsStat;

public class GetTermTopTfTopDoc {

	private static IndexSearcher m_unigSearcher;
	private static IndexSearcher m_bigramSearcher;
	private static IndexSearcher m_trigramSearcher;
	private static IndexSearcher m_fourgramSearcher;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws WikiApiException 
	 */
	public static void main(String[] args) throws IOException, WikiApiException {
		
		int topDocs = 50;
		// configure the database connection parameters
		String host = "localhost";
		String db = "hewiki";
		String user = "root";
		String pwd = "";
		
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setHost(host);
        dbConfig.setDatabase(db);
        dbConfig.setUser(user);
        dbConfig.setPassword(pwd);
        dbConfig.setLanguage(Language.hebrew);
        
        // Create a new Hebrew wikipedia.
        Wikipedia wiki = new Wikipedia(dbConfig);
		BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_morph.txt"));
//		BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\Test.txt"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("F:\\tfIdfTrainWikiTopDocs10.txt"));

		String indexFolder = "H:\\Prediction\\Responsa\\indexes";
		initSearchers(indexFolder);
		
		// Now search the index:
	    TermsStat stat = new TermsStat();
	    String line = reader.readLine();
	    while (line!=null){
	    	String targetTerm = line.split("\t")[0];
	    	BooleanQuery bq = new BooleanQuery();
	    	for(String term:line.split("\t")){
	    		Query query = new TermQuery(new Term("TERM_VECTOR",term));
	    		bq.add(query, Occur.SHOULD);
	    	}
		    IndexSearcher isearcher = getSearcher(targetTerm);
		    ScoreDoc[] hits = isearcher.search(bq, null, topDocs).scoreDocs;
		    writer.write(targetTerm);
		    if (hits.length==0){
			    Query query = new TermQuery(new Term("TITLE",targetTerm));
			    hits = isearcher.search(query, null, topDocs).scoreDocs;
			    if (hits.length>0){
			    	Document doc =isearcher.getIndexReader().document(hits[0].doc);
			    	String content = doc.get("TERM_VECTOR");
			    	String refTerm = targetTerm + " " + "הפניה";
			    	if (content.contains(refTerm)){
			    		refTerm = content.substring(refTerm.length()).trim();
			    		if (refTerm.trim().length()>1){
				    		int index = refTerm.indexOf("#");
				    		if(index != -1)
				    			refTerm = refTerm.substring(0,index);
			    		   	query = new TermQuery(new Term("TERM_VECTOR",refTerm));
			    		    isearcher = getSearcher(refTerm);
			    		   	hits = isearcher.search(query, null, topDocs).scoreDocs;
			    		   	System.out.println("ref title len=1");
			    		}
			    	}
			    }
			    else {
			    	Page page;
					page = wiki.getPage(targetTerm);
			    	String term = page.getTitle().getPlainTitle();
			    	query = new TermQuery(new Term("TERM_VECTOR",term));
			    	isearcher = getSearcher(term);
		    		hits = isearcher.search(query, null, topDocs).scoreDocs;
		    		System.out.println("wiki");
			    }
		    }
		    else { // if (hits.length>0)
		    	if (hits.length == 1) { //might be a redirect page
				    	Document doc =isearcher.getIndexReader().document(hits[0].doc);
				    	String content = doc.get("TERM_VECTOR");
				    	String refTerm = targetTerm + " " + "הפניה";
				    	if (content.contains(refTerm)){
				    		refTerm = content.substring(refTerm.length()).trim();
				    		if (refTerm.trim().length()>1){
					    		int index = refTerm.indexOf("#");
					    		if(index != -1)
					    			refTerm = refTerm.substring(0,index);
				    		   	Query query = new TermQuery(new Term("TERM_VECTOR",refTerm));
				    		   	isearcher =  getSearcher(refTerm);
				    		   	hits = isearcher.search(query, null, topDocs).scoreDocs;
				    		   	System.out.println("ref len=1");
				    		}
				    	}
		    	}
		    }
	    	String str="";
	    	System.out.println(targetTerm);
	    	HashMap<String, Double> dist = stat.tfDistribution(isearcher.getIndexReader(), hits, true);
	    	for(TermResult res:stat.getTopTfIdfDistribution(dist, 30)){
	    		str = str + "\t" + res.getTerm() + "#" + res.getScore(); 
	    	}
	    	writer.write("\t"+str.trim());
	    
		    writer.newLine();
		    line = reader.readLine();
	    }
	    reader.close();
	    writer.close();
	        

	}
	
	private static void initSearchers(String indexFolder) throws IOException{
			Directory dir = FSDirectory.open(new File(indexFolder+"\\unigWikiDirichletAccurate"));
			DirectoryReader reader = DirectoryReader.open(dir);
			m_unigSearcher = new IndexSearcher(reader);
			Similarity sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_unigSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File(indexFolder+"\\bigramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_bigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_bigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File(indexFolder +"\\trigramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_trigramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_trigramSearcher.setSimilarity(sim);
			
			dir = FSDirectory.open(new File(indexFolder+"\\fourgramWikiDirichletAccurate"));
			reader = DirectoryReader.open(dir);
			m_fourgramSearcher = new IndexSearcher(reader);
			sim = new LMDirichletSimilarityAccurateDocLength(1000);
			m_fourgramSearcher.setSimilarity(sim);

	}

	private static IndexSearcher getSearcher(String targetTerm){
		IndexSearcher iSearcher = null;
		switch(targetTerm.split(" ").length){
			case 1: 
				iSearcher = m_unigSearcher;
				break;
			case 2:
				iSearcher = m_bigramSearcher;
				break;
			case 3:
				iSearcher = m_trigramSearcher;
				break;
			case 4:
				iSearcher = m_fourgramSearcher;
				break;
		}
		return iSearcher;
				
		}
		
}


