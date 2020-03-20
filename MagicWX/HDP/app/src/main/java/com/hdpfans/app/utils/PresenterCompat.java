package com.hdpfans.app.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.hdpfans.app.frame.BasePresenter;
import com.hdpfans.app.frame.FrameActivity;
import com.hdpfans.app.frame.FrameBottomSheetFragment;
import com.hdpfans.app.frame.FrameFragment;
import com.hdpfans.app.frame.Presenter;


public class PresenterCompat {

    private static void process(Class clazz, ProcessRunnable runnable) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Presenter.class) != null) {
                runnable.run(field);
            }
        }
    }

    public static void inject(@Nonnull final FrameActivity activity) {
        process(activity.getClass(), field -> {
            try {
                field.setAccessible(true);
                Object o = field.get(activity);
                if (o == null) {
                    throw new NullPointerException(field.getName() + " is null");
                }
                if (o instanceof BasePresenter) {
                    BasePresenter presenter = ((BasePresenter) o);
                    registerPresenter(activity, presenter);
                } else {
                    throw new IllegalArgumentException("This filed must extends BasePresenter");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public static void inject(@Nonnull final FrameBottomSheetFragment fragment) {
        process(fragment.getClass(), field -> {
            try {
                field.setAccessible(true);
                Object o = field.get(fragment);
                if (o == null) {
                    throw new NullPointerException(field.getName() + " is null");
                }
                if (o instanceof BasePresenter) {
                    BasePresenter presenter = ((BasePresenter) o);
                    registerPresenter(fragment, presenter);
                } else {
                    throw new IllegalArgumentException("This filed must extends BasePresenter");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public static void inject(@Nonnull final FrameFragment fragment) {
        process(fragment.getClass(), field -> {
            try {
                field.setAccessible(true);
                Object o = field.get(fragment);
                if (o == null) {
                    throw new NullPointerException(field.getName() + "is null");
                }
                if (o instanceof BasePresenter) {
                    BasePresenter presenter = ((BasePresenter) o);
                    registerPresenter(fragment, presenter);
                } else {
                    throw new IllegalArgumentException("This filed must extends BasePresenter");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    public static List<BasePresenter> getPresenters(Object object) {
        List<BasePresenter> basePresenters = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(Presenter.class) != null) {
                field.setAccessible(true);
                try {
                    Object o = field.get(object);
                    if (o != null && o instanceof BasePresenter) {
                        BasePresenter presenter = ((BasePresenter) o);
                        basePresenters.add(presenter);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return basePresenters;
    }

    private static void registerPresenter(FrameActivity activity, BasePresenter presenter) {
        presenter.attachView(activity);
        presenter.setIntent(activity.getIntent());
        presenter.setApplicationContext(activity.getApplicationContext());
    }

    private static void registerPresenter(FrameBottomSheetFragment fragment, BasePresenter presenter) {
        presenter.attachView(fragment);
        presenter.setArguments(fragment.getArguments());
        presenter.setApplicationContext(fragment.getContext().getApplicationContext());
    }

    private static void registerPresenter(FrameFragment fragment, BasePresenter presenter) {
        presenter.attachView(fragment);
        presenter.setArguments(fragment.getArguments());
        presenter.setApplicationContext(fragment.getContext().getApplicationContext());
    }

    interface ProcessRunnable {
        void run(Field field);
    }

}
