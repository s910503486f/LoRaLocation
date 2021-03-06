<?php
 
/*
 * Following code will create a new row
 * All details are read from HTTP Post Request
 */
 
//read raw data from the request body
$request_body = file_get_contents('php://input');
//true:return array
$DATA= json_decode($request_body,true);

// array for JSON response
$response = array();
 
// check for required fields
if (isset($DATA["requireTime"]) && isset($DATA["sender"]) && isset($DATA["receiver"]) && isset($DATA["des_id"]) && isset($DATA["car_id"]) && isset($DATA["start_id"]) && isset($DATA["key"])){
 
    $requireTime = $DATA['requireTime'];
    $sender = $DATA['sender'];
    $receiver = $DATA['receiver'];
    $des_id = $DATA['des_id'];
    $car_id = $DATA['car_id'];
    $start_id = $DATA['start_id'];
    $key = $DATA['key'];
 
    // include db connect class
    require_once __DIR__ . '/db_config.php';
 
    // connecting to db
    $db = new PDO('mysql:host=localhost;dbname=postman;charset=utf8mb4', DB_USER ,DB_PASSWORD);
 
    // mysql inserting a new row
    $result = $db->query("INSERT INTO transport(requireTime, sender, receiver,start_id,des_id,car_id,packetKey) VALUES('$requireTime', '$sender', '$receiver','$start_id','$des_id','$car_id','$key')");
    //echo json_encode($db->errorinfo());
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "row successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
      //  echo json_encode($db->errorinfo());
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing.requireTime: "+$DATA['requireTime']+" sender: "+$DATA['sender']+" receiver: "+$DATA['receiver']+"start_id: "+$DATA['start_id']+" des_id: "+$DATA['des_id']+" car_id: "+$DATA['car_id'];
 
    // echoing JSON response
    echo json_encode($response);
}
?>