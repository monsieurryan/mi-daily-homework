package com.example.myday3application2

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myday3application2.ui.theme.MyDay3Application2Theme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 关键绑定
        // 减少过度绘制
        val searchEditText: EditText = findViewById(R.id.searchEditText)
        searchEditText.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // 初始化ListView
        val faqListView: ListView = findViewById(R.id.faqListView)
        val faqItems = arrayOf(
            "忘记账号了，该如何找回?",
            "忘记密码了，如何重置密码？",
            "手机号停用了，如何登录或换绑手机号？",
            "申诉不通过怎么办？"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, faqItems)
        faqListView.adapter = adapter
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyDay3Application2Theme {
        Greeting("Android")
    }
}
