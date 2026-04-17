<?php
header('Content-Type: application/json');
require_once 'db_config.php';
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

$stmt = $conn->prepare("SELECT uid, password, role FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$res = $stmt->get_result()->fetch_assoc();

if ($res && $password == $res['password']) {
    echo json_encode(["success" => true, "role" => $res['role'], "userId" => (string)$res['uid']]);
} else {
    echo json_encode(["success" => false, "message" => "Invalid credentials"]);
}
?>