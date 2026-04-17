<?php
require_once 'db_config.php';
$email = $_GET['student_id'];

$sql = "SELECT r.*, c.course_name FROM results r 
        JOIN courses c ON r.course_id = c.id 
        JOIN users u ON r.student_uid = u.uid 
        WHERE u.email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();
$rows = [];
while($row = $result->fetch_assoc()) { $rows[] = $row; }
echo json_encode($rows);
?>