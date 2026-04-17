<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$uid = $_GET['uid'] ?? '';

if (empty($uid)) {
    echo json_encode(["error" => "User UID is required"]);
    exit;
}

// 1. Get basic info and role from the login table
$sql = "SELECT uid, email, role FROM users WHERE uid = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $uid);
$stmt->execute();
$user = $stmt->get_result()->fetch_assoc();

if ($user) {
    $role = $user['role'];
    $data = [
        "uid" => (string)$user['uid'],
        "email" => $user['email'],
        "role" => $user['role'],
        "first_name" => "",
        "last_name" => "",
        "department_id" => ""
    ];

    // 2. Get profile details from the correct specialized table
    if ($role == 'Student') {
        $sql_p = "SELECT first_name, last_name, department_id FROM students WHERE user_uid = ?";
    } elseif ($role == 'Faculty') {
        $sql_p = "SELECT first_name, last_name, department_id FROM faculty WHERE user_uid = ?";
    } else {
        // Admin details (usually static or in a separate table)
        $data["first_name"] = "System";
        $data["last_name"] = "Admin";
        echo json_encode($data);
        exit;
    }

    $stmt_p = $conn->prepare($sql_p);
    $stmt_p->bind_param("i", $uid);
    $stmt_p->execute();
    $profile = $stmt_p->get_result()->fetch_assoc();

    if ($profile) {
        $data["first_name"] = $profile['first_name'];
        $data["last_name"] = $profile['last_name'];
        $data["department_id"] = (string)$profile['department_id'];
    }

    echo json_encode($data);
} else {
    http_response_code(404);
    echo json_encode(["error" => "User not found"]);
}
?>