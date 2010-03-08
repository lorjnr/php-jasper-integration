<?php

jasper_open_mngr("/var/www");
$handle = jasper_open_rep("ExRep");
jasper_register_modification($handle, "Parameter:StrParam=Test");
jasper_register_modification($handle, "Parameter:IntParam=5");
$rc = jasper_execute($handle);
echo "$rc\n";

?>

