package com.kqinfo.universal.dynamic.datasource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author Zijian Liao
 * @since 2.20.0
 */
@Component
public class DynamicDataSourceDao {

    private static final String INSERT_SQL = "INSERT INTO `dynamic_data_source` (`name`, `driver_class_name`, `jdbc_url`, `username`, `password`, `minimum_idle`, `maximum_pool_size`, `idle_timeout`, `connection_timeout`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String LIST_SQL = "select `id`, `name`, `driver_class_name`, `jdbc_url`, `username`, `password`, `minimum_idle`, `maximum_pool_size`, `idle_timeout`, `connection_timeout` from `dynamic_data_source`";
    private static final String DELETE_SQL = "delete from dynamic_data_source where id = ?";
    private static final String UPDATE_SQL = "update dynamic_data_source set `name` = ?, `driver_class_name` = ?, `jdbc_url` = ?, `username` = ?, `minimum_idle` = ?, `maximum_pool_size` = ?, `idle_timeout` = ?, `connection_timeout` = ? where id = ?";
    private static final String UPDATE_PASSWORD_SQL = "update dynamic_data_source set `username` = ?, `password` = ? where id = ?";
    private static final String GET_SQL = "select `id`, `name`, `driver_class_name`, `jdbc_url`, `username`, `password`, `minimum_idle`, `maximum_pool_size`, `idle_timeout`, `connection_timeout` from `dynamic_data_source` where id = ?";
    private final RowMapper<DynamicDataSourceInfo> rowMapper = new DynamicDataSourceInfoRowMapper();
    @Resource
    private JdbcTemplate jdbcTemplate;

    public void save(DynamicDataSourceInfo dynamicDataSourceInfo){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(INSERT_SQL, new String[]{"id"});
            ps.setString(1, dynamicDataSourceInfo.getName());
            ps.setString(2, dynamicDataSourceInfo.getDriverClassName());
            ps.setString(3, dynamicDataSourceInfo.getJdbcUrl());
            ps.setString(4, dynamicDataSourceInfo.getUsername());
            ps.setString(5, dynamicDataSourceInfo.getPassword());
            ps.setInt(6, dynamicDataSourceInfo.getMinimumIdle());
            ps.setInt(7, dynamicDataSourceInfo.getMaximumPoolSize());
            ps.setInt(8, dynamicDataSourceInfo.getIdleTimeout());
            ps.setInt(9, dynamicDataSourceInfo.getConnectionTimeout());
            return ps;
        }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        dynamicDataSourceInfo.setId(id);
    }

    public DynamicDataSourceInfo getById(Integer id){
        return jdbcTemplate.queryForObject(GET_SQL, rowMapper, id);
    }

    public List<DynamicDataSourceInfo> list(){
        return jdbcTemplate.query(LIST_SQL, rowMapper);
    }

    public void deleteById(Integer id){
        jdbcTemplate.update(DELETE_SQL, id);
    }

    public void updateById(DynamicDataSourceInfo dynamicDataSourceInfo){
        jdbcTemplate.update(UPDATE_SQL,
                dynamicDataSourceInfo.getName(),
                dynamicDataSourceInfo.getDriverClassName(),
                dynamicDataSourceInfo.getJdbcUrl(),
                dynamicDataSourceInfo.getUsername(),
                dynamicDataSourceInfo.getMinimumIdle(),
                dynamicDataSourceInfo.getMaximumPoolSize(),
                dynamicDataSourceInfo.getIdleTimeout(),
                dynamicDataSourceInfo.getConnectionTimeout(),
                dynamicDataSourceInfo.getId());
    }

    public void updatePassword(String username, String password, Integer id){
        jdbcTemplate.update(UPDATE_PASSWORD_SQL, username, password, id);
    }


    private static class DynamicDataSourceInfoRowMapper implements RowMapper<DynamicDataSourceInfo> {

        @Override
        public DynamicDataSourceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String driverClassName = rs.getString("driver_class_name");
            String jdbcUrl = rs.getString("jdbc_url");
            String username = rs.getString("username");
            String password = rs.getString("password");
            int minimumIdle = rs.getInt("minimum_idle");
            int maximumPoolSize = rs.getInt("maximum_pool_size");
            int idleTimeout = rs.getInt("idle_timeout");
            int connectionTimeout = rs.getInt("connection_timeout");
            return new DynamicDataSourceInfo(id, name, driverClassName, jdbcUrl, username, password, minimumIdle, maximumPoolSize, idleTimeout, connectionTimeout);
        }
    }
}
