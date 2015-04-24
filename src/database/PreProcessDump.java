package database;

public class PreProcessDump {

	    public static void main(String[] args) {
	    		org.apache.log4j.BasicConfigurator.configure();
	    		String args1[] = {"hebrew", "עמוד_ראשי", "פירושונים", "F:\\wikipedia\\jwpl"};
	            de.tudarmstadt.ukp.wikipedia.datamachine.domain.JWPLDataMachine.main(args1);
	    }

}
