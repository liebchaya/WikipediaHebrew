package qe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import morphology.Tagger;

public class QeTreatment {
	
	private HashMap<String,HashSet<String>> dataMap = null;
	private HashMap<String,Integer> lemma2ListIdMap = null;
	private IndexSearcher searcher = null;
	
	
	public QeTreatment() throws Exception {
		Tagger.init("F:\\BGUTaggerData\\");
		Directory directory = FSDirectory.open(new File("F:\\Responsa\\indexes\\unigPre"));
		DirectoryReader reader = DirectoryReader.open(directory);  
		searcher = new IndexSearcher(reader); 
	}

	/**
	 * @param args
	 */
	public LinkedList<Set<String>> getQeTerms4Query(String term) {
		LinkedList<Set<String>> termsList = new LinkedList<Set<String>>();
		if (dataMap.containsKey(term)){
			HashSet<String> terms = dataMap.get(term);
			for(String t:terms){
				HashSet<String> newTerms = new HashSet<String>();
				newTerms.add(t);
				termsList.add(newTerms);
			}
		}
		return termsList;
	}
	
	public LinkedList<LinkedList<String>> getQeTerms4TFquery(String term) {
		LinkedList<LinkedList<String>> termsList = new LinkedList<LinkedList<String>>();
		if (dataMap.containsKey(term)){
			HashSet<String> terms = dataMap.get(term);
			for(String t:terms){
				LinkedList<String> newTerms = new LinkedList<String>();
				newTerms.add(t);
				termsList.add(newTerms);
			}
		}
		return termsList;
	}
	
	public String getQeTermsLine(String term) {
		String expLine = "";
		if (dataMap.containsKey(term)){
			HashSet<String> terms = dataMap.get(term);
			for(String t:terms){
				expLine += "\t" + t;
			}
		}
		return expLine;
	}

	public void addQeTerms(String targetTerm, LinkedList<Set<String>> queryList) throws Exception{
		if (targetTerm.equals("נמל חיפה"))
			System.out.println("here");
		lemma2ListIdMap = new HashMap<String, Integer>();
		List<String> lemmaList = Tagger.getTaggerLemmasList(targetTerm);
		for(int i=0; i<lemmaList.size();i++)
			lemma2ListIdMap.put(lemmaList.get(i),i);
		for(String expTerm:dataMap.get(targetTerm)){
			expTerm = expTerm.trim().replaceAll(" +", " ");
			lemmaList = Tagger.getTaggerLemmasList(expTerm);
			for(int i=0; i<expTerm.split(" ").length; i++){
				int expFreq = searcher.getIndexReader().docFreq(new Term("TERM_VECTOR",expTerm.split(" ")[i]));
				if (expFreq == 0)
					continue;
				String wLemma = null;
				if (expTerm.split(" ").length > lemmaList.size())
					wLemma = expTerm.split(" ")[i];
				else
					wLemma = lemmaList.get(i);
				if(lemma2ListIdMap.containsKey(wLemma))
					queryList.get(lemma2ListIdMap.get(wLemma)).add(expTerm.split(" ")[i]);
				else{
					HashSet<String> newStringSet = new HashSet<String>();
					newStringSet.add(expTerm.split(" ")[i]);
					int pos = queryList.size();
					queryList.add(newStringSet);
					lemma2ListIdMap.put(wLemma, pos);
				}
					
			}
		}
//		System.out.println(queryList);
		
	}
	
	public void loadQeTerms(File targetTermsFile, File termsFile, int topTerms) throws IOException {
		HashMap<String, HashSet<String>> targetTermsMap = new HashMap<String, HashSet<String>>();
		BufferedReader fileReader = new BufferedReader(new FileReader(targetTermsFile));
		String line = fileReader.readLine();
		while (line!=null){
			String[] tokens = line.split("\t");
			HashSet<String> expSet = new HashSet<String>();
			for(int i=0; i<tokens.length; i++)
				expSet.add(tokens[i]);
			targetTermsMap.put(tokens[0],expSet);
			line = fileReader.readLine();
		}
		fileReader.close();

		
		dataMap = new HashMap<String, HashSet<String>>();
		fileReader = new BufferedReader(new FileReader(termsFile));
		line = fileReader.readLine();
		while (line!=null){
			int termsNum = 0;
			String[] tokens = line.split("\t");
			if (tokens.length > 1) {
				HashSet<String> expSet = new HashSet<String>();
				for(int i=0; termsNum<topTerms && i<tokens.length-1; i++){
					if (tokens[0].equals("כסף חי"))
						System.out.println(tokens[0]);
//					if (tokens.length-1 > i){
						String expTerm = tokens[i+1].split("#")[0];
						expTerm = expTerm.replaceAll("\\p{P}", "").trim();
						if(!targetTermsMap.get(tokens[0]).contains(expTerm) && expTerm.split(" ").length == tokens[0].split(" ").length){
							termsNum++;
							expSet.add(expTerm);
//						}
					}
				}
				dataMap.put(tokens[0],expSet);
			}
			line = fileReader.readLine();
		}
		fileReader.close();

	}
	 
	public void filterDataMap(SCQScorer scq, double threshold) throws IOException{
		for(String targetTerm:dataMap.keySet()){
			for (Iterator<String> i = dataMap.get(targetTerm).iterator(); i.hasNext();){
//			for(String expTerm:dataMap.get(targetTerm)){
				String expTerm = i.next();
				double avg = 0;
				for(String term:expTerm.split(" ")){
					HashSet<String> expSet = new HashSet<String>();
					expSet.add(term);
					avg += scq.score(expSet);
				}
				avg = avg/expTerm.split(" ").length;
				if (avg < threshold)
//					dataMap.get(targetTerm).remove(expTerm);
					i.remove();
			}
		}
	}

}
