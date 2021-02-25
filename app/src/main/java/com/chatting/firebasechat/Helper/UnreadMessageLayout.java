package com.chatting.firebasechat.Helper;

import android.os.Bundle;

public class UnreadMessageLayout {
    private static UnreadMessageLayout instance;
    private Bundle bundle;

    public static synchronized UnreadMessageLayout getInstance() {
        UnreadMessageLayout unreadMessageLayout = null;
        synchronized (UnreadMessageLayout.class) {
            if (instance == null) {
                instance = new UnreadMessageLayout();
                unreadMessageLayout = instance;
            }
        }
        return unreadMessageLayout;
    }

    private UnreadMessageLayout() {
        bundle = new Bundle();
    }

    public void setBundle(Bundle bundle){
        this.bundle = bundle;
    }

    public Bundle getBundle(){
        return this.bundle;
    }

    public int getUnreadMessageByDialog(String id){
        return this.bundle.getInt(id);
    }
}
