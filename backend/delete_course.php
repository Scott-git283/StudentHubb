<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$id = $_POST['id'] ?? '';

if (empty($id)) {
    echo json_encode(["success" => false, "message" => "ID required"]);
    exit;
}

$sql = "DELETE FROM courses WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Course deleted"]);
} else {
    echo json_encode(["success" => false, "message" => "Delete failed: " . $conn->error]);
}
?>