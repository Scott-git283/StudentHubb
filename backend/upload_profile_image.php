<?php
header('Content-Type: application/json');
require_once 'db_config.php';

$email = $_POST['username'] ?? '';
$target_dir = "uploads/";

if (!file_exists($target_dir)) {
    mkdir($target_dir, 0777, true);
}

if (!isset($_FILES["image"])) {
    echo json_encode(["success" => false, "message" => "No image uploaded"]);
    exit;
}

$file_name = time() . "_" . basename($_FILES["image"]["name"]);
$target_file = $target_dir . $file_name;

if (move_uploaded_file($_FILES["image"]["tmp_name"], $target_file)) {
    // 1. Get user role and UID
    $stmt = $conn->prepare("SELECT uid, role FROM users WHERE email = ?");
    $stmt->bind_param("s", $email);
    $stmt->execute();
    $user = $stmt->get_result()->fetch_assoc();

    if ($user) {
        $uid = $user['uid'];
        $role = $user['role'];
        
        // 2. Determine target table
        $table = "";
        if ($role == 'Student') $table = "students";
        elseif ($role == 'Faculty') $table = "faculty";
        elseif ($role == 'Admin') $table = "admins";

        if ($table != "") {
            // Update the correct table based on role
            $stmt_update = $conn->prepare("UPDATE $table SET profile_image_url = ? WHERE user_uid = ?");
            $stmt_update->bind_param("si", $target_file, $uid);
            
            if ($stmt_update->execute()) {
                echo json_encode(["success" => true, "message" => "Profile image updated"]);
            } else {
                echo json_encode(["success" => false, "message" => "Database update failed"]);
            }
        } else {
            echo json_encode(["success" => false, "message" => "Invalid role"]);
        }
    } else {
        echo json_encode(["success" => false, "message" => "User not found"]);
    }
} else {
    echo json_encode(["success" => false, "message" => "File upload failed"]);
}
?>