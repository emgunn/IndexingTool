package indexer;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

/**
 * A class that contains functions that can build an index, search an existing
 * index, and create or display results of a search.
 * @author Eric Gunn
 *
 */
public class Indexer {
	
	private static final int DOUBLE = 2;
	
	public Indexer() {}
	
	/**
	 * Main indexing function that creates an index in a given directory by
	 * indexing files from a different given directory. The function will
	 * only index files of the given suffix (works best with .txt)
	 * @param indexDir the directory of the index to be created
	 * @param dataDir the directory of the data files to be indexed
	 * @param suffix the suffix or extension of the files to be indexed
	 * @return int the number of files indexed
	 * @throws Exception if directory file is invalid
	 */
	public int index(File indexDir, File dataDir, String suffix) throws Exception {
		StandardAnalyzer standard = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(standard);
		Directory directory = FSDirectory.open(indexDir.toPath());
		
		//create IndexWriter object that writes to given directory
        IndexWriter indexWriter = new IndexWriter(directory, config);
        indexWriter.commit();
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        
        //delete all existing elements in index directory
        indexWriter.deleteAll();
        
        //index the files in our data directory
        indexDirectory(indexWriter, indexReader, indexSearcher, dataDir, suffix); 
        
        indexWriter.commit();
        
        int numIndexed = indexWriter.numDocs();
        
        indexWriter.close();        
        return numIndexed;        
    }

	/**
	 * Helper method for index() that handles going inside a directory
	 * @param indexWriter writer object from the index() method
	 * @param indexReader reader object from the index() method
	 * @param indexSearcher searcher object from the index() method
	 * @param dataDir the directory of the data files to be indexed
	 * @param suffix the suffix or extension of the files to be indexed
	 * @throws IOException if data directory file is invalid
	 */
	private void indexDirectory(IndexWriter indexWriter,
			IndexReader indexReader, IndexSearcher indexSearcher,
			File dataDir, String suffix)
					throws IOException {
		//process all files in the directory of suffix
		File[] files = dataDir.listFiles();
        for (File f : files) {
            indexFileWithIndexWriter(indexWriter, indexReader,
            		indexSearcher, f, suffix);
        }
    }
	
	/**
	 * Method that performs the actual indexing for a single file
	 * @param indexWriter writer object from the index() method
	 * @param indexReader reader object from the index() method
	 * @param indexSearcher searcher object from the index() method
	 * @param f the file to be indexed
	 * @param suffix the suffix or extension of the files to be indexed
	 * @throws IOException if passed in file is invalid
	 */
	private void indexFileWithIndexWriter(IndexWriter indexWriter,
			IndexReader indexReader, IndexSearcher indexSearcher,
			File f, String suffix)
					throws IOException {
		//make sure file is acceptable
		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
			return;
	    }
		//suffix null check and suffix at end of file check
	    if (suffix != null && !f.getName().endsWith(suffix)) {
	        return;
	    }      
	    System.out.println("Indexing file:... " + f.getCanonicalPath());
	    Document doc = getDocument(f);
	    
