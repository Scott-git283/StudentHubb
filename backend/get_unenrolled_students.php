<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$course_id = $_GET['course_id'] ?? '';

if (empty($course_id)) {
    echo json_encode([]);
    exit;
}

// GROUP BY ensures students with duplicate profile rows only show up once
$sql = "SELECT u.uid, s.first_name, s.last_name, u.email, s.student_id_number 
        FROM users u
        JOIN students s ON u.uid = s.user_uid
        WHERE u.role = 'Student' 
        AND u.uid NOT IN (SELECT student_uid FROM enrollments WHERE course_id = ?)
        GROUP BY u.uid";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $course_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode($students);
?>