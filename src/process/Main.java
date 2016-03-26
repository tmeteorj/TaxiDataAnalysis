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
		
		//fn.countOneDir("D:/PL/tj2012/2012","D:/PL/experiment/2012��ȫ�����񻮷�");
		
		//fn.outputNetMap("D:/PL/experiment/Fishnet.csv");
		
		fn.mergeCountOneDir("D:/PL/tj2012/2012", "D:/PL/experiment/2012����⳵���³�����");
		fn.outputNetMap("D:/PL/experiment/2012����⳵���³�����/Fishnet.csv");
	}
	public static void getCountTimes()throws IOException{
		StatisticTaxi st=new StatisticTaxi("2012-10-15 00:00:00","2012-10-22 00:00:00",168);
		st.countTimes("D:/PL/tj2012/2012/201210.txt");
		st.outputTimes("D:/PL/result/CountTimes.txt");
	}
	public static void getDistanceFile()throws IOException{
		MapInfoDataMake df=new MapInfoDataMake();
		//double top[]={2000,3000,4000,5000,10000,1000000000};
		//df.divideSpeed("D:/PL/tj2012/2012/201210.txt", "D:/PL/experiment/2012��10��ȫ��OD����",top);
		//df.makeDataFishnet("D:/PL/experiment/2012��10��ȫ��OD����", "D:/PL/experiment/2012��10��ȫ������ͼ����");
		
		df.getTeamInfo("D:/PL/experiment/2012��10��ȫ����������", "D:/PL/experiment/2012��10��ȫ��·��ӳ��");
		
		//df.makeDataEdge("D:/PL/result/2012��10��OD����/1000km.txt", "D:/PL/result/2012��10���罻������/EdgeList.txt");
	
		//df.makeDataTimeEdge("D:/PL/tj2012/2012/201210.txt", "D:/PL/experiment/2012��10�¿ռ������/TimeEdgeList.txt");
	}
	
	public static void getHourlyInOutYear() throws IOException{
		StatisticTaxi st=new StatisticTaxi();
		//st.computeNetInOut("D:/PL/tj2012/2012", "D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��");
		//st.mergeNetInOut("D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��","D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��");
		st.computeSomeItem2("D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��", "D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��");
	}
	
	public static void getDTWKmeans()throws IOException{
		DTWKMeans dtw=new DTWKMeans();
		dtw.loadComputePile("D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��/ȫ��1KM���������2012�����³����(������).csv");
		dtw.readPiles("D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��/StatisticalInOut.csv");
		for(int k=3;k<=10;k++){
			dtw.KMeans(k, "D:/PL/experiment/2012����⳵���³�����/���³�������ʱͳ��/������");
		}
	}
}
