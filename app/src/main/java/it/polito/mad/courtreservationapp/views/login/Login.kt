package it.polito.mad.courtreservationapp.views.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import it.polito.mad.courtreservationapp.R
import it.polito.mad.courtreservationapp.db.RemoteDataSource
import it.polito.mad.courtreservationapp.views.MainActivity
import it.polito.mad.courtreservationapp.views.ratings.ui.theme.CourtReservationAppTheme


class Login : ComponentActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val reqCode:Int=123
    private val firebaseAuth= FirebaseAuth.getInstance()
    private val fireDB = RemoteDataSource.instance


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure Google Sign In inside onCreate mentod

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        Log.i("Login","$gso")
        // getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)
        Log.i("Login","$mGoogleSignInClient")



        setContent {
            CourtReservationAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    PageLayout()
                }
            }
        }
    }

    // signInGoogle() function
    private fun signInGoogle(){

        val signInIntent: Intent =mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,reqCode)
    }
    // onActivityResult() function : this is where we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==reqCode){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }
    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUIGoogle(account)
            }
        } catch (e:ApiException){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun updateUIGoogle(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                Log.i("UpdateUi", "${account.account?.name}")
                Log.i("UpdateUi", "${account.email}")
                Log.i("UpdateUi", "${account.displayName}")
                SavedPreference.setEmail(this,account.email.toString())
                SavedPreference.setUsername(this,account.displayName.toString())
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    private fun updateUI(account: FirebaseUser?){

        val user = Firebase.auth.currentUser
        user?.let {
            Log.i("UpdateUi", "${account?.email}")
            Log.i("UpdateUi", "${it.email}")
            Log.i("UpdateUi", "${it.displayName}")
            SavedPreference.setEmail(this,it.email.toString())
            SavedPreference.setUsername(this,it.displayName.toString())
            SavedPreference.EMAIL = it.email.toString()
            SavedPreference.USERNAME = it.displayName.toString()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createAccount", "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                    val data = hashMapOf("username" to "user${(Math.random()*100000).toInt()}")
                    fireDB.collection("users").document(email).set(data, SetOptions.merge())
                    updateUI(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("createAccount", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createAccount", "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("createAccount", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    @Composable
    fun PageLayout() {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 50.dp),
                text = "Welcome",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_bold)),
                    fontSize = 50.sp,
                )
            )
            Login()
            LoginWith()
        }
    }


    @SuppressLint("NotConstructor")
    @Composable
    fun Login() {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        Column(modifier = Modifier.padding(top = 28.dp)) {
            Text(
                text = "Email",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_medium)),
                    fontSize = 30.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Email,
                    modifier = Modifier
                        .size(size = 30.dp)
                        .padding(1.dp),
                    tint = Color.Black,
                    contentDescription = "email"
                )
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(5.dp)),
                    value = email,
                    onValueChange = { newText ->
                        email = newText
                        println(email)
                        println(newText)

                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.red_button),
                        cursorColor = colorResource(id = R.color.red_button)
                    ),
                    label = { Text("Email") },
                    placeholder = { Text("Email") }
                )
            }
            Text(
                text = "Password",
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.red_hat_display_medium)),
                    fontSize = 30.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    modifier = Modifier
                        .size(size = 30.dp)
                        .padding(1.dp),
                    tint = Color.Black,
                    contentDescription = "password"
                )
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(5.dp)),
                    value = password,
                    onValueChange = { newText ->
                        password = newText
                        println(password)
                        println(newText)

                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = colorResource(id = R.color.red_button),
                        cursorColor = colorResource(id = R.color.red_button)
                    ),
                    label = { Text("Password") },
                    placeholder = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        // Please provide localized description for accessibility services
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    }
                )
            }
            Text(
                modifier = Modifier.padding(top = 28.dp),
                text = "Forget your password?",
                textAlign = TextAlign.End,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.roboto)),
                    fontSize = 18.sp
                )
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                shape = CircleShape,
                onClick = {
                    println("sign in")
                    signIn(email, password)
                }
            ) {

                Text("Login", textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.White)
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                shape = CircleShape,
                onClick = {
                    println("Sign up")
                    createAccount(email, password)
                }
            ) {

                Text("Sign up", textAlign = TextAlign.Center, fontSize = 12.sp, color = Color.White)
            }
        }
    }

    @Composable
    fun LoginWith(){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,

                ) {
                Button(
                    onClick = {
                        println("sign-in with google")
                        signInGoogle()
                    }
                ){
                    Icon(painterResource(id = R.drawable.wifi), "google")
                    Text(text = "Sign-in with Google")
                }
            }
        }

    }



}