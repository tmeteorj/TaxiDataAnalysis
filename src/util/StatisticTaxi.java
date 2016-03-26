package util;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import data.PileInfo;

public class StatisticTaxi {
	private int inCar[];
	private int outCar[];
	private String starttime;
	private Timestamp startstamp;
	private String endtime;
	private Timestamp endstamp;
	private int MAXN;
	private double MINX=116.70;
	private double MAXX=118.07;
	private double MINY=38.50;
	private double MAXY=40.27;
	private double CELL=0.011;
	private int timeMax;
	private int total;
	private int width;
	private int height;
	private int netIn[][];
	private int netOut[][];
	private HashSet<String> cityPile;
	private PileInfo[] yearCountPile;
	public StatisticTaxi(){
		width=(int)((MAXX-MINX)/CELL)+1;
		height=(int)((MAXY-MINY)/CELL)+1;
		total=width*height;
	}
	public StatisticTaxi(String starttime,String endtime,int MAXN){
		this.starttime=starttime;
		this.endtime=endtime;
		this.MAXN=MAXN;
		this.startstamp=Timestamp.valueOf(starttime);
		this.endstamp=Timestamp.valueOf(endtime);
		inCar=new int[MAXN];
		outCar=new int[MAXN];
		Arrays.fill(inCar, 0);
		Arrays.fill(outCar, 0);
	}
	public void loadComputePile(String filePath)throws IOException{
		System.out.println("loadComputePile...");
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		String line=br.readLine();
		cityPile=new HashSet<String>();
		while((line=br.readLine())!=null){
			cityPile.add(line.substring(0,line.indexOf(",")));
		}
		br.close();
		fr.close();
	}
	public void setTimeInterval(String starttime,String endtime){
		this.starttime=starttime;
		this.endtime=endtime;
		this.MAXN=MAXN;
		this.startstamp=Timestamp.valueOf(starttime);
		this.endstamp=Timestamp.valueOf(endtime);
		this.timeMax=(int)((endstamp.getTime()-startstamp.getTime())/3600000);
		netIn=new int[total][timeMax];
		netOut=new int[total][timeMax];
		for(int i=0;i<total;i++){
			for(int j=0;j<timeMax;j++){
				netIn[i][j]=netOut[i][j]=0;
			}
		}
	}
	public int getNetID(double x,double y){
		int i=(int)((x-MINX)/CELL);
		int j=(int)((y-MINY)/CELL);
		return i*height+j;
	}
	public void computeNetInOut(String inputDir,String outputDir)throws IOException{
		File list[]=(new File(inputDir)).listFiles();
		String timediv[]={"2012-01-01 00:00:00","2012-02-01 00:00:00","2012-03-01 00:00:00","2012-04-01 00:00:00","2012-05-01 00:00:00","2012-06-01 00:00:00","2012-07-01 00:00:00","2012-08-01 00:00:00","2012-09-01 00:00:00","2012-10-01 00:00:00","2012-11-01 00:00:00","2012-12-01 00:00:00","2013-01-01 00:00:00"};
		for(int i=0;i<12;i++){
			InputStreamReader fr=new InputStreamReader(new FileInputStream(list[i]),"utf-8");
			BufferedReader br=new BufferedReader(fr);
			String line;
			setTimeInterval(timediv[i],timediv[i+1]);
			int linenum=0;
			while((line=br.readLine())!=null){
				//车编号,上车时间,上车经度,上车纬度,上车状态,下车时间,下车经度,下车纬度,下车状态,行程总点数,行程时间(秒),行程位移(千米)
				String item[]=line.split(",");
				int inNetID=getNetID(Double.parseDouble(item[2]),Double.parseDouble(item[3]));
				int outNetID=getNetID(Double.parseDouble(item[6]),Double.parseDouble(item[7]));
				int inTimeID=getIndex(item[1]);
				int outTimeID=getIndex(item[5]);
				try{
					netIn[inNetID][inTimeID]++;
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.printf("%d->%d\n",inNetID,inTimeID);
				}
				try{
					netOut[outNetID][outTimeID]++;
				}catch(ArrayIndexOutOfBoundsException e){
					System.out.printf("%d->%d\n",outNetID,outTimeID);
				}
				linenum++;
				if(linenum%50000==0){
					System.out.printf("computeNetInOut(%s)-->%d\n",list[i].getName(),linenum);
				}
			}
			OutputStreamWriter fw_in=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/netIn2012-"+(i+1)+".txt")));
			OutputStreamWriter fw_out=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/netOut2012-"+(i+1)+".txt")));
			for(int id=0;id<total;id++){
				fw_in.append(String.valueOf(id));
				fw_out.append(String.valueOf(id));
				for(int t=0;t<timeMax;t++){
					fw_in.append(","+netIn[id][t]);
					fw_out.append(","+netOut[id][t]);
				}
				fw_in.append("\r\n");
				fw_out.append("\r\n");
			}
			fw_in.close();
			fw_out.close();
			br.close();
			fr.close();
		}
	}
	public void mergeNetInOut(String inputDir,String outputDir)throws IOException{
		OutputStreamWriter fw_in=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/netIn2012.txt")));
		OutputStreamWriter fw_out=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/netOut2012.txt")));
		BufferedReader br_in[]=new BufferedReader[12];
		BufferedReader br_out[]=new BufferedReader[12];
		for(int i=0;i<12;i++){
			br_in[i]=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netIn2012-"+(i+1)+".txt")),"utf-8"));
			br_out[i]=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netOut2012-"+(i+1)+".txt")),"utf-8"));
		}
		for(int i=0;i<total;i++){
			fw_in.append(String.valueOf(i));
			fw_out.append(String.valueOf(i));
			int remove=String.valueOf(i).length();
			for(int j=0;j<12;j++){
				fw_in.append(br_in[j].readLine().substring(remove));
				fw_out.append(br_out[j].readLine().substring(remove));
			}
			fw_in.append("\r\n");
			fw_out.append("\r\n");
			
		}
		for(int i=0;i<12;i++){
			br_in[i].close();
			br_out[i].close();
		}
		fw_in.close();
		fw_out.close();
	}
	
