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
if (isset($_GET["account"]) && isset($_GET["password"])) {
    $account = $_GET['account'];
    $password = $_GET['password'];

    // get a data from proj table
    $result = $db->query("SELECT *FROM user WHERE account = '$account'");

    //看錯誤
    //echo json_encode($db->errorinfo());
   
    if (!empty($result)) {
        // check for empty result
        if ($result->rowCount() > 0) {
            
            $result = $result->fetch(PDO::FETCH_ASSOC);
 
            $data = array();
            $data["id"] = $result["_id"];
            $data["name"] = $result["name"];
            $data["mail"] = $result["mail"];

            if($password != $result["password"]){

                $response["success"] = 0;
                $response["message"] = "Password Wrong.";
 
            // echo no users JSON
            echo json_encode($response);

            }else{
                // success
                $response["success"] = 1;
 
                // user node
                $response["data"] = array();
 
                array_push($response["data"], $data);
 
                // echoing JSON response
                echo json_encode($response);
            }    
            
        } 
        else {
           
            // no user found
            $response["success"] = 0;
            $response["message"] = "No user found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        
        // no user found
        $response["success"] = 0;
        $response["message"] = "No user found";
 
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