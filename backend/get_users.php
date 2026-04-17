<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// We use UNION to get names from both the students and faculty tables
$sql = "SELECT u.uid, u.email, u.role, s.first_name, s.last_name 
        FROM users u 
        JOIN students s ON u.uid = s.user_uid 
        WHERE u.role = 'Student'
        
        UNION
        
        SELECT u.uid, u.email, u.role, f.first_name, f.last_name 
        FROM users u 
        JOIN faculty f ON u.uid = f.user_uid 
        WHERE u.role = 'Faculty'
        
        UNION
        
        SELECT uid, email, role, 'System' as first_name, 'Admin' as last_name 
        FROM users 
        WHERE role = 'Admin'";

$result = $conn->query($sql);
$users = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
    }
    echo json_encode($users);
} else {
    http_response_code(500);
    echo json_encode(["error" => $conn->error]);
}
?>