package com.rahulraghuwanshi.dynamicfeaturedelivery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

class MainActivity : AppCompatActivity() {

    private var mySessionId = 0

    private lateinit var splitInstallManager: SplitInstallManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splitInstallManager = SplitInstallManagerFactory.create(this)
        val installedModules = splitInstallManager.installedModules
        Log.d("TAG", "onCreate: $installedModules")

        findViewById<Button>(R.id.btnOpenDynamiceModule).setOnClickListener {
            if (installedModules.contains("dynamicfeatureOne")){
                Log.d("TAG", "onCreate: Already Installed")
                openFirstModule()
            }else{
                Log.d("TAG", "onCreate: Need to install")
                downloadDynamicModule("dynamicfeatureOne")
            }
        }

    }

    private fun openFirstModule(){
        val intent = Intent()
        intent.setClassName("com.rahulraghuwanshi.dynamicfeaturedelivery", "com.rahulraghuwanshi.dynamicfeatureone.FirstModuleActivity")
        startActivity(intent)
    }

    private fun downloadDynamicModule(moduleName: String) {
        Log.d("TAG", "downloadDynamicModule: Module download start....")
        val request = SplitInstallRequest
            .newBuilder()
            .addModule(moduleName)
            .build()
        val listener =
            SplitInstallStateUpdatedListener { splitInstallSessionState ->
                if (splitInstallSessionState.sessionId() == mySessionId) {
                    when (splitInstallSessionState.status()) {
                        SplitInstallSessionStatus.INSTALLED -> {
                            Log.d("TAG", "Dynamic Module downloaded")
                          if (moduleName.equals("dynamicfeatureOne")){
                              openFirstModule()
                          }
                        }
                    }
                }
            }
        splitInstallManager.registerListener(listener)
        splitInstallManager.startInstall(request)
            .addOnFailureListener { e -> Log.d("TAG", "Exception: $e") }
            .addOnSuccessListener { sessionId -> mySessionId = sessionId }
    }
}