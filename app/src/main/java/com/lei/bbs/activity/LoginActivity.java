package com.lei.bbs.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lei.bbs.R;
import com.lei.bbs.bean.Response;
import com.lei.bbs.constant.Constants;
import com.lei.bbs.retrofit.HttpHelper;
import com.lei.bbs.retrofit.RetrofitService;
import com.lei.bbs.util.MyLog;
import com.lei.bbs.util.MyToast;
import com.lei.bbs.util.MyToolBar;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;




public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private String TAG="LoginActivity";
    private Button btnToLogin;
    private TextView tvRegister;
    private EditText edEmail,edPassword;
    private ImageView imgLeft;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setToolBar();
        initView();
    }
    @Override
    public void setToolBar() {
        toolBar = (MyToolBar) findViewById(R.id.toolbar);
        toolBar.setTitle("Login");
        //back
        imgLeft = toolBar.getImgLeft();
        imgLeft.setImageResource(R.mipmap.left);
        imgLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.finish();
            }
        });
    }
    private void initView(){
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        btnToLogin = (Button) findViewById(R.id.btnLogin);
        btnToLogin.setOnClickListener(this);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(this);
        imgLeft = (ImageView) findViewById(R.id.imgLeft);
//        imgLeft.setOnClickListener(this);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                if (edPassword.getText().toString().equals("") || edEmail.getText().toString().equals("")){
                    MyToast.showLong(this,"用户名或密码不能为空");
                }else {

                    gotoLogin(edEmail.getText().toString(),edPassword.getText().toString());
                }

                break;
            case R.id.tvRegister:
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.imgLeft:
                this.finish();
                break;
            default:

                break;
        }
    }




    public void saveUserInfo(int id,int level,String name,String sex,String avatar){
        SharedPreferences userPreferences = getSharedPreferences(Constants.SHARE_USER_INFO, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.putInt("userId",id);
        editor.putInt("level",level);
        editor.putString("userName", name);
        editor.putString("sex", sex);
        editor.putString("avatar", avatar);
        editor.commit();
        Constants.userId=id;
        Constants.level=level;
        Constants.userName=name;
        Constants.sex=sex;
        Constants.avatar=avatar;


    }

    private void gotoLogin(String email,String password){
        RetrofitService service = HttpHelper.createHubService(Constants.base_url);
        HashMap<String,String> params = new HashMap<>();
        params.put("name",email);
        params.put("pwd", password);
        Call<Response>  login = service.postLogin(params);
        login.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                MyLog.i(TAG, "onResponse: "+response.body().getStatus());
                if (response.body().getStatus() != null){
                    int status = Integer.parseInt(response.body().getStatus());
                    int id = response.body().getUserId();
                    int level = response.body().getLevel();
                    String avatar = response.body().getAvatar();
                    MyLog.i("lei","id: "+id);
                    String sex = response.body().getSex();
                    String name = response.body().getUserName();
                    switch (status){
                        case 1: //success
                            MyToast.showShort(LoginActivity.this,"login success");

                            saveUserInfo(id,level, name, sex, avatar);
                            //Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                          //  startActivity(intent);
                           LoginActivity.this.finish();
                            break;
                        case 2:

                            break;
                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

                MyLog.i(TAG, "onFailure: ");
            }
        });
        
    }

}
