package com.azhen.test;

import com.azhen.domain.Role;
import com.azhen.mapper.RoleMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
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
           param.put("roleName","Azhen");
           param.put("note","Hello");
            param.put("note1","Hello");
            roleMapper.saveRole(param);
            //System.out.println(roleMapper.find().size());
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
