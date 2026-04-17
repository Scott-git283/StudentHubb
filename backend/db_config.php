<?php
error_reporting(0);
ini_set('display_errors', 0);
$host = "localhost";
$user = "root";
$pass = "";
$db = "student_hub";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Connection failed: " . $conn->connect_error]));
}
?>