package database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;

public class CleanWikiPage {

	/**
	 * @param args
	 * @throws WikiApiException 
	 */
	public static void main(String[] args) throws WikiApiException {
		String host = "localhost";
		String db = "hewiki";
		String user = "root";
		String pwd = "";
		String title = "אפילפסיה_של_רפלקס";
		
        // configure the database connection parameters
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setHost(host);
        dbConfig.setDatabase(db);
        dbConfig.setUser(user);
        dbConfig.setPassword(pwd);
        dbConfig.setLanguage(Language.hebrew);
        
        // Create a new Hebrew wikipedia.
        Wikipedia wiki = new Wikipedia(dbConfig);
        
//       for(Page page:wiki.getArticles()) {
        // Get the page with title
        // May throw an exception, if the page does not exist.
        Page page = wiki.getPage(title);
        String pageContent = page.getPlainText();
        System.out.println(pageContent);
        System.out.println("****************************************");
        String cleanContent = clean(pageContent);
        System.out.println(cleanContent);
       for(Category cat:page.getCategories()){
        	System.out.println(cat.__getId()+":"+cat.getTitle());
        }
        
        
//       }
	}
	
	
	public static String clean(String pageContent){
	    String cutAt = "הערות שוליים TEMPLATE\\[הערות_שוליים\\]";
	    cutAt+="|"+"קישורים חיצוניים TEMPLATE\\[";
	    cutAt+="|"+"ראו גם TEMPLATE\\[";
	    cutAt+="|"+"לקריאה נוספת";
	    cutAt+="|"+"קטגוריה:";
	    cutAt+="|"+"קטגוריות:";
	    int index = indexOf(Pattern.compile(cutAt), pageContent);
	    if(index > 0 )
	       pageContent = pageContent.substring(0,index);
	    
	    pageContent = pageContent.replaceAll("TEMPLATE\\[(.*?)\\]", "");
	    pageContent = pageContent.replaceAll("\\[|\\]", "");
	    pageContent = pageContent.replaceAll("TEMPLATE|\\(TEMPLATE\\)", " ");
	    pageContent = pageContent.replaceAll("\\b\\S+\\|\\S+\\|\\b","");
//	        pageContent = pageContent.replaceAll("[a-zA-Z0-9]"," ");
	    pageContent = pageContent.replaceAll("poly[\\s|\\d]+","");
//	        pageContent = pageContent.replaceAll("\\p{P}", " ");
//	        pageContent = pageContent.replaceAll("<|>|=|\\+|-|\\*|@|#|$", " ");
	   return pageContent;    
	        
	}
	
	/** @return index of pattern in s or -1, if not found */
	private static int indexOf(Pattern pattern, String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}

}
