package it.polito.mad.courtreservationapp.views.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

    override fun onStart() {
        super.onStart()
        if(GoogleSignIn.getLastSignedInAccount(this)!=null){
            startMain(GoogleSignIn.getLastSignedInAccount(this)?.email.toString(),
                GoogleSignIn.getLastSignedInAccount(this)?.displayName.toString()
            )

        }
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            startMain(currentUser.email.toString(), currentUser.displayName.toString())
        }
    }

    @Composable
    fun PageLayout() {
        val showPopup = remember {mutableStateOf(false)}

        if(showPopup.value){
            RegistrationForm(showPopup)
        }else{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 50.dp),
                    text = "Welcome back!",
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.red_hat_display_bold)),
                        fontSize = 40.sp,
                    )
                )
                Login(showPopup)
                LoginWith()
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun RegistrationForm(showPopup: MutableState<Boolean>) {
        var email by rememberSaveable { mutableStateOf("") }
        var firstname by rememberSaveable { mutableStateOf("") }
        var lastname by rememberSaveable { mutableStateOf("") }
        var username by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var confirmPassword by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        var passwordVisible2 by rememberSaveable { mutableStateOf(false) }
        var firstNameError by remember { mutableStateOf(false) }
        var lastNameError by remember { mutableStateOf(false) }
        var emailError by remember { mutableStateOf(false) }
        var usernameError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var passwordErrorConfirm by remember { mutableStateOf(false) }

        val focusRequesterFirstName = remember { FocusRequester() }
        val focusRequesterLastName = remember { FocusRequester() }
        val focusRequesterEmail = remember { FocusRequester() }
        val focusRequesterUsername = remember { FocusRequester() }
        val focusRequesterPassword = remember { FocusRequester() }
        val focusRequesterConfirmPassword = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        Dialog(
            onDismissRequest = { showPopup.value = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(modifier = Modifier.fillMaxSize(), shape = RectangleShape) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxSize()
                ) {
                    Text(
                        modifier = Modifier.padding(start = 30.dp, end = 30.dp, top = 50.dp, bottom = 15.dp),
                        text = "Registration",
                        style = TextStyle(
                            fontFamily = FontFamily(Font(R.font.red_hat_display_bold)),
                            fontSize = 40.sp,
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly

                    ) {
                        OutlinedTextField(
                            value = firstname,
                            onValueChange = { newText ->
                                firstname = newText
                                println(firstname)
                                println(newText)
                                firstNameError = false  // clear the error when the field is updated
                            },
                            isError = firstNameError,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .focusRequester(focusRequesterFirstName)
                            ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                focusRequesterLastName.requestFocus()
                            }),
                            label = {Text("First Name")},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                                cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                                leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                                focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                            )
                        )
                        OutlinedTextField(
                            value = lastname,
                            onValueChange = { newText ->
                                lastname = newText
                                println(lastname)
                                lastNameError = false  // clear the error when the field is updated
                            },
                            isError = lastNameError,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequesterLastName)
                            ,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = {
                                focusRequesterUsername.requestFocus()
                            }),
                            label = {Text("Last Name")},
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                                cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                                leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                                focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                            )
                        )

                    }
                    if (firstNameError && !lastNameError) {
                        Text(text = "First name cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }
                    if (lastNameError && !firstNameError) {
                        Text(text = "Last name cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }
                    if (lastNameError && firstNameError){
                        Text(text = "First name and last name cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }
                    OutlinedTextField(
                        value = username,
                        onValueChange = { newText ->
                            username = newText
                            println(username)
                            println(newText)
                            usernameError = false  // clear the error when the field is updated
                        },
                        isError = usernameError,
                        label = {Text("Username")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 5.dp)
                            .focusRequester(focusRequesterUsername)
                        ,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterEmail.requestFocus()
                        }),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                            cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                            leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                            focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                        )
                    )
                    if (usernameError) {
                        Text(text = "Username cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { newText ->
                            email = newText
                            println(email)
                            println(newText)
                            emailError = false
                        },
                        isError = emailError,
                        label = {Text("Email")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 5.dp)
                            .focusRequester(focusRequesterEmail)
                        ,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterPassword.requestFocus()
                        }),

                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                            cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                            leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                            focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                        )
                    )
                    if (firstNameError) {
                        Text(text = "Email cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { newText ->
                            password = newText
                            passwordError = false
                        },
                        isError = passwordError,
                        label = {Text("Password")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 5.dp)
                            .focusRequester(focusRequesterPassword)
                        ,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusRequesterConfirmPassword.requestFocus()
                        }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                            cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                            leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                            focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                        ),
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
                    if (passwordError) {
                        Text(text = "Password cannot be empty", color = Color.Red, modifier = Modifier.padding(start = 30.dp))
                    }

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { newText ->
                            confirmPassword = newText
                            passwordErrorConfirm = false
                        },
                        isError = passwordErrorConfirm,
                        label = {Text("Confirm password")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp, vertical = 5.dp)
                            .focusRequester(focusRequesterConfirmPassword)
                        ,
                        visualTransformation = if (passwordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboardController?.hide()
                        }),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                            cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                            leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                            focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                        ),
                        trailingIcon = {
                            val image = if (passwordVisible2)
                                Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff

                            // Please provide localized description for accessibility services
                            val description = if (passwordVisible2) "Hide password" else "Show password"

                            IconButton(onClick = { passwordVisible2 = !passwordVisible2 }) {
                                Icon(imageVector = image, description)
                            }
                        }
                    )
                    if(passwordErrorConfirm){
                        Text(text = "Passwords do not match", color = Color.Red, modifier = Modifier.padding(start = 30.dp))

                    }
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 30.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            if(password!=confirmPassword){
                                passwordErrorConfirm=true
                            }
                            else if(firstname.isEmpty()){
                                firstNameError=true
                            }else if (lastname.isEmpty()){
                                lastNameError=true
                            }else if (email.isEmpty()){
                                emailError=true
                            }else if (password.isEmpty()){
                                passwordError=true
                            }
                            else if(!passwordError && !emailError && !firstNameError && !lastNameError && !usernameError && !passwordErrorConfirm){
                                signUp(firstname, lastname, username, email, password)
                            }
                        }
                    ) {

                        Text("REGISTER NOW", textAlign = TextAlign.Center, fontSize = 20.sp, color = Color.White, fontFamily = FontFamily(Font(R.font.roboto_bold)))
                    }
                    Row( modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,

                        ) {
                        Text(
                            text = "Already have an account? ",
                            textAlign = TextAlign.End,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.roboto_medium)),
                                fontSize = 18.sp
                            ),
                        )
                        Text(
                            text = "Sign In",
                            textAlign = TextAlign.End,
                            style = TextStyle(
                                fontFamily = FontFamily(Font(R.font.roboto_medium)),
                                fontSize = 18.sp
                            ),
                            color = colorResource(id = R.color.red_highlight),
                            modifier = Modifier.clickable {
                                showPopup.value = false
                            }
                        )
                    }

                }
            }
        }
    }

    @SuppressLint("NotConstructor")
    @Composable
    fun Login(showPopup: MutableState<Boolean>) {
        var email by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        Column(modifier = Modifier.padding(top = 28.dp)) {
            OutlinedTextField(
                value = email,
                onValueChange = { newText ->
                email = newText
                println(email)
                println(newText)
                },
                label = {Text("Enter your email")},
                leadingIcon = {Icon(Icons.Default.Email, contentDescription = "email")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                    cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                    leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                    focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                )
            )
            OutlinedTextField(
                value = password,
                onValueChange = { newText ->
                    password = newText
                },
                label = {Text("Enter your password")},
                leadingIcon = {Icon(Icons.Default.Password, contentDescription = "password")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, top = 10.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = colorResource(id = R.color.deep_blue),  // The border color when the text field is focused.
                    cursorColor = colorResource(id = R.color.deep_blue),  // The color of the cursor.
                    leadingIconColor = colorResource(id = R.color.deep_blue),  // The color of the leading icon.
                    focusedLabelColor = colorResource(id = R.color.deep_blue),  // The color of the label when the text field is focused.
                ),
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End) {
                Text(
                    text = "Forgot your password?",
                    textAlign = TextAlign.End,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.roboto_medium)),
                        fontSize = 18.sp
                    ),
                    color = colorResource(id = R.color.red_highlight)

                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 28.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red_button)),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    signIn(email, password)
                }
            ) {

                Text("LOGIN", textAlign = TextAlign.Center, fontSize = 20.sp, color = Color.White, fontFamily = FontFamily(Font(R.font.roboto_bold)))
            }
            Row( modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
                horizontalArrangement = Arrangement.Center,

                ) {
                Text(
                    text = "New user? ",
                    textAlign = TextAlign.End,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.roboto_medium)),
                        fontSize = 18.sp
                    ),
                )
                Text(
                    text = "Create an account",
                    textAlign = TextAlign.End,
                    style = TextStyle(
                        fontFamily = FontFamily(Font(R.font.roboto_medium)),
                        fontSize = 18.sp
                    ),
                    color = colorResource(id = R.color.red_highlight),
                    modifier = Modifier.clickable {
                        showPopup.value = true
                    }
                )
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
                GoogleButton(text = "Continue with Google", loadingText = "Logging in...", onClicked = { signInGoogle() })
            }
        }

    }

    @Composable
    fun GoogleButton(
        modifier: Modifier = Modifier,
        text: String = "Sign Up with Google",
        loadingText: String = "Creating Account...",
        icon: Int = R.drawable.ic_google_logo,
        shape: Shape = RoundedCornerShape(8.dp),
        borderColor: Color = Color.LightGray,
        backgroundColor: Color = MaterialTheme.colors.surface,
        onClicked: () -> Unit
    ) {
        var clicked by remember { mutableStateOf(false) }

        Surface(
            modifier = modifier.clickable { clicked = !clicked },
            shape = shape,
            border = BorderStroke(width = 1.dp, color = borderColor),
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "Google Button",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text =  text)
                if (clicked) {
                    onClicked()
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
            showToast(e.toString())
        }
    }

    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun updateUIGoogle(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                Log.i("GoogleLogin" ,"${task.result.additionalUserInfo?.isNewUser}")
                if(task.result.additionalUserInfo?.isNewUser == true){
                    val data = hashMapOf("username" to "user${(Math.random()*100000).toInt()}")
                    fireDB.collection("users").document(account.email!!).set(data, SetOptions.merge())
                }
                Log.i("UpdateUi", "${account.account?.name}")
                Log.i("UpdateUi", "${account.email}")
                Log.i("UpdateUi", "${account.displayName}")

                startMain(account.email.toString(), account.displayName.toString())
            }
        }
    }
    private fun updateUI(account: FirebaseUser?){

        val user = Firebase.auth.currentUser
        user?.let {
            Log.i("UpdateUi", "${account?.email}")
            Log.i("UpdateUi", "${it.email}")
            Log.i("UpdateUi", "${it.displayName}")

            startMain(it.email.toString(), it.displayName.toString())
        }
    }


    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            showToast("Email or Password null")
            updateUI(null)
        } else{
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
                        showToast("Authentication failed.")
                        updateUI(null)
                    }
                }
        }

        // [END create_user_with_email]
    }
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            showToast("Email or Password null")
            updateUI(null)
        } else {
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
                        showToast("Authentication failed.")
                        updateUI(null)
                    }
                }
            // [END sign_in_with_email]
        }
    }

    private fun signUp(firstname: String, lastname:String, username: String, email: String, password: String) {


        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("createAccount", "createUserWithEmail:success")
                    val user = firebaseAuth.currentUser
                    val data = hashMapOf("username" to username, "firstName" to firstname, "lastName" to lastname)
                    fireDB.collection("users").document(email).set(data, SetOptions.merge())
                    updateUI(user!!)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("createAccount", "createUserWithEmail:failure", task.exception)
                    showToast(task.exception.toString().split(": ")[1])
                    updateUI(null)
                }
            }

    }

    private fun startMain(email: String, username: String){
        SavedPreference.setEmail(this, email)
        SavedPreference.setUsername(this, username)
        SavedPreference.EMAIL = email
        SavedPreference.USERNAME = username
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT){
        Toast.makeText(
            baseContext,
            text,
            duration,
        ).show()
    }

}