package com.example.dvidd.loginsecurity.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnFocusChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import android.support.v7.app.AlertDialog
import android.util.Log
import com.creativityapps.gmailbackgroundlibrary.BackgroundMail
import com.example.dvidd.loginsecurity.R
import com.google.android.gms.tasks.OnCompleteListener

class LoginActivity : AppCompatActivity() {

    @BindView(R.id.user_edit)
    lateinit var userEdit: EditText
    @BindView(R.id.pass_edit)
    lateinit var passEdit: EditText
    @BindView(R.id.prefix_text)
    lateinit var prefixText: TextView
    @BindView(R.id.clear_id_btn)
    lateinit var clearIdBtn: Button
    @BindView(R.id.clear_pass_btn)
    lateinit var clearPassBtn: Button

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)
        db = FirebaseFirestore.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = ContextCompat.getColor(this, R.color.transparent)
        }

        userEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (userEdit.text.isNotEmpty()) {
                    clearIdBtn.visibility = View.VISIBLE
                    prefixText.visibility = View.VISIBLE
                } else {
                    clearIdBtn.visibility = View.INVISIBLE
                    prefixText.visibility = View.GONE
                }
            }

        })

        passEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (passEdit.text.isNotEmpty()) {
                    clearPassBtn.visibility = View.VISIBLE
                } else {
                    clearPassBtn.visibility = View.INVISIBLE
                }
            }

        })
        passEdit.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    @OnFocusChange(R.id.user_edit)
    fun onUserEditFocusChanged(b: Boolean) {
        if (userEdit.text.isNotEmpty()) {
            prefixText.visibility = View.VISIBLE
        } else {
            prefixText.visibility = View.GONE
        }
    }

    @OnClick(R.id.clear_id_btn)
    fun actionDeleteId() {
        userEdit.setText("")
    }

    @OnClick(R.id.clear_pass_btn)
    fun actionDeletePass() {
        passEdit.setText("")
    }

    @OnClick(R.id.recover_text)
    fun actionRecover() {
        if (userEdit.text.isEmpty()) {
            showDialog("Campo usuario vacio // Empty user field")
        } else {
            db.collection("data-access")
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                var list: MutableList<String> = document.data["access"] as MutableList<String>
                                if ("i" + userEdit.text.toString() == list[0]) {

                                    /** Allows to send an email with the password to the user's email **/
                                    BackgroundMail.newBuilder(this)
                                            .withUsername("prueba-mail@gmail.com") // Your email
                                            .withPassword("123456") // Your password
                                            .withMailto(list[2]) // email in user
                                            .withType(BackgroundMail.TYPE_PLAIN)
                                            .withSubject("Su contra es")
                                            .withBody(list[1])
                                            .withOnSuccessCallback(object : BackgroundMail.OnSuccessCallback {
                                                override fun onSuccess() {

                                                    showDialog("ENVIADO MAIL CON SU CONTRA")
                                                }
                                            }).send()
                                    break
                                }
                            }
                        } else {
                            Log.e("dt error", "Error getting documents.", task.exception)
                        }
                    })
        }
    }

    @OnClick(R.id.login_btn)
    fun actionLogin() {
        if (userEdit.text.isEmpty()) {
            showDialog("Campo usuario vacio // Empty user field")
        } else if (passEdit.text.isEmpty()) {
            showDialog("Campo contraseña vacio // Empty password field")
        } else {
            db.collection("data-access")
                    .get()
                    .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                        if (task.isSuccessful) {
                            for (document in task.result) {
                                var list: MutableList<String> = document.data["access"] as MutableList<String>
                                Log.e("DATA", document.id + " => " + document.data["access"])

                                if ("i" + userEdit.text.toString() == list[0] && passEdit.text.toString() == list[1]) {
                                    startActivity(Intent(
                                            this,
                                            HomeActivity::class.java
                                    ))
                                    break
                                }
                            }
                        } else {
                            Log.e("Exception", "Error getting documents.", task.exception)
                        }
                    })
        }
    }

    fun showDialog(message: String) {
        var alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                    }
                })
        alertDialog.show()
    }
}