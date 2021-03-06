<?php
 
/*
 * Following code will get single row details
 * A row is identified by id
 */
 

// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_config.php';
 
// connecting to db
$db = new PDO('mysql:host=localhost;dbname=postman;charset=utf8mb4', DB_USER ,DB_PASSWORD);

 
// check for post data
if (isset($_GET["id"]) && isset($_GET["type"])) {
    $id = $_GET['id'];
    $type = $_GET['type'];

    // get a data from proj table
    if($type == 1){
        $result = $db->query("SELECT *FROM transport WHERE receiver = '$id'");
    }else{
        $result = $db->query("SELECT *FROM transport WHERE sender = '$id'");
    }
    
 
    if (!empty($result)) {
        // check for empty result
        if ($result->rowCount() > 0) {
 
            $response["data"] = array();
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
 
                $data = array();
                $data["id"] = $row["_id"];
                $data["requireTime"] = $row["requireTime"];
                $sender= $row["sender"];
                $receiver = $row["receiver"];
                $des_id = $row["des_id"];
                $data["car_id"] = $row["car_id"];
                $data["state"] = $row["state"];
                $data["key"] = $row["packetKey"];

                //用ID找sender的名字
                $sender_result = $db->query("SELECT *FROM user WHERE _id = '$sender'");
                $sender_result = $sender_result->fetch(PDO::FETCH_ASSOC);
                $data["sender"] = $sender_result["name"];
                //用ID找receiver的名字
                $receiver_result = $db->query("SELECT *FROM user WHERE _id = '$receiver'");
                $receiver_result = $receiver_result->fetch(PDO::FETCH_ASSOC);
                $data["receiver"] = $receiver_result["name"];
                //用ID找收件地
                $des_result = $db->query("SELECT *FROM location WHERE _id = '$des_id'");
                $des_result = $des_result->fetch(PDO::FETCH_ASSOC);
                $data["des"] = $des_result["name"];
 
                array_push($response["data"], $data);
            }
             // success
            $response["success"] = 1;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No receiver found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No receiver found";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}

?>