	public void computeSomeItem(String inputDir,String outputDir)throws IOException{
		BufferedReader br_in=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netIn2012.txt")),"utf-8"));
		BufferedReader br_out=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netOut2012.txt")),"utf-8"));
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(outputDir+"/StatisticalInOut.csv"));
		fw.append("NetID,Hour,AveIn,StdIn,AveOut,StdOut,Diff,Diff2\r\n");
		for(int id=0;id<total;id++){
			String info[]=br_in.readLine().split(",");
			String ans[]=new String[24];
			double ex[]=new double[24];
			double ex2[]=new double[24];
			double dx[]=new double[24];
			Arrays.fill(ex,0);
			Arrays.fill(ex2,0);
			Arrays.fill(dx,0);
			for(int i=1;i<info.length;i++){
				double x=Double.parseDouble(info[i]);
				ex[(i-1)%24]+=x;
				ex2[(i-1)%24]+=x*x;
			}
			for(int i=0;i<24;i++){
				ex[i]/=366;
				ex2[i]/=366;
				dx[i]=ex2[i]-ex[i]*ex[i];
				ans[i]=id+","+i+","+ex[i]+","+Math.sqrt(dx[i])+",";
			}
			info=br_out.readLine().split(",");
			Arrays.fill(ex,0);
			Arrays.fill(ex2,0);
			Arrays.fill(dx,0);
			for(int i=1;i<info.length;i++){
				double x=Double.parseDouble(info[i]);
				ex[(i-1)%24]+=x;
				ex2[(i-1)%24]+=x*x;
			}
			for(int i=0;i<24;i++){
				ex[i]/=366;
				ex2[i]/=366;
				dx[i]=ex2[i]-ex[i]*ex[i];
				ans[i]+=ex[i]+","+Math.sqrt(dx[i]);
				String item[]=ans[i].split(",");
				ans[i]+=","+(Double.parseDouble(item[4])-Double.parseDouble(item[2]));
			}
			if(Math.random()*100<1){
				System.out.println("computeSomeItem-->"+id);
			}
		}
		fw.close();
		br_in.close();
		br_out.close();
	}
	public void computeSomeItem2(String inputDir,String outputDir)throws IOException{
		BufferedReader br_in=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netIn2012.txt")),"utf-8"));
		BufferedReader br_out=new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputDir+"/netOut2012.txt")),"utf-8"));
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(outputDir+"/Statistical2.csv"));
		fw.append("NetID,Hour,Statistic\r\n");
		for(int id=0;id<total;id++){
			String info_in[]=br_in.readLine().split(",");
			String info_out[]=br_out.readLine().split(",");
			double ans[]=new double[24];
			Arrays.fill(ans,0);
			for(int i=0;i<366;i++){
				double bot=0;
				double top[]=new double[24];
				for(int h=0;h<24;h++){
					double inh=Double.parseDouble(info_in[1+i*24+h]);
					double outh=Double.parseDouble(info_out[1+i*24+h]);
					bot+=(outh-inh)*(outh-inh);
					top[h]=outh-inh;
				}
				bot=Math.sqrt(bot);
				for(int h=0;h<24;h++){
					ans[h]+=top[h]/bot;
				}
			}
			for(int i=0;i<24;i++){
				ans[i]/=366;
				fw.append(id+","+i+","+ans[i]+"\r\n");
			}
			
			if(Math.random()*100<1){
				System.out.println("computeSomeItem2-->"+id);
			}
		}
		fw.close();
		br_in.close();
		br_out.close();
	}
	private int getIndex(String t){
		Timestamp ts=Timestamp.valueOf(t);
		return (int)((ts.getTime()-startstamp.getTime())/3600000);
	}
	
	private void solveOneRecord(String in,String out){
		int inindex=getIndex(in);
		int outindex=getIndex(out);
		if(inindex>=0&&inindex<MAXN){
			inCar[inindex]++;
		}
		if(outindex>=0&&outindex<MAXN){
			outCar[outindex]++;
		}
	}
	public void countTimes(String filePath)throws IOException{
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		String line;
		int solveLines=0;
		while((line=br.readLine())!=null){
			//车编号,上车时间,上车经度,上车纬度,上车状态,下车时间,下车经度,下车纬度,下车状态,行程总点数,行程时间(秒),行程位移(千米)
			String item[]=line.split(",");
			solveOneRecord(item[1],item[5]);
			++solveLines;
			if(solveLines%10000==0){
				System.out.println("In countTimes(String filePath) solve lines: "+solveLines);
			}
		}
	}
	public void outputTimes(String resultPath)throws IOException{
		OutputStreamWriter resultFile=new OutputStreamWriter(new FileOutputStream(new File(resultPath)));
		resultFile.append("Time,CountIn,CountOut\r\n");
		for(int i=0;i<MAXN;i++){
			resultFile.append(i+","+inCar[i]+","+outCar[i]+"\r\n");
		}
		resultFile.flush();
		resultFile.close();
	}
	public void computeYearCountPiles(String inputDir,String outputDir)throws IOException{
		File list[]=(new File(inputDir)).listFiles();
		yearCountPile=new PileInfo[total];
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/yearCountPiles.csv")));
		for(File F:list){
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(F),"utf-8"));
			String line;
			int solveLines=0;
			while((line=br.readLine())!=null){
				String item[]=line.split(",");
				int nid=getNetID(Double.parseDouble(item[2]),Double.parseDouble(item[3]));
				
				++solveLines;
				if(solveLines%10000==0){
					System.out.println("In computeYearCountPiles solve lines: "+solveLines);
				}
			}
		}
	}
}
