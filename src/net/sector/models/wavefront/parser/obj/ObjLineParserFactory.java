package net.sector.models.wavefront.parser.obj;


import net.sector.models.wavefront.loader.LineParserFactory;
import net.sector.models.wavefront.loader.NormalParser;
import net.sector.models.wavefront.loader.RenderModel;
import net.sector.models.wavefront.parser.CommentParser;
import net.sector.models.wavefront.parser.mtl.MaterialFileParser;



public class ObjLineParserFactory extends LineParserFactory {



	public ObjLineParserFactory(RenderModel object) {
		this.object = object;
		parsers.put("v", new VertexParser());
		parsers.put("vn", new NormalParser());
		parsers.put("vp", new FreeFormParser());
		parsers.put("vt", new TextureCooParser());
		parsers.put("f", new FaceParser(object));
		parsers.put("#", new CommentParser());
		parsers.put("mtllib", new MaterialFileParser(object));
		parsers.put("usemtl", new MaterialParser());
	}


}
