package net.sector.models.wavefront.loader;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;

import net.sector.Constants;
import net.sector.models.wavefront.parser.LineParser;
import net.sector.models.wavefront.parser.obj.ObjLineParserFactory;
import net.sector.textures.TextureManager;
import net.sector.util.Log;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;



/**
 * OBJ 3D object loaded and pre-rendered.
 * 
 * @author Fabien Sanglard
 */
public class RenderModel {

	private static final boolean DEBUG = Constants.LOG_MODELS;

	public String replTexture = null;

	private ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	private ArrayList<Vertex> normals = new ArrayList<Vertex>();
	private ArrayList<TextureCoordinate> textures = new ArrayList<TextureCoordinate>();
	private ArrayList<Face> faces = new ArrayList<Face>();

	private ObjLineParserFactory parserFactory;
	private Hashtable<String, Material> materials = new Hashtable<String, Material>();

	// This variable is used for both parsing and rendition
	private Material currentMaterial;

	private String contextfolder = "";

	public double radius = 0;

	/**
	 * Load object with alternate texture.
	 * 
	 * @param fileName
	 * @param textureName
	 */
	public RenderModel(String fileName, String textureName) {
		this.replTexture = textureName;

		int lastSlashIndex = fileName.lastIndexOf('/');
		if (lastSlashIndex != -1) this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

		lastSlashIndex = fileName.lastIndexOf('\\');
		if (lastSlashIndex != -1) this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

		parse(fileName);

		calculateRadius();

		if (DEBUG) Log.f3("Model " + fileName + " loaded.");
	}

	/**
	 * Load object with alternate texture.
	 * 
	 * @param fileName
	 * @param textureName
	 */
	public RenderModel(RenderModel other, String textureName) {
		this.vertices = other.vertices;
		this.normals = other.normals;
		this.textures = other.textures;
		this.contextfolder = other.contextfolder;
		this.radius = other.radius;

		String pathToTextureBinary = getContextfolder() + textureName;
		Texture texture = TextureManager.load(pathToTextureBinary);
		for (Entry<String, Material> e : other.materials.entrySet()) {
			materials.put(e.getKey(), new Material(e.getValue()).setTexture(texture));
		}
		for (Face f : other.faces) {
			Face f2;
			faces.add(f2 = f.copy());
			f2.setMaterial(materials.get(f2.getMaterial().getName()));
		}

	}

	public RenderModel(String fileName) {
		int lastSlashIndex = fileName.lastIndexOf('/');
		if (lastSlashIndex != -1) this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

		lastSlashIndex = fileName.lastIndexOf('\\');
		if (lastSlashIndex != -1) this.contextfolder = fileName.substring(0, lastSlashIndex + 1);

		parse(fileName);

		calculateRadius();

		if (DEBUG) Log.f3("Model " + fileName + " loaded.");
	}



	private void calculateRadius() {
		double currentNorm = 0;
		for (Vertex vertex : vertices) {
			currentNorm = vertex.norm();
			if (currentNorm > radius) radius = currentNorm;
		}

	}


	public String getContextfolder() {
		return contextfolder;
	}


	public void parse(String fileName) {
		parserFactory = new ObjLineParserFactory(this);


		InputStream fileInput = ResourceLoader.getResourceAsStream(fileName);
		if (fileInput == null) {
			// Could not find the file in the jar.
			try {
				File file = new File(fileName);
				if (file.exists()) fileInput = new FileInputStream(file);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}


		BufferedReader in = null;

		try {
			in = new BufferedReader(new InputStreamReader(fileInput));

			String currentLine = null;
			while ((currentLine = in.readLine()) != null)
				parseLine(currentLine);

			in.close();

		} catch (Exception e) {
			Log.e("Error reading file " + fileName, e);
			throw new RuntimeException(e);
		}


//
//		Log.finest("Loaded OBJ from file " + fileName);
//		Log.finest(getVertices().size() + " vertices.");
//		Log.finest(getNormals().size() + " normals.");
//		Log.finest(getTextures().size() + " textures coordinates.");
//		Log.finest(getFaces().size() + " faces.");
	}

	private int lineCounter = 0;

	private void parseLine(String currentLine) {
		if ("".equals(currentLine)) return;

		LineParser parser = parserFactory.getLineParser(currentLine);
		try {
			parser.parse();
			parser.incoporateResults(this);
		} catch (Throwable t) {
			Log.e("ERROR at line " + lineCounter + " : " + currentLine, t);
			System.exit(1);
		}
		lineCounter++;
	}

	public int displayListId = 0;

	public void render() {


		if (displayListId != 0) {
			GL11.glCallList(displayListId);
			return;
		}

		displayListId = GL11.glGenLists(1);

		GL11.glNewList(displayListId, GL11.GL_COMPILE);

		Face face = null;
		Material material = new Material("__non_existant");

		for (int i = 0; i < getFaces().size(); i++) {
			face = getFaces().get(i);

			if (!material.equals(face.getMaterial())) {
				// Set texture and setColor
				material = face.getMaterial();
				if (material == null) {
					Log.w("No material in model!");
				} else if (material.getTexture() != null) {
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, material.getTexture().getTextureID());
				} else
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

				if (material != null && material.getKd() != null)
					GL11.glColor3f(material.getKd().getX(), material.getKd().getY(), material.getKd().getZ());
			}

			if (face.getType() == Face.GL_TRIANGLES) {
				GL11.glBegin(GL11.GL_TRIANGLES);
			} else if (face.getType() == Face.GL_QUADS) {
				GL11.glBegin(GL11.GL_QUADS);
			} else {
				GL11.glBegin(GL11.GL_POLYGON);
			}

			Vertex vertex = null;
			Vertex normal = null;
			TextureCoordinate textureCoo = null;
			for (int j = 0; j < face.getVertices().length; j++) {
				vertex = face.getVertices()[j];
				if (j < face.getNormals().length && face.getNormals()[j] != null) {
					normal = face.getNormals()[j];
					GL11.glNormal3f(normal.getX(), normal.getY(), normal.getZ());
				}

				if (j < face.getTextures().length && face.getTextures()[j] != null) {
					textureCoo = face.getTextures()[j];
					GL11.glTexCoord2f(textureCoo.getU(), textureCoo.getV());
					//GL11.glTexCoord2f(textureCoo.getV(),textureCoo.getU());
				}

				GL11.glVertex3f(vertex.getX(), vertex.getY(), vertex.getZ());
			}
			GL11.glEnd();
		}
		GL11.glEndList();
	}

	public void setMaterials(Hashtable<String, Material> materials) {
		this.materials = materials;
	}

	public void setTextures(ArrayList<TextureCoordinate> textures) {
		this.textures = textures;
	}

	public ArrayList<TextureCoordinate> getTextures() {
		return textures;
	}

	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	public void setFaces(ArrayList<Face> faces) {
		this.faces = faces;
	}

	public ArrayList<Face> getFaces() {
		return faces;
	}

	public void setNormals(ArrayList<Vertex> normals) {
		this.normals = normals;
	}

	public ArrayList<Vertex> getNormals() {
		return normals;
	}

	public Hashtable<String, Material> getMaterials() {

		return this.materials;
	}

	public Material getCurrentMaterial() {
		return currentMaterial;
	}

	public void setCurrentMaterial(Material currentMaterial) {
		this.currentMaterial = currentMaterial;
	}



}
