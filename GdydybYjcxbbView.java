package com.ztkj.his.zygl.zycharge.views;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import com.ztkj.baseframe.server.exceptions.BusinessException;
import com.ztkj.his.common.platform.SuperContext;
import com.ztkj.his.zycharge.ybServer.vo.gdydyb.YdybyjVO;
import com.ztkj.his.zycharge.ybServer.vo.gdydyb.YjsbhzRequestDetail;
import com.ztkj.his.zygl.zycharge.common.util.DataSource;
import com.ztkj.his.zygl.zycharge.common.util.pos.ToolUtil;
import com.ztkj.his.zygl.zycharge.report.GdydybyjhzSource;
import com.ztkj.his.zygl.zycharge.report.GdydybyjmxSource;
import com.ztkj.his.zygl.zycharge.uis.GdydybYjcxbbUI;
import com.ztkj.his.zygl.zyquery.reportAndQuery.common.ExportTable;
import com.ztkj.his.zygl.zyquery.reportAndQuery.common.KTableModel.PublicKTableModel;
import com.ztkj.system.frame.dialog.DialogManager;
import com.ztkj.system.frame.print.IPtnSource;
import com.ztkj.system.frame.print.ReportPtnUtil;
import com.ztkj.system.frame.util.Assert;
import com.ztkj.system.frame.util.ViewUitl;

import de.kupzog.ktable.KTable;

public class GdydybYjcxbbView extends GdydybYjcxbbUI{
	public String str = "";
	public static final String ID="com.ztkj.his.zygl.zycharge.uis.GdydybYjcxbbUI";
	private SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM");
	private List<YdybyjVO> yjList;
	private String fsbywh="";
	private int headId;
	private Map<String,String> cbdMap;
	public GdydybYjcxbbView() {
		
	}
	
	@Override
	public void init() {
		xzlxCmb.add("全部");
		xzlxCmb.setData(null);
		xzlxCmb.add("职工医保");
		xzlxCmb.setData("职工医保", "310");
		xzlxCmb.add("城乡居民医保");
		xzlxCmb.setData("城乡居民医保", "390");
		/*xzlxCmb.add("城镇居民基本医疗保险");
		xzlxCmb.setData("城镇居民基本医疗保险", "391");
		xzlxCmb.add("公务员医疗补助");
		xzlxCmb.setData("公务员医疗补助", "320");
		xzlxCmb.add("大额医疗费用补助");
		xzlxCmb.setData("大额医疗费用补助", "330");
		xzlxCmb.add("离休人员医疗保障");
		xzlxCmb.setData("离休人员医疗保障", "340");
		xzlxCmb.add("一至六级残疾军人医疗补助");
		xzlxCmb.setData("一至六级残疾军人医疗补助", "350");
		xzlxCmb.add("老红军医疗保障");
		xzlxCmb.setData("老红军医疗保障", "360");
		xzlxCmb.add("企业补充医疗保险");
		xzlxCmb.setData("企业补充医疗保险", "370");
		xzlxCmb.add("新型农村合作医疗");
		xzlxCmb.setData("新型农村合作医疗", "380");
		xzlxCmb.add("工伤保险");
		xzlxCmb.setData("工伤保险", "410");
		xzlxCmb.add("生育保险");
		xzlxCmb.setData("生育保险", "510");*/
		xzlxCmb.select(0);
		
		cbdMap=new HashMap<String,String>();
		cbdMap.put("440100", "广州市");
		cbdMap.put("440200", "韶关市");
		cbdMap.put("440300", "深圳市");
		cbdMap.put("440400", "珠海市");
		cbdMap.put("440500", "汕头市");
		cbdMap.put("440600", "佛山市");
		cbdMap.put("440700", "江门市");
		cbdMap.put("440800", "湛江市");
		cbdMap.put("440900", "茂名市");
		cbdMap.put("441900", "东莞市");
		cbdMap.put("441200", "肇庆市");
		cbdMap.put("441300", "惠州市");
		cbdMap.put("441400", "梅州市");
		cbdMap.put("441500", "汕尾市");
		cbdMap.put("441600", "河源市");
		cbdMap.put("441700", "阳江市");
		cbdMap.put("441800", "清远市");
		cbdMap.put("445100", "潮州市");
		cbdMap.put("445200", "揭阳市");
		cbdMap.put("442000", "中山市");
		cbdMap.put("445300", "云浮市");

	}

