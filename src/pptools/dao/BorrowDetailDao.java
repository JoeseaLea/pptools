package pptools.dao;

import java.util.List;

import pptools.vo.BidDebtRecordVo;
import pptools.vo.BidRecordVo;
import pptools.vo.BorrowDetailPageVo;
import pptools.vo.BorrowDetailVo;
import pptools.vo.NeedReturnRecordNext6MonthVo;
import pptools.vo.OverTimeRecordLast6MonthVo;

/**
 * 借款详情DAO类
 * @author Joesea Lea
 */
public interface BorrowDetailDao {
	public void addBorrowDetailPageVo(BorrowDetailPageVo borrowDetailPageVo);

	public void addBidRecords(List<BidRecordVo> bidRecords);

	public void addBidDebtRecords(List<BidDebtRecordVo> bidDebtRecords);

	public void addBorrowDetails(List<BorrowDetailVo> borrowDetails);

	public void addNeedReturnRecordNext6MonthVos(List<NeedReturnRecordNext6MonthVo> needReturnRecordNext6MonthVos);

	public void addOverTimeRecordLast6MonthVos(List<OverTimeRecordLast6MonthVo> overTimeRecordLast6MonthVos);
}
