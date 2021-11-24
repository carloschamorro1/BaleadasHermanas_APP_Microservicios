package hn.edu.ujcv.baleadashermanas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_acceso.*

class Acceso : AppCompatActivity() {
    var click = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceso)
        btn_ingresar.setOnClickListener {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        }
    }


}