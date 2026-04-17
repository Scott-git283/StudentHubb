<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$course_id = $_GET['course_id'] ?? '';

if (empty($course_id)) {
    echo json_encode([]);
    exit;
}

// Added GROUP BY e.student_uid to prevent duplicate rows in the list
$sql = "SELECT 
            MIN(e.id) as enrollment_id, 
            u.uid as student_uid, 
            s.first_name, 
            s.last_name, 
            u.email, 
            s.student_id_number 
        FROM enrollments e
        JOIN users u ON e.student_uid = u.uid
        JOIN students s ON u.uid = s.user_uid
        WHERE e.course_id = ?
        GROUP BY u.uid";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $course_id);
$stmt->execute();
$result = $stmt->get_result();

$enrolled = [];
while ($row = $result->fetch_assoc()) {
    $enrolled[] = $row;
}

echo json_encode($enrolled);
?>