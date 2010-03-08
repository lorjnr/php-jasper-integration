<?php

  function print_error($msg) {
?>
<html>
  <head>
    <title>Report Error</title>
  </head>
  <body>
    <center>
      <?php print $msg; ?>
    </center>
  </body>
</html>
<?php
  }

  include 'classes/ExRep.php';

  jasper_open_mngr("./");

  $rep = new ExRep();
  $rep->getParameters()->setStrParam("String-Parameter");
  $rep->getParameters()->setIntParam(9999);
  if ($rep->execute()) {
    if ( ! $rep->return_pdf()) {
      print_error("Unable to return Report");
    }
  } else {
    print_error("Unable to execute Report");
  }

?>

