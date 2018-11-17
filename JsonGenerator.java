package indexer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import org.json.*;

/**
 * This class generates a JSON file given a result.
 * @author Eric Gunn
 *
 */
public class JsonGenerator {
	
	private static final int DEFAULT_INDENT = 4;
	
	/**
	 * Generates a JSONObject given a set of results in the form of a
	 * ResultStruct object
	 * @param res the ResultStruct object containing details of the search
	 * @return JSONObject containing the JSON
	 */
	public JSONObject generate(ResultStruct res) {
		
		JSONObject json = new JSONObject();
		
		json.put("extension", res.getExtension());
		json.put("query", res.getQuery());
		json.put("directory", res.getDirectory());
		json.put("number of results found", res.getNumResults());
		
		JSONArray list = new JSONArray();
		
		for(Entry<String, ResultStruct.InnerStruct> e : res.getMap().entrySet()) {
			ResultStruct.InnerStruct inner = e.getValue();
			JSONArray lineNums = new JSONArray();
			lineNums.put(0, new JSONObject().put("line start", inner.getLineStart()));
			lineNums.put(1, new JSONObject().put("line end", inner.getLineEnd()));
			lineNums.put(2, new JSONObject().put("query line", inner.getQueryLine()));
			lineNums.put(3, new JSONObject().put("number of lines", inner.getNumLines()));
			list.put(new JSONArray()
					.put(0, new JSONObject().put("file name", inner.getFileName()))
					.put(1, new JSONObject().put("buffer", inner.getBuffer()))
					.put(2, new JSONObject().put("lines", lineNums)));
		}
		
		json.put("results", list);
		
		return json;
	}
	
	/**
	 * Writes a JSONObject to a file, with default indent of size 4
	 * @param json JSONObject to be used
	 * @param outputPath file path to be written to
	 * @throws IOException if file path passed in already exists
	 */
	public void writeToFile(JSONObject json, String outputPath)
			throws IOException {
		File f = new File(outputPath);
		if(f.exists()) {
			throw new IOException("File already exists, failed to write");
		}
		String content = json.toString(DEFAULT_INDENT);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
	    writer.write(content);
	    writer.close();
	}
	
	/**
	 * Writes a JSONObject to a file
	 * @param json JSONObject to be used
	 * @param outputPath file path to be written to
	 * @param indent size of indent for JSON styling
	 * @throws IOException if file path passed in already exists
	 */
	public void writeToFile(JSONObject json, String outputPath, int indent)
			throws IOException {
		File f = new File(outputPath);
		if(f.exists()) {
			throw new IOException("File already exists, failed to write");
		}
		String content = json.toString(indent);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
	    writer.write(content);
	    writer.close();
	}
	
}
