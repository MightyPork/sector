package net.sector.models.wavefront.loader;


import java.util.Hashtable;

import net.sector.models.wavefront.parser.DefaultParser;
import net.sector.models.wavefront.parser.LineParser;



public abstract class LineParserFactory {

	protected Hashtable<String, LineParser> parsers = new Hashtable<String, LineParser>();
	protected RenderModel object = null;


	public LineParser getLineParser(String line) {
		if (line == null) return null;

		line = line.replaceAll("  ", " ");
		line = line.replaceAll("	", "");
		String[] lineWords = line.split(" ");

		if (lineWords.length < 1) return new DefaultParser();

		String lineType = lineWords[0];

		LineParser parser = parsers.get(lineType);
		if (parser == null) {
			parser = new DefaultParser();
		}

		parser.setWords(lineWords);
		return parser;
	}
}
