package com.timi.seulseul.presentation.apitest

import android.os.Bundle
import androidx.activity.viewModels
import com.timi.seulseul.R
import com.timi.seulseul.data.model.User
import com.timi.seulseul.databinding.ActivityApitestBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApiTestActivity : BaseActivity<ActivityApitestBinding>(R.layout.activity_apitest) {

    private val viewModel: ApiTestViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest)

        //DELETE
        binding.deleteButton.setOnClickListener {
            viewModel.deleteUser(
                id = viewModel.idDelete.value
            )
        }

        //POST
        binding.postButton.setOnClickListener {
            viewModel.postUser(
                User(
                    userId = viewModel.userIdPost.value,
                    id = viewModel.idPost.value,
                    title = viewModel.titlePost.value,
                    body = viewModel.bodyPost.value
                )
            )
        }

        //PATCH
        binding.patchButton.setOnClickListener {
            viewModel.patchUser(
                User(
                    userId = viewModel.userIdPatch.value,
                    id = viewModel.idPatch.value,
                    title = viewModel.titlePatch.value,
                    body = viewModel.bodyPatch.value
                )
            )

        }

        binding.getButton.setOnClickListener {
            viewModel.getUsers()
        }

        // Observe the selectedUser LiveData.
        viewModel.selectedUser.observe(this) { user ->
            // Update the TextViews with the user's information.
            binding.getUserId.text = user.userId.toString()
            binding.getId.text = user.id.toString()
            binding.getTitle.text = user.title
            binding.getBody.text = user.body
        }
    }

}