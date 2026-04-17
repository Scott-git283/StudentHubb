package com.example.studenth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studenth.model.DepartmentData

class ManageDepartmentsAdapter(
    private val departments: List<DepartmentData>,
    private val onEditClicked: (DepartmentData) -> Unit,
    private val onDeleteClicked: (DepartmentData) -> Unit
) : RecyclerView.Adapter<ManageDepartmentsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_department, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(departments[position], onEditClicked, onDeleteClicked)
    }

    override fun getItemCount() = departments.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.department_name_textview)
        private val editButton: ImageButton = itemView.findViewById(R.id.edit_button)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(department: DepartmentData, onEdit: (DepartmentData) -> Unit, onDelete: (DepartmentData) -> Unit) {
            nameTextView.text = department.name
            editButton.setOnClickListener { onEdit(department) }
            deleteButton.setOnClickListener { onDelete(department) }
        }
    }
}
