<?php
header('Content-Type: application/json');
require_once 'db_config.php';

error_reporting(0);
ini_set('display_errors', 0);

$student_id = $_GET['student_id'] ?? '';

if (empty($student_id)) {
    echo json_encode([]);
    exit;
}

try {
    // 1. Get student UID if email was passed
    if (!is_numeric($student_id)) {
        $stmt = $conn->prepare("SELECT uid FROM users WHERE email = ?");
        $stmt->bind_param("s", $student_id);
        $stmt->execute();
        $res = $stmt->get_result()->fetch_assoc();
        $student_uid = $res['uid'] ?? 0;
    } else {
        $student_uid = (int)$student_id;
    }

    // 2. Fetch announcements (Using 'faculty_uid' as per your DB snippet)
    $sql = "SELECT DISTINCT
                a.id, 
                a.title, 
                a.message, 
                a.created_at, 
                c.course_name,
                CONCAT(f.first_name, ' ', f.last_name) as faculty_name
            FROM announcements a
            INNER JOIN courses c ON a.course_id = c.id
            INNER JOIN enrollments e ON c.id = e.course_id
            LEFT JOIN faculty f ON a.faculty_uid = f.user_uid
            WHERE e.student_uid = ?
            ORDER BY a.created_at DESC";

    $stmt = $conn->prepare($sql);
    if (!$stmt) { throw new Exception($conn->error); }
    
    $stmt->bind_param("i", $student_uid);
    $stmt->execute();
    $result = $stmt->get_result();

    $announcements = [];
    while ($row = $result->fetch_assoc()) {
        $announcements[] = $row;
    }

    echo json_encode($announcements);

} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["success" => false, "message" => "SQL Error: " . $e->getMessage()]);
}
?>
