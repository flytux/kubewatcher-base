package com.kubeworks.watcher.data.typehandler;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.custom.Quantity;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//@MappedTypes(value = Quantity.class)
public class QuantityTypeHandler extends BaseTypeHandler<Quantity> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Quantity parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, ExternalConstants.toStringQuantityViaK8s(parameter));
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : Quantity.fromString(value);
    }

    @Override
    public Quantity getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : Quantity.fromString(value);
    }

    @Override
    public Quantity getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : Quantity.fromString(value);
    }
}
