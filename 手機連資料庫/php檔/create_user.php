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
if (isset($DATA["name"]) && isset($DATA["account"]) && isset($DATA["password"]) && isset($DATA["mail"])){
 
    $name = $DATA['name'];
    $account = $DATA['account'];
    $password = $DATA['password'];
    $email = $DATA['mail'];

    // include db connect class
    require_once __DIR__ . '/db_config.php';
 
    // connecting to db
    $db = new PDO('mysql:host=localhost;dbname=postman;charset=utf8mb4', DB_USER ,DB_PASSWORD);
 
    // mysql inserting a new row
    $result = $db->query("INSERT INTO user(account,password,name,mail,createTime) VALUES('$account', '$password','$name','$email',CURRENT_TIME)");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $result = $db->query("SELECT *FROM user where account = '$account'");
        if(!empty($result)){
            $result = $result->fetch(PDO::FETCH_ASSOC);
            $response["data"] = $result["_id"];
        }
        $response["message"] = "row successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing.name: "+$DATA['name']+" account: "+$DATA['account']+" password: "+$DATA['password']+" email: "+$DATA['mail'];
 
    // echoing JSON response
    echo json_encode($response);
}
?>