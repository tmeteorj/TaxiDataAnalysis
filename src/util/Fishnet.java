package util;
import java.util.*;
import java.io.*;

public class Fishnet {
	private int inCnt[][];
	private int outCnt[][];
	private int width;
	private int height;
	private OutputStreamWriter recordOD;
	/*private double MINX=117.08;
	private double MAXX=117.33;
	private double MINY=39.02;
	private double MAXY=39.26;*/
	private double MINX=116.70;
	private double MAXX=118.07;
	private double MINY=38.50;
	private double MAXY=40.27;
	//private double CELL=0.006;//560m
	private double CELL=0.011;
	private int total;
	
	public void clearCnt(){
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				inCnt[i][j]=0;
				outCnt[i][j]=0;
			}
		}
	}
	public Fishnet(){
		width=(int)((MAXX-MINX)/CELL)+1;
		height=(int)((MAXY-MINY)/CELL)+1;
		inCnt=new int[width][height];
		outCnt=new int[width][height];
		total=width*height;
		clearCnt();
	}
	
	public void outputNetMap(String path)throws IOException{
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(path)));
		fw.append("PID,X,Y\r\n");
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				fw.append((i*height+j)+","+(MINX+i*CELL+CELL/2)+","+(MINY+j*CELL+CELL/2)+"\r\n");
			}
		}
		fw.close();
	}
	
	private void solveOneRecord(double x1,double y1,double x2,double y2) throws IOException{
		int i1=(int)((x1-MINX)/CELL);
		int j1=(int)((y1-MINY)/CELL);
		int i2=(int)((x2-MINX)/CELL);
		int j2=(int)((y2-MINY)/CELL);
		int flag=0;
		if(i1>=0&&i1<width&&j1>=0&&j1<height){
			inCnt[i1][j1]++;
			flag++;
		}
		if(i2>=0&&i2<width&&j2>=0&&j2<height){
			outCnt[i2][j2]++;
			flag++;
		}
		if(flag==2){
			recordOD.append((i1*height+j1)+","+(i2*height+j2)+"\r\n");
		}
	}
	
	public void setRecordOD(String recordODPath) throws FileNotFoundException{
		recordOD=new OutputStreamWriter(new FileOutputStream(new File(recordODPath)));
	}
	
	public void countOneFile(File F,boolean closeFile)throws IOException{
		InputStreamReader fr=new InputStreamReader(new FileInputStream(F),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		String line;
		int solveLines=0;
		String name=F.getName();
		while((line=br.readLine())!=null){
			//车编号,上车时间,上车经度,上车纬度,上车状态,下车时间,下车经度,下车纬度,下车状态,行程总点数,行程时间(秒),行程位移(千米)
			String item[]=line.split(",");
			try{
				solveOneRecord(Double.parseDouble(item[2]),Double.parseDouble(item[3]),
						Double.parseDouble(item[6]),Double.parseDouble(item[7]));
			}catch(Exception e){
				e.printStackTrace();
			}
			++solveLines;
			if(solveLines%10000==0){
				System.out.println("In countOneFile( "+name+" ) solve lines: "+solveLines);
			}
		}
		recordOD.flush();
		if(closeFile){
			recordOD.close();
		}
	}
	
	public void countOneDir(String dirPath,String resultPath)throws IOException{
		File list[]=(new File(dirPath)).listFiles();
		for(int i=0;i<list.length;i++){
			String name=list[i].getName().replace(".txt", "");
			setRecordOD(resultPath+"/ODRecord_"+name+".txt");
			clearCnt();
			countOneFile(list[i],true);
			outputInOutCnt(resultPath+"/taxi_"+name+".csv");
		}
	}
	
	public void mergeCountOneDir(String dirPath,String resultPath)throws IOException{
		File list[]=(new File(dirPath)).listFiles();
		setRecordOD(resultPath+"/ODRecord2012.csv");
		clearCnt();
		for(int i=0;i<list.length;i++){
			countOneFile(list[i],i==list.length-1);
		}
		outputInOutCnt(resultPath+"/InOutCnt2012.csv");
	}
	
	public void outputInOutCnt(String carPath)throws IOException{
		OutputStreamWriter carFile=new OutputStreamWriter(new FileOutputStream(new File(carPath)));
		carFile.append("NetID,X,Y,CountIn,CountOut\r\n");
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				carFile.append((i*height+j)+","+(MINX+i*CELL+CELL/2.0)+","+(MINY+j*CELL+CELL/2.0)+","+inCnt[i][j]+","+outCnt[i][j]+"\r\n");
			}
		}
		carFile.flush();
		carFile.close();
	}
	
}
