package indexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * This class handles the retrieval of text from text files in the form of
 * a block of text consisting of the line containing the search query
 * and a specified number of lines before and after the line of the
 * search query.
 * @author Eric Gunn
 *
 */
public class TextBuffer {
	
	//HTML modifiers to denote the location of the queried string
	//by wrapping it
	public static final String HTML_MODIFIER_START = "<b>";
	public static final String HTML_MODIFIER_END = "</b>";
	
	private Queue<String> previousNLines;
	private String previousLine;
	private int halfTotalLines;
	private int queryLine;
	private int startLine;
	private int endLine;
	
	/**
	 * Constructor for a TextBuffer object
	 * @param halfTotalLines half the total size of the buffer or total size
	 * of the text line-wrap on either side (above and below the query string's
	 * location in the file).
	 * If this is set to 0, only the line of the query string will be
	 * returned. An exception will be thrown if less than 0
	 */
	public TextBuffer(int halfTotalLines) {
		if(halfTotalLines < 0) {
			throw(new IllegalArgumentException("Argument must be greater or"
					+ " equal to 0. Passing in 0 will return the single line"
					+ " of the queried string."));
		}
		previousNLines = new LinkedList<String>();
		this.halfTotalLines = halfTotalLines;
	}
	
	/**
	 * Gets the block of text in which the search query is found
	 * @param f file to be searched
	 * @param query string to be searched for in the given file
	 * @return String the block of text that wraps the query string
	 */
	public String getBuffer(File f, String query) {
		//make string lower case for case-insensitive comparisons
		String lowercaseQuery = query.toLowerCase();
		String bufferedString = "";
		try {
			Scanner scanner = new Scanner(f);
			int lineNum = 0;
			
			while(scanner.hasNextLine()) {
	//			System.out.println(lineNum + "th run:");
				//after first line
				if(lineNum != 0) {
					//store previous line
					storeLine(this.previousLine);
	//				printStoredLines();
				}
				String line = scanner.nextLine();
				String lowercaseLine = line.toLowerCase();
				updatePreviousLine(line);
	//			System.out.println(line);
				lineNum++;
				//if we found the first line containing the queried string
				if(lowercaseLine.contains(lowercaseQuery)) {
					//store which line the query was in
					this.queryLine = lineNum;
					int countAboveLines = 0;
					//flush the buffer
					while(!this.previousNLines.isEmpty()) {
						bufferedString += (this.previousNLines.remove() 
							+ "\n");
						countAboveLines++;
					}
					
					this.startLine = this.queryLine - countAboveLines;
					
					//cut up line containing query string in order to highlight
					int index = lowercaseLine.indexOf(lowercaseQuery);
					String lineBegin = line.substring(0, index);
					String lineMiddle = line.substring(index,
							index + query.length());
					String lineEnd = line.substring(index + query.length());
					
					//wrap query with HTML modifiers to highlight it
					String total = lineBegin + HTML_MODIFIER_START
							+ lineMiddle + HTML_MODIFIER_END + lineEnd;
					//add the buffer line
					bufferedString += (total + "\n");
					
					int countBelowLines = 0;
					
					//add the remaining halfTotalLines after
					for(int i = 0; i < this.halfTotalLines; i++) {
						if(scanner.hasNextLine()) {
							String line2 = scanner.nextLine();
							bufferedString += line2;
							if(i != this.halfTotalLines - 1) {
								bufferedString += "\n";
							}
							countBelowLines++;
						}
						//if EOF is reached, stop writing
						else {
							//delete last new line character that we added
							bufferedString = bufferedString.substring(0,
									bufferedString.length() - 1);
							break;
						}
					}
					this.endLine = this.queryLine + countBelowLines;
					//break after the rest of lines are written
					break;
				}
			}
			scanner.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
	//		System.out.println();
			return bufferedString;
		}
	}
	
	/**
	 * Helper method to store a given line into the object
	 * @param line single-line text to be stored
	 */
	public void storeLine(String line) {
		//if we don't want any buffer, don't store any lines
		if(halfTotalLines == 0) {
			return;
		}
		//if our queue reaches capacity, pop from the top of queue
		if(previousNLines.size() >= halfTotalLines) {
			previousNLines.remove();
		}
		previousNLines.add(line);
	}
	
	/**
	 * Tester method to print our stored lines
	 */
	public void printStoredLines() {
		for(String s : this.previousNLines) {
			System.out.print(s + " ");
		}
		System.out.println();
	}

	/**
	 * Setter method for previous line
	 * @param line single-line to be stored
	 */
	public void updatePreviousLine(String line) {
		this.previousLine = line;
	}
	
	/**
	 * Getter method for start line number
	 * @return int starting line number
	 */
	public int getStartLine() {
		return this.startLine;
	}
	
	/**
	 * Getter method for end line number
	 * @return int ending line number
	 */
	public int getEndLine() {
		return this.endLine;
	}
	
	/**
	 * Getter method for query line number
	 * @return int query line number
	 */
	public int getQueryLine() {
		return this.queryLine;
	}
	
}
