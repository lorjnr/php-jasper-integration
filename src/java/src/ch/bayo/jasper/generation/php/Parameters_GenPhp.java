package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Iterator;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.Parameter;
import ch.bayo.jasper.model.Parameters;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;

public class Parameters_GenPhp extends ClassGeneratorBase {
	
	Parameters parameters;
	
	String className;
	String memberName;
	
	public Parameters_GenPhp(Report report, Parameters parameters) {
		super(report, getLanguage());
		this.parameters = parameters;
		initSubsequent();
		
		className = getReport().getId()+"_Parameters";
		memberName = className+"_m";
	}
	
	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}

	protected void generateThisClass(BufferedWriter out) throws IOException {
		
		out.write("class "+className+" extends ReportObjectBase {\n");
		out.write("\n");
		out.write("  public function __construct($handle) {\n");
		out.write("    parent::__construct($handle);\n");
		out.write("  }\n");
		out.write("\n");
		Iterator it = parameters.getIterator();
		while (it.hasNext()) {
			Parameter p = (Parameter)it.next();
			out.write("  public function set"+p.getName()+"($value) {\n");
			out.write("    jasper_register_modification($this->getHandle(), \""+p.getModificationString("\".jasper_encode_param(\"$value\")")+");\n");
			out.write("  }\n");
			out.write("\n");
		}
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
		out.write("  public function getParameters() {\n");
		out.write("    return $this->"+memberName+";\n");
		out.write("  }\n");
		out.write("\n");
	}
	
}
