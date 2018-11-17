package indexer;

import java.io.File;
import java.time.Clock;
import java.util.Scanner;

import org.json.JSONObject;

/**
 * This application builds an index given a file path (first argument) from a
 * directory of text files given another file path (second argument). The
 * application uses a console UI for user input and resulting output. The
 * third argument represents the number of lines to be returned in the buffer
 * for the results of a search (only used in the /print and /generate
 * methods).
 * 
 * The user will be prompted for input. All searches are case-insensitive.
 * User inputs aside from the commands listed below will be treated as search
 * queries.
 * 
 * Here is a list of commands the user can use:
 * - "/directory", "/dir", or "d" - print the current index and data
 *   directories
 * - "/generate", "/gen", "/g", or "/json" - generate a JSON of the previous
 *   search result. Will not work if no prior search has been performed
 * - "/help" or "/h" - display help and instructions
 * - "/print" or "/p" - print the previous search result. Will not work if
 *   no prior search has been performed
 * - "/quit" or "/q" - exit the application
 * - "/size" or "/s" - display the size of the buffer
 * - "/write" or "/w" - write previous generated JSON to a JSON file. Will
 *   not work unless the /generate command has been called before
 * 
 * @author Eric Gunn
 *
 */
public class IndexSampleApplet {
	
	private static final int NUM_ARGS = 3;
	private static final int DEFAULT_INDENT = 4;

	public static void main(String[] args) throws Exception {
		if(args.length != NUM_ARGS) {
			System.out.println("Incorrect number of arguments (should be 2)");
			System.out.println("The first argument is the path to the " +
					"directory where we want to build the index.");
			System.out.println("The second argument is the path to the " +
					"directory of the text files we will index.");
			System.out.println("The third argument is the number of " +
					"lines that will be returned with the search query " +
					"line, above and below it.");
		}
		
		String indexPath = args[0];
		String dataPath = args[1];
		int size = Integer.parseInt(args[2]);
		
		Indexer indexer = new Indexer();
		
		Indexer.FileRetriever.printCurrentDirectory();
		File indexDir = new File(indexPath);
		File dataDir = new File(dataPath);
		
		int numIndex;
		
		String query;
		
		long timeStart1 = Clock.systemUTC().millis();
		numIndex = indexer.index(indexDir, dataDir, ".txt");
		long timeEnd1 = Clock.systemUTC().millis();
		
		System.out.println("Number of total files indexed:  " + numIndex +
				" in " + (timeEnd1 - timeStart1) + " ms");
		System.out.println();
		
		Scanner input = new Scanner(System.in);
	    System.out.println("Input a search query and press enter");
	    System.out.println("(Type in \"/help\" or \"/h\" for help)");
	    
	    ResultStruct resultStruct = null;
	    JSONObject json = null;
	    JsonGenerator gen = null;
	    boolean writtenToFile = false;
	    boolean generated = false;
	    String fileName = "";
	    
		while(true) {
			query = input.nextLine();
			if(query.equalsIgnoreCase("/quit") || query.equalsIgnoreCase("/q")) {
				break;
			}
			else if(query.equalsIgnoreCase("/directory") ||
					query.equalsIgnoreCase("/dir") ||
					query.equalsIgnoreCase("/d")) {
				System.out.println("Index directory is \"" +
					indexDir.getCanonicalPath() + "\"");
				System.out.println("Data directory is \"" +
					dataDir.getCanonicalPath() + "\"");
			}
			else if(query.equalsIgnoreCase("/help") ||
					query.equalsIgnoreCase("/h")) {
				System.out.println("This applet builds your index and " +
					"allows you to search for specific keywords.");
				System.out.println("Enter in the keyword by typing it in " +
					"and pressing enter.");
				System.out.println("(keywords are case-insensitive)");
				System.out.println("\nCommands:");
				System.out.println("- \"/directory\", \"/dir\", or \"/d\" " +
						"to print the current index and data directories");
				System.out.println("- \"/generate\", \"/gen\", \"/g\" or " +
						"\"/json\" to generate a JSON of the previous " + 
						"search result");
				System.out.println("- \"/help\" or \"/h\" for instructions " +
						"and help");
				System.out.println("- \"/print\" or \"/p\" to print the " +
						"previous results to console");
				System.out.println("- \"/quit\" or \"/q\" to exit applet");
				System.out.println("- \"/size\" or \"/s\" to view buffer " +
						"size");
				System.out.println("- \"/write\" or \"/w\" to write a " +
						"previously generated JSON to a file");
			}
			else if(query.equalsIgnoreCase("/generate") ||
					query.equalsIgnoreCase("/gen") ||
					query.equalsIgnoreCase("/g") ||
					query.equalsIgnoreCase("/json")) {
				if(resultStruct == null) {
					System.out.println("A search must be done before " +
							" generating a JSON");
				}
				else if(generated) {
					System.out.println("This search for " +
							resultStruct.getQuery() + " has already been " +
							"generated");
				}
				else {
					gen = new JsonGenerator();
					json = gen.generate(resultStruct);
					json.toString(DEFAULT_INDENT);
					
					System.out.println("JSON generation for query \"" +
							resultStruct.getQuery() + "\" successful");
					writtenToFile = false;
					generated = true;
				}
			}
			else if(query.equalsIgnoreCase("/print") ||
					query.equalsIgnoreCase("/p")) {
				if(resultStruct == null) {
					System.out.println("A search must be done before " +
							" printing results");
				}
				else {
					resultStruct.print();
				}
			}
			else if(query.equalsIgnoreCase("/size") ||
					query.equalsIgnoreCase("/s")) {
				System.out.println("The current buffer size is " + size);
			}
			else if(query.equalsIgnoreCase("/write") ||
					query.equalsIgnoreCase("/w")) {
				if(json == null) {
					System.out.println("Generate a JSON first before " +
							"writing it to a file");
				}
				else if(writtenToFile) {
					System.out.println("These results were already written " +
							"to the file " + fileName);
				}
				else {
					long curr = Clock.systemUTC().millis();
					fileName = Long.toString(curr) + ".json";
					gen.writeToFile(json, fileName);
					System.out.println("Wrote results to " + fileName);
					writtenToFile = true;
				}
			}
			else {
				long timeStart2 = Clock.systemUTC().millis();
				String[] results;
				results = indexer.searchWildcardIndex(indexDir, query, 100);
				long timeEnd2 = Clock.systemUTC().millis();
				
				System.out.println("Time elapsed for searching: "
						+ (timeEnd2 - timeStart2) + " ms");
				
				resultStruct = indexer.getResultStruct(results, size, query);
				generated = false;
			}
		}
		input.close();
	}
}
