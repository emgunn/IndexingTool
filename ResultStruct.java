package indexer;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A class that stores a variety of details regarding a search operation
 * performed with the Indexer or SimpleSearcher classes.
 * @author Eric Gunn
 *
 */
public class ResultStruct {
	
	private static final int LOAD_FACTOR_INVERSE = 2;
	
	private HashMap<String, ResultStruct.InnerStruct> map;
	
	private String extension;
	private String query;
	private String directory;
	private int numResults;

	/**
	 * Default constructor, should use the other one
	 */
	public ResultStruct() {
		this.map = new HashMap<String, ResultStruct.InnerStruct>();
	}
	
	/**
	 * Detailed constructor for a ResultStruct object
	 * @param fileNames list of file names that were searched
	 * @param buffers list of buffers that were generated for each file
	 * @param startLines list of each file's buffer's starting line number
	 * @param endLines list of each file's buffer's ending line number
	 * @param queryLines list of each file's buffer's query line number
	 * @param extension file extension to be searched
	 * @param directory file directory to look in
	 * @param query search string to look for in each file
	 */
	public ResultStruct(String[] fileNames, String[] buffers, int[] startLines,
			int[] endLines, int[] queryLines, String extension,
			String directory, String query) {
		//create hash map with double size to avoid going over load factor
		this.map = new HashMap<String, ResultStruct.InnerStruct>
			(fileNames.length * LOAD_FACTOR_INVERSE);
		this.extension = extension;
		this.query = query;
		this.directory = directory;
		this.numResults = fileNames.length;
		
		//build each inner struct and put them in the hash map
		for(int i = 0; i < fileNames.length; i++) {
			InnerStruct inner = new InnerStruct(fileNames[i]);
			inner.setBuffer(buffers[i]);
			inner.setLineStart(startLines[i]);
			inner.setLineEnd(endLines[i]);
			inner.setQueryLine(queryLines[i]);
			if(queryLines[i] == 0) {
				inner.setNumLines(0);
			}
			else inner.setNumLines(endLines[i] - startLines[i] + 1);
			this.map.put(fileNames[i], inner);
		}
	}
	
	/**
	 * Prints the member variables of the ResultStruct
	 */
	public void print() {
		System.out.println("Result Struct: Searched for \"" + this.query + "\"");
		System.out.println("Directory: " + this.directory);
		System.out.println("Extension: " + this.extension);
		System.out.println("Number of files found: " + this.map.size());
		System.out.println();
		
		//prints each inner struct for each file
		for(Entry<String, ResultStruct.InnerStruct> e : this.map.entrySet()) {
			e.getValue().print();
		}
	}
	
	/**
	 * Getter method for map
	 * @return HashMap map of results
	 */
	public HashMap<String, ResultStruct.InnerStruct> getMap() {
		return this.map;
	}
	
	/**
	 * Getter method for extension
	 * @return String extension searched
	 */
	public String getExtension() {
		return this.extension;
	}
	
	/**
	 * Getter method for query
	 * @return String query
	 */
	public String getQuery() {
		return this.query;
	}
	
	/**
	 * Getter method for directory
	 * @return String directory
	 */
	public String getDirectory() {
		return this.directory;
	}
	
	public int getNumResults() {
		return this.numResults;
	}
	
	/**
	 * Inner class that holds each search result and its relevant details
	 * @author Eric Gunn
	 *
	 */
	public class InnerStruct {
		
		private String fileName;
		private String buffer;
		private int lineStart;
		private int lineEnd;
		private int queryLine;
		private int numLines;
		
		/**
		 * Constructor for the inner struct
		 * @param fileName the file name associated
		 */
		public InnerStruct(String fileName) {
			this.fileName = fileName;
		}
		
		/**
		 * Setter method for the associated file name
		 * @param fileName the associated file name
		 */
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		
		/**
		 * Setter method for the associated buffer
		 * @param buffer the associated buffer
		 */
		public void setBuffer(String buffer) {
			this.buffer = buffer;
		}
		
		/**
		 * Setter method for the start line number
		 * @param lineStart the starting line number
		 */
		public void setLineStart(int lineStart) {
			this.lineStart = lineStart;
		}
		
		/**
		 * Setter method for the end line number
		 * @param lineEnd the ending line number
		 */
		public void setLineEnd(int lineEnd) {
			this.lineEnd = lineEnd;
		}
		
		/**
		 * Setter method for the query line number
		 * @param queryLine the query line number
		 */
		public void setQueryLine(int queryLine) {
			this.queryLine = queryLine;
		}
		
		/**
		 * Setter method for the number of lines of buffer
		 * @param numLines the number of lines of the associated buffer
		 */
		public void setNumLines(int numLines) {
			this.numLines = numLines;
		}
		
		/**
		 * Getter method for file name
		 * @return String file name
		 */
		public String getFileName() {
			return this.fileName;
		}
	
		/**
		 * Getter method for buffer that wraps query string
		 * @return String buffer
		 */
		public String getBuffer() {
			return this.buffer;
		}
		
		/**
		 * Getter method for starting line number of buffer
		 * @return int starting line number of buffer
		 */
		public int getLineStart() {
			return this.lineStart;
		}
		
		/**
		 * Getter method for ending line number of buffer
		 * @return int ending line number of buffer
		 */
		public int getLineEnd() {
			return this.lineEnd;
		}
		
		/**
		 * Getter method for line number of the query string
		 * @return int line number of query string
		 */
		public int getQueryLine() {
			return this.queryLine;
		}
		
		/**
		 * Getter method for number of lines in the buffer
		 * @return int number of lines in the buffer
		 */
		public int getNumLines() {
			return this.numLines;
		}
		
		/**
		 * Print the member variables of the inner struct
		 */
		public void print() {
			System.out.println("InnerStruct: " + this.fileName);
			System.out.println("Block of text: " + this.buffer);
			System.out.println("Line start: " + this.lineStart);
			System.out.println("Line end: " + this.lineEnd);
			System.out.println("Query line: " + this.queryLine);
			System.out.println("Number of lines: " + this.numLines + "\n");
			
		}
	}
}
