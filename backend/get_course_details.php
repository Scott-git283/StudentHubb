<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$id = $_GET['id'] ?? '';

if (empty($id)) {
    echo json_encode(["error" => "Course ID required"]);
    exit;
}

$sql = "SELECT id, course_name, course_code, department_id, instructor_uid FROM courses WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);
$stmt->execute();
$course = $stmt->get_result()->fetch_assoc();

if ($course) {
    // Return numeric IDs as strings for the Android model
    $course['id'] = (string)$course['id'];
    $course['department_id'] = (string)$course['department_id'];
    $course['instructor_uid'] = (string)$course['instructor_uid'];
    echo json_encode($course);
} else {
    http_response_code(404);
    echo json_encode(["error" => "Course not found"]);
}
?>