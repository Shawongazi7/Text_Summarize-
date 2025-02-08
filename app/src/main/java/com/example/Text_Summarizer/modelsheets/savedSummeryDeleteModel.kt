package com.example.Text_Summarizer.modelsheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import com.example.Text_Summarizer.R
import com.example.Text_Summarizer.modules.SavedScreenActivity
import com.example.Text_Summarizer.services.TextEntity
import com.example.Text_Summarizer.services.TextViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class savedSummeryDeleteModel(private val textEntity: TextEntity) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "DeleteConformationModel"
    }

    private val textViewModel: TextViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_saved_summery_delete_model, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deleteButton: Button = view.findViewById(R.id.delete_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_delete_btn)

        deleteButton.setOnClickListener {
            textViewModel.deleteText(textEntity)
            dismiss()
            val intent = Intent(requireContext(), SavedScreenActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}