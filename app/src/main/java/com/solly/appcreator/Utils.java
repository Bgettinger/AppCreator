package com.solly.appcreator;


import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public abstract class Utils {
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/* Checks if external storage is available to at least read */
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}

	@Nullable public static Drawable loadAppIcon(String appName) throws IOException, XmlPullParserException {
		File tld = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File mainSrc = new File(new File(new File(new File(new File(tld, "AppCreator"), appName), "app"), "src"), "main");
		File manifest = new File(mainSrc, "AndroidManifest.xml");
		XmlPullParser parser = XML.parse(manifest);

		while(parser.next() != XmlPullParser.END_TAG) {
			// Starts by looking for the entry tag
			if(parser.getName().equals("application") && parser.getAttributeValue("android", "icon") == appName) {
				String resourceId = parser.getAttributeValue("android", "icon");
				String[] resource = resourceId.split("@|/", 2);
				File res = new File(mainSrc, "res");
				File file, dir;
				switch(resource[1]) {
					case "drawable":
						dir = new File(res, "drawable");
						break;
					case "mipmap":
						dir = new File(res, "mipmap");
						break;
					default:
						throw new FileNotFoundException(resourceId);
				}
				file = new File(dir, resource[2] + ".png");
				if(!file.exists()) {
					file = new File(dir, resource[2] + ".jpeg");
					if(!file.exists()) {
						file = new File(dir, resource[2] + ".gif");
						if(!file.exists()) {
							throw new FileNotFoundException(resourceId);
						}
					}
				}
				return Drawable.createFromPath(file.getAbsolutePath());
			} else {
				XML.skip(parser);
			}
		}
		return null;
	}

	public abstract static class XML {
		public static XmlPullParser parse(File file) throws FileNotFoundException, XmlPullParserException {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new FileReader(file));
			return parser;
		}

		public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
			if(parser.getEventType() != XmlPullParser.START_TAG) {
				throw new IllegalStateException();
			}
			int depth = 1;
			while(depth != 0) {
				switch(parser.next()) {
					case XmlPullParser.END_TAG:
						depth--;
						break;
					case XmlPullParser.START_TAG:
						depth++;
						break;
				}
			}
		}
	}
}
