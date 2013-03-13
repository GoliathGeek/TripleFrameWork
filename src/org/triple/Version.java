package org.triple;

public class Version {
	public final static int MajorVersion = 1;
	public final static int MinorVersion = 0;
	public final static int RevisionVersion = 1;

	public static String getVersionNumber() {
		return Version.MajorVersion + "." + Version.MinorVersion + "." + Version.RevisionVersion;
	}
}
