<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// Disable error reporting for clean JSON output
error_reporting(0);
ini_set('display_errors', 0);

try {// Use SELECT DISTINCT to prevent duplicates if JOINs match multiple rows
    $sql = "SELECT DISTINCT
                c.id, 
                c.course_name, 
                c.course_code, 
                c.department_id, 
                c.instructor_uid, 
                d.name as department_name, 
                CONCAT(f.first_name, ' ', f.last_name) as instructor_name 
            FROM courses c
            LEFT JOIN departments d ON c.department_id = d.id
            LEFT JOIN faculty f ON c.instructor_uid = f.user_uid
            GROUP BY c.id"; // Grouping by course ID is the safest way to ensure uniqueness

    $result = $conn->query($sql);
    $courses = [];

    if ($result) {
        while ($row = $result->fetch_assoc()) {
            $courses[] = $row;
        }
        echo json_encode($courses);
    } else {
        echo json_encode([]);
    }
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["error" => "Database error: " . $conn->error]);
}

$conn->close();
?>