/*
 *
 * Created on Nov 30, 2009 | 12:10:52 PM
 *
 */
package nl.liacs.dbdm.ftse.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nl.liacs.dbdm.ftse.model.FtseIndex;
import nl.liacs.dbdm.ftse.model.FtseIndexTimeComparator;
import nl.liacs.dbdm.ftse.utils.FtseUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
public class FtseJdbcManager extends JdbcDaoSupport {

	private String insertQuery;
	private String findAllQuery;
	private String findByFromDateToDateQuery;
	private String findByDateQuery;
	private String findNextByDateQuery;

	public FtseJdbcManager() {
	}

	@Transactional(readOnly = false)
	public void save(FtseIndex ftse) {
		if (ftse == null) {
			return;
		}
		try {
			int update = getJdbcTemplate().update(
					insertQuery,
					new Object[] { ftse.getDateString(), ftse.getOpen(), ftse.getLow(), ftse.getHigh(),
							ftse.getClose(), ftse.getVolume(), ftse.getAdjClose() });
			if (update > 0) {
				logger.debug("FTSE inserted: " + ftse);
			} else {
				logger.warn("Seems no update was done: " + update);
			}
		} catch (DataAccessException e) {
			logger.error("Failed to insert [" + ftse + "]. Reason: ");
			throw new RuntimeException(e);
		}
	}

	@Transactional(readOnly = false)
	public void saveAll(final List<FtseIndex> all) {
		getJdbcTemplate().batchUpdate(insertQuery, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FtseIndex ftse = all.get(i);
				ps.setString(1, ftse.getDateString());
				ps.setDouble(2, ftse.getOpen());
				ps.setDouble(3, ftse.getLow());
				ps.setDouble(4, ftse.getHigh());
				ps.setDouble(5, ftse.getClose());
				ps.setDouble(6, ftse.getVolume());
				ps.setDouble(7, ftse.getAdjClose());
				logger.debug("SQL prepared for: " + ftse);
			}

			@Override
			public int getBatchSize() {
				return all.size();
			}
		});
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public List<FtseIndex> findAll() {
		return getJdbcTemplate().query(findAllQuery, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				FtseIndex ftse = createFtseIndex(rs);
				return ftse;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<FtseIndex> findByFromDateToDate(final java.util.Date from, final java.util.Date to) {
		return getJdbcTemplate().query(findByFromDateToDateQuery, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, FtseUtils.getMySqlDateTimeString(from));
				ps.setString(2, FtseUtils.getMySqlDateTimeString(to));
			}

		}, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return createFtseIndex(rs);
			}
		});
	}

	@Transactional
	public List<FtseIndex> findByFromDateToDate(String from, String to) {
		if (from.equals(to)) {
			return findByDate(from);
		}
		return findByFromDateToDate(FtseUtils.getDate(from), FtseUtils.getDate(to));
	}

	@Transactional
	public List<FtseIndex> findByDate(String date) {
		return findByDate(FtseUtils.getDate(date));
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<FtseIndex> findByDate(final Date date) {
		return getJdbcTemplate().query(findByDateQuery, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, FtseUtils.getMySqlDateTimeString(date));
			}
		}, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return createFtseIndex(rs);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public FtseIndex findNextByDate(final Date date) {
		List result = getJdbcTemplate().query(findNextByDateQuery, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, FtseUtils.getMySqlDateTimeString(date));
			}
		}, new RowMapper() {

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				return createFtseIndex(rs);
			}
		});
		if (result == null || result.isEmpty()) {
			return null;
		}
		Collections.sort(result, FtseIndexTimeComparator.INSTANCE);
		return (FtseIndex) result.get(0);
	}

	public void setInsertQuery(String insertQuery) {
		this.insertQuery = insertQuery;
	}

	public void setFindAllQuery(String findAllQuery) {
		this.findAllQuery = findAllQuery;
	}

	public void setFindByFromDateToDateQuery(String findByFromDateToDateQuery) {
		this.findByFromDateToDateQuery = findByFromDateToDateQuery;
	}

	public void setFindByDateQuery(String findByDateQuery) {
		this.findByDateQuery = findByDateQuery;
	}

	public void setFindNextByDateQuery(String findNextByDateQuery) {
		this.findNextByDateQuery = findNextByDateQuery;
	}

	protected static FtseIndex createFtseIndex(ResultSet rs) throws SQLException {
		Long id = rs.getLong(1);
		java.util.Date date = rs.getDate(2);
		Double open = rs.getDouble(3);
		Double low = rs.getDouble(4);
		Double high = rs.getDouble(5);
		Double close = rs.getDouble(6);
		Double volume = rs.getDouble(7);
		Double adjClose = rs.getDouble(8);
		FtseIndex ftse = new FtseIndex(date, open, low, high, close);
		ftse.setId(id);
		ftse.setVolume(volume);
		ftse.setAdjClose(adjClose);
		return ftse;
	}

}
