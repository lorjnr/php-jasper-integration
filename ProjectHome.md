This project inegrates Jasper-Reports into PHP in an unusual manner. The known approaches use the PHP-Java-Bridge to connect the two technologies, this project does not. The folloging four components are responsible for it to work:

## Components ##

### Administration ###

As the name indicates, the administration is a central management tool, written in Java, where all Jasper-Reports are collected, grouped by projects. That is not only for being user-friendly but it's a central design property. The administration stores the server configuration and reports and generates a PHP-Representation for each Report which must be deployed to the webserver. Generation of other Source then PHP is intended but not implemented yet.

### Backend ###

The backend is where the reports get generated. It's a socket-server written in Java which takes requests, generates reports and returns results. A request contains the Report-Template, parameters and a list of Modification-Items. The backend then creates a report object, applies modifications generates a log about the process and the expected output.

### Library ###

To communicate with the backend, there is a client library written in c++ witch a c interface. It handles the communication and configuration and stores results. Because this library has a c interface it could be used for other languagen then PHP as well.

### PHP-Extension ###

This extension just forewards interface of the library to PHP. It does not contain any importend logic for the process.

## Example ##

In my opinion, the biggest difference to the Java-Php-Bridge-Approach is the PHP-Representation for each report. The PHP-Programmers to not have to call Java functions in PHP. A sample code could look like this:

```
include 'rep_classes/TestRep.php';

jasper_open_mngr("./");
$rep = new TestRep();
$rep->getParameters()->setParam1("Value1");
$rep->getParameters()->setParam2("Value2");
$rep->getTitleBand()->getTitle()->setText("New Title");
if ($rep->execute()) {
  $rep->return_pdf();
}
```