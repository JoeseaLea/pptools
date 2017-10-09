package pptools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import pptools.dao.BlackListDao;
import pptools.dao.BorrowDetailDao;
import pptools.dao.impl.BlackListDaoImpl;
import pptools.dao.impl.BorrowDetailDaoImpl;
import pptools.htmlpage.analysis.BlackList;
import pptools.htmlpage.analysis.BorrowDetail;
import pptools.utils.DownloadHtmlPageUtil;
import pptools.utils.FileUtil;
import pptools.utils.PPDUtil;
import pptools.utils.PPDtool;
import pptools.utils.PropertiesUtil;
import pptools.vo.BidDebtRecordVo;
import pptools.vo.BidRecordVo;
import pptools.vo.BlacklistVo;
import pptools.vo.BorrowDetailPageVo;
import pptools.vo.BorrowDetailVo;
import pptools.vo.NeedReturnRecordNext6MonthVo;
import pptools.vo.OverTimeRecordLast6MonthVo;
import pptools.vo.RepaymentDetailVo;

public class PPDmain {
	
	private static BlackListDao blackListDao = null;
	private static BorrowDetailDao borrowDetailDao = null;
	
	public static void main(String[] args) {
		/**
		 * 加载spring配置文件
		 */
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		blackListDao = ctx.getBean("blackListDao", BlackListDaoImpl.class);
		borrowDetailDao = ctx.getBean("borrowDetailDao", BorrowDetailDaoImpl.class);
		
		try {
			HtmlPage htmlPage = null;
			
			String username = PropertiesUtil.getInstance().getProperty("username", "zhangsan");
			String password = PropertiesUtil.getInstance().getProperty("password", "******");
			
			int maxLoginTimes = 20;
			Random r = new Random();
			
			//登录
			for (int i = 1; i <= maxLoginTimes; i ++) {
				htmlPage = PPDtool.login(username, password);
				if (PPDtool.isLoginSuccess()) {
					break;
				}
				
				if (maxLoginTimes == i) {
					System.out.println("已经连续" + maxLoginTimes + "次登录失败，请确保账户没在其他地方已经登录。");
					return;
				}
				try {
					Thread.sleep(r.nextInt(5000));
				} catch (InterruptedException e) { }
			}
			
			htmlPage = PPDtool.getUrlPage("http://invest.ppdai.com/account/lend");
			System.out.println(htmlPage.asText());
			// 获取黑名单页面
			htmlPage = PPDtool.getUrlPage("http://invest.ppdai.com/account/blacklistnew");
			
			List<BlacklistVo> blacklist = null;
			List<RepaymentDetailVo> repaymentDetails = null;
			BorrowDetailPageVo borrowDetailPageVo = null;
			List<BidRecordVo> bidRecords = null;
			List<BidDebtRecordVo> bidDebtRecords = null;
			List<BorrowDetailVo> borrowDetails = null;
			List<NeedReturnRecordNext6MonthVo> needReturnRecordNext6MonthVos = null;
			List<OverTimeRecordLast6MonthVo> overTimeRecordLast6MonthVos = null;
			
			
			BlackList bk = new BlackList();
			BorrowDetail bd = new BorrowDetail();
			
			HtmlPage htmlDetailPage = null;
			
			String url = null;
			String listingid = null;
			
			while (true) {
				try {
					//获取黑名单信息
					blacklist = bk.getBlackListInfoNew(htmlPage);
					//保存黑名单信息
					blackListDao.addBlackLists(blacklist);
					
					//获取黑名单还款详情页面
					htmlPage = bk.getRepaymentDetailsHtmlPage(htmlPage);
					
					//获取黑名单还款详情
					repaymentDetails = bk.getRepaymentDetails(htmlPage);
					//保存黑名单还款详情
					blackListDao.addRepaymentDetails(repaymentDetails);
					
					//获取所有手机app用户的闪电借款按钮
					List<HtmlElement> trRepaymentDetailslist = bk.getBlacklistdetailURL(htmlPage);
					for (HtmlElement htmlElement : trRepaymentDetailslist) {
						try {
							url = htmlElement.getOneHtmlElementByAttribute("a", "class", "c39a1ea fs16").getAttribute("href");
							listingid = htmlElement.getAttribute("listingid");
							
							htmlDetailPage = PPDtool.getUrlPage(url);
							
							//保存htmlDetailPage文件
							DownloadHtmlPageUtil.saveHtmlPage(url, htmlDetailPage.asXml());
							
							//加载网页
							bd.loadPage(htmlDetailPage, listingid);
							
							borrowDetailPageVo = bd.getBorrowDetailPageVo();
							borrowDetailDao.addBorrowDetailPageVo(borrowDetailPageVo);
							
							//获取投标记录数据
							bidRecords = bd.getBidRecords();
							//插入投标记录数据到数据库
							borrowDetailDao.addBidRecords(bidRecords);
							
							//获取债权转让记录数据
							bidDebtRecords = bd.getBidDebtRecords();
							//插入债权转让记录数据到数据库
							borrowDetailDao.addBidDebtRecords(bidDebtRecords);
							
							//获取借款记录数据
							borrowDetails = bd.getBorrowDetails();
							//插入借款记录数据到数据库
							borrowDetailDao.addBorrowDetails(borrowDetails);
							
							//获取未来6个月的待还记录数据
							needReturnRecordNext6MonthVos = bd.getNeedReturnRecordNext6MonthVos();
							//插入未来6个月的待还记录数据到数据库
							borrowDetailDao.addNeedReturnRecordNext6MonthVos(needReturnRecordNext6MonthVos);
							
							//获取过去6个月有回款记录的逾期天数数据
							overTimeRecordLast6MonthVos = bd.getOverTimeRecordLast6MonthVos();
							//插入过去6个月有回款记录的逾期天数到数据库
							borrowDetailDao.addOverTimeRecordLast6MonthVos(overTimeRecordLast6MonthVos);
							
							blackListDao.updateFullUserName(bd.getFullUsername(), bd.getListingid());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if (bk.isLastPage(htmlPage)) {
						break;
					} else {
						htmlPage = PPDtool.getUrlPage(bk.getNextHtmlPageURL(htmlPage));
					}
					
					PPDUtil.sleep(Integer.valueOf(PropertiesUtil.getInstance().getProperty("interval_time", "1000")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ctx.close();
		}
	}
}
