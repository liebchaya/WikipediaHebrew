package test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import qe.QeTreatment;
import qe.SCQScorer;


public class TestQEfile {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		QeTreatment qe = new QeTreatment();
		qe.loadQeTerms(new File("F:\\ScoredFile\\FinalTestSet_morph.txt"),new File("F:\\tfIdfTestWikiTopDocs50.txt"), 5);
		
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
//		IndexSearcher searcher = new IndexSearcher(reader);  
//		SCQScorer scq = new SCQScorer(searcher);
//		qe.filterDataMap(scq,80);
		
//		System.out.println(qe.addTerms4Query("הארצה"));
//		
		LinkedList<Set<String>> queryList = new LinkedList<Set<String>>();
//		String line = "דלוחין";
		String line = "מלחמת העולם השניה	ממלחמת העולם השניה	במלחמת העולם השניה";
		for(String t:line.split("\t")){
			for(int pos=0; pos< t.split(" ").length; pos++){
				if (queryList.size()-1 < pos){
					Set<String> queries = new HashSet<String>();
					queries.add( t.split(" ")[pos]);
					queryList.add(pos, queries);
				} else {
					queryList.get(pos).add(t.split(" ")[pos]);
				}
			}
		}
		
//		qe.AddQeTerms("דלוחין",queryList);
		qe.addQeTerms("מלחמת העולם השניה",queryList);
		System.out.println(queryList);
//		qe.AddQeTerms("בויסק יש", null);
		
//		String line = "מחזור הדם	ממחזור הדם	שמחזור הדם	ומחזור הדם	למחזור הדם	במחזור הדם	ולמחזור הדם";
		List<LinkedList<String>> queryListTf = new LinkedList<LinkedList<String>>();
//		// build query list for tfscorer
		for(String t:line.split("\t")){
			LinkedList<String> queries = new LinkedList<String>();
			for(String t1:t.split(" "))
				queries.add(t1);
			queryListTf.add(queries);
		}
		System.out.println(queryListTf);
			
		System.out.println(qe.getQeTerms4TFquery("מחזור הדם"));
//		System.out.println(qe.addTerms4TFquery("מכונת אמת"));

	}

}
