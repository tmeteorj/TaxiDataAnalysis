package process;
import java.util.*;
import java.io.*;

import util.DTWKMeans;
import util.MapInfoDataMake;
import util.Fishnet;
import util.StatisticTaxi;

public class Main {
	public static void main(String []args)throws IOException{
		getDTWKmeans();
	}
	public static void getInOutFishnet()throws IOException{
		Fishnet fn=new Fishnet();
		/*fn.setRecordOD("D:/PL/result/ODRecord_201210.txt");
		fn.clearCnt();
		fn.countOneFile(new File("D:/PL/tj2012/2012/201210.txt"));
		fn.outputInOutCnt("D:/PL/result/taxi_201210.csv");*/
		
		//fn.countOneDir("D:/PL/tj2012/2012","D:/PL/experiment/2012年全市网格划分");
		
		//fn.outputNetMap("D:/PL/experiment/Fishnet.csv");
		
		fn.mergeCountOneDir("D:/PL/tj2012/2012", "D:/PL/experiment/2012年出租车上下车分析");
		fn.outputNetMap("D:/PL/experiment/2012年出租车上下车分析/Fishnet.csv");
	}
	public static void getCountTimes()throws IOException{
		StatisticTaxi st=new StatisticTaxi("2012-10-15 00:00:00","2012-10-22 00:00:00",168);
		st.countTimes("D:/PL/tj2012/2012/201210.txt");
		st.outputTimes("D:/PL/result/CountTimes.txt");
	}
	public static void getDistanceFile()throws IOException{
		MapInfoDataMake df=new MapInfoDataMake();
		//double top[]={2000,3000,4000,5000,10000,1000000000};
		//df.divideSpeed("D:/PL/tj2012/2012/201210.txt", "D:/PL/experiment/2012年10月全市OD分析",top);
		//df.makeDataFishnet("D:/PL/experiment/2012年10月全市OD分析", "D:/PL/experiment/2012年10月全市社区图数据");
		
		df.getTeamInfo("D:/PL/experiment/2012年10月全市社区网络", "D:/PL/experiment/2012年10月全市路块映射");
		
		//df.makeDataEdge("D:/PL/result/2012年10月OD分析/1000km.txt", "D:/PL/result/2012年10月社交多样性/EdgeList.txt");
	
		//df.makeDataTimeEdge("D:/PL/tj2012/2012/201210.txt", "D:/PL/experiment/2012年10月空间多样性/TimeEdgeList.txt");
	}
	
	public static void getHourlyInOutYear() throws IOException{
		StatisticTaxi st=new StatisticTaxi();
		//st.computeNetInOut("D:/PL/tj2012/2012", "D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计");
		//st.mergeNetInOut("D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计","D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计");
		st.computeSomeItem2("D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计", "D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计");
	}
	
	public static void getDTWKmeans()throws IOException{
		DTWKMeans dtw=new DTWKMeans();
		dtw.loadComputePile("D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计/全市1KM划分网格块2012年上下车情况(仅市内).csv");
		dtw.readPiles("D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计/StatisticalInOut.csv");
		for(int k=3;k<=10;k++){
			dtw.KMeans(k, "D:/PL/experiment/2012年出租车上下车分析/上下车数量按时统计/聚类结果");
		}
	}
}
