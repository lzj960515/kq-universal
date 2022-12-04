package com.kqinfo.universal.delay.task.dao;

import com.kqinfo.universal.delay.task.config.DelayTaskProperties;
import com.kqinfo.universal.delay.task.core.domain.DelayTaskInfo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Zijian Liao
 * @since 1.0.0
 */
@Component
public class DelayTaskDao {

    private static final String INSERT_SQL = "INSERT INTO `delay_task` (`name`, `description`, `info`, `execute_time`, `execute_status`, `execute_message`, `real_execute_time`, `create_time`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String FIND_SQL = "SELECT `id`, `name`, `info`, `execute_time`, `execute_status` from `delay_task` where id = ?";
    private static final String FIND_BY_EXECUTE_TIME_SQL = "select `id`, `name`, `info`, `execute_time`, `execute_status` from delay_task where execute_time <= ? and execute_status = 1 limit ?";
    private static final String DELETE_BY_EXECUTE_TIME_SQL = "delete from delay_task where execute_status = 3 and execute_time <= ?";
    private static final String UPDATE_STATUS_SQL = "update delay_task set real_execute_time = ?, execute_status = ?, execute_message = ? where  id = ?";
    private final RowMapper<DelayTaskInfo> rowMapper = new DelayTaskInfoRowMapper();

    @Resource
    private DelayTaskProperties delayTaskProperties;
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void save(DelayTaskInfo delayTaskInfo){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, new String[]{"id"});
            ps.setString(1, delayTaskInfo.getName());
            ps.setString(2, delayTaskInfo.getDescription());
            ps.setString(3, delayTaskInfo.getInfo());
            ps.setLong(4, delayTaskInfo.getExecuteTime());
            ps.setInt(5, delayTaskInfo.getExecuteStatus());
            ps.setString(6, delayTaskInfo.getExecuteMessage());
            ps.setLong(7, 0);
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        delayTaskInfo.setId(id);
    }

    public DelayTaskInfo findById(Long id){
        return jdbcTemplate.queryForObject(FIND_SQL, rowMapper, id);
    }

    public List<DelayTaskInfo> findByExecuteTime(Long executeTime){
        return jdbcTemplate.query(FIND_BY_EXECUTE_TIME_SQL, rowMapper, executeTime, delayTaskProperties.getConcurrency());
    }

    public void deleteByExecuteTime(Long executeTime){
        jdbcTemplate.update(DELETE_BY_EXECUTE_TIME_SQL, executeTime);
    }

    public void updateStatus(Long id, Integer status, String message){
        jdbcTemplate.update(UPDATE_STATUS_SQL, System.currentTimeMillis(), status, message, id);
    }


    private static class DelayTaskInfoRowMapper implements RowMapper<DelayTaskInfo> {

        @Override
        public DelayTaskInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            final long id = rs.getLong(1);
            final String name = rs.getString(2);
            final String info = rs.getString(3);
            final Long executeTime = rs.getLong(4);
            final Integer executeStatus = rs.getInt(5);
            return new DelayTaskInfo(id, name, info, executeTime, executeStatus);
        }
    }

}
