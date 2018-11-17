package indexer;

import java.io.File;
import java.time.Clock;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

public class Main {

	public static void main(String[] args) {
		
//		Indexer.FileRetriever.printCurrentDirectory();
//		System.out.println();
		
		/*for(File f : Indexer.FileRetriever.retrieveFiles(".txt", "testdir")) {
			System.out.println(f.getName());
		}*/
		
		//Indexer.FileRetriever.printFiles(".txt", "testdir");
		
		//TextBuffer buff = new TextBuffer(30);
		
//		File[] files = Indexer.FileRetriever.retrieveFiles("test.txt", "testdir");
				
//		HashMap<String, String> pairs = new HashMap<String, String>(files.length * 2);
		
//		for(File f : files) {
//			TextBuffer buff = new TextBuffer(3);
			//System.out.println(f.getName());
	//		System.out.println("Final Result for " + f.getName()
	//			+ ":\n" + buff.getBuffer(f, "deer"));
			
//			pairs.put(f.getName(), buff.getBuffer(f,  "deer"));
//		}

//		System.out.println(pairs.size());
		
//		for(Entry<String, String> e : pairs.entrySet()) {
//			System.out.println("Key: " + e.getKey());
//			System.out.println("Value: " + e.getValue());
//			System.out.println();
//		}
		
		Indexer indexer = new Indexer();
//		HashMap<String, String> pairs = 
//				indexer.getSearchResults(".txt", "testdir", 4, "dog");	
//		indexer.printPairs(pairs);
		
/*		ResultStruct struct = indexer.getSearchResultStruct(".txt",
				"testdir", 5, "goat");
		
		struct.print();
*/
		Indexer.FileRetriever.printCurrentDirectory();
		File indexDir = new File("C:/Users/Eric Gunn/Desktop/Indexing Tool/Indexer/index");
		File dataDir = new File("C:/Users/Eric Gunn/Desktop/Indexing Tool/Indexer/testdir");
		
		int numIndex;
		try {
			String query = "poo";
			
			long timeStart1 = Clock.systemUTC().millis();
			
			numIndex = indexer.index(indexDir, dataDir, ".txt");
			
			long timeEnd1 = Clock.systemUTC().millis();
			
			System.out.println("Number of total files indexed:  " + numIndex);
			System.out.println();
			
			long timeStart2 = Clock.systemUTC().millis();
			String[] results = indexer.searchWildcardIndex(indexDir, query, 100);
			long timeEnd2 = Clock.systemUTC().millis();
			
			//indexer.printIndex(indexDir);
			
			System.out.println("Time elapsed for indexing "
					+ numIndex + " files: " + (timeEnd1 - timeStart1) + " ms");
			
			System.out.println("Time elapsed for searching: "
					+ (timeEnd2 - timeStart2) + " ms");
			
			ResultStruct struct = indexer.getResultStruct(results, 3, query);
			//struct.print();
			
			JsonGenerator gen = new JsonGenerator();
			JSONObject json = gen.generate(struct);
			
			System.out.println(json.toString(4));
			
			gen.writeToFile(json, "json.json");
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}
}
