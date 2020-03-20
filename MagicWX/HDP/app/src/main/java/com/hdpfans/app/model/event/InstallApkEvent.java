package com.hdpfans.app.model.event;

public class InstallApkEvent {

    private String filePath;

    private boolean finish;

    public InstallApkEvent(String filePath) {
        this.filePath = filePath;
    }

    public InstallApkEvent(String filePath, boolean finish) {
        this.filePath = filePath;
        this.finish = finish;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isFinish() {
        return finish;
    }
}
