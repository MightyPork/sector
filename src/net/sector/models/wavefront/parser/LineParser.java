package net.sector.models.wavefront.parser;


import net.sector.models.wavefront.loader.RenderModel;


public abstract class LineParser {

	protected String[] words = null;

	public void setWords(String[] words) {
		this.words = words;
	}

	public abstract void parse();

	public abstract void incoporateResults(RenderModel wavefrontObject);

}