	@Override
	public void addListen() {
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!buttonZhhb.getSelection()){
					headId=0;
					doSearch();
					model.setInput(yjList);
					table.redraw();
				}else{
					doGetYjhz();
				}
			}
		});

		exportButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doExport();
			}
		});
		
		printButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if("".equals(fsbywh) || "0".equals(fsbywh) || null == fsbywh ){
					DialogManager.invokeInfoDlg("请先做完月结申报在来打印！");
					return;
				}
				doPrint();
			}
		});
		
		tcButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doExit();
			}
		});
		
		buttonDymx.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				headId=0;
				doSearch();
				model.setInput(yjList);
				table.redraw();
			}
		});
		
		buttonZhhb.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				doGetYjhz();
			}
		});
	}
	
	protected void doPrint() {
		String yymc=SuperContext.getHosName();
		String title="广东省医疗保险异地就医医疗费用结算申报";
		String[] split=jsndTxt.getText().split("-");
		String fjsnd=split[0];
		String fjsyd=split[1];
		String fbegindate=fjsnd+"年"+fjsyd+"月"+"01日";
		String fenddate=fjsnd+"年"+fjsyd+"月"+getDayCount(jsndTxt.getText())+"日";
		if(buttonDymx.getSelection()){
	      if("全部".equals(xzlxCmb.getText())){
			title=title+"明细表";
		 }else if(xzlxCmb.getText().equals("职工医保")){
			title=title+"明细表（职工医保）";
		 }else{
		    title=title+"明细表（城乡居民医保）";
		 }
	     // 明细需要分参保地打印
	      Map<String, List<YdybyjVO>> cbdMxMap = new HashMap<String, List<YdybyjVO>>();
	      List<YdybyjVO> tmpList = null;
	      for(YdybyjVO vo : yjList){
	    	  if(Assert.isNull(cbdMxMap.get(vo.getAab301()))){
	    		  tmpList = new ArrayList<YdybyjVO>();
	    		  tmpList.add(vo);
	    		  cbdMxMap.put(vo.getAab301(), tmpList);
	    	  } else {
	    		  cbdMxMap.get(vo.getAab301()).add(vo);
	    	  }
	      }
	      for (Map.Entry<String, List<YdybyjVO>> entry : cbdMxMap.entrySet()) {
	    	  // 打印排序
	    	  for(int i=0; i<entry.getValue().size(); i++){
	    		  YdybyjVO vo = entry.getValue().get(i);
	    		  vo.setAae013((i+1)+"");
	    	  }
	    	  model.setInput(entry.getValue());
	    	  table.redraw();
	    	  IPtnSource printSource = new GdydybyjmxSource(getData(),yymc,fbegindate,fenddate,title,fsbywh);
			  ReportPtnUtil.ptnCustomerReport(printSource); 
	      }
		}else{
	   if("全部".equals(xzlxCmb.getText())){
		 title=title+"汇总表";
		}else if(xzlxCmb.getText().equals("职工医保")){
		 title=title+"表（职工医保）";
		}else{
		 title=title+"表（城乡居民医保）";
			}
		IPtnSource printSource = new GdydybyjhzSource(getData(),yymc,fbegindate,fenddate,title,fsbywh);
		ReportPtnUtil.ptnCustomerReport(printSource);
	}
	}
	private String[][] getData() {
		int colimns = table.getModel().getColumnCount();// 列数
		int rows = table.getModel().getRowCount();// 行数
		String[][] strs = new String[rows][colimns];
		String[] str1;
		for (int i = 0; i < rows; i++) {
			str1 = new String[colimns];
			for (int j = 0; j < colimns; j++) {
				str1[j] = table.getModel().getContentAt(j, i).toString();
			}
			strs[i] = str1;
		}
		return strs;
	}
	
	/**
	 * 导出
	 */
	protected void doExport() {
		try {
			ExportTable.exportToExcel("广东异地医保月结查询报表", table, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected String[][] getData(KTable table) {
		int colimns=table.getModel().getColumnCount();
		int rows = table.getModel().getRowCount();
		String[][] strs = new String[rows][colimns];
		String[] str1;
		for (int i = 0; i < rows; i++) {
			str1 = new String[colimns];
			for (int j = 0; j < colimns; j++) {
				str1[j] = table.getModel().getContentAt(j, i).toString();
			}
			strs[i] = str1;
		}
		return strs;
	}
	
	/**
	 * 参保地分组
	 * @param map
	 */
	private void doCbdGroup(Map<String,List<YdybyjVO>> yjhzmap) {
		for(YdybyjVO info:yjList) {
			if(yjhzmap.containsKey(info.getAab301())) {
				yjhzmap.get(info.getAab301()).add(info);
			}else {
				List<YdybyjVO> list = new ArrayList<YdybyjVO>();
				list.add(info);
				yjhzmap.put(info.getAab301(), list);
			}
		}
		
	}
	
	
	/**
	 * 月结申报汇总
	 */
	protected void doGetYjhz() {
		//获取表头信息
		headId=4;
		doSearch();
		doSelectTableHead();
		if(yjList!=null && yjList.size()>0) {
			List<YjsbhzRequestDetail> detail=new ArrayList<YjsbhzRequestDetail>();
			Map<String,List<YdybyjVO>> yjhzmap=new HashMap<String,List<YdybyjVO>>();
			doCbdGroup(yjhzmap);
			Iterator ite=yjhzmap.keySet().iterator();
			List<YdybyjVO> yjhzList = new ArrayList<YdybyjVO>();
			int seq=1;
			while(ite.hasNext()) {
				YjsbhzRequestDetail row=new YjsbhzRequestDetail();
				double fzje=0d;
				double ftczc=0d;
				double fdbtczf=0d;
				double fgrzfje=0d;
				
				int frc=0;//人次
				Set set=new HashSet();
				
				String fcbdbm=(String) ite.next();
				yjhzList=yjhzmap.get(fcbdbm);
				for(YdybyjVO info:yjhzList) {
					frc++;
					set.add(info.getAkc190());
					fzje+=Double.parseDouble(info.getAkc264());
					ftczc+=Double.parseDouble(info.getAkb068());
					fdbtczf+=Double.parseDouble(info.getYkc630());
					fgrzfje+=Double.parseDouble(info.getYzz139());
				}
				
				int frs=set.size();//人数
				
				row.setAab301(fcbdbm);
				row.setAke096(frs+"");
				row.setAke098(frc+"");
				row.setAkc264(new BigDecimal(fzje).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				row.setAkb068(new BigDecimal(ftczc).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				row.setYkc630(new BigDecimal(fdbtczf).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				row.setYzz139(new BigDecimal(fgrzfje).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
				row.setAae013(seq+"");
				detail.add(row);
				seq++;
			}
			
			model.setInput(detail);
			table.redraw();
		}
	}


	/**
	 * 查询需要上报月结信息
	 */
	protected void doSearch() {
		doSelectTableHead();
		String fjsrq=sf.format(jsndTxt.getDate());
		String fxzlx=(String) xzlxCmb.getData(xzlxCmb.getText());
		try {
			yjList=DataSource.igdydybservice.getYdjsdxx(null,null,fjsrq,fxzlx,"1",null,null);
			// 排序
			sortAsc();
			// 编码转换
			codeToName();
			if(yjList!=null && yjList.size()>0){
				fsbywh =yjList.get(0).getFbya();
				fsbywh = yjList.get(0).getFsbbz();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 编码转名称
	 */
	private void codeToName(){
		// 参保地转换/险种类型转换
		for (YdybyjVO obj : yjList) {
			obj.setAab301(cbdMap.get(obj.getAab301())); //参保地
			obj.setAae140("390".equals(obj.getAae140()) ? "城乡" : "城职"); //险种类型
		}
	}
	
	/**
	 * 数据升序
	 */
	private void sortAsc(){
		// mothod one：从数据库返回序列
		for (int i = 0; i < yjList.size(); i++) {
			for (int j = 1; j < yjList.size() - i; j++) {
				YdybyjVO obj = yjList.get(j - 1);
				YdybyjVO obj1 = yjList.get(j);
				if ((SuperContext.stringToInteger(obj.getAae013())).compareTo(SuperContext.stringToInteger(obj1.getAae013())) > 0) { // 比较两个整数的大小
					YdybyjVO temp = yjList.get(j - 1);
					yjList.set((j - 1), yjList.get(j));
					yjList.set(j, temp);
				}
			}
		}
		
		// method two：序列为空
//		for(int i = 0; i < yjList.size(); i++){
//			YdybyjVO obj = yjList.get(i);
//			obj.setAae013((i+1)+"");
//		}
	}
	
	/**
	 * 给一个yyyy-MM(2012-01)日期格式，判断出所传月一共多少天 
	 * @param dateTime
	 * @return
	 */
	public static int getDayCount(String dateTime) {  
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");  
		// GregorianCalendar 是 Calendar 的一个具体子类， 提供了世界上大多数国家/地区使用的标准日历系统
		Calendar calendar = new GregorianCalendar();  
		try {   
			//使用给定的 Date 设置此 Calendar 的时间   
			calendar.setTime(sdf.parse(dateTime));  
		} catch (ParseException e) {   
				e.printStackTrace();
		}  
		//返回此日历字段可能具有的最大值。DAY_OF_MONTH 用于指示一个月的某天 
		int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  
		return day; 
	}



	
	private void doSelectTableHead() {
		String[] strs=null;
		String[] str2=null;
		int[] widths=null;
		switch(headId) {
		 case 0:	
			strs = new String[] { "序号","姓  名 ","参保所属市","身份证号码","住院号","入院日期","出院日期","住院天数","结算日期","入院诊断"
					,"出院诊断","医疗费用总额","记账金额","个人自负金额","大病保险","就诊类别","备注"};
			str2=new String[] { "aae013","aac003","aab301","aac044","akc190","ykc701","ykc702","akb063","akc194","akc050"
					,"akc185","akc264","akb068","yzz139","ykc630","aae140","aae013"};
			widths=new int[] { 50, 80,80, 180, 70, 70, 70 ,70,70,70,70,70,70,70,70,70,50};
			break;

		 default:
				strs = new String[] { "序号", "参保所属市","就医人数",
						"就医人次", "医疗费总金额 ","个人自负金额","记账金额","大病保险" ,"备注"};
				str2=new String[] {"aae013", "aab301", "ake096","ake098", "akc264", "yzz139","akb068","ykc630","aae013"};
				widths=new int[] { 80,100, 100,100, 100, 100,100,100,100};
		}
		 	
		model = new PublicKTableModel(null,
				"com.ztkj.his.zygl.zyquery.reportAndQuery.uis"
						+ headId, strs);
		model.setColumns(str2);
		model.setDftWidth(widths);
		table.setModel(model);
	}
	
	protected void doExit() {
		ViewUitl.close(ID);
		
	}
}
