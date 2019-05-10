package com.jeffjeong.foodrecipes;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;


// 코드 재사용성을 위한 기본 액티비티 -> 어디에서도 사용가능하도록 한다.
// 이것은 일반적으로 만드는 액티비티가 아니기 때문에 추상클래스로 만든다.
// 즉 매니페스트에서 추가할 필요가 없다.
// 추상클래스는 인스턴스를 만들수 없다. 즉 메모리에 올라가지 않는다.
// extends로 상속할때만 쓰인다.
// 예) SomeActivity extends BaseActivity
public abstract class BaseActivity extends AppCompatActivity {

    // 페이징 처리할때 보여줄 프로그래스바
    public ProgressBar mProgressBar;

    // 컨텐트 뷰를 설정한다.
    @Override
    public void setContentView(int layoutResID) {

        // 현재 액티비티의 레이아웃 부모는 컨스트레인트 레이아웃이다.
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        // 안에 있는 프레임 레이아웃을 가져온다.
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        // 프로그래스바를 가져온다.
        mProgressBar = constraintLayout.findViewById(R.id.progress_bar);


        // 이렇게 함으로써 프레임 레이아웃이 다른 액티비티의 컨테이너 로서 역할을 할수가 있다.
        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        // setContentView 에 위에서 가져온 컨스트레인트 레이아웃을 넣어준다.
        super.setContentView(constraintLayout);
    }



    // 프로그래스바를 보여주는 메소드
    public void showProgressBar(boolean visibility){
        // visibility 가 트루면 보여주고 아니면 안보이게 한다.
        mProgressBar.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }


}
