package statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import obj.TermResult;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.util.BytesRef;

import utils.MathUtils;

public class TermsStat {
	
	
	public LinkedList<TermResult> topTfIdf4Doc(IndexReader ireader, int docId, int topRank) throws IOException{
		int totalNum = ireader.numDocs();
		TreeMap<Double,TreeSet<String>> map = new TreeMap<Double,TreeSet<String>>();
		Terms vector = ireader.getTermVector(docId, "TERM_VECTOR");
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
	    LinkedList<TermResult> resultsMap = new LinkedList<TermResult>();
	    for(Double score:map.descendingKeySet()){
	    	for(String term:map.get(score))
	    		resultsMap.add(new TermResult(term, score));
	    	count++;
	    	if(count==topRank)
	    		break;
	    }
	    return resultsMap;
	}
	
	public HashMap<String,Double> tfDistribution(IndexReader ireader, ScoreDoc[] hits, boolean includeIdf) throws IOException{
		HashMap<String,Double> resultsMap = new HashMap<String,Double>();
		// Iterate through the results:
	    for (int i = 0; i < hits.length; i++) {
		    Terms vector = ireader.getTermVector(hits[i].doc, "TERM_VECTOR");
		    TermsEnum termsEnum = null;
		    termsEnum = vector.iterator(termsEnum);
		    BytesRef text = null;
		    while ((text = termsEnum.next()) != null) {
		        String term = text.utf8ToString();
		        int freq = (int) termsEnum.totalTermFreq();
	        	 if (!resultsMap.containsKey(term))
	        		 resultsMap.put(term, (double)freq);
		        else{
		        	double prevFreq = resultsMap.get(term);
		        	resultsMap.put(term, prevFreq+freq);
		        }
		    }
	        if(includeIdf) {
	        	int totalNum = ireader.numDocs();
	        	Set<String> termsSet = resultsMap.keySet();
	        	for(String term:termsSet) {
			        Term termInstance = new Term("TERM_VECTOR", term); 
			        long termFreq = ireader.docFreq(termInstance);
			        double tfIdf = resultsMap.get(term)*MathUtils.log2(totalNum/termFreq);
			        resultsMap.put(term,tfIdf);
	        	}
	        }
	    }
	    
	    return resultsMap;
	}
	
	    public HashMap<String,Double> tfDistribution(IndexReader ireader, List<Integer> hits, boolean includeIdf) throws IOException{
			HashMap<String,Double> resultsMap = new HashMap<String,Double>();
			// Iterate through the results:
		    for (int i = 0; i < hits.size(); i++) {
			    Terms vector = ireader.getTermVector(hits.get(i), "TERM_VECTOR");
			    TermsEnum termsEnum = null;
			    termsEnum = vector.iterator(termsEnum);
			    BytesRef text = null;
			    while ((text = termsEnum.next()) != null) {
			        String term = text.utf8ToString();
			        int freq = (int) termsEnum.totalTermFreq();
		        	 if (!resultsMap.containsKey(term))
		        		 resultsMap.put(term, (double)freq);
			        else{
			        	double prevFreq = resultsMap.get(term);
			        	resultsMap.put(term, prevFreq+freq);
			        }
			    }
		        if(includeIdf) {
		        	int totalNum = ireader.numDocs();
		        	Set<String> termsSet = resultsMap.keySet();
		        	for(String term:termsSet) {
				        Term termInstance = new Term("TERM_VECTOR", term); 
				        long termFreq = ireader.docFreq(termInstance);
				        double tfIdf = resultsMap.get(term)*MathUtils.log2(totalNum/termFreq);
				        resultsMap.put(term,tfIdf);
		        	}
		        }
		    }
		    return resultsMap;
		    
	}
	    
	    public LinkedList<TermResult> getTopTfIdfDistribution(Map<String,Double> resultsMap, int topRank) {
	        Comparator<Map.Entry<String, Double>> byMapValues = new Comparator<Map.Entry<String,Double>>() {
	            @Override
	            public int compare(Map.Entry<String,Double> left, Map.Entry<String,Double> right) {
	                return right.getValue().compareTo(left.getValue());
	            }
	        };
	        
	        // create a list of map entries
	        List<Map.Entry<String, Double>> sortedMap = new ArrayList<Map.Entry<String, Double>>();
	        // add all candy bars
	        sortedMap.addAll(resultsMap.entrySet());
	        // sort the collection
	        Collections.sort(sortedMap, byMapValues);
	        
	        int count = 0;
		    LinkedList<TermResult> resultsList = new LinkedList<TermResult>();
		    ListIterator<Entry<String, Double>> it = sortedMap.listIterator();
		    while(it.hasNext()){
		    	Entry<String, Double> entry = it.next();
		    	resultsList.add(new TermResult(entry.getKey(), entry.getValue()));
		    	count++;
		    	if(count==topRank)
		    		break;
		    }
		    return resultsList;
	        }
}
