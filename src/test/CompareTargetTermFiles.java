package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class CompareTargetTermFiles {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File testFile = new File("F:\\ScoredFile\\FixedTestSet_orig.txt");
		File trainFile = new File("F:\\ScoredFile\\FixedTrainSet_orig.txt");
		File annoFile = new File("H:\\chaya backup07.01.2014\\iterative\\thesaurus terms\\fromJonathan.txt");
		
		loadOrigAnno(annoFile,1,2);
		HashSet<String> annoSet = new HashSet<String>(loadAnnoFile(trainFile));
		annoSet.addAll(loadAnnoFile(testFile));
		
		HashSet<String> difference = new HashSet<String>(allAnnoSet);
		difference.removeAll(annoSet);
		for(String s:difference)
			System.out.println(s);
		System.out.println(difference.size());
		
	}
	
	
	
	private static void loadOrigAnno(File annoFile, int firstAnno, int secondAnno) throws IOException{
		allAnnoSet = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(annoFile));
		String line = reader.readLine();
		line = reader.readLine();
		while(line!=null){
			String[] tokens = line.split("\t");
			String first = tokens[firstAnno];
			String second = tokens[secondAnno];
			if(first.equals("1") && second.equals("1"))
			//if(first.isEmpty() && second.isEmpty())
				allAnnoSet.add(tokens[0]);
			line = reader.readLine();
		}
		reader.close();
	}
	
	private static HashSet<String> loadAnnoFile(File annoFile) throws IOException{
		HashSet<String> annoSet = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(annoFile));
		String line = reader.readLine();
		while(line!=null){
			String[] tokens = line.split("\t");
			int anno = Integer.parseInt(tokens[1]);
			if(anno==1)
				annoSet.add(tokens[0]);
			line = reader.readLine();
		}
		reader.close();
		return annoSet;
	}

	private static HashSet<String> allAnnoSet = null;

}
