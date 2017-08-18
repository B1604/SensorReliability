import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
	
	//--MODE=T => timestamp and heart rate will be extracted and saved to p1d1_data.csv
	//--MODE=F => hourly diff frequency file will be generated for sensor reliability measurement. 
	public static boolean MODE = true;  
	
	public final static String[] RANGES = {"99", "199", "299", "399", "499", "599", "699", "799", "899", "999", 
			"1099", "1199", "1299", "1399", "1499", "1599", "1699", "1799", "1899", "1999", "2099", "2199",
			"2299", "2399" ,"2499", "2599"};
	
	public static void main (String[] args){
		try{
			BufferedReader bufin = new BufferedReader(new FileReader("./polar.csv"));
			PrintWriter pWriter;
			
			if(MODE){
				pWriter = new PrintWriter("polar_Day2.csv", "UTF-8");
				extractStampAndData(pWriter, bufin);
				pWriter.close();
				bufin.close();
			}
			else{
				pWriter = new PrintWriter("p3d2_diff.txt", "UTF-8");
				calcDiffAndHour(pWriter, bufin);
				pWriter.close();
				bufin.close();
				
				PrintWriter pWriter2 = new PrintWriter("p3d2_final.dat", "UTF-8");
				BufferedReader bufin2 = new BufferedReader(new FileReader("./p3d2_diff.txt"));
				
				createFinal(pWriter2, bufin2);
				pWriter2.close();
				bufin.close();
			}					
			
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	//Method to format into comma delimited csv file with timestamp and HR data.
	public static void extractStampAndData(PrintWriter pw, BufferedReader bin){
		String line;
		try {
			line = bin.readLine();
			String[] pieces = line.split("\t");
			
			while(!pieces[0].equals("Timespan:")){
				line = bin.readLine();				
				pieces = line.split("\t");
				
				if(pieces[0].equals("Timespan:")){
					break;
				}
			}
			
			pw.println("Timestamp,HR");
			line = bin.readLine();
			
			do{
				pieces = line.split("\t");
				pw.println(pieces[0]+","+pieces[3]);
				line = bin.readLine();
			}while(line != null);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//Method to calculate diff's in each hour.
	public static void calcDiffAndHour(PrintWriter pw, BufferedReader bin){
		try{
			String line = bin.readLine();
			String[] pieces = line.split("\t");
			String[] pieces2;
			int hour;
			long prev_time= 0; 
			long cur_time; 
			
			while(!pieces[0].equals("Timespan:")){
				line = bin.readLine();				
				pieces = line.split("\t");
				
				if(pieces[0].equals("Timespan:")){
					break;
				}
			}
			
			line = bin.readLine();
			
			do{
				pieces = line.split(" ");
				
				pieces2 = pieces[1].split(":");
				hour = Integer.parseInt(pieces2[0]);
				
				pieces = line.split("\t");
				cur_time = Long.parseLong(pieces[0]);
				long diff = cur_time - prev_time;
				
				pw.println(hour + " " + diff);
				
				prev_time = cur_time;
				line = bin.readLine();
				
			}while(line != null);
			
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Method to create final data file with hour and diff frequency information for all 24 hours. 
	//This method handles missing hour cases too. 
	//Output file format: Hour Diff-Bin Frequency
	public static void createFinal(PrintWriter pw, BufferedReader bin){
		String line;
		try {
			line = bin.readLine();
			boolean started = false;
			String[] pieces;
			int i =0; 
			for(i=0; i<=23; i++){
				pieces = line.split(" ");
				int hour_stamp = Integer.parseInt(pieces[0]);
				
				long[] bins = new long[26];
				boolean eof = false; 
				
				if(hour_stamp == i){
					
					started = true;
					
					while(started){
						if(!pieces[1].equals("null")){
							long diff = Long.parseLong(pieces[1]);
							long index = diff/100;
							
							if(index >= 25){
								index = 25;
							}
							
							bins[(int)index]++;
							
							line = bin.readLine();
							if(line == null){
								eof = true;
								break;
							}
							pieces = line.split(" ");
							int value_pieces = Integer.parseInt(pieces[0]);
							
							if(value_pieces !=i){
								started = false;
							}
						}
						else{
							started = false;
							eof = true;
						}
					}
					
					if(eof){
						for(int j=i; j<=23; j++){
							//System.out.println("hello there!");
							if(j>i){
								bins = new long[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
							}
							for(int k=0; k<bins.length; k++){
								pw.println(j + " " + RANGES[k] + " " + bins[k]);
							}	
							pw.println();
							pw.flush();
						}						
						break;
					}
					else{
						for(int j=0; j<bins.length; j++){
							pw.println(i + " " + RANGES[j] + " " + bins[j]);
						}
						pw.println();
						pw.flush();
					}
					
				}
				else if(hour_stamp > i ){
					for(int j=0; j < RANGES.length; j++){
						pw.println(i + " " + RANGES[j] + " 0");
					}
					pw.println();
				}
				else if(hour_stamp < i){
					System.out.println("donno what to do :S");
					break;
					
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
