package net.sector.models.wavefront.parser.mtl;


import net.sector.models.wavefront.loader.LineParserFactory;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.CommentParser;



public class MtlLineParserFactory extends LineParserFactory {



	public MtlLineParserFactory(RenderModel object) {
		this.object = object;
		parsers.put("newmtl", new MaterialParser());
		parsers.put("Kd", new KdParser());
		parsers.put("map_Kd", new KdMapParser(object));
		parsers.put("#", new CommentParser());
	}



}
