package com.niuza.trans.utils;

/**
 * Created by 牛杂辉 on 2016/7/23.
 */
public class MessageBean {
    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_COMMAND = 1;
    public static final int COMMAND_SEND_FILE = 0;
    public static final int COMMAND_READY_TO_RECEIVE_FILE = 1;
    public int mType = TYPE_MESSAGE;
    public int mCommand = COMMAND_SEND_FILE;
    public String message;
    public String fileName;
    public String devName;
    public long fileSize;

    public static int getTypeMessage() {
        return TYPE_MESSAGE;
    }

    public static int getTypeCommand() {
        return TYPE_COMMAND;
    }

    public static int getCommandSendFile() {
        return COMMAND_SEND_FILE;
    }

    public static int getCommandReadyToReceiveFile() {
        return COMMAND_READY_TO_RECEIVE_FILE;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public int getmCommand() {
        return mCommand;
    }

    public void setmCommand(int mCommand) {
        this.mCommand = mCommand;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
