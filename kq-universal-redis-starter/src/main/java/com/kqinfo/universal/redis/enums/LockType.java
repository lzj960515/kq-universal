package com.kqinfo.universal.redis.enums;

/**
 * 锁类型
 *
 * @author Zijian Liao
 * @since 1.0.0
 */
public enum LockType {
    /**
     * 可重入锁
     */
    Lock,
    /**
     * 读锁
     */
    ReadLock,
    /**
     * 写锁
     */
    WriteLock;
}
