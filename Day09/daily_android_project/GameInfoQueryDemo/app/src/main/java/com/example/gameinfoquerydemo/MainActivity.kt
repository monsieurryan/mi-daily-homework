package com.example.gameinfoquerydemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.gameinfoquerydemo.databinding.ActivityMainBinding
import com.example.gameinfoquerydemo.viewmodel.GameViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 设置Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.gameInfo.observe(this) { gameInfo ->
            if (gameInfo != null) {
                binding.gameInfoCard.visibility = View.VISIBLE
                binding.placeholderCard.visibility = View.GONE
                binding.errorText.visibility = View.GONE
                binding.gameNameText.text = "游戏名称：${gameInfo.gameName}"
                binding.gameBriefText.text = "游戏简介：${gameInfo.brief}"
                binding.gameScoreText.text = "评分：${gameInfo.score}"
                binding.gamePlayNumText.text = "下载量：${gameInfo.playNumFormat}"
                binding.gameVersionText.text = "版本号：${gameInfo.versionName}"
                binding.gameTagsText.text = "标签：${gameInfo.tags}"
                binding.gameIntroductionText.text = "详细介绍：${gameInfo.introduction}"
                
                // 加载游戏图标
                Glide.with(this)
                    .load(gameInfo.icon)
                    .into(binding.gameIconImage)
            } else {
                binding.gameInfoCard.visibility = View.GONE
                binding.placeholderCard.visibility = View.VISIBLE
            }
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                binding.gameInfoCard.visibility = View.GONE
                binding.placeholderCard.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = error
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            } else {
                binding.errorText.visibility = View.GONE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.searchButton.isEnabled = !isLoading
            binding.gameIdInput.isEnabled = !isLoading
        }
    }

    private fun setupClickListeners() {
        binding.searchButton.setOnClickListener {
            val gameId = binding.gameIdInput.text.toString()
            if (gameId.isNotEmpty()) {
                viewModel.fetchGameInfo(gameId)
            } else {
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = "请输入游戏ID"
                Toast.makeText(this, "请输入游戏ID", Toast.LENGTH_SHORT).show()
            }
        }

        binding.downloadButton.setOnClickListener {
            viewModel.gameInfo.value?.let { gameInfo ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(gameInfo.apkUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "无法打开下载链接", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}