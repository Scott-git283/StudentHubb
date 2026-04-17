<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$student_id = $_POST['student_id'] ?? '';
$course_id = $_POST['course_id'] ?? '';
$date = $_POST['date'] ?? '';
$status = $_POST['status'] ?? '';

if (empty($student_id) || empty($course_id) || empty($date) || empty($status)) {
    echo json_encode(["success" => false, "message" => "Missing data"]);
    exit;
}

// 1. Check if record already exists for this student, course, and date
$sql_check = "SELECT id FROM attendance WHERE student_uid = ? AND course_id = ? AND attendance_date = ?";$stmt_check = $conn->prepare($sql_check);
$stmt_check->bind_param("iis", $student_id, $course_id, $date);
$stmt_check->execute();
$res_check = $stmt_check->get_result();

if ($res_check->num_rows > 0) {
    // 2. Update existing record
    $sql_upd = "UPDATE attendance SET status = ? WHERE student_id = ? AND course_id = ? AND attendance_date = ?";
    $stmt_upd = $conn->prepare($sql_upd);
    $stmt_upd->bind_param("siis", $status, $student_id, $course_id, $date);
    if ($stmt_upd->execute()) {
        echo json_encode(["success" => true, "message" => "Attendance updated"]);
    } else {
        echo json_encode(["success" => false, "message" => "Update failed"]);
    }
} else {
    // 3. Insert new record
    $sql_ins = "INSERT INTO attendance (student_uid, course_id, attendance_date, status) VALUES (?, ?, ?, ?)";
    $stmt_ins = $conn->prepare($sql_ins);
    $stmt_ins->bind_param("iiss", $student_id, $course_id, $date, $status);
    if ($stmt_ins->execute()) {
        echo json_encode(["success" => true, "message" => "Attendance saved"]);
    } else {
        echo json_encode(["success" => false, "message" => "Save failed"]);
    }
}
?>


