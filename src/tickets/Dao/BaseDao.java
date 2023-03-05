package tickets.Dao;
import org.apache.log4j.Logger;
import tickets.Common.Column;
import tickets.Common.Dbutils;


import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.Date;


public class BaseDao {

    public static Logger logger = Logger.getLogger(BaseDao.class);


    /**
     * 通用的查询方法，能够查询单个对象
     *
     * @param sql
     * @param params
     * @param clz
     * @param <T>
     * @return
     * @throws SQLException
     */
    public <T> T querySingleObject(String sql, Object[] params, Class<T> clz) throws SQLException {
        List<T> list = this.queryObjectList(sql, params, clz);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }


    /**
     * 查询列表
     *
     * @param sql
     * @param params
     * @param clz
     * @return
     * @throws SQLException
     */
    public <T> List<T> queryObjectList(String sql, Object[] params, Class<T> clz) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        if (params != null) {
            int index = 1;
            for (Object object : params) {
                statement.setObject(index++, object);
            }
        }
        ResultSet resultSet = statement.executeQuery();
        //获取本次查询的结果中包含的列名
        Set<String> columnNames = parseColumnNames(resultSet);

        List<T> list = new ArrayList<>();
        T object = null;

        //获取类中的所有属性
        Field[] fields = clz.getDeclaredFields();
        //解析每个Field对应的列名
        Map<Field, String> fieldColumnNames = new HashMap<Field, String>();
        for (Field field : fields) {
            fieldColumnNames.put(field, parseColumnName(field));
        }
        String columnName;
        Object columnValue = null;
        while (resultSet.next()) {
            try {
                //创建对象
                object = clz.getDeclaredConstructor().newInstance();
                for (Field field : fields) {
                    //获取对应的数据库中的列名
                    columnName = fieldColumnNames.get(field);
                    columnValue = null;
                    //判断本次查询的结果集中是否包含该列
                    if (columnNames.contains(columnName)) {
                        //根据类中的成员的类型，调用结果集中对应的方法，获取对应类型的结果
                        if (field.getType() == Short.class) {
                            if (resultSet.getObject(columnName) != null)
                                columnValue = resultSet.getShort(columnName);
                        } else if (field.getType() == Integer.class) {
                            if (resultSet.getObject(columnName) != null)
                                columnValue = resultSet.getInt(columnName);
                        } else if (field.getType() == Long.class) {
                            if (resultSet.getObject(columnName) != null)
                                columnValue = resultSet.getLong(columnName);
                        } else if (field.getType() == Float.class) {
                            if (resultSet.getObject(columnName) != null)
                                columnValue = resultSet.getFloat(columnName);
                        } else if (field.getType() == Date.class) {
                            Timestamp timestamp = resultSet.getTimestamp(columnName);
                            if (timestamp != null) {
                                columnValue = new Date(timestamp.getTime());
                            }
                        } else {
                            columnValue = resultSet.getObject(columnName);
                        }
                        //设置该字段的可访问性为true，使得可以直接给私有字段赋值
                        field.setAccessible(true);
                        field.set(object, columnValue);
                    }
                }
                //设置从CommonVO中继承的属性的值
                setCommonVOProperty(object, resultSet, columnNames);
                list.add(object);
            } catch (Exception e) {
                logger.error("反射创建对象并设置属性值时异常:" + e.getMessage());
            }
        }
        return list;
    }


    /**
     * 解析类中该字段对应的数据库中的列名
     *
     * @param field
     * @return
     */
    private String parseColumnName(Field field) {
        String columnName = null;
        //查看是否提供了注解
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            columnName = column.value();
        } else {
            columnName = field.getName();
            StringBuilder stringBuilder = new StringBuilder();
            char c;
            for (int i = 0; i < columnName.length(); i++) {
                c = columnName.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    if (i > 0) {
                        stringBuilder.append("_");
                    }
                    stringBuilder.append((char) (c + 32));
                } else {
                    stringBuilder.append(c);
                }
            }
            columnName = stringBuilder.toString();
        }
        return columnName;
    }

    /**
     * 解析本次查询结果中的所有列名
     *
     * @param resultSet
     * @return
     */
    protected Set<String> parseColumnNames(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        Set<String> set = new HashSet<>();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            set.add(metaData.getColumnLabel(i));
        }
        return set;
    }

    /**
     * 设置值对象中的通用属性的值
     *
     * @param object
     * @param resultSet
     * @param <T>
     * @throws SQLException
     */
    public <T> void setCommonVOProperty(T object, ResultSet resultSet, Set<String> resultColumnNames) throws SQLException {
        Class<?> clz = object.getClass();
        String[] fieldNames = {"delFlag", "cerator", "updator", "createDate", "updateDate"};
        String[] columnNames = {"del_flag", "creator", "updator", "create_date", "update_date"};
        Class<?>[] fieldTypes = {Short.class, Integer.class, Integer.class, Date.class, Date.class};
        Field field;
        try {
            String fieldName, columnName;
            Object columnValue = null;
            for (int i = 0; i < fieldNames.length; i++) {
                fieldName = fieldNames[i];
                field = clz.getField(fieldName);
                columnName = columnNames[i];
                //判断本次查询结果中是否包含了对应的列
                if (!resultColumnNames.contains(columnName)) {
                    continue;
                }
                if (field.getType() == Short.class) {
                    if (resultSet.getObject(columnName) != null)
                        columnValue = resultSet.getShort(columnName);
                } else if (field.getType() == Integer.class) {
                    if (resultSet.getObject(columnName) != null)
                        columnValue = resultSet.getInt(columnName);
                } else if (field.getType() == Long.class) {
                    if (resultSet.getObject(columnName) != null)
                        columnValue = resultSet.getLong(columnName);
                } else if (field.getType() == Float.class) {
                    if (resultSet.getObject(columnName) != null)
                        columnValue = resultSet.getFloat(columnName);
                } else if (field.getType() == Date.class) {
                    Timestamp timestamp = resultSet.getTimestamp(columnName);
                    if (timestamp != null) {
                        columnValue = new Date(timestamp.getTime());
                    }
                } else {
                    columnValue = resultSet.getObject(columnName);
                }
                //
                field.setAccessible(true);
                field.set(object, columnValue);
            }
        } catch (Exception e) {
            logger.error("设置值对象中的通用属性值时异常:" + e.getMessage());
        }
    }

    public int update(String sql, Object[] params) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        int count = 0;
        try {
            statement = connection.prepareStatement(sql);
            if (params != null) {
                int index = 1;
                for (Object value : params) {
                    statement.setObject(index++, value);
                }
            }
            count = statement.executeUpdate();
        } finally {
            Dbutils.close(null, statement);
        }
        return count;
    }

    /**
     * 执行插入语句，并返回自增列的值
     *
     * @param sql
     * @param params
     * @return
     * @throws SQLException
     */
    public int insertWithAutoIncrement(String sql, Object[] params) throws SQLException {
        Connection connection = Dbutils.getConnection();
        PreparedStatement statement = null;
        int count = 0;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null) {
                int index = 1;
                for (Object value : params) {
                    statement.setObject(index++, value);
                }
            }
            count = statement.executeUpdate();
            if (count > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        } finally {
            Dbutils.close(resultSet, statement);
        }
        return count;
    }



}
