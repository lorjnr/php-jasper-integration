package ch.bayo.jasper.model.generation;

import java.io.BufferedWriter;
import java.io.IOException;

public abstract class CommonGeneratorBase extends GeneratorBase {
	
	public CommonGeneratorBase(GenLanguages language) {
		super(language);
	}
	
	protected abstract void generate(BufferedWriter out) throws IOException;
	
	public final void generateClass(BufferedWriter out) throws IOException {
		generate(out);
	}

}
