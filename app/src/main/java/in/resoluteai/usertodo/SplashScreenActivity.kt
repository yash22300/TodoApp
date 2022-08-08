package `in`.resoluteai.usertodo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        var first = findViewById(R.id.line1) as View
        var second = findViewById(R.id.line2) as View
        var third = findViewById(R.id.line3) as View
        var fourth = findViewById(R.id.line4) as View
        var fifth = findViewById(R.id.line5) as View


        //Animation calls

        //Animation calls
        var topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)

        //set animation position according to us
        first.animation = topAnimation
        second.animation = topAnimation
        third.animation = topAnimation
        fourth.animation = topAnimation
        fifth.animation = topAnimation

        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
                finish()
            },4000
        )
    }
}