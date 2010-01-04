/*
 *
 * Created on Dec 24, 2009 | 12:16:10 PM
 *
 */
package nl.liacs.dbdm.ftse.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 
 * 
 * @author Behrooz Nobakht [bnobakht@liacs.nl]
 **/
public class FtseLikelihoodJdbcManager extends JdbcDaoSupport {

	private String likelihoodByDateQuery;
	private String saveQuery;
	private String updateQuery;
	private String ftseByLikelihoodToleranceQuery;
	private String deleteQuery;

	@SuppressWarnings("unchecked")
	@Transactional
	public Double findLikelihoodByDate(final Date date) {
		List result = getJdbcTemplate().query(likelihoodByDateQuery, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, FtseUtils.getMySqlDateTimeString(date));
			}
		}, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getDouble(1);
			}
		});
		if (result == null || result.isEmpty()) {
			return null;
		}
		return (Double) result.get(0);
	}

	@Transactional(readOnly = false)
	public void saveOrUpdate(String date, Double likelihood) {
		saveOrUpdate(FtseUtils.getDate(date), likelihood);
	}

	@Transactional(readOnly = false)
	public void saveOrUpdate(Date date, Double likelihood) {
		Double current = findLikelihoodByDate(date);
		if (current == null) {
			save(date, likelihood);
		} else {
			update(date, likelihood);
		}
	}

	@Transactional(readOnly = false)
	public void save(Date date, Double likelihood) {
		getJdbcTemplate().update(saveQuery, new Object[] { FtseUtils.getMySqlDateTimeString(date), likelihood });
	}

	@Transactional(readOnly = false)
	public void update(Date date, Double likelihood) {
		getJdbcTemplate().update(updateQuery, new Object[] { likelihood, FtseUtils.getMySqlDateTimeString(date) });
	}

	@Transactional(readOnly = false)
	public void clearData() {
		getJdbcTemplate().update(deleteQuery);
	}

	public Double findLikelihoodByDate(String date) {
		return findLikelihoodByDate(FtseUtils.getDate(date));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<FtseIndex> findByLikelihoodTolerance(final Date date, final Double likelihood, final Double tolerance) {
		return getJdbcTemplate().query(ftseByLikelihoodToleranceQuery, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, FtseUtils.getMySqlDateTimeString(date));
				ps.setDouble(2, likelihood - tolerance);
				ps.setDouble(3, likelihood + tolerance);
			}
		}, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return FtseJdbcManager.createFtseIndex(rs);
			}

		});
	}

	public void setLikelihoodByDateQuery(String likelihoodByDateQuery) {
		this.likelihoodByDateQuery = likelihoodByDateQuery;
	}

	public void setSaveQuery(String saveQuery) {
		this.saveQuery = saveQuery;
	}

	public void setUpdateQuery(String updateQuery) {
		this.updateQuery = updateQuery;
	}
	
	public void setDeleteQuery(String deleteQuery) {
		this.deleteQuery = deleteQuery;
	}

	public void setFtseByLikelihoodToleranceQuery(String ftseByLikelihoodToleranceQuery) {
		this.ftseByLikelihoodToleranceQuery = ftseByLikelihoodToleranceQuery;
	}

}
