package evaluation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import obj.ClassificationEvaluation;

public class EvalCutOffTable {
	private HashMap<String, Boolean> annoMap = null;
	private ClassificationEvaluation eval = null;
	private TreeMap<Double,Set<String>> scoreMap = null;
	private int maxCorrectlyClassified = 0;
	private double threshold = 0;
	
	/**
	 * Get classification results for each possible score cutoff
	 * @param annoFile
	 * @param scoreFile
	 * @param scoreColumn
	 * @return
	 * @throws IOException
	 */
	public String getSpecificScoreData(String annoFile, String scoreFile, int scoreColumn, double threshold) throws IOException{
		loadGoldStandardAnnotations(annoFile);
		loadScores(scoreFile, scoreColumn);
		String result = "";
		Boolean bFound = false;
		for (Double score:scoreMap.descendingKeySet()){
			if (score<threshold){
				result = score + "\t" + threshold + "\t" + eval.getEvaluationData();
				bFound = true;
				break;
			}
			for(String targetTerm:scoreMap.get(score)){
				if(annoMap.get(targetTerm)){
					eval.incTP();
					eval.decFN();
				} else {
					eval.incFP();
					eval.decTN();
				}
			}
		}
		if (!bFound)
			result = "NA\t" + threshold + "\t" + eval.getEvaluationData();
		return result;
	}
	
	/**
	 * Get classification results for each possible score cutoff
	 * @param annoFile
	 * @param scoreFile
	 * @param scoreColumn
	 * @return
	 * @throws IOException
	 */
	public String getTable(String annoFile, String scoreFile, int scoreColumn) throws IOException{
		loadGoldStandardAnnotations(annoFile);
		loadScores(scoreFile, scoreColumn);
		if (eval.getCorrectlyClassified() > maxCorrectlyClassified)
			maxCorrectlyClassified = eval.getCorrectlyClassified();
		String resultsTable = "Score\t" + eval.getEvaluationCaption() + "all_False\t" + eval.getEvaluationData();
		for (Double score:scoreMap.descendingKeySet()){
			for(String targetTerm:scoreMap.get(score)){
				if(annoMap.get(targetTerm)){
					eval.incTP();
					eval.decFN();
				} else {
					eval.incFP();
					eval.decTN();
				}
			}
			if (eval.getCorrectlyClassified() > maxCorrectlyClassified){
				maxCorrectlyClassified = eval.getCorrectlyClassified();
				threshold = score;
			}
			resultsTable += score + "\t" + eval.getEvaluationData();
		}
		return resultsTable;
	}
	
	/**
	 * Get the measure's maximum correctly classified terms 
	 * @return
	 */
	public int getMaxCorrectlyClassified(){
		return maxCorrectlyClassified;
	}
	
	/**
	 * Get the score of the measure's maximum correctly classified terms 
	 * @return
	 */
	public double getThreshold(){
		return threshold;
	}
	
	/**
	 * Load goldStandard classification initiate evaluation object to classifying all the terms as false
	 * @param annoFile
	 * @throws IOException
	 */
	private void loadGoldStandardAnnotations(String annoFile) throws IOException{
		eval = new ClassificationEvaluation();
		annoMap = new HashMap<String, Boolean>();
		BufferedReader reader = new BufferedReader(new FileReader(annoFile));
		String line = reader.readLine();
		while (line!=null){
			boolean bAnno = (line.split("\t")[1].equals("0")?false:true);
			annoMap.put(line.split("\t")[0], bAnno);
			// Initiate eval for "all false" classification
			if (!bAnno)
				eval.incTN();
			else
				eval.incFN();
			line = reader.readLine();
		}
		reader.close(); 
	}
	
	/**
	 * Load measure's scores
	 * @param scoreFile
	 * @param scoreColumn
	 * @throws IOException
	 */
	private void loadScores(String scoreFile, int scoreColumn) throws IOException{
		scoreMap = new TreeMap<Double,Set<String>>();
		BufferedReader reader = new BufferedReader(new FileReader(scoreFile));
		String line = reader.readLine();
		System.out.println(scoreFile);
		while (line!=null){
			String targetTerm = line.split("\t")[0];
			if(line.split("\t")[scoreColumn].equals("NA")){
				System.out.println("NA: "+scoreFile);
				line = reader.readLine();
				continue;
			}
			Double score = Double.parseDouble(line.split("\t")[scoreColumn]);
			if(scoreMap.containsKey(score))
				scoreMap.get(score).add(targetTerm);
			else {
				HashSet<String> set = new HashSet<String>();
				set.add(targetTerm);
				scoreMap.put(score, set);
			}
			line = reader.readLine();
		}
		reader.close(); 
	}
}
