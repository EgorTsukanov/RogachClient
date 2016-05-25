<?php
	$host='localhost';
	$uname='adminXf8BJkq';
	$pwd='YAHsWAJtLIjb';
	$db="android_api";

	$con = mysql_connect($host,$uname,$pwd) or die("connection failed");
	mysql_select_db($db,$con) or die("db selection failed");

	$result = mysql_query("SELECT * FROM  `news` ") or die(mysql_error());
	 
	if (mysql_num_rows($result) > 0) {
    		$response["news"] = array();
 
    	while ($row = mysql_fetch_array($result)) {
        	$news = array();
        	$news["id"] = $row["id"];
        	$news["name"] = $row["name"];
        	$news["text"] = $row["text"];
       		//$product["created_at"] = $row["created_at"];
                //$product["updated_at"] = $row["updated_at"];
 
        	array_push($response["news"], $news);
    }
    $response["success"] = 1;
 
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "No news found";
 
    echo json_encode($response);
}
?>
?>