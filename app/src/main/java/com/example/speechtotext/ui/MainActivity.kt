package com.example.speechtotext.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.speechtotext.R
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer

    private lateinit var englishGermanTranslator: Translator
    private lateinit var englishSpanishTranslator: Translator

    private lateinit var englishText: TextView
    private lateinit var germanText: TextView
    private lateinit var spanishText: TextView

    private val recordAudioRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        val micButton = findViewById<ImageView>(R.id.micButton)
        englishText = findViewById(R.id.recognizedEnglishText)
        germanText = findViewById(R.id.recognizedGermanText)
        spanishText = findViewById(R.id.recognizedSpanishText)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkPermission()
        }

        // Create an English-German translator:

        val germanOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.GERMAN)
            .build()


        // Create an English-Spanish translator:
        val spanishOptions = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()

        englishGermanTranslator = Translation.getClient(germanOptions)

        englishSpanishTranslator = Translation.getClient(spanishOptions)

        englishGermanTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { _ ->
            }

        englishSpanishTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { _ ->
            }


        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(bundle: Bundle?) {
                micButton.setImageResource(R.drawable.ic_mic_hollow)
                val data = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                data?.get(0)?.let { input ->
                    performLanguageTranslations(input)
                }

            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })


        micButton.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                speechRecognizer.stopListening()
            }
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                micButton.setImageResource(R.drawable.ic_mic_filled)
                Toast.makeText(this, getString(R.string.start_speaking), Toast.LENGTH_SHORT).show()
                speechRecognizer.startListening(speechRecognizerIntent)
            }
            false
        }


    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                recordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == recordAudioRequestCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
            else {
                checkPermission()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    private fun performLanguageTranslations(input: String) {

        englishText.text = input

        englishGermanTranslator.translate(input)
            .addOnSuccessListener { translatedText ->
                // Translation successful.
                germanText.text = translatedText
            }
            .addOnFailureListener { _ ->
                // Error.
                // ...
            }
        englishSpanishTranslator.translate(input)
            .addOnSuccessListener { translatedText ->
                // Translation successful.
                spanishText.text = translatedText
            }
            .addOnFailureListener { _ ->
                // Error.
                // ...
            }
    }

}