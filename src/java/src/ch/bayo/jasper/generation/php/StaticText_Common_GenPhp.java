package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;

import ch.bayo.jasper.model.generation.CommonGeneratorBase;
import ch.bayo.jasper.model.modification.Modification;

public class StaticText_Common_GenPhp extends CommonGeneratorBase {

	public StaticText_Common_GenPhp() {
		super(getLanguage());
	}

	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}
	
	protected void generate(BufferedWriter out) throws IOException {
		out.write("class StaticTextBase extends ReportObjectBase {\n");
		out.write("\n");
		out.write("  private $id;\n");
		out.write("\n");
		out.write("  public function __construct($id, $handle) {\n");
		out.write("    parent::__construct($handle);\n");
		out.write("    $this->id = $id;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function setText($value) {\n");
		out.write("    jasper_register_modification($this->getHandle(), \"StaticText"+Modification.C_PARAM_SEP+"\".$this->id.\""+Modification.C_VALUE_SEP+"\".jasper_encode_param(\"$value\"));\n");
		out.write("  }\n");
		out.write("\n");
		out.write("}\n");
		out.write("\n");
	}
	
}
