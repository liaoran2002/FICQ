package org.lc.ficq.contant;

public final class Constant {

    private Constant() {
    }

    /**
     * 在线状态过期时间 600s
     */
    public static final long ONLINE_TIMEOUT_SECOND = 600;

    /**
     * 消息允许撤回时间 300s
     */
    public static final long ALLOW_RECALL_SECOND = 300;

    /**
     * 群成员最大 20人
     */
    public static final int MAX_LARGE_GROUP_MEMBER = 20;

    /**
     * 文件最大 20M
     */
    public static final int MAX_FILE_SIZE = 20971520;

    /**
     * 图片最大 20M
     */
    public static final int MAX_IMAGE_SIZE = 20971520;
}
