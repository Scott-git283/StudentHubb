package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.AdminUserData

class ManageUsersAdapter(
    private val userList: List<AdminUserData>,
    private val onEditClicked: (AdminUserData) -> Unit,
    private val onDeleteClicked: (AdminUserData) -> Unit
) : RecyclerView.Adapter<ManageUsersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = userList[position]
        holder.bind(userData, onEditClicked, onDeleteClicked)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.user_name_textview)
        private val emailTextView: TextView = itemView.findViewById(R.id.user_email_textview)
        private val roleTextView: TextView = itemView.findViewById(R.id.user_role_textview)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(
            userData: AdminUserData,
            onEditClicked: (AdminUserData) -> Unit,
            onDeleteClicked: (AdminUserData) -> Unit
        ) {
            nameTextView.text = "${userData.firstName} ${userData.lastName}"
            emailTextView.text = userData.email
            roleTextView.text = "Role: ${userData.role}"

            editButton.setOnClickListener { onEditClicked(userData) }
            deleteButton.setOnClickListener { onDeleteClicked(userData) }
        }
    }
}
