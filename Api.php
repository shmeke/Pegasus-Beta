<?php 
        //getting the database connection
 require_once 'DbConnect.php';
 
 //an array to display response
 $response = array();
 
 //if it is an api call 
 //that means a get parameter named api call is set in the URL 
 //and with this parameter we are concluding that it is an api call 
 if(isset($_GET['apicall'])){
 
 switch($_GET['apicall']){
 
 case 'signup':
 
    if(isTheseParametersAvailable(array('username','email','password'))){
 
        //getting the values 
        $username = $_POST['username']; 
        $email = $_POST['email']; 
        $password = md5($_POST['password']);
        
        //checking if the user is already exist with this username or email
        //as the email and username should be unique for every user 
        $stmt = $conn->prepare("SELECT id FROM users WHERE username = ? OR email = ?");
        $stmt->bind_param("ss", $username, $email);
        $stmt->execute();
        $stmt->store_result();
        
        //if the user already exist in the database 
        if($stmt->num_rows > 0){
        $response['error'] = true;
        $response['message'] = 'User already registered';
        $stmt->close();
        }else{
        
        //if user is new creating an insert query 
        $stmt = $conn->prepare("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
        $stmt->bind_param("sss", $username, $email, $password);
        
        //if the user is successfully added to the database 
        if($stmt->execute()){
        
        //fetching the user back 
        $stmt = $conn->prepare("SELECT id, username, email FROM users WHERE username = ?"); 
        $stmt->bind_param("s",$username);
        $stmt->execute();
        $stmt->bind_result($id, $username, $email);
        $stmt->fetch();
        
        $user = array(
        'id'=>$id, 
        'username'=>$username, 
        'email'=>$email
        );
        
        $stmt->close();
        
        //adding the user data in response 
        $response['error'] = false; 
        $response['message'] = 'User registered successfully'; 
        $response['user'] = $user; 
        }
        }
        
        }else{
        $response['error'] = true; 
        $response['message'] = 'required parameters are not available'; 
        }
 
 break; 

 case 'horseinfo':
 
   if(isTheseParametersAvailable(array('id', 'username','name','breed', 'height'))){

       //getting the values 
       $id = $_POST['id'];
       $username = $_POST['username']; 
       $name = $_POST['name']; 
       $breed = $_POST['breed'];
       $height = $_POST['height'];
       

       $stmt = $conn->prepare("SELECT username FROM additional_user_info WHERE username = ? AND name = ?");
       $stmt->bind_param("ss", $username, $name);
       $stmt->execute();
       $stmt->store_result();
       
       //if the user already exist in the database 
       if($stmt->num_rows > 0){
       $response['error'] = true;
       $response['message'] = 'This horse is already registered to your account';
       $stmt->close();
       }else{

          
       //if user is new creating an insert query 
       $stmt = $conn->prepare("INSERT INTO additional_user_info (id, username, name, breed, height) VALUES (?, ?, ?, ?, ?)");
       $stmt->bind_param("sssss",$id, $username, $name, $breed, $height);
       
       //if the user is successfully added to the database 
       if($stmt->execute()){
       
       //fetching the user back 
       $stmt = $conn->prepare("SELECT name, breed, height FROM additional_user_info WHERE username = ?"); 
       $stmt->bind_param("s",$username);
       $stmt->execute();

         $stmt->bind_result($name, $breed, $height);
         $stmt->fetch();
       
       $horse = array(
       'name'=>$name,
       'breed'=>$breed,
       'height'=>$height
       );
       
       $stmt->close();
       
       //adding the user data in response 
       $response['error'] = false; 
       $response['message'] = 'Successfully added info'; 
       $response['horse'] = $horse; 
       }
      }
       
       }else{
       $response['error'] = true; 
       $response['message'] = 'required parameters are not available'; 
       }

break; 

 
 case 'login':
 
    if(isTheseParametersAvailable(array('username', 'password'))){
     
        //getting values 
        $username = $_POST['username'];
        $password = md5($_POST['password']); 

        session_start();
        $_SESSION['user'] = $username;
        

        //creating the query 
        $stmt = $conn->prepare("SELECT id, username, email FROM users WHERE username = ? AND password = ?");
        $stmt->bind_param("ss",$username, $password);
        
        $stmt->execute();
        
        $stmt->store_result();
        
        //if the user exist with given credentials 
        if($stmt->num_rows > 0){
        
        $stmt->bind_result($id, $username, $email);
        $stmt->fetch();
        
        $user = array(
        'id'=>$id, 
        'username'=>$username, 
        'email'=>$email
        );

               

        
        $response['error'] = false; 
        $response['message'] = 'Login successfull'; 
        $response['user'] = $user; 
        
        
        }else{
        //if the user not found 
        $response['error'] = true; 
        $response['message'] = 'Invalid username or password';
        }
        }

        
 
 break; 

 case 'gethorse':
   if(isTheseParametersAvailable(array('username'))){
      $username = $_POST['username'];

      
                //creating the query 
                $stmt = $conn->prepare("SELECT name FROM additional_user_info WHERE username = ?");
                $stmt->bind_param("s",$username);
                
                $stmt->execute();
                $result = $stmt->get_result();

                
                $json = [];

               if($result->num_rows > 0){
                  while($row = $result->fetch_assoc()){
                     $horse[] = array(
                        'name' => $row["name"]
                        );
                        
                  }
               } 
               $json = json_encode($horse);


                $response['horse'] = $json;
   }
   


break;

 case 'tempworkout':
    if(isTheseParametersAvailable(array('id','username', 'oldVelEst', 'newVelEst', 'stepTime'))){
        $id = $_POST['id'];
        $username = $_POST['username'];
        $oldVelEst = $_POST['oldVelEst'];
        $newVelEst = $_POST['newVelEst'];
        $stepTime = $_POST['stepTime'];
        //$averagespeed = $_POST['averagespeed'];
        
     
        

         //Insert workout data to database
         $stmt = $conn->prepare("INSERT INTO testvelest (id, username, oldVelEst, newVelEst, stepTime, date) VALUES (?, ?, ?, ?, ?, now())");
         $stmt->bind_param('ssssd', $id, $username, $oldVelEst, $newVelEst, $stepTime);
        
         $stmt->execute();
         $stmt->close();

         
        
    }
   break;



   case 'getWorkouts':
      if(isTheseParametersAvailable(array('username', 'date'))){
     
         //getting values 
         $username = $_POST['username'];
         $date = $_POST['date']; 
 
         
         //creating the query 
         $stmt = $conn->prepare("SELECT nmbrstops, averagespeed, meters FROM tempworkout WHERE username = ? AND date = ?");
         $stmt->bind_param("ss",$username, $date);
         
         $stmt->execute();
         
         $stmt->store_result();
         
         //if the user exist with given credentials 
         if($stmt->num_rows > 0){
         
         $stmt->bind_result($username, $stops, $speed, $meters);
         $stmt->fetch();
         
         $WorkoutsList = array(
         'user'=>$username,
         'nmbrstops'=>$stops, 
         'avrgspeed'=>$speed, 
         'meters'=>$meters
         );
         
         $response['error'] = false; 
         $response['message'] = 'Login successfull'; 
         $response['workout'] = $WorkoutsList; 
         
         }else{
         //if the user not found 
         $response['error'] = true; 
         $response['message'] = 'Invalid username or password';
         }
         }
 
   

   break;
         
         default: 
         $response['error'] = true; 
         $response['message'] = 'Invalid Operation Called';
   }
 
 }else{
 //if it is not api call 
 //pushing appropriate values to response array 
 $response['error'] = true; 
 $response['message'] = 'Invalid API Call';
 }
 
 //displaying the response in json structure 
 echo json_encode($response);


 function isTheseParametersAvailable($params){
 
    //traversing through all the parameters 
    foreach($params as $param){
    //if the paramter is not available
    if(!isset($_POST[$param])){
    //return false 
    return false; 
    }
    }
    //return true if every param is available 
    return true; 
    }