package com.hdpfans.app.frame;

import android.arch.lifecycle.Lifecycle;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.hdpfans.app.model.event.InstallApkEvent;
import com.hdpfans.app.model.event.ToastEvent;
import com.hdpfans.app.ui.EventBusLifecycle;
import com.hdpfans.app.ui.LayoutInflaterConvert;
import com.hdpfans.app.utils.PhoneCompat;
import com.hdpfans.app.utils.PresenterCompat;
import com.hdpfans.app.utils.SafetyHandler;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasFragmentInjector;
import dagger.android.support.HasSupportFragmentInjector;
import dagger.internal.Beta;

@Beta
public abstract class FrameActivity extends AppCompatActivity
        implements HasFragmentInjector, HasSupportFragmentInjector, BaseView, SafetyHandler.Delegate {

    private final LifecycleProvider<Lifecycle.Event> provider
            = AndroidLifecycle.createLifecycleProvider(this);

    @Inject
    DispatchingAndroidInjector<Fragment> supportFragmentInjector;
    @Inject
    DispatchingAndroidInjector<android.app.Fragment> frameworkFragmentInjector;
    @Inject
    LayoutInflaterConvert mLayoutInflaterConvert;

    private Unbinder unbinder;

    private final SafetyHandler mSafetyHandler = SafetyHandler.create(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            AndroidInjection.inject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LayoutInflaterCompat.setFactory2(getLayoutInflater(), mLayoutInflaterConvert);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        PresenterCompat.inject(this);

        getLifecycle().addObserver(new EventBusLifecycle(this));
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        unbinder = ButterKnife.bind(this);

        // 存在presenter onCreate调用，保证View已经初始化
        List<BasePresenter> presenters = PresenterCompat.getPresenters(this);
        for (BasePresenter presenter : presenters) {
            getLifecycle().addObserver(presenter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return supportFragmentInjector;
    }

    @Override
    public AndroidInjector<android.app.Fragment> fragmentInjector() {
        return frameworkFragmentInjector;
    }

    protected SafetyHandler getSafetyHandler() {
        return this.mSafetyHandler;
    }

    @Override
    public void onReceivedHandlerMessage(Message message) {

    }

    @Override
    public LifecycleProvider<Lifecycle.Event> getLifecycleProvider() {
        return provider;
    }

    @Override
    public void toast(CharSequence message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInstallApkEvent(InstallApkEvent installApkEvent) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setDataAndType(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".my.package.name.provider", new File(installApkEvent.getFilePath())),
                    "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(installApkEvent.getFilePath())),
                    "application/vnd.android.package-archive");
        }
        startActivity(intent);
        if (installApkEvent.isFinish()) {
            PhoneCompat.exit(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toast(ToastEvent toastEvent) {
        toast(toastEvent.getText());
    }
}
