package obj;

public class ClassificationEvaluation {
	private int TP = 0;
	private int FP = 0;
	private int TN = 0;
	private int FN = 0;
	
	public int getTP() {
		return TP;
	}
	public void incTP() {
		TP++;
	}
	public int getFP() {
		return FP;
	}
	public void incFP() {
		FP++;
	}
	public int getTN() {
		return TN;
	}
	public void incTN() {
		TN++;
	}
	public void decTN() {
		TN--;
	}
	public int getFN() {
		return FN;
	}
	public void incFN() {
		FN++;
	}
	public void decFN() {
		FN--;
	}
	
	public float getRecall(){
		return (float)TP/(TP+FN);
	}
	
	public float getPrecision(){
		return (float)TP/(TP+FP);
	}
	
	public int getCorrectlyClassified(){
		return TP+TN;
	}
	
	public int getIncorrectlyClassified(){
		return FP+FN;
	}
	
	public String getEvaluationCaption(){
		String caption = "Recall\tPrecision\tCorrectlyClassified\tIncorrectlyClassified\n";
		return caption;
	}
	
	public String getEvaluationData(){
		String data = getRecall()+"\t"+getPrecision() + "\t" + getCorrectlyClassified() + "\t" + getIncorrectlyClassified() + "\n";
		return data;
	}
	
}
