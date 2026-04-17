<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$email = $_POST['email'];
$password = $_POST['password'];
$first_name = $_POST['first_name'];
$last_name = $_POST['last_name'];
$role = $_POST['role'];
$dept_id = $_POST['department_id'] ?? null;

$conn->begin_transaction();

try {
    // 1. Insert Login Credentials into users table
    $sql = "INSERT INTO users (email, password, role) VALUES (?, ?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("sss", $email, $password, $role);
    $stmt->execute();
    $new_uid = $conn->insert_id;

    // 2. Insert Profile Data into specialized table
    if ($role == 'Student') {
        $std_id = "S" . date("Y") . str_pad($new_uid, 3, "0", STR_PAD_LEFT);
        $sql_s = "INSERT INTO students (user_uid, first_name, last_name, student_id_number, department_id) VALUES (?, ?, ?, ?, ?)";
        $stmt_s = $conn->prepare($sql_s);
        // 5 placeholders, 5 variables: user_uid(i), first_name(s), last_name(s), std_id(s), dept_id(i)
        $stmt_s->bind_param("isssi", $new_uid, $first_name, $last_name, $std_id, $dept_id);
        $stmt_s->execute();
    } elseif ($role == 'Faculty') {
        $sql_f = "INSERT INTO faculty (user_uid, first_name, last_name, office_number, department_id) VALUES (?, ?, ?, 'TBD', ?)";
        $stmt_f = $conn->prepare($sql_f);
        // 4 placeholders, 4 variables: user_uid(i), first_name(s), last_name(s), dept_id(i)
        $stmt_f->bind_param("issi", $new_uid, $first_name, $last_name, $dept_id);
        $stmt_f->execute();
    }

    $conn->commit();
    echo json_encode(["success" => true, "message" => "User added successfully"]);

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "message" => "Error: " . $e->getMessage()]);
}
?>