<?php
 
/*
 * Following code will list all the rows
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_config.php';
 
// connecting to db
$db = new PDO('mysql:host=localhost;dbname=postman;charset=utf8mb4', DB_USER ,DB_PASSWORD);

// get all rows from location table
$result = $db->query("SELECT *FROM location");
 
// check for empty result
if ($result->rowCount() > 0) {
    // looping through all results
    // products node
    $response["data"] = array();
 
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        // temp user array
        $data = array();
        $data["id"] = $row["_id"];
        $data["name"] = $row["name"];
 
        // push single product into final response array
        array_push($response["data"], $data);
    }

    //get all user from user table
    $result = $db->query("SELECT *FROM user");
    if($result->rowCount() > 0){
        $response["usr"] = array();
        while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
            // temp user array
            $usr = array();
            $usr["id"] = $row["_id"];
            $usr["name"] = $row["name"];
 
            // push single product into final response array
            array_push($response["usr"], $usr);
        }
        // success
        $response["success"] = 1;
    }

    else{
        $response["message"] = "No user found";
    }
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No location found";
 
    // echo no users JSON
    echo json_encode($response);
}


?>