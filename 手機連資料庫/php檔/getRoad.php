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


if(isset($_GET["district"])){
    // check for empty result
    $district = $_GET["district"];
    $result = $db->query("SELECT *FROM test where site_id = '$district'");
    if ($result->rowCount() > 0) {
    // looping through all results
        $response["data"] = array();
        $response["success"] = 1;
 
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            $data = array();
            $data["road"] = $row["road"]; 
            // push single product into final response array
            array_push($response["data"], $data);
        }
    }
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "district not found";
 
    // echo no users JSON
    echo json_encode($response);
}


?>