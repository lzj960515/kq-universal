package com.kqinfo.universal.log.service;

import com.kqinfo.universal.log.domain.OperateLog;
import com.kqinfo.universal.log.result.PageResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
public class DefaultLogRecordServiceImpl implements LogRecordService {

    private static final String INSERT_SQL = "insert into tbl_operate_log (user_id, username, content, category, business_no, param, ip_address, geo, browser, operate_time) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String PAGE_SQL = "select id, user_id, username, content, category, business_no, ip_address, geo, browser, operate_time from tbl_operate_log where category = ? and business_no = ? order by operate_time desc limit ?, ?";
    private static final String COUNT_SQL = "select count(*) from tbl_operate_log where category = ? and business_no = ?";
    private final RowMapper<OperateLog> rowMapper = new OperateLogRowMapper();
    private final RowMapper<OperateLog> detailRowMapper = new OperateLogDetailRowMapper();

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void record(OperateLog operateLog) {
        jdbcTemplate.update(INSERT_SQL, getFields(operateLog));
    }

    @Override
    public PageResult<OperateLog> page(int current, int size, String category, String businessNo) {
        final Integer total = jdbcTemplate.queryForObject(COUNT_SQL, Integer.class, category, businessNo);
        if(total == null || total.equals(0)){
            return new PageResult<>(current, size, 0 , null);
        }
        final List<OperateLog> records = jdbcTemplate.query(PAGE_SQL, rowMapper, category, businessNo, (current - 1) * size, size);
        return new PageResult<>(current, size, total, records);
    }

    @Override
    public OperateLog getById(Long id) {
        return jdbcTemplate.queryForObject("select * from tbl_operate_log where id = ?", detailRowMapper, id);
    }

    private Object[] getFields(OperateLog operateLog) {
        return new Object[]{operateLog.getUserId(),
                operateLog.getUsername(),
                operateLog.getContent(),
                operateLog.getCategory(),
                operateLog.getBusinessNo(),
                operateLog.getParam(),
                operateLog.getIpAddress(),
                operateLog.getGeo(),
                operateLog.getBrowser(),
                operateLog.getOperateTime()};
    }

    private static class OperateLogRowMapper implements RowMapper<OperateLog> {

        @Override
        public OperateLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            final long id = rs.getLong(1);
            final String userId = rs.getString(2);
            final String username = rs.getString(3);
            final String content = rs.getString(4);
            final String category = rs.getString(5);
            final String businessNo = rs.getString(6);
            final String ipAddress = rs.getString(7);
            final String geo = rs.getString(8);
            final String browser = rs.getString(9);
            final LocalDateTime operateTime = rs.getObject(10, LocalDateTime.class);
            return new OperateLog(id, userId, username, content, category, businessNo, ipAddress, geo, browser, operateTime);
        }
    }

    private static class OperateLogDetailRowMapper implements RowMapper<OperateLog> {

        @Override
        public OperateLog mapRow(ResultSet rs, int rowNum) throws SQLException {
            final long id = rs.getLong(1);
            final String userId = rs.getString(2);
            final String username = rs.getString(3);
            final String content = rs.getString(4);
            final String category = rs.getString(5);
            final String businessNo = rs.getString(6);
            final String param = rs.getString(7);
            final String ipAddress = rs.getString(8);
            final String geo = rs.getString(9);
            final String browser = rs.getString(10);
            final LocalDateTime operateTime = rs.getObject(11, LocalDateTime.class);
            return new OperateLog(id, userId, username, content, category, businessNo, param, ipAddress, geo, browser, operateTime);
        }
    }
}
