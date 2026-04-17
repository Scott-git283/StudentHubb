<?php
require_once 'db_config.php';
$student_id = $_POST['student_id'];
$course_id = $_POST['course_id'];
$assessment = $_POST['assessment_name'];
$obtained = $_POST['marks_obtained'];
$total = $_POST['total_marks'];
$grade = $_POST['grade'];

$sql = "INSERT INTO results (student_uid, course_id, assessment_name, marks_obtained, total_marks, grade) 
        VALUES (?, ?, ?, ?, ?, ?)";
$stmt = $conn->prepare($sql);
$stmt->bind_param("iisdds", $student_id, $course_id, $assessment, $obtained, $total, $grade);

echo json_encode(["success" => $stmt->execute()]);
?>