package net.sector.level.loading;


import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sector.Constants;
import net.sector.level.dataobj.AiObjParser;
import net.sector.util.Log;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


/**
 * XML loading utility
 * 
 * @author Ondřej Hruška (MightyPork)
 */
public class XmlUtil {
	private static final boolean DEBUG = Constants.LOG_XML_LOADING;

	/**
	 * Get XML document from input stream
	 * 
	 * @param in input stream
	 * @return document
	 */
	public static Document getDocument(InputStream in) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// Fake code simulating the copy
		// You can generally do better with nio if you need...
		// And please, unlike me, do something about the Exceptions :D
		byte[] buffer = new byte[1024];
		int len;
		try {

			while ((len = in.read(buffer)) > -1) {
				baos.write(buffer, 0, len);
			}
			baos.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Open new InputStreams using the recorded bytes
		// Can be repeated as many times as you wish
		InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
		InputStream is2 = new ByteArrayInputStream(baos.toByteArray());


		try {
			SAXBuilder builder = new SAXBuilder();
			Document d = builder.build(is1);
			return d;
		} catch (IOException io) {
			Log.e("XmlUtil: " + io.getMessage());
		} catch (JDOMException jdomex) {
			Log.e("XmlUtil: " + jdomex.getMessage());
		}

		try {
			String msg = "";

			msg += "\n\nInvalid XML file:\n";
			InputStreamReader sr = new InputStreamReader(is2);
			BufferedReader br = new BufferedReader(sr);
			String line;
			while ((line = br.readLine()) != null) {
				msg += "\t" + line + "\n";
			}
			msg += "End of file.\n\n";

			Log.e(msg);
		} catch (IOException e) {
			Log.e("IO Error:", e);
		}

		return null;
	}

	/**
	 * Get root element of an input stream XML
	 * 
	 * @param in the input stream
	 * @return root element
	 */
	public static Element getRootElement(InputStream in) {
		return getDocument(in).getRootElement();
	}

	/**
	 * Load all files from directory, described in manifest. There can be
	 * multiple file lists in manifest, each with different "fileListTagName".
	 * 
	 * @param dirLoader directory loader
	 * @param manifestFileListTag tag in manifest, containing elements
	 *            &lt;file&gt;filename.xml&lt;/file&gt; which describe loading
	 *            order of the directory files.
	 * @return map of Filename → Root Element
	 */
	public static Map<String, Element> loadFromDirectoryWithManifest(DirectoryLoader dirLoader, String manifestFileListTag) {
		Map<String, Element> roots = new LinkedHashMap<String, Element>();

		if (DEBUG) Log.f2("XmlUtil: Request to load documents from directory " + dirLoader + ".");

		if (!dirLoader.fileExists("manifest.xml")) {
			Log.w("XmlUtil: Folder accessed by " + dirLoader + " has no 'manifest.xml'.");
			return null;
		}

		InputStream is = dirLoader.openFile("manifest.xml");
		if (DEBUG) Log.f3("Loading manifest file.");
		Document doc = getDocument(is);
		Element root = doc.getRootElement();
		Element fileList = root.getChild(manifestFileListTag);
		List<Element> files = fileList.getChildren("file");

		for (Element elem : files) {
			String fname = elem.getText().trim();

			if (DEBUG) Log.f3("Loading file " + fname);
			InputStream is2 = dirLoader.openFile(fname);
			Document doc2 = getDocument(is2);
			if (doc2 != null) roots.put(fname, doc2.getRootElement());
		}

		return roots;
	}

	/**
	 * Load arguments from wrapper tag
	 * 
	 * @param wrapper wrapper tag
	 * @return map of the arguments
	 */
	public static Map<String, Object> loadArgs(Element wrapper) {
		Map<String, Object> args = new HashMap<String, Object>();

		for (Element argNode : wrapper.getChildren()) {
			String argName = argNode.getName().trim().toLowerCase();

			List<Attribute> at2 = argNode.getAttributes();

			if (at2.size() == 0) {
				Log.w("Parsing XML file: Argument '" + argName + "' has no value in '" + wrapper.getName() + ":" + wrapper.getAttributes() + "'.");
				continue;
			}

			String argType = at2.get(0).getName().trim().toLowerCase();
			String argValueSrc = at2.get(0).getValue().trim();

			Object argValue = null;

			if (argType.equals("str") || argType.equals("string") || argType.equals("s") || argType.equals("name")) {
				argValue = AiObjParser.getString(argValueSrc);

			} else if (argType.equals("num") || argType.equals("float") || argType.equals("double")) {
				argValue = AiObjParser.getDouble(argValueSrc);

			} else if (argType.equals("int")) {
				argValue = AiObjParser.getInteger(argValueSrc);

			} else if (argType.equals("bool") || argType.equals("boolean") || argType.equals("flag") || argType.equals("on")
					|| argType.equals("state")) {
				argValue = AiObjParser.getBoolean(argValueSrc);

			} else if (argType.equals("coord") || argType.equals("vec") || argType.equals("dir") || argType.equals("coordinate")
					|| argType.equals("vector") || argType.equals("direction")) {
				argValue = AiObjParser.getCoord(argValueSrc);

			} else if (argType.equals("range") || argType.equals("rand") || argType.equals("scale")) {
				argValue = AiObjParser.getRange(argValueSrc);
			}

			// argName, argValue

			// add new arg to args map
			args.put(argName, argValue);
		}

		return args;
	}
}
