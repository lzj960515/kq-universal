package com.kqinfo.universal.retry.service;

import com.kqinfo.universal.retry.domain.RetryRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.12.0
 */
public class DefaultRetryRecordServiceImpl implements RetryRecordService {

    private static final String INSERT_SQL = "insert into tbl_retry_record (bean_name, method, `parameter`, `retry_times`, `max_retry_times`, `fail_reason`, `fixed_rate`, `next_time`, `status`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String LIST_RECORD_SQL = "select id, bean_name, method, `parameter`, `retry_times`, `max_retry_times`, `fail_reason`, `fixed_rate`, `next_time`, `status` from tbl_retry_record where status = 1";
    private static final String UPDATE_RECORD_SUCCESS_SQL = "update tbl_retry_record set status = 2 where id = ?";
    private static final String UPDATE_RETRY_TIMES_SQL = "update tbl_retry_record set retry_times = retry_times + 1, fail_reason = ?, next_time = ? where id = ?";
    private static final String UPDATE_RECORD_FAIL_SQL = "update tbl_retry_record set retry_times = retry_times + 1, fail_reason = ?, status = 3 where id = ?";
    private final RowMapper<RetryRecord> rowMapper = new RetryRecordRowMapper();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void record(RetryRecord retryRecord) {
        jdbcTemplate.update(INSERT_SQL, getFields(retryRecord));
    }

    private Object[] getFields(RetryRecord retryRecord) {
        return new Object[]{
                retryRecord.getBeanName(),
                retryRecord.getMethod(),
                retryRecord.getParameter(),
                retryRecord.getRetryTimes(),
                retryRecord.getMaxRetryTimes(),
                retryRecord.getFailReason(),
                retryRecord.getFixedRate(),
                retryRecord.getNextTime(),
                retryRecord.getStatus()
        };
    }

    @Override
    public List<RetryRecord> listExecutingRecord() {
        return jdbcTemplate.query(LIST_RECORD_SQL, rowMapper);
    }

    @Override
    public void updateRecordSuccess(Long id) {
        jdbcTemplate.update(UPDATE_RECORD_SUCCESS_SQL, id);
    }

    @Override
    public void updateRetryTimes(Long id, boolean isFail, String failReason, int fixedRate) {
        if (isFail) {
            jdbcTemplate.update(UPDATE_RECORD_FAIL_SQL, failReason, id);
        } else {
            final LocalDateTime nextTime = LocalDateTime.now().plusMinutes(fixedRate);
            jdbcTemplate.update(UPDATE_RETRY_TIMES_SQL, failReason, nextTime, id);
        }
    }

    private static class RetryRecordRowMapper implements RowMapper<RetryRecord> {

        @Override
        public RetryRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            // id, bean_name, method, `parameter`, `max_retry_times`, `fail_reason`, `fixed_rate`, `next_time`, `status`
            final long id = rs.getLong(1);
            final String beanName = rs.getString(2);
            final String method = rs.getString(3);
            final String parameter = rs.getString(4);
            final int retryTimes = rs.getInt(5);
            final int maxRetryTimes = rs.getInt(6);
            final String failReason = rs.getString(7);
            final int fixedRate = rs.getInt(8);
            final LocalDateTime nextTime = rs.getObject(9, LocalDateTime.class);
            final int status = rs.getInt(10);
            return new RetryRecord(id, beanName, method, parameter, retryTimes, maxRetryTimes, failReason, fixedRate, nextTime, status);
        }
    }
}
