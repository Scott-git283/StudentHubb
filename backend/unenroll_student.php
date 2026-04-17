<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$enrollment_id = $_POST['enrollment_id'];

$sql = "DELETE FROM enrollments WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $enrollment_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "message" => $conn->error]);
}
?>