package net.sector.models.wavefront.parser.mtl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import net.sector.models.wavefront.loader.Material;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.LineParser;

import org.newdawn.slick.util.ResourceLoader;



public class MaterialFileParser extends LineParser {

	Hashtable<String, Material> materials = new Hashtable<String, Material>();
	private RenderModel object;
	private MtlLineParserFactory parserFactory = null;

	public MaterialFileParser(RenderModel object) {
		this.object = object;
		this.parserFactory = new MtlLineParserFactory(object);
	}

	@Override
	public void incoporateResults(RenderModel wavefrontObject) {
		// Material are directly added by the parser, no need to do anything here...
	}

	@Override
	public void parse() {
		String filename = words[1];

		String pathToMTL = object.getContextfolder() + filename;

		InputStream fileInput = ResourceLoader.getResourceAsStream(pathToMTL);
		if (fileInput == null) {
			// Could not find the file in the jar.
			try {
				File file = new File(pathToMTL);
				if (file.exists()) fileInput = new FileInputStream(file);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(fileInput));

			String currentLine = null;
			while ((currentLine = in.readLine()) != null) {

				LineParser parser = parserFactory.getLineParser(currentLine);
				parser.parse();
				parser.incoporateResults(object);
			}

			if (in != null) in.close();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error parsing " + pathToMTL);
		}

	}


}