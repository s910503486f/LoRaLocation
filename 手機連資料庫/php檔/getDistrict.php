<?php
 
/*
 * Following code will list all the rows
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_config.php';
 
// connecting to db
$db = new PDO('mysql:host=localhost;dbname=roadName;charset=utf8mb4', DB_USER ,DB_PASSWORD);


if(isset($_GET["county"])){
    // check for empty result
    //$county = utf8_decode($_GET["county"]);
    $county = $_GET["county"];
    $result = $db->query("SELECT *FROM test where city = '$county' group by site_id");
    if ($result->rowCount() > 0) {
    // looping through all results
        $response["data"] = array();
        $response["success"] = 1;
 
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $data = array();
            $data["district"] = $row["site_id"];
 
            // push single product into final response array
            array_push($response["data"], $data);
            // echoing JSON response
        }
        echo json_encode($response);
    }

    
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "county not found";
 
    // echo no users JSON
    echo json_encode($response);
}


?>