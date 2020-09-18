package com.shourya.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_excercise.*
import kotlinx.android.synthetic.main.dialog_custom_back_confirmation.*
import java.util.*
import kotlin.collections.ArrayList

class ExcerciseActivity : AppCompatActivity() , TextToSpeech.OnInitListener {

    private var restTimer: CountDownTimer? = null
    private var restProgress =0
    private var restTimerDuration:Long =9

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseProgress =0

    private var exerciseDurationTimer : Long =30

    private var exerciseList : ArrayList<ExerciseModel>? =null
    private var currentExercisePosition = -1

    private var tts : TextToSpeech? =null
    private var player: MediaPlayer? =null

    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_excercise)

        setSupportActionBar(toolbar_exercise_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Navigate the activity on click on back button of action bar.
        toolbar_exercise_activity.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        tts = TextToSpeech(this,this)
        exerciseList = Constants.defaultExerciseList()

        llExerciseView.visibility = View.GONE
        setupRestView()

        setupExerciseStatusRecyclerView()

    }

    override fun onDestroy() {

        if(restTimer!=null )
        {
            restTimer!!.cancel()
            restProgress=0
        }

        if(exerciseTimer!=null )
        {
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }

        if(tts!=null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        if(player!=null) {
            player!!.stop()
        }

        super.onDestroy()
    }

    private fun setRestProgressBar() {
        progressBar.progress = restProgress
        restTimer = object :CountDownTimer(restTimerDuration*1000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                restProgress += 1
                progressBar.progress = 10 - restProgress
                if(restProgress==6)
                    speakOut("Get Ready for Upcoming Exercise. . . ${exerciseList!![currentExercisePosition].getName()}")
                tvTimer.text = (10 - restProgress).toString()
            }

            override fun onFinish() {

                llRestView.visibility = View.GONE
                llExerciseView.visibility =View.VISIBLE
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar() {
        exerciseProgressBar.progress = exerciseProgress
        exerciseTimer = object :CountDownTimer(exerciseDurationTimer*1000,1000) {
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress += 1
                speakOut(exerciseProgress.toString())
                exerciseProgressBar.progress = exerciseDurationTimer.toInt() - exerciseProgress
                tvExerciseTimer.text = (exerciseProgress).toString()
            }

            override fun onFinish() {
                if(currentExercisePosition<exerciseList?.size!! -1) {
                    llExerciseView.visibility =View.GONE
                    llRestView.visibility = View.VISIBLE
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    speakOut("Take a Rest")
                    setupRestView()
                }
                else{
                    finish()
                    val intent = Intent(this@ExcerciseActivity , FinishActivity::class.java)
                    startActivity(intent)
                }
            }
        }.start()
    }

    private fun setupRestView() {
        try {
            player = MediaPlayer.create(applicationContext,R.raw.press_start)
            player!!.isLooping =false
            player!!.start()

        }catch (e :Exception) {
            e.printStackTrace()
        }

        if(restTimer!=null) {
            restTimer!!.cancel()
            restProgress=0
        }
        currentExercisePosition++
        tv_exercise_name.text = exerciseList!![currentExercisePosition].getName()
        setRestProgressBar()
    }

    private fun setupExerciseView() {
        if(exerciseTimer!=null) {
            exerciseTimer!!.cancel()
            exerciseProgress=0
        }

        setExerciseProgressBar()

        iv_image.setImageResource(exerciseList!![currentExercisePosition].getImage())
        tvExerciseName.text = exerciseList!![currentExercisePosition].getName()
    }

    override fun onInit(status: Int) {
        if(status ==TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)

            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
            {
                Log.e("TTS","Languague specified not supported")
            }
        }else
        {
            Log.e("TTS","Initialization failed")
        }
    }

    private fun speakOut(text:String){
        tts!!.speak(text,TextToSpeech.QUEUE_FLUSH,null,"")
    }

    private fun setupExerciseStatusRecyclerView() {

        // Defining a layout manager to recycle view
        // Here we have used Linear Layout Manager with horizontal scroll.
        rvExerciseStatus.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // As the adapter expect the exercises list and context so initialize it passing it.
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!, this)

        // Adapter class is attached to recycler view
        rvExerciseStatus.adapter = exerciseAdapter
    }

    private fun customDialogForBackButton() {
        val customDialog = Dialog(this)
        customDialog.setContentView(R.layout.dialog_custom_back_confirmation)
       customDialog.tvYes.setOnClickListener {
           finish()
           customDialog.dismiss()
       }
        customDialog.tvNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }
}
