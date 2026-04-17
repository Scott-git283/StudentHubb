<?php
require_once 'db_config.php';

$name = $_POST['name'];
$description = $_POST['description'];

$sql = "INSERT INTO departments (name, description) VALUES (?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $name, $description);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Department added"]);
} else {
    echo json_encode(["success" => false, "message" => "Error adding department"]);
}
?>