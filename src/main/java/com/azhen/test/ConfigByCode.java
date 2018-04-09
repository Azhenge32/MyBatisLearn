package com.azhen.test;

import com.azhen.domain.Role;
import com.azhen.mapper.RoleMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Azhen on 2017/7/20.
 */
public class ConfigByCode {
    public static void main(String[] args) throws Exception {
        getByXml();
    }

    private static void getByXml() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = null;
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = null;
        try {
            sqlSession = sqlSessionFactory.openSession();

            RoleMapper roleMapper = sqlSession.getMapper(RoleMapper.class);
           /* Role role = roleMapper.getRole(1L);
            System.out.println(role.getRoleName());*/
           Map<String, Object> param = new HashMap<String, Object>();
         /*  param.put("roleName","Azhen");
           param.put("createTime",new Date());
           param.put("age",11);
            //roleMapper.saveRole(param);
            roleMapper.saveRoleByMap(param);
*/
            param = new HashMap<String, Object>();
            Date date = new Date();
            param.put("roleName","Azhen");
            param.put("createTime", date);
            param.put("longTime",Instant.now().getEpochSecond());
            param.put("age",11);
            //roleMapper.saveRole(param);
            roleMapper.saveRoleByMap(param);

            param = new HashMap<String, Object>();
            param.put("roleName","Azhen");
            param.put("createTime", LocalDateTime.now());
            param.put("age",11);
            //roleMapper.saveRole(param);
            //roleMapper.saveRoleByMap(param);

            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
            sqlSession.rollback();
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
