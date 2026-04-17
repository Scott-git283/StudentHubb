<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$student_uid = $_POST['student_id'] ?? '';
$course_id = $_POST['course_id'] ?? '';
$faculty_uid = $_POST['faculty_id'] ?? '';

if (empty($student_uid) || empty($course_id) || empty($faculty_uid)) {
    echo json_encode(["success" => false, "message" => "Missing required IDs"]);
    exit;
}

$sql = "INSERT INTO enrollments (student_uid, course_id, faculty_uid) VALUES (?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iii", $student_uid, $course_id, $faculty_uid);

if ($stmt->execute()) {
    echo json_encode(["success" => true, "message" => "Student enrolled successfully"]);
} else {
    echo json_encode(["success" => false, "message" => "Database error: " . $conn->error]);
}
?>