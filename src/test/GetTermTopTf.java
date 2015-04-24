package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import obj.TermResult;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
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

public class GetTermTopTf {

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
        
		BufferedReader reader = new BufferedReader(new FileReader("F:\\ScoredFile\\FinalTrainSet_orig.txt"));
		BufferedWriter writer = new BufferedWriter(new FileWriter("F:\\tfIdfTrainWikiTitle.txt"));
//		File indexDir = new File("F:\\Responsa\\indexes\\wikiPages");
//		Directory fsDir = FSDirectory.open(indexDir);
		String indexFolder = "H:\\Prediction\\Responsa\\indexes";
		initSearchers(indexFolder);
		
		// Now search the index:
//	    DirectoryReader dirReader = DirectoryReader.open(fsDir);
//	    IndexSearcher isearcher = new IndexSearcher(dirReader);
	    TermsStat stat = new TermsStat();
	    LinkedList<String> missList = new LinkedList<>();
	    String line = reader.readLine();
	    while (line!=null){
	    	String targetTerm = line.split("\t")[0];
		    // Parse a simple query that searches for "text":
		    Query query = new TermQuery(new Term("TITLE",targetTerm));
		    IndexSearcher isearcher = getSearcher(targetTerm);
		    ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
		    writer.write(targetTerm);
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
		    		   	query = new TermQuery(new Term("TITLE",refTerm));
		    		   	hits = isearcher.search(query, null, 1000).scoreDocs;
		    		}
		    	}
		    	String str="";
		    	System.out.println(targetTerm);
		    	for(TermResult res:stat.topTfIdf4Doc(isearcher.getIndexReader(), hits[0].doc, 30)){
		    		str = str + "\t" + res.getTerm() + "#" + res.getScore(); 
		    	}
		    	writer.write("\t"+str.trim());
		    } else {
		    	Page page;
				page = wiki.getPage(targetTerm);
		    	String term = page.getTitle().getPlainTitle();
		    	query = new TermQuery(new Term("TITLE",term));
			    hits = isearcher.search(query, null, 1000).scoreDocs;
			    if (hits.length>0){
			    	String str="";
			    	for(TermResult res:stat.topTfIdf4Doc(isearcher.getIndexReader(), hits[0].doc, 30)){
			    		str = str + "\t" + res.getTerm()  + "#" + res.getScore(); 
			    	}
			    	writer.write("\t"+str.trim());
			    }
		    }
		    writer.newLine();
		    line = reader.readLine();
	    }
	    reader.close();
	    writer.close();
	    System.out.println(missList);
	        

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


