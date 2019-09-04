package topica.linh.trungnt9.networking.bai1;

import static topica.linh.trungnt9.networking.bai1.Config.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Main class
 * @author ljnk975
 *
 */
public class Main {

	// error code
	private static final int ERR_INPUT_FILE_NOT_EXISTS  = 1;
	private static final int ERR_INPUT_FILE_NOT_FILE    = 2;
	private static final int ERR_INPUT_FILE_FORMAT      = 3;
	private static final int ERR_OUTPUT_FILE_CANT_WRITE = 4;

	public static void main(String[] args) {
		//-> Read data
		File f = new File(inFileName);
		// check exists
		if(!f.exists()) {
			System.out.println("Input file not exists!");
			System.exit(ERR_INPUT_FILE_NOT_EXISTS);
		}
		// check file
		if(!f.isFile()) {
			System.out.println("Input file is not a file!");
			System.exit(ERR_INPUT_FILE_NOT_FILE);
		}
		// read main
		Device device;
		List<Device> listDevice = new ArrayList<Device>();

		Scanner sc = null;
		try {
			sc = new Scanner(f);
			while((device = Device.readDevice(sc)) != null)
				listDevice.add(device);
		} catch (FileNotFoundException e) {
		} catch (ParseException e) {
			System.out.println("Input file format error");
			System.exit(ERR_INPUT_FILE_FORMAT);
		} finally {
			try {
				sc.close();
			} catch(Exception e) {
			}
		}
		// create output file
		PrintStream ps = null;
		try {
			ps = new PrintStream(new File(outFileName));
			
			final PrintStream psFinal = ps; // final print stream to print in inner class

			/// Print device to output follow warranty oder
			listDevice.stream().sorted(Device.warrantyYearOder).forEach((d)-> {
				psFinal.println(d);
			});

			/// Standardized owner name and print to file
			ps.println("###");
			listDevice.forEach((d) -> {
				d.standardizedOwner();
				psFinal.println(d);
			});

			/// Print code name contain topica, inputdate in range (31/10/2018, 31/10/2019)
			ps.println("###");

			// param to filter device
			final String topica = "TOPICA";
			@SuppressWarnings("deprecation")
			final Date startDate = new Date(2018-1900, 10, 31);
			@SuppressWarnings("deprecation")
			final Date endDate   = new Date(2019-1900, 10, 31);

			listDevice.stream().filter((d)-> {
				if(!d.getCodeName().contains(topica)) // not contain topica
					return false;
				if(d.getInputDate().before(startDate)) // before start date
					return false;
				if(d.getInputDate().after(endDate)) // after end date
					return false;
				return true;
			}).sorted(Device.inputDateOder).forEach((d) -> {
				psFinal.println(d);
			});

			/// print word appear the most in owner name
			ps.println("###");

			final Map<String, Integer> mapNumberOfWord = new HashMap<String, Integer>(); // create map number of word
			listDevice.forEach((d) -> {
				String[] arrWord = d.getOwner().split(" ");
				for(String word : arrWord) {
					// if map not contain word then add word to map
					if(!mapNumberOfWord.containsKey(word))
						mapNumberOfWord.put(word, 1); 
					// else increment value
					else
						mapNumberOfWord.put(word, mapNumberOfWord.get(word)+1);
				}
			});

			// if map is null then return
			if(mapNumberOfWord.isEmpty())
				return;

			// print map
//			System.out.println(mapNumberOfWord);

			// comparator to sort list of word by it's number descending order
			Comparator<String> compareByMapValue = (s1, s2) -> {
				return mapNumberOfWord.get(s2)-mapNumberOfWord.get(s1);
			};

			// create list and, all key of map and sort it by it's number descending order
			List<String> listOfWord = new ArrayList<String>();
			listOfWord.addAll(mapNumberOfWord.keySet());
			Collections.sort(listOfWord, compareByMapValue);

			// get max of number and first word
			String word = listOfWord.get(0);
			int max = mapNumberOfWord.get(word);

			// print first word
			ps.println(word);

			// print word have the same number max
			for (int i = 1; i < listOfWord.size(); i++)
				if (mapNumberOfWord.get(word = listOfWord.get(i)) == max)
					ps.println(word);
		} catch (FileNotFoundException e) {
			System.out.println("Cant open output file to write");
			System.exit(ERR_OUTPUT_FILE_CANT_WRITE);
		} finally {
			try {
				ps.close();
			} catch(Exception e) {
			}
		}
	}

}
