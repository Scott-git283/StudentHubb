<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$id = $_POST['id'] ?? '';
$name = $_POST['course_name'] ?? '';
$code = $_POST['course_code'] ?? '';
$dept_id = $_POST['department_id'] ?? '';
$inst_id = $_POST['instructor_uid'] ?? '';

if (empty($id) || empty($name)) {
    echo json_encode(["success" => false, "message" => "Missing required data"]);
    exit;
}

$sql = "UPDATE courses SET course_name = ?, course_code = ?, department_id = ?, instructor_uid = ? WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssiii", $name, $code, $dept_id, $inst_id, $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Course updated successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Update failed: " . $conn->error]);
}
?>