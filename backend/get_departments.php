<?php
header('Content-Type: application/json');
require_once 'db_config.php';
$result = $conn->query("SELECT id, name, description FROM departments");
$depts = [];
while($row = $result->fetch_assoc()) { $depts[] = $row; }
echo json_encode($depts);
?>