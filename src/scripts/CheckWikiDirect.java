package scripts;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;

public class CheckWikiDirect {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws WikiApiException 
	 */
	public static void main(String[] args) throws IOException, WikiApiException {
		String host = "localhost";
		String db = "hewiki";
		String user = "root";
		String pwd = "";
		String title = "מכונת אמת";
		String LF = de.tudarmstadt.ukp.wikipedia.api.WikiConstants.LF;
		
        // configure the database connection parameters
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setHost(host);
        dbConfig.setDatabase(db);
        dbConfig.setUser(user);
        dbConfig.setPassword(pwd);
        dbConfig.setLanguage(Language.hebrew);
        
        // Create a new Hebrew wikipedia.
        Wikipedia wiki = new Wikipedia(dbConfig);
        
//        /*First demo*/
//        // Get the page with title
//        // May throw an exception, if the page does not exist.
        BufferedReader reader = new BufferedReader(new FileReader("F:\\direct.txt"));
        String line = reader.readLine();
        System.out.println(line);
        Page page;
        LinkedList<String> missList = new LinkedList<>();
        while(line!=null) {
			try {
				page = wiki.getPage(line);
			} catch (WikiApiException e) {
				missList.add(line);
				 line = reader.readLine();
				 continue;
			}
	        System.out.println(line);
	        System.out.println(page.getTitle().getPlainTitle()+"\t"+page.getPageId());
	        line = reader.readLine();
        }
        System.out.println(missList.size());
        System.out.println(missList);
        reader.close();
	}

}
