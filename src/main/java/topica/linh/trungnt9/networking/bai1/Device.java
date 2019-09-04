package topica.linh.trungnt9.networking.bai1;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Pozo Class, model a device have been read from txt
 * @author ljnk975
 *
 */
public class Device {

	/**
	 * Code name of device, the short name to recognize that device eg DELLTOPICA01, HPTOPICA01
	 */
	private final String codeName;

	/**
	 * Device full name
	 */
	private final String deviceName;

	/**
	 * Owner of that device
	 */
	private final HumanName owner;

	/**
	 * Input date of that device
	 */
	private final Date inputDate;

	/**
	 * Warranty Year of that device
	 */
	private final int warrantyYear;

	/**
	 * Comparator to compare device by warranty year
	 */
	public static Comparator<Device> warrantyYearOder = (d1, d2) -> {
		return d1.warrantyYear-d2.warrantyYear;
	};

	/**
	 * Comparator to compare device by input date, if input date the same then compare warranty year
	 */
	public static Comparator<Device> inputDateOder = (d1, d2) -> {
		int compare = d1.inputDate.compareTo(d2.inputDate); // compare input date
		if (compare == 0) // if date the same then compare warranty year
			return d1.warrantyYear-d2.warrantyYear;
		return compare;
	};

	/**
	 * Create adevice
	 * @param codeName      device short name
	 * @param deviceName    device full name
	 * @param owner         owner name
	 * @param inputDate     input date of that device
	 * @param warrantyYear  warranty year of that device
	 * @throws ParseException when input date not in fomart of dd/MM/yyyy
	 */
	public Device(String codeName, String deviceName, String owner, String inputDate, int warrantyYear) throws ParseException {
		this.codeName     = codeName;
		this.deviceName   = deviceName;
		this.owner        = new HumanName(owner);
		this.inputDate    = DateUtil.gI().parse(inputDate);
		this.warrantyYear = warrantyYear;
	}

	/**
	 * Code name of device, the short name to recognize that device eg DELLTOPICA01, HPTOPICA01
	 */
	public final String getCodeName() {
		return codeName;
	}

	/**
	 * Device full name
	 */
	public final String getDeviceName() {
		return deviceName;
	}

	/**
	 * Owner of that device
	 */
	public final String getOwner() {
		return owner.toString();
	}

	/**
	 * Input date of that device
	 */
	public final Date getInputDate() {
		return inputDate;
	}

	/**
	 * Warranty Year of that device
	 */
	public final int getWarrantyYear() {
		return warrantyYear;
	}

	/**
	 * This method use to standardized name of owner,<br/>
	 * eg owner name "nGuyEn VaN A" after doing it become "Nguyen Van A"
	 */
	public final void standardizedOwner() {
		this.owner.standardized();
	}

	/**
	 * Read a device from a scanner
	 * @param sc input scanner
	 * @return   null if there have no device to read <br/> otherwise next device in input file
	 * @throws ParseException when input date not in fomart of dd/MM/yyyy
	 */
	public static final Device readDevice(Scanner sc) throws ParseException {
		try {
			// Read next line of input file
			String nextLine = sc.nextLine();

			String codeName, deviceName, owner, inputDate;
			int warrantyYear;

			// Split line by ',' to get all element of device
			String[] arrString = nextLine.split(",");
			codeName = arrString[0].trim();
			deviceName = arrString[1].trim();
			owner = arrString[2].trim();
			inputDate = arrString[3].trim();
			warrantyYear = Integer.parseInt(arrString[4].trim());

			// return device
			return new Device(codeName, deviceName, owner, inputDate, warrantyYear);
		} catch(NoSuchElementException e) { // exception when scanner have no line
		}
		return null; // return null to tell that sannner is end
	}

	@Override
	public String toString() {
		return this.codeName+","+this.deviceName+","+this.owner+","+DateUtil.gI().format(inputDate)+","+warrantyYear;
	}

}
