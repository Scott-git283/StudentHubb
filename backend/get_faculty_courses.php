<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// The app sends faculty_id (either UID or Email)
$faculty_id = $_GET['faculty_id'] ?? '';

if (empty($faculty_id)) { echo json_encode([]); exit; }

// If it's an email, find the UID first
if (!is_numeric($faculty_id)) {
    $res = $conn->query("SELECT uid FROM users WHERE email = '$faculty_id'");
    $u = $res->fetch_assoc();
    $faculty_id = $u['uid'] ?? 0;
}

// Fetch courses where this faculty is the instructor
$sql = "SELECT id as course_id, course_code, course_name FROM courses WHERE instructor_uid = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $faculty_id);
$stmt->execute();
echo json_encode($stmt->get_result()->fetch_all(MYSQLI_ASSOC));
?>