
<?php

  include 'classes/ExRep.php';

?>

<html>
  <head>
    <title>Jasp Example</title>
    <script type="text/javascript">
      function showReport(rep) {
        window.location.href = rep;
      }
    </script>
  </head>

  <body>
    <center>
      <?php

        jasper_open_mngr("./");

        $rep = new ExRep();
 	$rep->getParameters()->setStrParam("String-Parameter");
 	$rep->getParameters()->setIntParam(9999);
        $rep->getTitleBand()->getTxtTitle()->setText("New Title");
        if ($rep->execute()) {
          $pdf = $rep->autosave_pdf();
	  echo "$pdf\n";

          if ($pdf != "") {
            echo "Redirect<br>\n";
            echo "<script type=\"text/javascript\">showReport(\"$pdf\");</script>\n";
          } else {
            echo "Error occured<br>\n";
          }
        } else {
          echo "Error occured<br>\n";
        }
      ?>
    </center>
  </body>
</html>

