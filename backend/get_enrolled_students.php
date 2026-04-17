<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$faculty_id = $_GET['faculty_id'] ?? '';

if (empty($faculty_id)) {
    echo json_encode([]);
    exit;
}

// Convert email to numeric UID if necessary
if (!is_numeric($faculty_id)) {
    $stmt = $conn->prepare("SELECT uid FROM users WHERE email = ?");
    $stmt->bind_param("s", $faculty_id);
    $stmt->execute();
    $res = $stmt->get_result()->fetch_assoc();
    $faculty_id = $res['uid'] ?? 0;
}

// Use GROUP BY to prevent the "two students" issue in the UI
$sql = "SELECT 
            u.uid, 
            s.first_name, 
            s.last_name, 
            u.email, 
            s.student_id_number 
        FROM users u
        JOIN students s ON u.uid = s.user_uid
        JOIN enrollments e ON u.uid = e.student_uid
        JOIN courses c ON e.course_id = c.id
        WHERE c.instructor_uid = ? OR e.faculty_uid = ?
        GROUP BY u.uid";

$stmt = $conn->prepare($sql);
$stmt->bind_param("ii", $faculty_id, $faculty_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode($students);
?>