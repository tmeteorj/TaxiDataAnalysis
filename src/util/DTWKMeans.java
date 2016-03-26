package util;

import java.io.*;
import java.util.*;

import data.PileInfo;

public class DTWKMeans {
	private final double MAXDIFF=60.0;
	private final double INF=1e100;
	HashSet<String> cityPile; 
	List<PileInfo> list;
	List cluster[];
	double center[][];
	/**
	 * 读取需要计算的NetID
	 * @param filePath
	 * @throws IOException
	 */
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
	/**
	 * 读取各个NetID的特征向量
	 * @param filePath
	 * @throws IOException
	 */
	public void readPiles(String filePath)throws IOException{
		System.out.println("readPiles...");
		InputStreamReader fr=new InputStreamReader(new FileInputStream(new File(filePath)),"utf-8");
		BufferedReader br=new BufferedReader(fr);
		String line=br.readLine();
		list=new ArrayList<PileInfo>();
		while((line=br.readLine())!=null){
			String info[]=line.split(",");
			if(cityPile.contains(info[0])){
				PileInfo elem=new PileInfo();
				elem.setNetID(Integer.parseInt(info[0]));
				double ar[]=new double[24];
				ar[0]=Double.parseDouble(info[6]);
				for(int i=1;i<24;i++){
					line=br.readLine();
					info=line.split(",");
					//System.out.println(line+"\t"+info.length+"\t"+info[6]);
					ar[i]=Double.parseDouble(info[6]);
				}
				elem.setDiff(ar);
				list.add(elem);
			}
		}
		br.close();
		fr.close();
	}
	
	public void KMeans(int k,String outputDir)throws IOException{
		System.out.println("KMeans-"+k+"...");
		cluster=new List[k];
		center=new double[k][24];
		int belong[]=new int[list.size()];
		for(int i=0;i<k;i++){
			cluster[i]=new ArrayList<PileInfo>();
			center[i]=list.get((int)(Math.random()*list.size())).getDiff();
		}
		Arrays.fill(belong,-1);
		boolean flag=true;
		int itertimes=0;
		while(flag){
			System.out.println("进行第"+(++itertimes)+"次迭代");
			flag=false;
			int itemid=0;
			for(Iterator<PileInfo> it=list.iterator();it.hasNext();itemid++){
				PileInfo item=it.next();
				double mindis=INF;
				int minid=-1;
				for(int i=0;i<k;i++){
					double tmpdis=DTWDistance(item.getDiff(),center[i],24,24);
					if(tmpdis<mindis){
						mindis=tmpdis;
						minid=i;
					}
				}
				cluster[minid].add(item);
				if(belong[itemid]!=minid){
					flag=true;
					belong[itemid]=minid;
				}
			}
			for(int i=0;i<k;i++){
				center[i]=getCenter(cluster[i]);
			}
			if(flag){
				for(int i=0;i<k;i++){
					cluster[i].clear();
				}
			}
		}
		OutputStreamWriter fw_cluster=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/cluster-"+k+".csv")));
		OutputStreamWriter fw_center=new OutputStreamWriter(new FileOutputStream(new File(outputDir+"/center-"+k+".csv")));
		fw_cluster.append("NetID,ClusterID\r\n");
		fw_center.append("ClusterID");
		for(int i=0;i<24;i++){
			fw_center.append(","+i);
		}
		fw_center.append("\r\n");
		for(int i=0;i<k;i++){
			for(Iterator<PileInfo> it=cluster[i].iterator();it.hasNext();){
				fw_cluster.append(it.next().getNetID()+","+(i+1)+"\r\n");
			}
			fw_center.append(String.valueOf(i+1));
			for(int j=0;j<24;j++){
				fw_center.append(","+center[i][j]);
			}
			fw_center.append("\r\n");
		}
		fw_cluster.close();
		fw_center.close();
	}
	public double[] getCenter(List clu){
		double cen[]=new double[24];
		Arrays.fill(cen, 0);
		for(Iterator<PileInfo> it=clu.iterator();it.hasNext();){
			double now[]=it.next().getDiff();
			for(int i=0;i<24;i++){
				cen[i]+=now[i];
			}
		}
		int si=clu.size();
		for(int i=0;i<24;i++){
			cen[i]/=si;
		}
		return cen;
	}
	public double DTWDistance(double a[],double b[],int n,int m){
		double dp[][]=new double[n+1][m+1];
		for(int i=0;i<=n;i++){
			for(int j=0;j<=m;j++){
				dp[i][j]=INF;
			}
		}
		dp[0][0]=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				double dist=a[i]-b[j];
				dist*=dist;
				dp[i+1][j+1]=dist+Math.min(dp[i][j+1], Math.min(dp[i+1][j], dp[i][j]));
			}
		}
		return dp[n][m];
	}

}
