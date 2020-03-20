package com.hdpfans.app.model.event;

public class ToastEvent {
    private CharSequence text;

    public ToastEvent(CharSequence text) {
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }
}
