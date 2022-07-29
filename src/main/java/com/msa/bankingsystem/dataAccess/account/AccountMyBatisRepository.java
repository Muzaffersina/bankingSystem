package com.msa.bankingsystem.dataAccess.account;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.stereotype.Repository;

import com.msa.bankingsystem.models.Account;

@Repository
public class AccountMyBatisRepository implements IAccountRepository {

	Reader reader;

	public AccountMyBatisRepository() {

	}

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
	public void save(Account account) {

		SqlSession session = init(reader).openSession();
		session.insert("accountMapper.save", account);
		session.commit();

	}

	@Override
	public Account delete(String accountNumber) {

		SqlSession session = init(reader).openSession();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lastUpdateDate", System.currentTimeMillis());
		params.put("accountNumber", accountNumber);

		if (session != null) {
			session.update("accountMapper.updateDeletedColumn", params);
			session.commit();
		}
		return getByAccountNumber(accountNumber);
	}

	@Override
	public Account update(String accountNumber, double amount) {

		SqlSession session = init(reader).openSession();
		Account account = getByAccountNumber(accountNumber);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lastUpdateDate", System.currentTimeMillis());
		params.put("balance", account.getBalance() + amount);
		params.put("accountNumber", accountNumber);

		if (session != null) {
			session.update("accountMapper.updateBalance", params);
			session.commit();
			account = session.selectOne("accountMapper.getByAccountNumber", accountNumber);
		}
		return account;
	}

	@Override
	public Account transferBetweenAccounts(String senderAccountNumber, String transferredAccountNumber, double amount,
			double exchangeAmount) {

		SqlSession session = init(reader).openSession();

		Account senderFromAccount = getByAccountNumber(senderAccountNumber);
		Account transferredAccount = getByAccountNumber(transferredAccountNumber);

		Map<String, Object> senderAccountParams = new HashMap<String, Object>();
		senderAccountParams.put("lastUpdateDate", System.currentTimeMillis());
		senderAccountParams.put("balance", senderFromAccount.getBalance() - amount);
		senderAccountParams.put("accountNumber", senderAccountNumber);

		Map<String, Object> transferredAccountParams = new HashMap<String, Object>();
		transferredAccountParams.put("lastUpdateDate", System.currentTimeMillis());
		transferredAccountParams.put("balance", transferredAccount.getBalance() + exchangeAmount);
		transferredAccountParams.put("accountNumber", transferredAccountNumber);

		if (session != null) {
			try {
				session.update("accountMapper.updateBalance", senderAccountParams);
				session.update("accountMapper.updateBalance", transferredAccountParams);
				session.commit();
				senderFromAccount = session.selectOne("accountMapper.getByAccountNumber", senderAccountNumber);

			} catch (RuntimeException e) {
				session.rollback();
			}

		}
		return senderFromAccount;
	}

	@Override
	public Account getByAccountNumber(String accountNumber) {

		SqlSession session = init(reader).openSession();
		Account account = null;

		if (session != null) {
			account = session.selectOne("accountMapper.getByAccountNumber", accountNumber);
		}
		return account;
	}

	@Override
	public Account getByAccounId(int id) {

		SqlSession session = init(reader).openSession();
		Account account = null;

		if (session != null) {
			account = session.selectOne("accountMapper.getByAccountId", id);
		}
		return account;
	}

	@Override
	public List<Account> getAll() {

		SqlSession session = init(reader).openSession();
		List<Account> accounts = new ArrayList<>();
		if (session != null) {
			accounts = session.selectList("accountMapper.getAll");
		}
		return accounts;
	}

}
