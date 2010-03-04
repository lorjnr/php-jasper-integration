package ch.bayo.jasper.generation.php;

import java.io.BufferedWriter;
import java.io.IOException;

import ch.bayo.jasper.model.generation.CommonGeneratorBase;

public class ReportModel_Common_GenPhp extends CommonGeneratorBase {

	public ReportModel_Common_GenPhp() {
		super(getLanguage());
	}

	public static GenLanguages getLanguage() {
		return GenLanguages.glPHP;
	}

	protected void generate(BufferedWriter out) throws IOException {
		out.write("class ReportObjectBase {\n");
		out.write("\n");
		out.write("  private $handle;\n");
		out.write("\n");
		out.write("  public function __construct($handle) {\n");
		out.write("    $this->handle = $handle;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  protected function getHandle() {\n");
		out.write("    return $this->handle;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("}\n");
		out.write("\n");

		out.write("class ExecutionLogItem {\n");
		out.write("\n");
		out.write("  private $level;\n");
		out.write("  private $msg;\n");
		out.write("\n");
		out.write("  public function __construct($level, $msg) {\n");
		out.write("    $this->level = $level;\n");
		out.write("    $this->msg = $msg;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function getLevel() {\n");
		out.write("    return $this->level;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function getMsg() {\n");
		out.write("    return $this->msg;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("}\n");
		out.write("\n");

		out.write("class ExecutionLog extends ReportObjectBase {\n");
		out.write("\n");
		out.write("  public function __construct($handle) {\n");
		out.write("    parent::__construct($handle);\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function getCount() {\n");
		out.write("    return jasper_execlog_count($this->getHandle());\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function getItem($idx) {\n");
		out.write("    $level = jasper_execlog_level($this->getHandle(), $idx);\n");
		out.write("    $msg = jasper_execlog_msg($this->getHandle(), $idx);\n");
		out.write("    return new ExecutionLogItem($level, $msg);\n");
		out.write("  }\n");
		out.write("\n");
		out.write("}\n");
		out.write("\n");
		
		out.write("class ReportBase extends ReportObjectBase {\n");
		out.write("\n");
		out.write("  private $exec_log;\n");
		out.write("\n");
		out.write("  public function __construct($id) {\n");
		out.write("    parent::__construct(jasper_open_rep($id));\n");
		out.write("    $this->exec_log = new ExecutionLog($this->getHandle());\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function __destruct() {\n");
		out.write("    jasper_close_rep($this->getHandle());\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function execute() {\n");
		out.write("    return (jasper_execute($this->getHandle()) == 0);\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function autosave_pdf() {\n");
		out.write("    $pdf_file = jasper_save_random($this->getHandle());\n");
		out.write("    return $pdf_file;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function return_pdf() {\n");
		out.write("    $bs = jasper_buffer_size($this->getHandle());\n");
		out.write("    if ($bs > 0) {\n");
		out.write("      header('Content-Type: application/pdf');\n");
		out.write("      header('Content-Length: ' . $bs);\n");
		out.write("      jasper_buffer_read($this->getHandle());\n");
		out.write("      return true;\n");
		out.write("    } else {\n");
		out.write("      return false;\n");
		out.write("    }\n");
		out.write("  }\n");
		out.write("\n");
		out.write("  public function getExecutionLog() {\n");
		out.write("    return $this->exec_log;\n");
		out.write("  }\n");
		out.write("\n");
		out.write("}\n");
		out.write("\n");
	}

}
