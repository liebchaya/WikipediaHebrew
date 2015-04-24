/**
 * 
 */
package database;



import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.StringTokenizer;

import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;



/**
 * Responsa document reader
 * @author HZ
 */
public class WikipediaDocReader extends DocReader
{
	private Wikipedia wiki = null;
	private Iterator<Page> pageIterator = null;
	
	private int currID = -1;
	private String currTitle = null;
	
	private StringReader currReader;
	private StringTokenizer st;
	private String currDocText;
	private String currCatgoryId;
	private String currCatgoryName;

	/**
	 * @param dir folder containing files to read
	 * @throws IndexerException
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public WikipediaDocReader() throws IndexerException, InstantiationException, IOException
	{
		super();
		init();
	}
	
	/* (non-Javadoc)
	 * @see new_search.DocReader#next()
	 */
	@Override
	public boolean next() throws IndexerException
	{
		boolean ret;
		if (ret = pageIterator.hasNext())
		{
			Page currPage = pageIterator.next();
			currID = currPage.getPageId();
			try {
				currTitle = currPage.getTitle().getPlainTitle();
				currDocText = CleanWikiPage.clean(currPage.getPlainText());
				currCatgoryId = "";
				currCatgoryName = "";
				for(Category cat:currPage.getCategories()){
					currCatgoryId +=  cat.__getId() + "$";
					currCatgoryName += cat.getTitle()  + "$";
				}
			} catch (WikiApiException e) {
				throw new IndexerException("Problen in wiki: currPage.getTitle().getPlainTitle() or currPage.getPlainText()");
			}

			st = new StringTokenizer(currDocText);
			if (currReader != null)
				currReader.close();
			currReader = new StringReader(currDocText);
			 
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#docId()
	 */
	@Override
	public int docId() 
	{
		return currID;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#doc()
	 */
	@Override
	public String doc()
	{
		return currDocText;
	}

	/* (non-Javadoc)
	 * @see new_search.DocReader#readToken()
	 */
	@Override
	public String readToken() throws IndexerException
	{
		if (st == null)
			throw new IndexerException("you must call next() before calling readToken()");
		
		return st.hasMoreTokens() ? st.nextToken() : null;
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException
	{
		return currReader.read(cbuf, off, len);
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException
	{
		if (currReader != null)
			currReader.close();
	}

	
	/* (non-Javadoc)
	 * @see new_search.DocReader#length()
	 */
	@Override
	public int length(){
		return currDocText.split(" ").length;
	}

	//
	////////////////////////////////////////////////////////// PRIVATE /////////////////////////////////////////////
	//
	
	/**
	 * Wikipedia database initialization
	 * @throws IndexerException 
	 */
	private void init() throws IndexerException
	{
		String host = "localhost";
		String db = "hewiki";
		String user = "root";
		String pwd = "";
		
        // configure the database connection parameters
        DatabaseConfiguration dbConfig = new DatabaseConfiguration();
        dbConfig.setHost(host);
        dbConfig.setDatabase(db);
        dbConfig.setUser(user);
        dbConfig.setPassword(pwd);
        dbConfig.setLanguage(Language.hebrew);
        
        // Create a new Hebrew wikipedia.
        try {
			wiki = new Wikipedia(dbConfig);
		} catch (WikiInitializationException e) {
			throw new IndexerException("Problen in wiki intialization");
		}
        pageIterator = wiki.getPages().iterator();
	}
	

	@Override
	public String title() throws IndexerException {
		// TODO Auto-generated method stub
		return currTitle;
	}

	@Override
	public String categoryId() throws IndexerException {
		// TODO Auto-generated method stub
		return currCatgoryId;
	}

	@Override
	public String categoryName() throws IndexerException {
		// TODO Auto-generated method stub
		return currCatgoryName;
	}
	
}
	
