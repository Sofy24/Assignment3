//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


public interface CommChannel {
    void sendMsg(String var1);

    String receiveMsg() throws InterruptedException;

    boolean isMsgAvailable();
}

