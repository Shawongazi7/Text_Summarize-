package com.example.Text_Summarizer.modelsheets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.viewModels
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.modules.ViewSavedSummmeryScreen
import com.example.Text_Summarizer.services.TextEntity
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class editSummeryModel(private val textEntity: TextEntity) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "editSummeryModel"
    }

    private val textViewModel: TextViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_edit_summery_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleInputLayout: TextInputLayout = view.findViewById(R.id.inputSummeryName)
        val descriptionInputLayout: TextInputLayout = view.findViewById(R.id.inputDescription)
        val saveButton: Button = view.findViewById(R.id.save_button)
        val cancelButton: Button = view.findViewById(R.id.summery_edit_cancel_btn)

        val titleEditText = titleInputLayout.editText
        val descriptionEditText = descriptionInputLayout.editText

//set the original title and description
        titleEditText?.setText(textEntity.title)
        descriptionEditText?.setText(textEntity.description)
        Log.d("editSummeryModel", "Original description: ${textEntity.description}")

        saveButton.setOnClickListener {
            val title = titleEditText?.text.toString()
            val description = descriptionEditText?.text.toString()
            val id: Long = textEntity.id

            textViewModel.updateTitle(id, title)
            textViewModel.updateDescription(id, description)
            dismiss()
            val intent = Intent(requireContext(), ViewSavedSummmeryScreen::class.java)
            intent.putExtra("TEXT_ID", textEntity.id)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}