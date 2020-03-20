package com.hdpfans.app.utils.plugin;

import android.content.Context;
import android.support.annotation.Nullable;

import hdpfans.com.BuildConfig;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import dalvik.system.DexClassLoader;
import com.hdpfans.api.Api;
import com.hdpfans.api.annotation.Plugin;
import com.hdpfans.app.data.manager.FileManager;

@Singleton
public class PluginLoader {

    @Inject
    Context context;
    @Inject
    FileManager mFileManager;

    private Map<Class, Api> mCachedPluginApi;

    @Inject
    public PluginLoader() {
        mCachedPluginApi = new ConcurrentHashMap<>();
    }

    /**
     * 获取插件Api对象
     *
     * @param clazz 插件接口
     * @param <T>   插件Class类
     * @return Api对象
     */
    @Nullable
    public <T extends Api> T createApi(Class<T> clazz) {
        Plugin plugin = clazz.getAnnotation(Plugin.class);
        if (plugin == null) {
            throw new IllegalArgumentException(clazz.getName() + " must be annotation " + Plugin.class.getName());
        }

        if (mCachedPluginApi.get(clazz) != null) {
            return clazz.cast(mCachedPluginApi.get(clazz));
        }

        T api = null;
        try {
            if (BuildConfig.PLUGIN_MODE) {
                DexClassLoader dexClassLoader = new DexClassLoader(
                        new File(mFileManager.getPluginsDir(), plugin.pack()).getAbsolutePath(),
                        context.getDir("dex", Context.MODE_PRIVATE).getAbsolutePath(),
                        mFileManager.getSystemLibsDir().getAbsolutePath(),
                        context.getClassLoader());
                api = clazz.cast(dexClassLoader.loadClass(plugin.main()).newInstance());
            } else {
                api = clazz.cast(Class.forName(plugin.main()).newInstance());
            }

            api.onCreate(context);
            mCachedPluginApi.put(clazz, api);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return api;
    }

    /**
     * 获取插件相关信息，包括插件来源和编译配置信息
     *
     * @param clazz 插件接口
     * @param <T>   插件Class类
     * @return 插件的编译配置信息
     */
    public <T extends Api> PluginBuildConfig getPluginBuildConfig(Class<T> clazz) {
        PluginBuildConfig pluginBuildConfig = new PluginBuildConfig();
        if (!BuildConfig.PLUGIN_MODE) {
            pluginBuildConfig.setFrom(PluginBuildConfig.FromType.LOCAL);
        } else {
            pluginBuildConfig.setFrom(PluginBuildConfig.FromType.ONLINE);
        }

        // 获取BuildConfig
        T api = createApi(clazz);
        if (api != null) {
            Plugin plugin = clazz.getAnnotation(Plugin.class);
            if (plugin != null) {
                try {
                    Class buildConfigClass = api.getClass().getClassLoader().loadClass(api.getClass().getPackage().getName() + ".BuildConfig");
                    Object buildConfigObj = buildConfigClass.newInstance();
                    pluginBuildConfig.setBuildType(buildConfigClass.getField("BUILD_TYPE").get(buildConfigObj).toString());
                    pluginBuildConfig.setVersionCode(buildConfigClass.getField("VERSION_CODE").getInt(buildConfigObj));
                    pluginBuildConfig.setVersionName(buildConfigClass.getField("VERSION_NAME").get(buildConfigObj).toString());
                    pluginBuildConfig.setFlavor(buildConfigClass.getField("FLAVOR").get(buildConfigObj).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return pluginBuildConfig;
    }
}
