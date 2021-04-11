package com.example.ditugaode.view.base;

public interface TTS {
    public void init();
    public void playText(String playText);
    public void stopSpeak();
    public void destroy();
    public boolean isPlaying();
    public void setCallback(ICallBack callback);
}

