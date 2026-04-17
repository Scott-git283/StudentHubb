<?php
header('Content-Type: application/json');
require_once 'db_config.php';
error_reporting(0);

$student_id = $_GET['student_id'] ?? '';
$month = $_GET['month'] ?? '';
$year = $_GET['year'] ?? '';

if (!is_numeric($student_id)) {
    $res = $conn->query("SELECT uid FROM users WHERE email = '$student_id'");
    $u = $res->fetch_assoc();
    $student_id = $u['uid'] ?? 0;
}

$sql = "SELECT a.attendance_date, a.status, c.course_name 
        FROM attendance a 
        JOIN courses c ON a.course_id = c.id 
        WHERE a.student_uid = ?";

if (!empty($month) && !empty($year)) {
    $sql .= " AND MONTH(a.attendance_date) = $month AND YEAR(a.attendance_date) = $year";
}
$sql .= " ORDER BY a.attendance_date DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $student_id);
$stmt->execute();
$result = $stmt->get_result();

$attendance = [];
while ($row = $result->fetch_assoc()) { $attendance[] = $row; }
echo json_encode($attendance);
?>