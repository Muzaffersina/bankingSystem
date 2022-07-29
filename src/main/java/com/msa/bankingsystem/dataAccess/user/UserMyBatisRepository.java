package com.msa.bankingsystem.dataAccess.user;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Repository;

import com.msa.bankingsystem.models.UserEntity;

@Repository
public class UserMyBatisRepository implements IUserRepository {

	Reader reader;

	private SqlSessionFactory init(Reader reader) {
		try {
			reader = Resources.getResourceAsReader("myBatis_conf.xml");
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

			return sqlSessionFactory;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public UserEntity loadUserByUsername(String username) {

		SqlSession session = init(reader).openSession();
		UserEntity user = null;

		if (session != null) {
			user = session.selectOne("userMapper.loadUserByUsername", username);
		}
		return user;
	}
}
