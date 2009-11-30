/*
 *
 * Created on Nov 30, 2009 | 12:10:52 PM
 *
 */
package nl.liacs.dbdm.ftse.data.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import nl.liacs.dbdm.ftse.model.FtseIndex;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
				ps.setFloat(2, ftse.getOpen());
				ps.setFloat(3, ftse.getLow());
				ps.setFloat(4, ftse.getHigh());
				ps.setFloat(5, ftse.getClose());
				ps.setFloat(6, ftse.getVolume());
				ps.setFloat(7, ftse.getAdjClose());
				logger.debug("SQL prepared for: " + ftse);
			}

			@Override
			public int getBatchSize() {
				return all.size();
			}
		});
	}

	public void setInsertQuery(String insertQuery) {
		this.insertQuery = insertQuery;
	}

}
