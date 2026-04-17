<?php
header('Content-Type: application/json');
require_once 'db_config.php';
error_reporting(0);

$student_id = $_GET['student_id'] ?? '';
$response = ["current_month_percentage" => 0.0, "previous_month_percentage" => 0.0, "average_percentage" => 0.0];

if (empty($student_id)) { echo json_encode($response); exit; }

if (!is_numeric($student_id)) {
    $res = $conn->query("SELECT uid FROM users WHERE email = '$student_id'");
    $u = $res->fetch_assoc();
    $uid = $u['uid'] ?? 0;
} else { $uid = (int)$student_id; }

function getPercentage($conn, $uid, $month = null, $year = null) {
    $sql = "SELECT SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) as present, COUNT(*) as total FROM attendance WHERE student_uid = ?";
    if ($month && $year) {
        $sql .= " AND MONTH(attendance_date) = ? AND YEAR(attendance_date) = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("iii", $uid, $month, $year);
    } else {
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $uid);
    }
    $stmt->execute();
    $row = $stmt->get_result()->fetch_assoc();
    return ($row && $row['total'] > 0) ? ($row['present'] / $row['total']) * 100 : 0;
}

$curM = (int)date('m'); $curY = (int)date('Y');
$prevM = (int)date('m', strtotime('first day of last month'));
$prevY = (int)date('Y', strtotime('first day of last month'));

$response["current_month_percentage"] = (float)round(getPercentage($conn, $uid, $curM, $curY), 1);
$response["previous_month_percentage"] = (float)round(getPercentage($conn, $uid, $prevM, $prevY), 1);
$response["average_percentage"] = (float)round(getPercentage($conn, $uid), 1);

echo json_encode($response);
?>