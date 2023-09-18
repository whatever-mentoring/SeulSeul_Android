package com.timi.seulseul.presentation.apitest

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.timi.seulseul.R
import com.timi.seulseul.data.model.User
import com.timi.seulseul.databinding.ActivityApitestBinding
import com.timi.seulseul.presentation.common.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiTestActivity : BaseActivity<ActivityApitestBinding>(R.layout.activity_apitest) {

    private lateinit var apiTestViewModel: ApiTestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apitest)


        apiTestViewModel = ViewModelProvider(this).get(ApiTestViewModel::class.java)

        // ViewModel 초기화 및 LiveData 관찰 등 필요한 설정 진행...


        // DELETE
        binding.deleteButton.setOnClickListener {
            val idToDelete = binding.deleteEdittext.text.toString()
            apiTestViewModel.deleteUser(idToDelete)
        }

        // POST
        binding.postButton.setOnClickListener {

            val userPost = User(
                userId = binding.postEdittextUserid.text.toString(),
                id = binding.postEdittextId.text.toString(),
                title = binding.postEdittextTitle.text.toString(),
                body = binding.postEdittextBody.text.toString()
            )
            apiTestViewModel.postUser(userPost)
        }


        // PATCH
        binding.patchButton.setOnClickListener {

            val userPatch =
                User(
                    userId = binding.patchEdittextUserid.text.toString(),
                    id = binding.patchEdittextId.text.toString(),
                    title = binding.patchEdittextTitle.text.toString(),
                    body = binding.patchEdittextBody.text.toString()
                )
            apiTestViewModel.patchUser(userPatch)
        }

        // GET
        binding.getButton.setOnClickListener {
            apiTestViewModel.getUsers()

            lifecycleScope.launchWhenStarted {
                // LiveData나 StateFlow 등으로 사용자 목록을 관찰하여 변화가 있을 때마다 TextView 업데이트
                apiTestViewModel.userStateFlow.collect { users ->
                    // 예시로 첫 번째 사용자의 정보만 표시합니다.
                    val firstUser = users.firstOrNull()
                    if (firstUser != null) {
                        withContext(Dispatchers.Main) {
                            binding.getUserid.text = firstUser.userId.toString()
                            binding.getId.text = firstUser.id.toString()
                            binding.getTitle.text = firstUser.title
                            binding.getBody.text = firstUser.body
                        }
                    }
                }
            }
        }


    }


}