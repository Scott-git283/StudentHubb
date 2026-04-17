<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// Disable raw errors to prevent breaking JSON
error_reporting(0);
ini_set('display_errors', 0);

$uid = $_POST['uid'] ?? '';
$first_name = $_POST['first_name'] ?? '';
$last_name = $_POST['last_name'] ?? '';
$role = $_POST['role'] ?? '';
$dept_id = $_POST['department_id'] ?? null;

// Convert to correct types
$uid = (int)$uid;
$dept_id = !empty($dept_id) ? (int)$dept_id : null;

if ($uid <= 0) {
    echo json_encode(["success" => false, "message" => "Invalid User UID: " . $_POST['uid']]);
    exit;
}

$conn->begin_transaction();

try {
    // 1. Update the role in the main users table
    $sql_u = "UPDATE users SET role = ? WHERE uid = ?";
    $stmt_u = $conn->prepare($sql_u);
    $stmt_u->bind_param("si", $role, $uid);
    $stmt_u->execute();

    // 2. Identify the correct profile table
    $table = ($role == 'Student') ? 'students' : 'faculty';
    $other_table = ($role == 'Student') ? 'faculty' : 'students';

    // 3. Remove from the "other" table if they switched roles (e.g. Faculty -> Student)
    $conn->query("DELETE FROM $other_table WHERE user_uid = $uid");

    // 4. Check if record exists in the target specialized table
    $check = $conn->query("SELECT id FROM $table WHERE user_uid = $uid");
    
    if ($check->num_rows > 0) {
        // UPDATE existing profile
        $sql_p = "UPDATE $table SET first_name = ?, last_name = ?, department_id = ? WHERE user_uid = ?";
        $stmt_p = $conn->prepare($sql_p);
        $stmt_p->bind_param("ssii", $first_name, $last_name, $dept_id, $uid);
    } else {
        // INSERT new profile if it didn't exist (or they switched roles)
        $std_id = ($role == 'Student') ? "S" . date("Y") . str_pad($uid, 3, "0", STR_PAD_LEFT) : null;
        
        if ($role == 'Student') {
            $sql_p = "INSERT INTO students (user_uid, first_name, last_name, student_id_number, department_id) VALUES (?, ?, ?, ?, ?)";
            $stmt_p = $conn->prepare($sql_p);
            $stmt_p->bind_param("isssi", $uid, $first_name, $last_name, $std_id, $dept_id);
        } else {
            $sql_p = "INSERT INTO faculty (user_uid, first_name, last_name, office_number, department_id) VALUES (?, ?, ?, 'TBD', ?)";
            $stmt_p = $conn->prepare($sql_p);
            $stmt_p->bind_param("isssi", $uid, $first_name, $last_name, $dept_id);
        }
    }
    
    $stmt_p->execute();

    // Verify if anything was actually changed
    if ($conn->affected_rows >= 0) {
        $conn->commit();
        echo json_encode(["success" => true, "message" => "User updated successfully"]);
    } else {
        throw new Exception("No changes were made to the database.");
    }

} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "message" => "Update failed: " . $e->getMessage()]);
}

$conn->close();
?>