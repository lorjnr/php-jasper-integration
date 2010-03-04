package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Vector;

import ch.bayo.jasper.cockpit.Report;
import ch.bayo.jasper.model.ReportModel;
import ch.bayo.jasper.model.generation.ClassGeneratorBase;
import ch.bayo.jasper.model.generation.Generatable;

public class ReportModel_GenPhp extends ClassGeneratorBase {
	
	private ReportModel model;
	
	private String className;
	private String memberName;
	
	public ReportModel_GenPhp(Report report, ReportModel model) {
		super(report, getLanguage());
		this.model = model;
		initSubsequent();

		this.className = report.getId();
		this.memberName = className+"_m";
	}
	
	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}
	
	protected void collectSubsequent(Vector<Generatable> list) {
		if (model.getParameters().hasItems()) list.add(model.getParameters());
		if (model.getTitleBand().hasItems()) list.add(model.getTitleBand());
		if (model.getDetailBand().hasItems()) list.add(model.getDetailBand());
	}
	
	protected void generateThisClass(BufferedWriter out) throws IOException {
		out.write("class "+className+" extends ReportBase {\n");
		out.write("\n");
		generateSsMembers(out);
		out.write("\n");
		out.write("  public function __construct() {\n");
		out.write("    parent::__construct(\""+getReport().getId()+"\");\n");
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
		out.write("    $this->"+memberName+" = new "+className+"();\n");
	}
	
	protected void generateThisGetter(BufferedWriter out) throws IOException
	{
		out.write("  public function get"+className+"() {\n");
		out.write("    return $this->"+memberName+";\n");
		out.write("  }\n");
		out.write("\n");
	}
	
}
