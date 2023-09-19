package com.timi.seulseul.presentation.apitest

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.timi.seulseul.R
import com.timi.seulseul.data.model.User
import com.timi.seulseul.databinding.ActivityApitestBinding
import com.timi.seulseul.databinding.ActivityMainBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ApiTestActivity : AppCompatActivity() {

    // Inject ViewModel using Dagger-Hilt.
    private val viewModel by viewModels<ApiTestViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use DataBindingUtil to set the layout.
        val binding: ActivityApitestBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_apitest)

        binding.lifecycleOwner = this

        binding.viewmodel = viewModel

        //POST
        binding.postButton.setOnClickListener {
            viewModel.postUser(
                userId = binding.postUserIdEditText.text.toString(),
                id = binding.postIdEditText.text.toString(),
                title = binding.postTitleEditText.text.toString(),
                body = binding.postBodyEditText.text.toString()
            )
        }

        //GET
        binding.getButton.setOnClickListener {
            val inputId = binding.getIdEditText.text.toString()
            if (inputId.isNotBlank()) {
                viewModel.getUser(inputId)
            } else {
            }
        }

        //PATCH
        binding.patchButton.setOnClickListener {
            viewModel.patchUser(
                userId = binding.patchUserIdEditText.text.toString(),
                id = binding.patchIdEditText.text.toString(),
                title = binding.patchTitleEditText.text.toString(),
                body = binding.patchBodyEditText.text.toString()
            )
        }

        //DELETE
        binding.deleteButton.setOnClickListener {
            val inputId = binding.deleteIdEditText.text.toString()
            if (inputId.isNotBlank()) {
                viewModel.deleteUser(inputId)
            } else {

            }
        }
    }
}