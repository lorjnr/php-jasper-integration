package ch.bayo.jasper.model.generation;

import java.io.BufferedWriter;
import java.util.Iterator;
import java.util.Vector;
import ch.bayo.jasper.cockpit.Report;

import java.io.IOException;

public abstract class ClassGeneratorBase extends GeneratorBase {
	
	private Report report;
	private Vector<ClassGeneratorBase> subsequent;
	
	public ClassGeneratorBase(Report report, GenLanguages language) {
		super(language);
		this.report = report;
		this.subsequent = new Vector<ClassGeneratorBase>();
	}
		
	protected void collectSubsequent(Vector<Generatable> list) {
		//do nothing
	}
	
	protected Report getReport() {
		return report;
	}
	
	protected abstract void generateThisClass(BufferedWriter out) throws IOException;
	protected abstract void generateThisMember(BufferedWriter out) throws IOException;
	protected abstract void generateThisMemberInit(BufferedWriter out) throws IOException;
	protected abstract void generateThisGetter(BufferedWriter out) throws IOException;
	
	private final void generateClassSubsequent(BufferedWriter out) throws IOException {
		Iterator it = subsequent.iterator();
		while (it.hasNext()) {
			ClassGeneratorBase gb = (ClassGeneratorBase)it.next();
			gb.generateClass(out);
		}
	}
	
	protected final void initSubsequent() {
		Vector<Generatable> sq = new Vector<Generatable>();
		collectSubsequent(sq);
		Iterator it = sq.iterator();
		while (it.hasNext()) {
			Generatable g = (Generatable)it.next();
			ClassGeneratorBase gb = g.getGenerator(report, getObjLanguage());
			if (gb != null) {
				this.subsequent.add(gb);
			}
		}
	}
	
	public final void generateClass(BufferedWriter out) throws IOException {
		generateClassSubsequent(out);
		generateThisClass(out);
	}
	
	protected final void generateSsMembers(BufferedWriter out) throws IOException {
		Iterator it = subsequent.iterator();
		while (it.hasNext()) {
			ClassGeneratorBase gb = (ClassGeneratorBase)it.next();
			gb.generateThisMember(out);
		}
	}

	protected final void generateSsMemberInit(BufferedWriter out) throws IOException {
		Iterator it = subsequent.iterator();
		while (it.hasNext()) {
			ClassGeneratorBase gb = (ClassGeneratorBase)it.next();
			gb.generateThisMemberInit(out);
		}
	}

	protected final void generateSsGetter(BufferedWriter out) throws IOException {
		Iterator it = subsequent.iterator();
		while (it.hasNext()) {
			ClassGeneratorBase gb = (ClassGeneratorBase)it.next();
			gb.generateThisGetter(out);
		}
	}
	
}
