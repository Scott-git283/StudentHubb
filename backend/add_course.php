<?php
header('Content-Type: application/json');
require_once 'db_config.php';

// App sends these fields
$name = $_POST['course_name'] ?? '';
$code = $_POST['course_code'] ?? '';
$dept_id = $_POST['department_id'] ?? '';
$inst_id = $_POST['instructor_id'] ?? '';

if (empty($name) || empty($code) || empty($dept_id) || empty($inst_id)) {
    echo json_encode(["success" => false, "message" => "All fields are required"]);
    exit;
}

try {
    // IMPORTANT: Check if your column is named instructor_uid or instructor_id
    // If your previous logs are correct, it should be instructor_uid
    $sql = "INSERT INTO courses (course_name, course_code, department_id, instructor_uid) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($sql);
    
    // Bind types: name(s), code(s), dept_id(i), inst_id(i)
    $stmt->bind_param("ssii", $name, $code, $dept_id, $inst_id);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "message" => "Course added successfully"]);
    } else {
        echo json_encode(["success" => false, "message" => "SQL Error: " . $conn->error]);
    }
} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Exception: " . $e->getMessage()]);
}

$conn->close();
?>