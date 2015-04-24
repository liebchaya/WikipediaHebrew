package obj;

public class TermResult {
	public TermResult(String term, Double score) {
		super();
		this.term = term;
		this.score = score;
	}
	
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	
	String term;
	Double score;
}
