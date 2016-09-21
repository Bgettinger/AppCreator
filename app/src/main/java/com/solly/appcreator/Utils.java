package com.solly.appcreator;


import android.content.ContextWrapper;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.solly.appcreator.Utils.Folders.SrcType;

import org.jetbrains.annotations.Contract;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public abstract class Utils {
	public static class Resources {
		@NonNull public static Drawable loadAppIcon(ContextWrapper context, String appName) throws SAXException, ParserConfigurationException, IOException {
			return new IconLoader(context, appName).icon;
		}

		public static String extractStringResource(ContextWrapper context, String appName, String label) throws SAXException, ParserConfigurationException, IOException {
			return new StringLoader(context, appName).string;
		}

		public static Drawable extractImageResource(ContextWrapper context, String appName, String label) {
			final String[] resourceId = label.split("@|/", 3);
			switch(resourceId[1]) {
				case "mipmap":
					return Drawable.createFromPath(new File(Folders.getResSrc(context, appName, SrcType.MAIN), "mipmap").getAbsolutePath());
				default:
					return Drawable.createFromPath(new File(Folders.getResSrc(context, appName, SrcType.MAIN), resourceId[1]).getAbsolutePath());
			}
		}

		public static File getResourceFolder(String appName, String file) {
			return null;
		}

		public static class AppNotFoundException extends SAXException {
			public AppNotFoundException() {
			}

			public AppNotFoundException(String appName) {
				super(appName);
			}

			@Override public String getMessage() {
				return "Couldn't find an entry for " + super.getMessage() + " in its 'app/src/main/AndoidManifest.xml'";
			}

			@Override public String getLocalizedMessage() {
				Locale locale = Locale.getDefault();
				switch(locale.getDisplayLanguage()) {
					case "es":
						return "No pudia encontrar una entrada para " + super.getMessage() + "en su 'app/src/main/AndoidManifest.xml'";
					default:
						return getMessage();
				}
			}

			@Override public String toString() {
				return getLocalizedMessage();
			}
		}

		private static class IconLoader extends DefaultHandler {
			private final String appName;
			private final ContextWrapper context;
			public Drawable icon;
			private int depth;

			public IconLoader(ContextWrapper context, String appName) throws SAXException, ParserConfigurationException, IOException {
				this.context = context;
				this.appName = appName;
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				IconLoader handler = this;
				saxParser.parse(Folders.getAndroidManifest(context, appName, SrcType.MAIN), handler);
			}

			@Override public void startDocument() throws SAXException {
				depth = 0;
			}

			@Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				depth++;
				try {
					if(depth == 2 && qName.equals("android:application") && extractStringResource(context, appName, attributes.getValue("label")).equals(appName)) {
						icon = extractImageResource(context, appName, attributes.getValue("string"));
					}
				} catch(ParserConfigurationException | IOException e) {
					throw new SAXException(e);
				}
			}

			@Override public void endElement(String uri, String localName, String qName) throws SAXException {
				depth--;
			}

			@Override public void endDocument() throws SAXException {
				if(icon == null) {
					throw new AppNotFoundException(appName);
				}
			}

		}

		public static class StringNotFoundException extends SAXException {
			public StringNotFoundException() {
			}

			public StringNotFoundException(String appName) {
				super(appName);
			}

			@Override public String getMessage() {
				return "Couldn't find an entry for " + super.getMessage() + " in its 'app/src/main/AndoidManifest.xml'";
			}

			@Override public String getLocalizedMessage() {
				Locale locale = Locale.getDefault();
				switch(locale.getDisplayLanguage()) {
					case "es":
						return "No pudia encontrar una entrada para " + super.getMessage() + "en su 'app/src/main/AndoidManifest.xml'";
					default:
						return getMessage();
				}
			}

			@Override public String toString() {
				return getLocalizedMessage();
			}
		}

		private static class StringLoader extends DefaultHandler {
			private final String appName;
			private final ContextWrapper context;
			public String string;
			private int depth;

			public StringLoader(ContextWrapper context, String appName) throws SAXException, ParserConfigurationException, IOException {
				this.context = context;
				this.appName = appName;
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				StringLoader handler = this;
				saxParser.parse(Folders.getAndroidManifest(context, appName, SrcType.MAIN), handler);
			}

			@Override public void startDocument() throws SAXException {
				depth = 0;
			}

			@Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
				try {
					depth++;
					if(depth == 2 && qName.equals("android:application") && extractStringResource(context, appName, attributes.getValue("name")).equals(appName)) {
						string = extractStringResource(context, appName, attributes.getValue("string"));
					}
				} catch(ParserConfigurationException | IOException e) {
					throw new SAXException(e);
				}
			}

			@Override public void endElement(String uri, String localName, String qName) throws SAXException {
				depth--;
			}

			@Override public void endDocument() throws SAXException {
				if(string == null) {
					throw new StringNotFoundException(appName);
				}
			}
		}
	}

	public static class Folders {
		public static File getRoot(ContextWrapper context) {
			return context.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath());
		}

		@NonNull public static File getRoot(ContextWrapper context, String appName) {
			return new File(getRoot(context), appName);
		}

		@NonNull public static File getMainModuleRoot(ContextWrapper context, String appName) {
			return new File(getRoot(context, appName), "app");
		}

		@NonNull public static File getRootSrc(ContextWrapper context, String appName) {
			return new File(getMainModuleRoot(context, appName), "src");
		}

		@NonNull public static File getSrc(ContextWrapper context, String appName) {
			return getSrc(context, appName, SrcType.MAIN);
		}

		@NonNull public static File getSrc(ContextWrapper context, String appName, SrcType srcType) {
			return new File(getRootSrc(context, appName), srcType.toString());
		}

		@NonNull public static File getJavaSrc(ContextWrapper context, String appName) {
			return getJavaSrc(context, appName, SrcType.MAIN);
		}

		@NonNull public static File getJavaSrc(ContextWrapper context, String appName, SrcType srcType) {
			return new File(getSrc(context, appName, srcType), "main");
		}

		@NonNull public static File getResSrc(ContextWrapper context, String appName) {
			return getResSrc(context, appName, SrcType.MAIN);
		}

		@NonNull public static File getResSrc(ContextWrapper context, String appName, SrcType srcType) {
			return new File(getSrc(context, appName, srcType), "res");
		}

		@NonNull public static File getAndroidManifest(ContextWrapper context, String appName) {
			return getAndroidManifest(context, appName, SrcType.MAIN);
		}
		
		@NonNull public static File getAndroidManifest(ContextWrapper context, String appName, SrcType srcType) {
			return new File(getSrc(context, appName, srcType), "main");
		}

		public enum SrcType {
			MAIN,
			TEST;

			@NonNull @Contract(pure = true) @Override public String toString() {
				switch(this) {
					case MAIN:
						return "main";
					case TEST:
						return "test";
					default:
						return this.name();
				}
			}
		}
	}
}
