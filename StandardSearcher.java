package indexer;

import java.io.File;
import java.util.HashMap;

public class StandardSearcher {
	
	public StandardSearcher() {}
	
	/**
	 * Gets results of a search for the existence of the given query string
	 * in the given directory inside files of the given extension
	 * @param extension the file extension for files to be searched
	 * @param directory the file directory to search in
	 * @param halfBufferSize half the total size of the buffer or total size
	 * of the text line-wrap on either side (above and below the query string's
	 * location in the file)
	 * @param queryString text or string to be searched for
	 * @return HashMap a hash map that pairs file names with
	 * their block of text in which their query string was found
	 */
	public HashMap<String, String> getSearchResults(String extension,
			String directory, int halfBufferSize, String queryString) {
		
		if(halfBufferSize < 0) {
			throw(new IllegalArgumentException("Argument must be greater or"
					+ " equal to 0. Passing in 0 will return the single line"
					+ " of the queried string."));
		}
		File[] files = Indexer.FileRetriever.retrieveFiles(extension,
				directory);
		
		HashMap<String, String> results =
				new HashMap<String, String>(files.length * 2);
		
		for(File f : files) {
			TextBuffer buff = new TextBuffer(halfBufferSize);
			results.put(f.getName(), buff.getBuffer(f,  queryString));
		}
		
		return results;
	}
	
	/**
	 * Gets results of a search for the existence of the given query string
	 * in the form of our custom structure defined in ResultStruct.java
	 * @param extension the file extension for files to be searched
	 * @param directory the file directory to search in
	 * @param halfBufferSize half the total size of the buffer or total size
	 * of the text line-wrap on either side (above and below the query string's
	 * location in the file)
	 * @param queryString text or string to be searched for
	 * @return ResultStruct a custom struct that extends the Java standard
	 * hash map and stores a lot of relevant information for a search operation
	 */
	public ResultStruct getSearchResultStruct(String extension,
			String directory, int halfBufferSize, String queryString) {
		
		if(halfBufferSize < 0) {
			throw(new IllegalArgumentException("Argument must be greater or"
					+ " equal to 0. Passing in 0 will return the single line"
					+ " of the queried string."));
		}
		File[] files = Indexer.FileRetriever.retrieveFiles(extension,
				directory);
		String[] fileNames = new String[files.length];
		String[] buffers = new String[files.length];
		int[] startLines = new int[files.length];
		int[] endLines = new int[files.length];
		int[] queryLines = new int[files.length];
		
		int count = 0;
		
		for(File f : files) {
			TextBuffer buff = new TextBuffer(halfBufferSize);
			fileNames[count] = f.getName();
			buffers[count] = buff.getBuffer(f, queryString);
			startLines[count] = buff.getStartLine();
			endLines[count] = buff.getEndLine();
			queryLines[count] = buff.getQueryLine();
			count++;
		}
		
		ResultStruct results =
				new ResultStruct(fileNames, buffers, startLines, endLines,
						queryLines, extension, directory, queryString);
		
		return results;
	}
}
