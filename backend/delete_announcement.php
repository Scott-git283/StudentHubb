<?php
require_once 'db_config.php';
$id = $_POST['id'];
$sql = "DELETE FROM announcements WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $id);
echo json_encode(["success" => $stmt->execute()]);
?>