<?php
require_once 'db_config.php';
$faculty_id = $_GET['faculty_id']; // Using email/username

$sql = "SELECT a.*, c.course_name 
        FROM announcements a 
        JOIN courses c ON a.course_id = c.id 
        JOIN users u ON a.faculty_uid = u.uid 
        WHERE u.email = ? 
        ORDER BY a.created_at DESC";

$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $faculty_id);
$stmt->execute();
$result = $stmt->get_result();

$announcements = [];
while($row = $result->fetch_assoc()) {
    $announcements[] = $row;
}
echo json_encode($announcements);
?>