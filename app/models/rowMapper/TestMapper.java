package models.rowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import models.vo.TestVo;
import org.springframework.jdbc.core.RowMapper;
import utils.ExcelUtils;

/**
 * 版本: [1.0]
 * 功能说明: PatientInfoMapper
 *
 * 作者: 李承达
 */
public class TestMapper implements RowMapper<TestVo> {

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.RowMapper#mapRow(java.sql.ResultSet, int)
     */
    public TestVo mapRow(ResultSet rs, int rowNum) throws SQLException {


            TestVo testVo = new TestVo();
            testVo.setId(rs.getInt("tid"));
            testVo.setPid(rs.getInt("pid"));
            testVo.setName(rs.getString("name"));
            testVo.setStringDate(rs.getString("start_time"));
            testVo.setUseStatus(rs.getString("use_Status"));
            testVo.setOrgName(rs.getString("org_Name"));
            testVo.setOrgCode(rs.getInt("id"));
            testVo.setStatus(rs.getString("status"));
            testVo.setPopulation(rs.getInt("population"));
            testVo.setTurn(rs.getInt("turn"));
            testVo.setReport(rs.getString("report"));
            testVo.setAnswerNoCommitNumber(rs.getInt("sums") - rs.getInt("cou"));
            return testVo;

    }

}
