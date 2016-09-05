package parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import constants.SQLPunctuation;

public class Tokenizer {

	private static final List<String> SEPARATORS_KEEP = Arrays.asList("(", ")", ",", ".", "'");
	private static final List<String> SEPARATORS_IGNORE = Arrays.asList(" ", "\n", "\t", "-");
	private static List<String> SEPARATORS;
	static {
		SEPARATORS = new ArrayList<>();
		SEPARATORS.addAll(SEPARATORS_KEEP);
		SEPARATORS.addAll(SEPARATORS_IGNORE);
	}

	private String filePath;

	Iterator<String> lineIterator;
	private String[] currentLine;
	private int currentLineCounter;
	int lineIndicator = 0;

	public Tokenizer(String path) throws IOException {
		this.filePath = path;
		List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.ISO_8859_1);
		lineIterator = lines.iterator();
		currentLineCounter = 0;
		if (lineIterator.hasNext()) {
			// currentLine = lineIterator.next().split(" ");
			splitString(getNextLine());
		}
	}

	public String getNextToken() {
		String result = null;
		if (currentLineCounter < currentLine.length) {
			result = currentLine[currentLineCounter++];
		} else {
			if (lineIterator.hasNext()) {
				currentLineCounter = 0;
				String newLine = getNextLine();
				splitString(newLine);
				if (currentLine.length < 1) {
					result = getNextToken();
				} else {
					result = currentLine[currentLineCounter++];
				}
			}
		}
		if (result != null) {
			result = result.trim();
		}
		return result;
	}

	public boolean hasNext() {
		return currentLineCounter < currentLine.length || lineIterator.hasNext();
	}

	private String getNextLine() {
		++lineIndicator;
		String line = null;
		if (lineIterator.hasNext()) {
			line = lineIterator.next();
			currentLineCounter = 0;
			if (line.startsWith(SQLPunctuation.LINE_COMMENT)) {
				line = getNextLine();
			}
		}
		return line;
	}

	public boolean skipLine() {
		boolean result = false;
		if (lineIterator.hasNext()) {
			currentLineCounter = 0;
			splitString(getNextLine());
			result = true;
		}
		return result;
	}

	public int getCurrentLine() {
		return lineIndicator;
	}

	private void splitString(String str) {
		// currentLine = str.split(" ");
		List<String> splitString = new ArrayList<>();
		int prev = 0;
		if (str != null) {
			for (int i = 0; i < str.length(); ++i) {
				char character = str.charAt(i);
				if (character == '-' && i > 0 && str.charAt(i-1) == '-'){
					break;
				}
				if (SEPARATORS.contains(Character.toString(character))) {
					String subString = str.substring(prev, i).trim();
					if (!"".equals(subString)) {
						splitString.add(subString);
					}
					if (i + 1 < str.length()) {
						if (SEPARATORS_KEEP.contains(Character.toString(character))) {
							splitString.add(str.substring(i, i + 1).trim());
							++i;
						}
					}
					prev = i;
				}
			}
			if (str.length() > prev) {
				splitString.add(str.substring(prev));
			}
		}
		currentLine = splitString.toArray(new String[splitString.size()]);
	}
}
