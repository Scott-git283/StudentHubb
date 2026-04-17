<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// Correct JOIN: Get UID from 'users' and NAMES from 'faculty'
$sql = "SELECT u.uid, f.first_name, f.last_name 
        FROM users u 
        JOIN faculty f ON u.uid = f.user_uid 
        WHERE u.role = 'Faculty'";

$result = $conn->query($sql);
$faculty = [];

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $faculty[] = [
            "uid" => (string)$row['uid'],
            "first_name" => $row['first_name'],
            "last_name" => $row['last_name']
        ];
    }
}

echo json_encode($faculty);
?>