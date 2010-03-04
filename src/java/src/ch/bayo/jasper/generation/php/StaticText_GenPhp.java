package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.elements.StaticText;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;

public class StaticText_GenPhp extends ClassGeneratorBase {

	StaticText elemStaticText;
	
	String key;
	
	String className;
	String memberName;
	
	public StaticText_GenPhp(Report report, StaticText elemStaticText) {
		super(report, getLanguage());
		this.elemStaticText = elemStaticText;
		initSubsequent();
		
		key = elemStaticText.getElement().getKey();
		className = getReport().getId()+"_"+key;
		memberName = className+"_m";
	}

	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}

	protected void generateThisClass(BufferedWriter out) throws IOException {
		
		out.write("class "+className+" extends StaticTextBase {\n");
		out.write("\n");
		out.write("  public function __construct($handle) {\n");
		out.write("    parent::__construct(\""+key+"\", $handle);\n");
		out.write("  }\n");
		out.write("\n");
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
		out.write("  public function get"+key+"() {\n");
		out.write("    return $this->"+memberName+";\n");
		out.write("  }\n");
		out.write("\n");
	}
	
}
