package com.josieAlan.videohdplayerjosie.Activity

import androidx.databinding.DataBindingUtil

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager

import com.afollestad.materialdialogs.DialogBehavior
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.ModalDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.josieAlan.videohdplayerjosie.PrefData
import com.josieAlan.videohdplayerjosie.R
import com.josieAlan.videohdplayerjosie.adapter.RequestListAdapter
import com.josieAlan.videohdplayerjosie.databinding.ActivityWalletBinding
import com.josieAlan.videohdplayerjosie.model.CommonModel
import com.josieAlan.videohdplayerjosie.model.RequestModel
import com.josieAlan.videohdplayerjosie.model.Users
import com.josieAlan.videohdplayerjosie.utils.MyNumFormatter
import java.lang.Exception
import java.util.*

class WalletActivity : BaseActivity(), View.OnClickListener, RewardedVideoAdListener {

    private var prefData: PrefData? = null

    // [START declare_auth]
    private var mAuth: FirebaseAuth? = null
    private val coins = 0.0
    private val rupee = 0.0
    private val conversion = 0.0
    private var time = 0
    private var timer: Timer? = null
    private var binding: ActivityWalletBinding? = null
    // [END declare_auth]

    private var mCallbackManager: CallbackManager? = null
    private var mDatabase: DatabaseReference? = null
    private var commonModel: CommonModel? = null;
    private lateinit var users: Users;
    private val menu: MutableList<RequestModel> = mutableListOf()
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var mRewardedVideoAd: RewardedVideoAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet)
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        prefData = PrefData.getInstance()
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create()

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this

        facebookLogin()
        setData()
        setListner()
        linearLayoutManager = LinearLayoutManager(this)
        binding!!.recycleList.layoutManager = linearLayoutManager
        loadRewardedVideoAd();
        startTimer()
    }

    private fun startTimer() {
        timer = Timer();
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (time < 2) {
                    time++
                }else if (time == 2){
                    setVisibility();

                }
            }
        },6000,6000)

    }

    private fun setVisibility() {
        this.runOnUiThread(Runnable { if (commonModel!!.showreward == 1 && !binding!!.cardAppVideo.isVisible) {
            binding!!.cardAppVideo.visibility = View.VISIBLE;
        }  })


    }

    private fun loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-7014688784067346/8655398808",
                AdRequest.Builder().addTestDevice("57E22DE3F9AFCB046E7DF0D50266D222").build())
    }

    private fun setListner() {
        binding!!.imgConvertoMoney.setOnClickListener(this)
        binding!!.imgWithDrawMoney.setOnClickListener(this)
        binding!!.cardAppVideo.setOnClickListener(this)
    }

    private fun setData() {
        binding!!.tvCoinsValue.text = MyNumFormatter.getFormatted(coins) + " coins"
        binding!!.tvMoneyValue.text = MyNumFormatter.getFormatted(rupee) + " rupee"

    }

    private fun setCommonData(commonModel: CommonModel, user: FirebaseUser) {
        this.commonModel = commonModel;
        binding!!.tvConversionRate.text = MyNumFormatter.getFormatted(commonModel.conversion) + " coins = 1 Rupee"
        binding!!.tvAppValue.text = MyNumFormatter.getFormatted(commonModel.appvideo) + " Coins\n/minutes"
        binding!!.tvAppVideoValue.text = MyNumFormatter.getFormatted(commonModel.reward) + " Coins\n/Video"
        if (commonModel.showreward == 1) {
            binding!!.cardAppVideo.visibility = View.VISIBLE
        } else {
            binding!!.cardAppVideo.visibility = View.GONE
        }

        val userRef = mDatabase!!.child("users").child(user.uid)
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    //create new user
                    val common = CommonModel(commonModel.appvideo,commonModel.conversion
                            ,commonModel.reward,commonModel.showreward,commonModel.minwithdraw,commonModel.userNumber+1,commonModel.showwallet);
                    mDatabase!!.child("Common").setValue(common)
                    val users = Users(user.displayName, 0.0, 0.0,"",0,commonModel.userNumber)
                    mDatabase!!.child("users").child(user.uid).setValue(users)
                } else {
                    val users = dataSnapshot.getValue<Users>(Users::class.java)
                    if (users == null) {
                        Toast.makeText(prefData, "data user is null please create one", Toast.LENGTH_SHORT).show()
                        return
                    }
                    setData(users)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        userRef.addValueEventListener(eventListener)

        val requestRef = mDatabase!!.child("Request").child(prefData!!.reqUserId)
        val requestListner = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    menu.clear()
                    try {
                        dataSnapshot.children.mapNotNullTo(menu) { it.getValue<RequestModel>(RequestModel::class.java) }
                        handleRequestData();
                    }catch (e : Exception){
                        e.printStackTrace()
                    }

                }
            }
        }
        requestRef.addValueEventListener(requestListner)
    }

    private fun handleRequestData() {
        binding!!.recycleList.adapter = RequestListAdapter(menu);
    }

    private fun setData(users: Users) {
        this.users = users;
        binding!!.tvCoinsValue.text = MyNumFormatter.getFormatted(users.coins) + " coins"
        binding!!.tvMoneyValue.text = MyNumFormatter.getFormatted(users.rupee) + " rupee"
        binding!!.tvRequest.text = users.msg;
        prefData!!.reqUserId = prefData!!.userId + "User-" +  users.userNumber
    }

    private fun facebookLogin() {
        val loginButton = findViewById<LoginButton>(R.id.buttonFacebookLogin)
        loginButton.setReadPermissions("email", "public_profile")
        loginButton.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                updateUI(null)
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                updateUI(null)
            }
        })
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        updateUI(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        showProgressDialog()

        val credential = FacebookAuthProvider.getCredential(token.token)
        mAuth!!.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = mAuth!!.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this@WalletActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                    hideProgressDialog()
                }
    }
    // [END auth_with_facebook]

    fun signOut() {
        mAuth!!.signOut()
        LoginManager.getInstance().logOut()

        updateUI(null)
    }

    private fun updateUI(user: FirebaseUser?) {
        hideProgressDialog()
        if (user != null) {
            prefData!!.userId = user.uid
            findViewById<View>(R.id.buttonFacebookLogin).visibility = View.GONE
            binding!!.conwallet.visibility = View.VISIBLE
            makeProperLogin(user)

            //            findViewById(R.id.buttonFacebookSignout).setVisibility(View.VISIBLE);
        } else {

            findViewById<View>(R.id.buttonFacebookLogin).visibility = View.VISIBLE
            binding!!.conwallet.visibility = View.GONE
            //            findViewById(R.id.buttonFacebookSignout).setVisibility(View.GONE);
        }
    }

    private fun makeProperLogin(user: FirebaseUser) {
        mDatabase!!.child("Common").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commonModel = dataSnapshot.getValue<CommonModel>(CommonModel::class.java!!)
                if (commonModel == null) {
                    Toast.makeText(prefData, "data commonModel is null please create one", Toast.LENGTH_SHORT).show()
                    return
                }
                this@WalletActivity.setCommonData(commonModel,user)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

    }


    override fun onClick(v: View) {
        val i = v.id
        //        if (i == R.id.buttonFacebookSignout) {
        //            signOut();
        //        }
        if (v === binding!!.imgConvertoMoney) {
            //            showConverMoneyDiaolg((new BottomSheet(LayoutMode.WRAP_CONTENT)));
            showCustomViewDialog(BottomSheet(LayoutMode.WRAP_CONTENT))
        }else if (v == binding!!.imgWithDrawMoney){
            if (users.rupee < commonModel!!.minwithdraw){
                val msg = "you don't have enough money\nminmum required is " + commonModel!!.minwithdraw + " Rupee";
                handleMinWithDraw(msg);
            }else{
                enterMobileNo();
            }
        }else if (v == binding!!.cardAppVideo){
            if (mRewardedVideoAd.isLoaded) {
                mRewardedVideoAd.show()
            }
        }
    }

    override fun finish() {
        super.finish()
        if (timer != null) {
            timer!!.cancel()
            timer = null;
        }

    }

    private fun enterMobileNo() {
        MaterialDialog(this).show {
            title(R.string.mobileTitle)
            message(R.string.mobileMsg)
            input(
                    hint = "Enter Mobile No",
                    prefill = "",
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    maxLength = 10
            ) { _, text ->
                askForMoney(text);
            }
            positiveButton(android.R.string.ok)
            negativeButton(android.R.string.cancel)
            lifecycleOwner(this@WalletActivity)
        }

    }

    private fun askForMoney(text: CharSequence) {
        MaterialDialog(this).show {
            message(R.string.conversiontitile,"We are sending " + users.rupee + " Rupees to " + text + " paytm number")
            positiveButton(android.R.string.ok){
                sendMoneyRequest(text);
            }
            negativeButton(android.R.string.cancel)
            lifecycleOwner(this@WalletActivity)
        }
    }

    private fun sendMoneyRequest(text: CharSequence) {
        val rupees = users.rupee;
        val msg = " Your request for withdraw " + users.coins + " Rupee on " + text + " is pending it may take upto 3-5 business day"
        val user = Users("", users.coins,0.0,msg,users.reqnumber+1,users.userNumber)
        mDatabase!!.child("users").child(prefData!!.userId).setValue(user)

        val requestRef = mDatabase!!.child("Request").child(prefData!!.reqUserId)
        val  requestChile = requestRef.child(users.reqnumber.toString());
        val requestModel = RequestModel(text.toString(),rupees,"pending",users.reqnumber)
        requestChile.setValue(requestModel)
    }

    private fun handleMinWithDraw(msg: String) {
        MaterialDialog(this).show {
            message(R.string.conversiontitile,msg)
            positiveButton(android.R.string.ok)
            lifecycleOwner(this@WalletActivity)
        }
    }

    private fun showCustomViewDialog(dialogBehavior: DialogBehavior = ModalDialog) {
        val dialog = MaterialDialog(this, dialogBehavior).show {
            title(R.string.conversiontitile)
            customView(R.layout.conversion_dialog, scrollable = true, horizontalPadding = true)
            positiveButton(android.R.string.ok) { dialog ->
                // Pull the password out of the custom view when the positive button is pressed
                val user = Users("", 0.0,users.rupee + (users.coins / commonModel!!.conversion),users.msg,users.reqnumber,users.userNumber)
                mDatabase!!.child("users").child(prefData!!.userId).setValue(user)
            }
            negativeButton(android.R.string.cancel)
            lifecycleOwner(this@WalletActivity)
        }
        val customView = dialog.getCustomView();
        val converstionTv: TextView = customView.findViewById(R.id.tvConversionRate)
        val tvYourCoins: TextView = customView.findViewById(R.id.tvYourCoins);
        val tvYourRupee: TextView = customView.findViewById(R.id.tvYourRupee);

        converstionTv.text = MyNumFormatter.getFormatted(commonModel!!.conversion) + " coins = 1 Rupee"
        tvYourCoins.text = "Your coins = " + MyNumFormatter.getFormatted(users.coins) + " coins"
        tvYourRupee.text = "Your rupee for coins = " + MyNumFormatter.getFormatted(users.coins / commonModel!!.conversion) + " rupee"
    }

    companion object {
        private val TAG = "FacebookLogin"
    }

    override fun onRewarded(reward: RewardItem) {
        // Reward the user.
        val user = Users("", users.coins + commonModel!!.reward,users.rupee,users.msg,users.reqnumber+1,users.userNumber)
        mDatabase!!.child("users").child(prefData!!.userId).setValue(user)
    }

    override fun onRewardedVideoAdLeftApplication() {
    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        binding!!.cardAppVideo.visibility = View.GONE;
        time = 0;
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
    }

    override fun onRewardedVideoAdLoaded() {
    }

    override fun onRewardedVideoAdOpened() {
    }

    override fun onRewardedVideoStarted() {
    }

    override fun onRewardedVideoCompleted() {
    }
    override fun onPause() {
        super.onPause()
        mRewardedVideoAd.pause(this)
    }

    override fun onResume() {
        super.onResume()
        mRewardedVideoAd.resume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRewardedVideoAd.destroy(this)
    }
}
