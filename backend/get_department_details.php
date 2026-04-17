<?php
require_once 'db_config.php';

$id = $_GET['id']; // This MUST match @Query("id") in Android

$sql = "SELECT id, name, description FROM departments WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $id);
$stmt->execute();
$result = $stmt->get_result();

if ($dept = $result->fetch_assoc()) {
    // Return JUST the department object, not wrapped in another array
    echo json_encode($dept);
} else {
    // If not found, returning an error response
    http_response_code(404);
    echo json_encode(["message" => "Department not found"]);
}
?>
