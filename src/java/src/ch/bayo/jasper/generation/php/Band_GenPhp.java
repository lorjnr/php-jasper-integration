package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.elements.ElementBase;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;
import ch.bayo.jasper.model.Band;

public class Band_GenPhp extends ClassGeneratorBase {
	
	Band band;
	
	String bandName;
	String className;
	String memberName;
	
	public Band_GenPhp(Report report, Band band, String bandName) {
		super(report, getLanguage());
		this.band = band;
		this.bandName = bandName;
		initSubsequent();
		
		className = getReport().getId()+"_"+bandName;
		memberName = className+"_m";
	}
	
	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}

	protected void collectSubsequent(Vector<Generatable> list) {
		Iterator it = band.getIterator();
		while (it.hasNext()) {
			ElementBase el = (ElementBase)it.next();
			list.add(el);
		}
	}
	
	protected void generateThisClass(BufferedWriter out) throws IOException {
		
		out.write("class "+className+" extends ReportObjectBase {\n");
		out.write("\n");
		generateSsMembers(out);
		out.write("\n");		
		out.write("  public function __construct($handle) {\n");
		out.write("    parent::__construct($handle);\n");
		generateSsMemberInit(out);
		out.write("  }\n");
		out.write("\n");		
		generateSsGetter(out);
		out.write("}\n");
		out.write("\n");
		
	}
	
	protected void generateThisMember(BufferedWriter out) throws IOException
	{
		out.write("  private $"+memberName+";\n");
	}
	
	protected void generateThisMemberInit(BufferedWriter out) throws IOException
	{
		out.write("    $this->"+memberName+" = new "+className+"($this->getHandle());\n");
	}
	
	protected void generateThisGetter(BufferedWriter out) throws IOException
	{
		out.write("  public function get"+bandName+"() {\n");
		out.write("    return $this->"+memberName+";\n");
		out.write("  }\n");
		out.write("\n");
	}
	
}
