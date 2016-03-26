package data;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

public class PileInfo {
	int netID;
	double []diff;
	int carIn[],carOut[];
	public PileInfo(){
		carIn=carOut=null;
		diff=null;
	}
	public int getNetID() {
		return netID;
	}
	public void setNetID(int netID) {
		this.netID = netID;
	}
	public double[] getDiff() {
		return diff;
	}
	public void setDiff(double[] diff) {
		this.diff = diff;
	}
	public void initInOut(){
		carIn=new int[366];
		carOut=new int[366];
		Arrays.fill(carIn, 0);
		Arrays.fill(carOut, 0);
	}
	public void addIn(int day){
		carIn[day]++;
	}
	public void addOut(int day){
		carOut[day]++;
	}
	public void writeYearCount (OutputStreamWriter fw,int workday[])throws IOException{
		if(carIn==null||carOut==null)return;
		for(int i=0;i<366;i++){
			fw.append(netID+","+i+","+(workday[i]==0?"休息日":"工作日")+","+carIn[i]+","+carOut[i]+"\r\n");
		}
		fw.flush();
	}
}
