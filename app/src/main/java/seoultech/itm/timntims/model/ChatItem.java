package seoultech.itm.timntims.model;

import org.jetbrains.annotations.Nullable;

public abstract class ChatItem {
    public static final int TYPE_MESSAGE_SENT = 0;
    public static final int TYPE_MESSAGE_RECEIVED = 1;
    public static final int TYPE_IMAGE_SENT = 2;
    public static final int TYPE_IMAGE_RECEIVED = 3;

    abstract public int getType();

}


