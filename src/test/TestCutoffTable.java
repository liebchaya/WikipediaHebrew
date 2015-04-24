package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import evaluation.EvalCutOffTable;

public class TestCutoffTable {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		generateTrainCutOffs();
		calcTestScores(); 
	}
		
	
	public static void calcTestScores() throws IOException {
		EvalCutOffTable eval = new EvalCutOffTable();
		String annoFile = "F:\\ScoredFile\\FinalTestSet_orig.txt";
		File scoredFileDir = new File("F:\\ScoredFileTest\\WikiTitleExp5\\Pre\\Test");
		File summaryFile = new File("F:\\ScoredFileTest\\WikiTitleExp5\\Pre\\Train\\CutOff\\Summary.txt");
		
		File cutoffDir = new File(scoredFileDir+"\\CutOff");
		if (!cutoffDir.exists())
			cutoffDir.mkdir();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(cutoffDir + "\\Summary.txt"));
		writer.write("Measure\tScore\tThreshold\tRecall\tPrecision\tCorrectlyClassifed\tIncorrectlyClassifed\n");
		
		BufferedReader summaryReader = new BufferedReader(new FileReader(summaryFile));
		String line = summaryReader.readLine(); // skip the file caption
		line = summaryReader.readLine();
		while (line!=null){
			String[] tokens = line.split("\t");
			double threshold = Double.parseDouble(tokens[3]);
			if (!tokens[0].equals("QF")) {
				File scoreFile = new File(scoredFileDir + "\\" +  tokens[0] + ".txt");
				writer.write(tokens[0]+"\t"+ eval.getSpecificScoreData(annoFile, scoreFile.getAbsolutePath(), 1, threshold));
			} else {
				File scoreFile = new File(scoredFileDir + "\\CLARITY.txt");
				writer.write(tokens[0]+"\t"+ eval.getSpecificScoreData(annoFile, scoreFile.getAbsolutePath(), 2, threshold));
			}
			line = summaryReader.readLine();
		}
		summaryReader.close();
		writer.close();
	}
	
	
	public static void generateTrainCutOffs() throws IOException {
		String annoFile = "F:\\ScoredFile\\FinalTrainSet_orig.txt";
		File scoredFileDir = new File("F:\\ScoredFileTest\\WikiTitleExp5\\Pre\\Train");
		File resultFolder = new File(scoredFileDir+"\\CutOff");
		if (!resultFolder.exists())
			resultFolder.mkdir();
		BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(resultFolder+"\\Summary.txt"));
		summaryWriter.write("Measure\tCorrectlyClassifed\tPerCorrectlyClassified\tThreshold\n");
		for (File f:scoredFileDir.listFiles()){
			if (f.isFile()&& f.getName().endsWith(".txt")){
				EvalCutOffTable eval = new EvalCutOffTable();
				BufferedWriter writer = new BufferedWriter(new FileWriter(resultFolder+"\\"+f.getName()));
				writer.write(eval.getTable(annoFile, f.getAbsolutePath(), 1));
				writer.close();
				summaryWriter.write(f.getName().replace(".txt", "")+"\t"+eval.getMaxCorrectlyClassified()+"\t"+(double)eval.getMaxCorrectlyClassified()/500+"\t"+eval.getThreshold()+"\n");
				if (f.getName().equals("CLARITY.txt")){
					writer = new BufferedWriter(new FileWriter(resultFolder+"\\QF.txt"));
					EvalCutOffTable eval2 = new EvalCutOffTable();
					writer.write(eval2.getTable(annoFile, f.getAbsolutePath(), 2));
					writer.close();
					summaryWriter.write("QF\t"+eval2.getMaxCorrectlyClassified()+"\t"+(double)eval2.getMaxCorrectlyClassified()/500+"\t"+eval2.getThreshold()+"\n");
				}
				
			}
		}
		summaryWriter.close();
	//		System.out.println(eval.getTable("F:\\ScoredFile\\FinalTrainSet_orig.txt", "F:\\ScoredFile\\Post\\Train\\WIG.txt", 1));
	//		System.out.println("maxCorrectlyClassifed: " + eval.getMaxCorrectlyClassified());
	//		System.out.println("CorrectlyClassifed: " + (double)eval.getMaxCorrectlyClassified()/500);
	//		System.out.println("CorrectlyClassifed: " + eval.getTreshold());
	
	}

}
