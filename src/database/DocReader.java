package database;

import java.io.Reader;



/**
 * Document reader
 * @author HZ
 */
public abstract class DocReader extends Reader 
{
	/**
	 * Progresses the reader's head to the next doc to read
	 * (either sentence or paragraph depending on the implementation).
	 * @return false iff reached the end of corpus. 
	 */
	public abstract boolean next() throws IndexerException;

	/**
	 * the current doc's ID
	 * @return the current doc's ID
	 * @throws IndexerException
	 */
	public abstract int docId() throws IndexerException;
	
	/**
	 * Gets the current doc's contents
	 * @return the current doc's contents
	 * @throws IndexerException
	 */
	public abstract String doc() throws IndexerException;

	/**
	 * Gets the next token
	 * @return the next token, or null when finished current text.
	 * @throws IndexerException
	 */
	public abstract String readToken() throws IndexerException;
	
	/**
	 * Gets the wikipedia page title
	 * @return title
	 * @throws IndexerException
	 */
	public abstract String title() throws IndexerException;
	
	/**
	 * Gets the wikipedia page category id
	 * @return category id
	 * @throws IndexerException
	 */
	public abstract String categoryId() throws IndexerException;
	
	/**
	 * Gets the wikipedia page category name
	 * @return category name
	 * @throws IndexerException
	 */
	public abstract String categoryName() throws IndexerException;
	
	/**
	 * Gets the document length
	 * @return document length
	 * @throws IndexerException
	 */
	public abstract int length() throws IndexerException;
	
}
