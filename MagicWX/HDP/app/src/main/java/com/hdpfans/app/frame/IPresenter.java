package com.hdpfans.app.frame;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public interface IPresenter<V extends BaseView> {

    void attachView(V view);

    V getView();

    Context getApplicationContext();

    void setApplicationContext(Context content);

    Intent getIntent();

    void setIntent(Intent intent);

    Bundle getArguments();

    void setArguments(Bundle arguments);

}
