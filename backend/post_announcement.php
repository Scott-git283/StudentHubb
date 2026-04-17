<?php
require_once 'db_config.php';
$course_id = $_POST['course_id'];
$faculty_email = $_POST['faculty_id'];
$title = $_POST['title'];
$message = $_POST['message'];

// Get faculty UID from email
$sql_user = "SELECT uid FROM users WHERE email = ?";
$stmt_user = $conn->prepare($sql_user);
$stmt_user->bind_param("s", $faculty_email);
$stmt_user->execute();
$faculty_uid = $stmt_user->get_result()->fetch_assoc()['uid'];

$sql = "INSERT INTO announcements (course_id, faculty_uid, title, message) VALUES (?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iiss", $course_id, $faculty_uid, $title, $message);

echo json_encode(["success" => $stmt->execute()]);
?>