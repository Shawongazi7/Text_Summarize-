package com.example.Text_Summarizer.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.services.TextEntity
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveSummaryActivity(private val summary: String, private val originalText: String) : BottomSheetDialogFragment() {

    private val textViewModel: TextViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_result_model_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleInputLayout: TextInputLayout = view.findViewById(R.id.inputSummeryName)
        val titleInput: TextInputEditText = titleInputLayout.editText as TextInputEditText
        val descriptionInput: EditText = view.findViewById(R.id.editTextTextMultiLine)
        val saveButton: Button = view.findViewById(R.id.save_summery_btn)
        val cancelButton: Button = view.findViewById(R.id.cancel_save_btn)

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val textEntity = TextEntity(
                id = 0, // Assuming auto-generated ID
                title = title,
                date = date,
                description = description,
                summary = summary,
                originalText = originalText
            )
            textViewModel.insertText(textEntity)
            Toast.makeText(requireContext(), "Summary saved", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}