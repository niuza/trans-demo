package com.niuza.trans.utils;

/**
 * Created by 牛杂辉 on 2016/7/23.
 */
public class MessageBean {
    public static final int TYPE_MESSAGE=0;
    public static final int TYPE_COMMAND=1;
    public static final int COMMAND_SEND_FILE=0;
    public static final int COMMAND_READY_TO_RECEIVE_FILE=1;
    public int mType=TYPE_MESSAGE;
    public int mCommand=COMMAND_SEND_FILE;
    public String message;
    public String fileName;
    public String devName;
    public long fileSize;

}
