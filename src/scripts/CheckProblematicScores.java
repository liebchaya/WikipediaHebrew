package scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedMap;
import java.util.TreeMap;

public class CheckProblematicScores {

	private static SortedMap<String,Boolean> annoMap = null;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		String[] trainDirs = new String[]{"F:\\ScoredFile\\Pre\\Train","F:\\ScoredFile\\Post\\Train"};
		String[] testDirs = new String[]{"F:\\ScoredFile\\Pre\\Test","F:\\ScoredFile\\Post\\Test"};
		CheckProbTerms(trainDirs,"Train");
		CheckProbTerms(testDirs,"Test");
		
	}
	
	
	private static void CheckProbTerms(String[] dirs,String expName) throws IOException {
		HashMap<String, HashSet<String>> matchesMap = new HashMap<String, HashSet<String>>();
		for(String dir:dirs){
			File expFolder = new File(dir);
			for (File expFile:expFolder.listFiles()) {
				if (expFile.isFile()&&expFile.getName().endsWith(".txt")){
					String measureName = expFile.getName().replace(".txt", "");
					BufferedReader reader = new BufferedReader(new FileReader(expFile));
					String line = reader.readLine();
					while (line!=null){
						 String[] tokens = line.split("\t");
						 String targetTerm = tokens[0];
						 Double score = 0.0;
						 if(tokens[1].equals("NA")){
							 line = reader.readLine();
							 continue;
						 }
						score = Double.parseDouble(tokens[1]);
						if (score.isNaN()){
							if (matchesMap.containsKey(targetTerm))
								matchesMap.get(targetTerm).add(measureName);
							else {
								HashSet<String> set = new HashSet<String>();
								set.add(measureName);
								matchesMap.put(targetTerm,set);
							}
						}
						else if (score.isInfinite()){
							if (matchesMap.containsKey(targetTerm))
								matchesMap.get(targetTerm).add(expFile.getName());
							else {
								HashSet<String> set = new HashSet<String>();
								set.add(expFile.getName());
								matchesMap.put(targetTerm,set);
							}
						}
						
						line = reader.readLine();
					 }
					 reader.close();
				}
			}
		}
		System.out.println("******"+expName+"\t"+ matchesMap.size()+"******");
		for(String term:matchesMap.keySet())
			System.out.println(term + "\t" + matchesMap.get(term));
	}
	
		

	
}
