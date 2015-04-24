package database;

import java.util.Set;
import java.util.TreeSet;

import de.tudarmstadt.ukp.wikipedia.api.*;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;

public class ExtractDataDemo {

	/**
	 * @param args
	 * @throws WikiApiException 
	 */
	public static void main(String[] args) throws WikiApiException {
		String host = "localhost";
		String db = "hewiki";
		String user = "root";
		String pwd = "";
		String title = "שן";
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
        Page page = wiki.getPage(title);
        System.out.println(page.getPlainText());
        ParsedPage pp = page.getParsedPage();
        System.out.println(pp.getParagraphs());
//        
//        
//        /*Second demo*/
//     // the title of the page
//        System.out.println("Queried string       : " + title);
//        System.out.println("Title                : " + page.getTitle());
//
//        // whether the page is a disambiguation page
//        System.out.println("IsDisambiguationPage : " + page.isDisambiguation());
//        
//        // whether the page is a redirect
//        // If a page is a redirect, we can use it like a normal page.
//        // The other infos in this example are transparently served by the page that the redirect points to. 
//        System.out.println("redirect page query  : " + page.isRedirect());
//        
//        // the number of links pointing to this page
//        System.out.println("# of ingoing links   : " + page.getNumberOfInlinks());
//        
//        // the number of links in this page pointing to other pages
//        System.out.println("# of outgoing links  : " + page.getNumberOfOutlinks());
//        
//        // the number of categories that are assigned to this page
//        System.out.println("# of categories      : " + page.getNumberOfCategories());
//        
//        /*Third demo*/
//        StringBuilder sb = new StringBuilder();
//
//        // the title of the page
//        sb.append("Queried string : " + title + LF);
//        sb.append("Title          : " + page.getTitle() + LF);
//        sb.append(LF);
//
//        // output the page's redirects 
//        sb.append("Redirects" + LF);
//        for (String redirect : page.getRedirects()) {
//            sb.append("  " + new Title(redirect).getPlainTitle() + LF);
//        }
//        sb.append(LF);
//        
//        // output the page's categories
//        sb.append("Categories" + LF);
//        for (Category category : page.getCategories()) {
//            sb.append("  " + category.getTitle() + LF);
//        }
//        sb.append(LF);
//
//        // output the ingoing links
//        sb.append("In-Links" + LF);
//        for (Page inLinkPage : page.getInlinks()) {
//            sb.append("  " + inLinkPage.getTitle() + LF);
//        }
//        sb.append(LF);
//
//        // output the outgoing links
//        sb.append("Out-Links" + LF);
//        for (Page outLinkPage : page.getOutlinks()) {
//            sb.append("  " + outLinkPage.getTitle() + LF);
//        }
//        System.out.println(sb);
        
        /*Fourth demo*/
//        Category cat = wiki.getCategory("יהדות");
//        StringBuilder sb = new StringBuilder();
//
//        // the title of the category
//        sb.append("Title : " + cat.getTitle() + LF);
//        sb.append(LF);

//        // the number of links pointing to this page (number of superordinate categories)
//        sb.append("# super categories : " + cat.getParents().size() + LF);
//        for (Category parent : cat.getParents()) {
//            sb.append("  " + parent.getTitle() + LF);
//        }
//        sb.append(LF);
//        
//        // the number of links in this page pointing to other pages (number of subordinate categories)
//        sb.append("# sub categories : " + cat.getChildren().size() + LF);
//        for (Category child : cat.getChildren()) {
//            sb.append("  " + child.getTitle() + LF);
//        }
//        sb.append(LF);

        // the number of pages that are categorized under this category
//        sb.append("# pages : " + cat.getArticles().size() + LF);
//        for (Page page1 : cat.getArticles()) {
//            sb.append("  " + page1.getTitle() + LF);
//        }
//        
//        // extract only the pageIDs of pages that are categorized under this category   
//        sb.append("# pageIDs : " + cat.getArticleIds().size() + LF);
//        for (int pageID : cat.getArticleIds()) {
//            sb.append("  " + pageID + LF);
//        }
//        
//        System.out.println(sb);
        
        /*Fifth demo*/
//     // Add the pages categorized under the category
//        Set<String> pages = new TreeSet<String>();
//        for (Page p : cat.getArticles()) {
//            pages.add(p.getTitle().getPlainTitle());
//        }
//        
//        // Get the pages categorized under each subcategory of C
//        for (Category childCategory : cat.getDescendants()) {
//        	System.out.println(childCategory.getTitle());
//            for (Page p : childCategory.getArticles()) {
//                pages.add(p.getTitle().getPlainTitle());
//            }
//            System.out.println("Number of pages: " + pages.size());
//        }
//        
//        // Output the pages
//        for (String page1 : pages) {
//            System.out.println(page1);
//        }
        

	}

}
