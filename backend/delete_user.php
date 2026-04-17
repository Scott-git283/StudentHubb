<?php
require_once 'db_config.php';

$uid = $_POST['uid']; // Make sure this matches the @Field name in Android

$sql = "DELETE FROM users WHERE uid = ?"; // Use the correct column name from your DB
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $uid);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "User deleted"]);
} else {
    echo json_encode(["success" => false, "message" => "Error deleting user: " . $conn->error]);
}
?>