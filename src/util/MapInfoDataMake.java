package util;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;


public class MapInfoDataMake {
	private OutputStreamWriter distFileWriter[];
	private final double R=6371000;
	private final double MINX=117.08;
	private final double MAXX=117.33;
	private final double MINY=39.02;
	private final double MAXY=39.26;
	
//	private double MINX=116.70;
//	private double MAXX=118.07;
//	private double MINY=38.50;
//	private double MAXY=40.27;
	private final double CELL=0.006;
	private int width;
	private int height;
	private int total;
	public MapInfoDataMake(){
		width=(int)((MAXX-MINX)/CELL)+1;
		height=(int)((MAXY-MINY)/CELL)+1;
		total=width*height;
	}
	private double getAngle(String p){
		return Double.parseDouble(p)/180.0*Math.PI;
	}
	private double getDistance(double x1,double y1,double x2,double y2){
		return R*Math.acos(Math.cos(y1)*Math.cos(y2)*Math.cos(x2-x1)+Math.sin(y1)*Math.sin(y2));
	}
	public void writeRecord(OutputStreamWriter out,double x1,double y1,double x2,double y2)throws IOException{
		int i1=(int)((x1-MINX)/CELL);
		int j1=(int)((y1-MINY)/CELL);
		int i2=(int)((x2-MINX)/CELL);
		int j2=(int)((y2-MINY)/CELL);
		if(i1>=0&&i1<width&&j1>=0&&j1<height&&i2>=0&&i2<width&&j2>=0&&j2<height){
			out.append((i1*height+j1)+","+(i2*height+j2)+"\r\n");
		}
	}
	public int getIndex(double x,double y){
		int i=(int)((x-MINX)/CELL);
		int j=(int)((y-MINY)/CELL);
		return i*height+j;
	}
	public void divideSpeed(String filePath,String dirPath,double top[])throws IOException{
		File dir=new File(dirPath);
		if(!dir.exists())dir.mkdirs();
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		int len=top.length;
		distFileWriter=new OutputStreamWriter[len];
		for(int i=0;i<len;i++){
			distFileWriter[i]=new OutputStreamWriter(new FileOutputStream(new File(dirPath+"/"+(int)(top[i]/1000)+"km.txt")));
		}
		String line;
		int solveLines=0;
		while((line=br.readLine())!=null){
			//车编号,上车时间,上车经度,上车纬度,上车状态,下车时间,下车经度,下车纬度,下车状态,行程总点数,行程时间(秒),行程位移(千米)
			String item[]=line.split(",");
			double dist=getDistance(getAngle(item[2]),getAngle(item[3]),getAngle(item[6]),getAngle(item[7]));
			for(int i=0;i<len;i++){
				if(dist<top[i]){
					writeRecord(distFileWriter[i],Double.parseDouble(item[2]),Double.parseDouble(item[3]),
							Double.parseDouble(item[6]),Double.parseDouble(item[7]));
				}
			}
			++solveLines;
			if(solveLines%10000==0){
				System.out.println("In divideSpeed(String filePath,String dirPath) solve lines: "+solveLines);
			}
		}
		for(int i=0;i<len;i++){
			distFileWriter[i].flush();
			distFileWriter[i].close();
		}
	}
	
	
	public void makeData(String mapPath,String dirPath,String resultPath)throws IOException{
		HashMap<String,String> nameMap=new HashMap<String,String>();
		BufferedReader mbr=new BufferedReader(new InputStreamReader(new FileInputStream(new File(mapPath))));
		String line;
		while((line=mbr.readLine())!=null){
			String item[]=line.split(",");
			nameMap.put(item[0], item[1]);
		}
		File list[]=(new File(dirPath)).listFiles();
		for(int i=0;i<list.length;i++){
			InputStreamReader fr=new InputStreamReader(new FileInputStream(list[i]));
			BufferedReader br=new BufferedReader(fr);
			OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(resultPath+"/Graph_"+list[i].getName().replace(".txt", ".net"))));
			HashMap<String,Integer> hash=new HashMap<String,Integer>();
			
