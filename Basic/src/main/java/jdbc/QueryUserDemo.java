package jdbc;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author steven01.gan
 * @version 1.0
 * @date 2021/7/14-15:21
 */
public class QueryUserDemo {

    static String url = "jdbc:mysql://www.xiaowenshu.cn:3388/test_table?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
    static String userName = "root";
    static String password = "123456";


    public static void main(String[] args) {
        System.out.println(queryUserList());
        String sql = "SELECT * FROM tb_user";
        List list = Lists.newArrayList();
        List<TbUser> tbUsers = query(sql, list, TbUser.class);
        System.out.println(tbUsers);
    }

    public static List<TbUser> queryUserList() {
        String sql = "SELECT * FROM `tb_user`;";

        List<TbUser> list = Lists.newArrayList();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String userId = resultSet.getString("user_id");
                String userName = resultSet.getString("user_name");
                String email = resultSet.getString("email");
                String address = resultSet.getString("address");
                String corp = resultSet.getString("corp");
                TbUser tbUser = new TbUser(id, userId, userName, email, address, corp);
                list.add(tbUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> List<T> query(String sql, List<Object> params, Class<T> clazz) {

        List<T> results = Lists.newArrayList();
        try (Connection connection = DriverManager.getConnection(url, userName, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();
        ) {
            for (int i = 0; i < params.size(); i++) {
                statement.setObject(i + 1, params.get(i));
            }
            while (resultSet.next()) {
                // ??????????????????????????????
                T t = clazz.newInstance();
                // ??????????????????
                Field[] declaredFields = clazz.getDeclaredFields();
                // ??????????????? name ???,??????????????????????????????
                for (Field declaredField : declaredFields) {
                    // ?????????????????????
                    declaredField.setAccessible(true);
                    // ?????????????????????????????????,??????????????? Object ??????
                    Object val = resultSet.getObject(declaredField.getName());
                    // ??????????????????,???????????????(?????????????????????????????????????????? Type ???????????????)
                    if (val instanceof BigInteger) {
                        val = ((BigInteger) val).longValue();
                    }
                    // ???????????????????????? t ?????????
                    declaredField.set(t, val);
                }
                results.add(t);

            }
        } catch (SQLException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return results;
    }
}
