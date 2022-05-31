package com.kqinfo.universal.retry.service;

import com.kqinfo.universal.retry.domain.RetryRecord;
import com.kqinfo.universal.retry.properties.AlarmProperties;
import com.kqinfo.universal.retry.util.MailUtil;

import javax.annotation.Resource;
import java.util.List;

/**
 * 默认的告警机制，邮箱告警
 * @author Zijian Liao
 * @since 1.12.0
 */
public class DefaultRetryAlarmServiceImpl implements RetryAlarmService {

    private static final String TEXT = "任务重试失败，bean:%s, method:%s, failReason:%s, recordId:%d";

    @Resource
    private MailUtil mailUtil;
    @Resource
    private AlarmProperties alarmProperties;

    @Override
    public void alarm(RetryRecord retryRecord) {
        String text = String.format(TEXT, retryRecord.getBeanName(), retryRecord.getMethod(), retryRecord.getFailReason(), retryRecord.getId());
        List<String> mailAddress = alarmProperties.getMailAddress();
        mailUtil.sendEmail("任务重试失败", text, mailAddress.toArray(new String[0]));
    }
}