			fw.append("*Vertices "+total+"\r\n");
			for(int j=1;j<=total;j++){
				fw.append(j+"\t\""+j+"\"\r\n");
			}
			fw.append("*Edges ");
			int solveLines=0;
			while((line=br.readLine())!=null){
				//id_a,id_b<20000
				int cnt=1;
				String key=line.trim();
				if(hash.containsKey(key))cnt+=hash.get(key);
				hash.put(key, cnt);
				++solveLines;
				if(solveLines%10000==0){
					System.out.println("In makeData(String "+list[i].getName()+") solve lines: "+solveLines);
				}
			}
			fw.append(hash.size()+"\r\n");
			for(Iterator it=hash.entrySet().iterator();it.hasNext();){
				Map.Entry elem=(Entry) it.next();
				String item[]=elem.getKey().toString().split(",");
				if(nameMap.containsKey(item[0])&&nameMap.containsKey(item[1])){
					fw.append((Integer.parseInt(nameMap.get(item[0]))+1)+" "+(Integer.parseInt(nameMap.get(item[1]))+1)+" "+elem.getValue()+"\r\n");
				}
			}
			fw.flush();
			fw.close();
		}
	}
	public void makeDataFishnet(String dirPath,String resultPath)throws IOException{
		File dir=new File(resultPath);
		if(!dir.exists()){
			dir.mkdirs();
		}
		String line;
		File list[]=(new File(dirPath)).listFiles();
		for(int i=0;i<list.length;i++){
			InputStreamReader fr=new InputStreamReader(new FileInputStream(list[i]));
			BufferedReader br=new BufferedReader(fr);
			OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(resultPath+"/Graph_"+list[i].getName().replace(".txt", ".net"))));
			HashMap<String,Integer> hash=new HashMap<String,Integer>();
			
			fw.append("*Vertices "+total+"\r\n");
			for(int j=1;j<=total;j++){
				fw.append(j+"\t\""+j+"\"\r\n");
			}
			fw.append("*Edges ");
			int solveLines=0;
			while((line=br.readLine())!=null){
				//id_a,id_b<20000
				int cnt=1;
				String key=line.trim();
				if(hash.containsKey(key))cnt+=hash.get(key);
				hash.put(key, cnt);
				++solveLines;
				if(solveLines%10000==0){
					System.out.println("In makeDataFishnet(String "+list[i].getName()+") solve lines: "+solveLines);
				}
			}
			fw.append(hash.size()+"\r\n");
			for(Iterator it=hash.entrySet().iterator();it.hasNext();){
				Map.Entry elem=(Entry) it.next();
				String item[]=elem.getKey().toString().split(",");
				fw.append((Integer.parseInt(item[0])+1)+" "+(Integer.parseInt(item[1])+1)+" "+elem.getValue()+"\r\n");
			}
			fw.flush();
			fw.close();
		}
	}
	public void getTeamInfo(String dirPath,String resultPath)throws IOException{
		File resultDir=new File(resultPath);
		if(!resultDir.exists()){
			resultDir.mkdirs();
		}
		File list[]=(new File(dirPath)).listFiles();
		for(int i=0;i<list.length;i++){
			InputStreamReader fr=new InputStreamReader(new FileInputStream(list[i]));
			BufferedReader br=new BufferedReader(fr);
			OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(resultPath+"/MAP_"+list[i].getName().replace(".tree", ".csv"))));
			String line=br.readLine();
			fw.append("PID,Belong_1,Belong_2,Belong_3,Belong_4\r\n");
			while((line=br.readLine())!=null){
				String []item=line.split(" ");
				String []team=item[0].split(":");
				fw.append(item[3]+",");
				for(int j=0;j<team.length;j++){
					fw.append(team[j]+(j==team.length-1?"\r\n":","));
				}
			}
			fw.flush();
			fw.close();
		}
	}
	public void makeDataEdge(String filePath, String resultPath) throws IOException {
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)));
		BufferedReader br=new BufferedReader(fr);
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(resultPath)));
		HashMap<String,Integer> hash=new HashMap<String,Integer>();
		int solveLines=0;
		String line;
		while((line=br.readLine())!=null){
			//id_a,id_b<20000
			int cnt=1;
			String key=line.trim();
			if(hash.containsKey(key))cnt+=hash.get(key);
			hash.put(key, cnt);
			++solveLines;
			if(solveLines%10000==0){
				System.out.println("In makeDataEdge(String "+filePath+","+resultPath+") solve lines: "+solveLines);
			}
		}
		for(Iterator it=hash.entrySet().iterator();it.hasNext();){
			Map.Entry elem=(Entry) it.next();
			String item[]=elem.getKey().toString().split(",");
			fw.append((Integer.parseInt(item[0]))+","+(Integer.parseInt(item[1]))+","+elem.getValue()+"\r\n");
		}
		fw.flush();
		fw.close();
	}
	public void makeDataTimeEdge(String filePath,String resultPath)throws IOException{
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)),"utf-8");
		OutputStreamWriter fw=new OutputStreamWriter(new FileOutputStream(new File(resultPath)),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		HashMap<String,Double> hash=new HashMap<String,Double>();
		String line;
		int solveLines=0;
		while((line=br.readLine())!=null){
			//车编号,上车时间,上车经度,上车纬度,上车状态,下车时间,下车经度,下车纬度,下车状态,行程总点数,行程时间(秒),行程位移(千米)
			String item[]=line.split(",");
			Double tval=Double.parseDouble(item[10]);
			int a=getIndex(Double.parseDouble(item[2]),Double.parseDouble(item[3]));
			int b=getIndex(Double.parseDouble(item[6]),Double.parseDouble(item[7]));
			if(a<0||a>total||b<0||b>total)continue;
			String key=a+","+b;
			if(hash.containsKey(key)){
				tval+=hash.get(key);
			}
			hash.put(key, tval);
			++solveLines;
			if(solveLines%10000==0){
				System.out.println("In makeDataTimeEdge(String filePath,String resultPath) solve lines: "+solveLines);
			}
		}
		for(Iterator it=hash.entrySet().iterator();it.hasNext();){
			Map.Entry elem=(Entry) it.next();
			String item[]=elem.getKey().toString().split(",");
			fw.append((Integer.parseInt(item[0]))+","+(Integer.parseInt(item[1]))+","+elem.getValue()+"\r\n");
		}
		fw.flush();
		fw.close();
		fw.close();
	}
}
