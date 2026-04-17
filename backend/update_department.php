<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// Get POST data
$id = $_POST['id'] ?? null;
$name = $_POST['name'] ?? null;
$description = $_POST['description'] ?? null;

if (!$id || !$name) {
    echo json_encode(["success" => false, "message" => "Missing ID or Name"]);
    exit;
}

// SQL Update - Ensure column names match your DB (id, name, description)
$sql = "UPDATE departments SET name = ?, description = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssi", $name, $description, $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Department updated successfully!"]);
} else {
    echo json_encode(["success" => false, "message" => "Database error: " . $conn->error]);
}

$stmt->close();
$conn->close();
?>