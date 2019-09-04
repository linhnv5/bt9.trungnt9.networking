package topica.linh.trungnt9.networking.bai2.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A messenger transmit between client and server
 * @author ljnk975
 *
 */
public final class Message {

	/**
	 * Authen Command
	 */
	public static final int CMD_AUTHEN = 0;

	/**
	 * Insert command
	 */
	public static final int CMD_INSERT = 1;

	/**
	 * Commit command
	 */
	public static final int CMD_COMMIT = 2;

	/**
	 * Select command
	 */
	public static final int CMD_SELECT = 3;

	/**
	 * Error command
	 */
	public static final int CMD_ERROR  = 4;

	/**
	 * Command code of message, 0 = authen, 1 = insert, 2 = commit, 3 = select, 4 = error
	 */
	private final short cmd;

	/**
	 * Version of message, default 0
	 */
	private short version = 0;

	/**
	 * Represent a tlv info, it contain a tag and a value of tag
	 * @author ljnk975
	 *
	 */
	public static class Tag {

		/**
		 * Tag key
		 */
		public static final int TAG_KEY = 0;

		/**
		 * Tag phone number
		 */
		public static final int TAG_PHONE_NUMBER = 1;

		/**
		 * Tag name
		 */
		public static final int TAG_NAME = 2;

		/**
		 * Tag result code
		 */
		public static final int TAG_RESULT_CODE = 3;

		/**
		 * Tag of this message payload, 0=key, 1=phone number, 2=name, 3=result code
		 */
		private final short tag;

		/**
		 * A string contain info of tag
		 */
		private final String value;

		/**
		 * Create a tag by it tag and value
		 * @param tag   the tag
		 * @param value the value
		 */
		public Tag(int tag, String value) {
			this.tag = (short) tag;
			this.value = value;
		}

		/**
		 * Tag of this message payload, 0=key, 1=phone number, 2=name, 3=result code
		 * @return the tag
		 */
		public final short getTag() {
			return tag;
		}

		/**
		 * A string contain info of tag
		 * @return the value
		 */
		public final String getValue() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			return this.tag == ((Tag)obj).tag;
		}

		@Override
		public int hashCode() {
			return this.tag;
		}


	}

	/**
	 * List of tag in this message
	 */
	private final List<Tag> listTag = new ArrayList<Tag>();

	/**
	 * Create a message with it's cmd code
	 * @param cmd   Command code of message <br/> 0 = authen, 1 = insert, 2 = commit, 3 = select, 4 = error
	 */
	public Message(int cmd) {
		this.cmd = (short) cmd;
	}

	/**
	 * Version of message, default 0
	 * @return the version
	 */
	public final short getVersion() {
		return version;
	}

	/**
	 * Command code of message, 0 = authen, 1 = insert, 2 = commit, 3 = select, 4 = error
	 * @return the cmd
	 */
	public final short getCmd() {
		return cmd;
	}

	/**
	 * Add a tag to payload message
	 * @param t the tag
	 */
	public final void addTag(Tag t) {
		// if contain tag t then result
		if(this.listTag.contains(t))
			return;
		// else add it to list
		this.listTag.add(t);
	}

	/**
	 * Read a tag from this message with specific tag code
	 * @return null if there have no tag to be read <br/>otherwise return the next tag
	 */
	public final Tag getTag(int tag) {
		// find in set, if have tag then return
		for (Tag t : this.listTag) {
			if (t.tag == tag)
				return t;
		}
		// return null if cant find tag
		return null;
	}

	/**
	 * Convert this message to byte array to transmit throught network
	 * @return a byte array to transmit
	 */
	public final byte[] toByteArray() {
		ByteArrayOutputStream bos = null;
		DataOutputStream ds = null;

		try {
			bos = new ByteArrayOutputStream();
			ds = new DataOutputStream(bos);

			// write cmd code and version
			ds.writeShort(this.cmd);
			ds.writeShort(this.version);

			// write tag
			for (Tag t : this.listTag) {
				ds.writeShort(t.tag);
				ds.writeUTF(t.value == null ? "null" : t.value);
			}
		} catch(IOException e) {
		} finally {
			try {
				ds.close();
				bos.close();
			} catch(Exception e) {
			}
		}

		return bos.toByteArray();
	}

	/**
	 * Read a message from byte data
	 * @param data data of message include cmd code, version, tag, ...
	 * @return null if there have an error reading<br/>if no error return a message
	 */
	public static final Message readMessage(byte[] data) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);

		int cmdCode = dis.readUnsignedShort(); // read cmd code
		int version = dis.readUnsignedShort(); // read version

		if(version == 0) {
			Message mss = new Message(cmdCode);
			// while have next tag
			while(dis.available() > 0) {
				// read next tag
				int tag = dis.readUnsignedShort();
				String value = dis.readUTF();
				mss.addTag(new Tag(tag, value));
			}
			// return the message
			return mss;
		}

		return null;
	}

	@Override
	public String toString() {
		StringBuilder strBuff = new StringBuilder();

		// write cmd code
		strBuff.append(this.cmd == CMD_AUTHEN ? "AUTHEN"
						: this.cmd == CMD_INSERT ? "INSERT"
						: this.cmd == CMD_COMMIT ? "COMMIT"
						: this.cmd == CMD_SELECT ? "SELECT"
						: this.cmd == CMD_ERROR ? "ERROR"
						: "UNKNOW");

		// write tag
		for(Tag t : this.listTag) {
			strBuff.append(" ")
			       .append(t.tag == Tag.TAG_KEY ? "Key"
			    	        : t.tag == Tag.TAG_NAME ? "Name"
			    	        : t.tag == Tag.TAG_PHONE_NUMBER ? "PhoneNumber"
			    	        : t.tag == Tag.TAG_RESULT_CODE ? "ResultCode"
			    	        : "Unknow")
			        .append(" ")
					.append(t.value);
		}

		return strBuff.toString();
	}

}
