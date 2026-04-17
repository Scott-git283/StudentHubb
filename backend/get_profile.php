<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$email = $_GET['username'] ?? '';

if (empty($email)) {
    echo json_encode(["success" => false, "message" => "Username required"]);
    exit;
}

$stmt = $conn->prepare("SELECT uid, role, email FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$user = $stmt->get_result()->fetch_assoc();

if ($user) {
    $uid = $user['uid'];
    $role = $user['role'];

    if ($role == 'Student') {
        $q = "SELECT first_name, last_name, student_id_number, profile_image_url, d.name as department_name 
              FROM students s LEFT JOIN departments d ON s.department_id = d.id WHERE user_uid = ?";
    } elseif ($role == 'Faculty') {
        $q = "SELECT first_name, last_name, office_number, profile_image_url, d.name as department_name 
              FROM faculty f LEFT JOIN departments d ON f.department_id = d.id WHERE user_uid = ?";
    } else {
        // Admin: pull from admins table
        $q = "SELECT first_name, last_name, profile_image_url, 'Administration' as department_name 
              FROM admins WHERE user_uid = ?";
    }
    
    $stmt_p = $conn->prepare($q);
    $stmt_p->bind_param("i", $uid);
    $stmt_p->execute();
    $profile = $stmt_p->get_result()->fetch_assoc();
    
    if ($profile) {
        $profile['email'] = $user['email'];
        echo json_encode(["success" => true, "data" => $profile]);
    } else {
        echo json_encode(["success" => false, "message" => "Profile not found"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "User not found"]);
}
?>