	    //add document to our index directory by writing it
	    indexWriter.addDocument(doc);
	}
	
	/**
	 * Searches through an index to find a list of files that contain the
	 * exact given query string
	 * @param indexDir the directory of the index
	 * @param queryStr the string to be searched for
	 * @param maxHits the max number of files to return
	 * @return String[] a list of canonical file paths of the files found
	 * @throws Exception if index directory file is invalid
	 */
	public String[] searchIndex(File indexDir, String queryStr, int maxHits)
			throws Exception {        
		Directory directory = FSDirectory.open(indexDir.toPath());
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		queryStr = queryStr.toLowerCase();
		
		//search the contents for our query
		Term t = new Term("contents", queryStr);
		TermQuery query = new TermQuery(t);        
		TopDocs topDocs = searcher.search(query, maxHits);       
		ScoreDoc[] hits = topDocs.scoreDocs;
		
		String[] filenames = new String[hits.length];
		
		//print which files are found
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			filenames[i] = d.get("filename");
			System.out.println(d.get("filename"));
		}
		//print the total number of files found
		System.out.println("Found " + hits.length);
		return filenames;
	}
	
	/**
	 * Searches through an index to find a list of files that contain words
	 * that contain the given query string/substring
	 * @param indexDir the directory of the index
	 * @param queryStr the string to be searched for
	 * @param maxHits the max number of files to return
	 * @return String[] a list of canonical file paths of the files found
	 * @throws Exception if index directory file is invalid
	 */
	public String[] searchWildcardIndex(File indexDir, String queryStr,
			int maxHits) throws Exception {        
		Directory directory = FSDirectory.open(indexDir.toPath());
		IndexReader reader = DirectoryReader.open(directory);
		IndexSearcher searcher = new IndexSearcher(reader);

		queryStr = queryStr.toLowerCase();
		
		//search the contents for our query + wildcard (*)
		//in essence, this acts as a substring search
		Term t = new Term("contents", "*" + queryStr + "*");
		WildcardQuery query = new WildcardQuery(t);        
		TopDocs topDocs = searcher.search(query, maxHits);       
		ScoreDoc[] hits = topDocs.scoreDocs;
		
		String[] filenames = new String[hits.length];
		
		//print which files are found
		System.out.println("Listing files...");
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document d = searcher.doc(docId);
			filenames[i] = d.get("filename");
			System.out.println(d.get("filename"));
		}
		//print the total number of files found
		System.out.println("Found \"" + queryStr + "\" in " + hits.length + " files");
		return filenames;
	}
	
	/**
	 * Prints to console the documents and their contents stored in the index
	 * @param indexDir directory of the index
	 * @throws Exception if index directory file is invalid
	 */
	public void printIndex(File indexDir) throws Exception {
		Directory directory = FSDirectory.open(indexDir.toPath());
		IndexReader reader = DirectoryReader.open(directory);
		System.out.println("Index contains " + reader.numDocs()
				+ " documents");
		for(int i = 0; i < reader.numDocs(); i++) {
			System.out.println("File: " + reader.document(i).get("filename"));
			System.out.println(reader.document(i).get("contents"));
		}
	}	
	
	/**
	 * Creates a virtual document object given a file, storing its file name
	 * and contents
	 * @param f file passed in to be read
	 * @return Document a virtual document containing fields found in the
	 * Lucene API
	 * @throws IOException if passed in file is invalid
	 */
	private Document getDocument(File f) throws IOException {
		Document document = new Document();
		
		//index file contents
		FileReader read = new FileReader(f);
	    BufferedReader br = new BufferedReader(read);
	    StringBuilder sb = new StringBuilder();
	    String line = br.readLine();
	    while (line != null) {
	    	sb.append(line).append("\n");
	    	line = br.readLine();
	    }
	    String s = sb.toString();
		
		TextField contentField = new TextField("contents",
				s, Field.Store.YES);
   
		//index file name
		StringField fileNameField = new StringField("filename",
				f.getCanonicalPath(), Field.Store.YES);
   
	   	document.add(contentField);
	   	document.add(fileNameField);
	   	
	   	br.close();
		
		return document;
	}
	
	/**
	 * Creates a HashMap given a list of canonical file paths, a size
	 * for the buffer to be returned, and the string to be searched for
	 * @param filenames canonical file paths of the files to create the
	 * ResultStruct object with
	 * @param halfBufferSize number of lines above and below the line
	 * containing the query string that will be returned in the results.
	 * If this is set to 0, only the line of the query string will be
	 * returned. An exception will be thrown if less than 0
	 * @param queryString the text to be searched for
	 * @return HashMap containing simple details of a search
	 */
	public HashMap<String, String> getResults(String[] filenames,
			int halfBufferSize, String queryString) {
		if(halfBufferSize < 0) {
			throw(new IllegalArgumentException("Argument must be greater or"
					+ " equal to 0. Passing in 0 will return the single line"
					+ " of the queried string."));
		}
		File[] files = new File[filenames.length];
		for(int i = 0; i < files.length; i++) {
			//uses canonical path, so directory is not required
			files[i] = new File(filenames[i]);
		}
		
		HashMap<String, String> results =
				new HashMap<String, String>(files.length * DOUBLE);
		
		for(File f : files) {
			TextBuffer buff = new TextBuffer(halfBufferSize);
			results.put(f.getName(), buff.getBuffer(f,  queryString));
		}
		
		return results;
	}
	
	/**
	 * Creates a ResultStruct object given a list of canonical file paths,
	 * a size for the buffer to be returned, and the string to be searched for
	 * @param filenames canonical file paths of the files to create the
	 * ResultStruct object with
	 * @param halfBufferSize number of lines above and below the line
	 * containing the query string that will be returned in the results.
	 * If this is set to 0, only the line of the query string will be
	 * returned. An exception will be thrown if less than 0
	 * @param queryString the text to be searched for
	 * @return ResultStruct an object containing details of a search
	 */
	public ResultStruct getResultStruct(String[] filenames,
			int halfBufferSize, String queryString) {
		if(halfBufferSize < 0) {
			throw(new IllegalArgumentException("Argument must be greater or"
					+ " equal to 0. Passing in 0 will return the single line"
					+ " of the queried string."));
		}
		File[] files = new File[filenames.length];
		for(int i = 0; i < files.length; i++) {
			//uses canonical path, so directory is not required
			files[i] = new File(filenames[i]);
		}
		String[] buffers = new String[files.length];
		int[] startLines = new int[files.length];
		int[] endLines = new int[files.length];
		int[] queryLines = new int[files.length];
		
		int count = 0;
		
		String parentPath = "";
		
		for(File f : files) {
			parentPath = f.getParent();
			TextBuffer buff = new TextBuffer(halfBufferSize);
			buffers[count] = buff.getBuffer(f, queryString);
			startLines[count] = buff.getStartLine();
			endLines[count] = buff.getEndLine();
			queryLines[count] = buff.getQueryLine();
			count++;
		}
		
		//assume .txt extension
		ResultStruct results =
				new ResultStruct(filenames, buffers, startLines, endLines,
						queryLines, ".txt", parentPath, queryString);
		
		return results;
	}
	
	/**
	 * Prints each key-value pair in a given hash map of the form
	 * that we use in this class
	 * @param pairs the hash map containing file name to resulting string
	 * pairs
	 */
	public void printPairs(HashMap<String, String> pairs) {
		for(Entry<String, String> e : pairs.entrySet()) {
			System.out.println("Key: " + e.getKey());
			System.out.println("Value: " + e.getValue());
			System.out.println();
		}
	}
	
	/**
	 * A static class that has helper methods for file handling and testing.
	 * @author Eric Gunn
	 *
	 */
	public static class FileRetriever {
		
		/**
		 * Retrieves a list of File objects given a certain file
		 * extension by default in the current working directory
		 * @param extension the file extension for files to be grabbed
		 * @return File[] list of File objects that match the extension
		 */
		public static File[] retrieveFiles(String extension) {
			File dir = new File(".");
			return dir.listFiles((d, name) -> name.endsWith(extension));
		}
		
		/**
		 * Retrieves a list of File objects given a certain file
		 * extension and directory to grab from
		 * @param extension the file extension for files to be grabbed
		 * @param directory the directory to grab files from
		 * @return File[] list of File objects that match the extension from
		 * the given directory
		 */
		public static File[] retrieveFiles(String extension, String directory) {
			File dir = new File(directory);
			return dir.listFiles((d, name) -> name.endsWith(extension));
		}
		
		/**
		 * Prints the current working directory
		 */
		public static void printCurrentDirectory() {
			System.out.println("Working Directory = " 
					+ System.getProperty("user.dir"));
			return;
		}
		
		/**
		 * Prints file names of given extension by default in the
		 * current working directory
		 * @param extension the file extension to search for
		 */
		public static void printFiles(String extension) {
			File dir = new File(".");
			System.out.println("Listing files with extension \"" + extension
					+ "\" in current working directory:");
			for(File f : dir.listFiles((d, name) -> name.endsWith(extension))) {
				System.out.println(f.getName());
			}
		}
			
		/**
		 * Prints file names of given extension in the given directory 
		 * @param extension the file extension to search for
		 * @param directory the folder or directory to look in
		 */
		public static void printFiles(String extension, String directory) {
			File dir = new File(directory);
			System.out.println("Listing files with extension \"" + extension
					+ "\" in directory \"" + directory + "\":");
			for(File f : dir.listFiles((d, name) -> name.endsWith(extension))) {
				System.out.println(f.getName());
			}
		}
	}
}
