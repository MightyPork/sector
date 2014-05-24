package net.sector.level;


import net.sector.network.levels.NetLevelContainer;

import org.jdom2.Element;


public class LevelContainer {
	public ELevel type;
	public String filename;
	public Element rootNode;
	public String lid = "";

	public LevelContainer(NetLevelContainer nlc) {
		this.lid = nlc.lid;
		this.rootNode = nlc.levelRootNode;
		this.filename = this.lid;
		this.type = ELevel.NET;
	}

	public LevelContainer(String filename, Element rootNode, ELevel type) {
		this.filename = filename;
		this.type = type;
		this.rootNode = rootNode;
	}

	public LevelBundle toBundle() {
		LevelBundle lb = new LevelBundle(filename, rootNode, type);
		if (type == ELevel.NET) lb.lid = lid;
		return lb;
	}
}
