package hn.edu.ujcv.baleadashermanas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File

class PDF : AppCompatActivity() {
    var path = ""
    var file = File("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        inicializar()
        pdfView.fromFile(file).load()
    }

    private fun inicializar(){
        var intent = intent
        path = intent.getSerializableExtra("path") as String
        file = File(path)
    }

    companion object {
        private const val PDF_SELECTION_CODE = 99
    }

}