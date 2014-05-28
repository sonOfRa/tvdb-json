/*
 * Copyright (c) 2014, Simon Levermann
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package de.slevermann.tvdb.util;

import de.slevermann.tvdb.models.Actor;
import de.slevermann.tvdb.models.Director;
import de.slevermann.tvdb.models.Episode;
import de.slevermann.tvdb.models.Genre;
import de.slevermann.tvdb.models.Series;
import de.slevermann.tvdb.models.Writer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import play.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods
 *
 * @author Simon Levermann
 */
public class Util {
	private static final DateFormat xmlDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Parse the specified date or get null
	 *
	 * @param date the date string to parse
	 * @return the date, or null on failure
	 */
	public static Date parseDateOrNull(String date) {
		try {
			return xmlDateFormat.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * Strips pipes out of a TVDB "list" and writes them into a List
	 *
	 * @param pipedList string containing piped list
	 * @return list containing the strings without pipes
	 */
	public static List<String> unPipeStringList(String pipedList) {
		return Arrays.asList(pipedList.split("\\|")).parallelStream().filter(s -> (!s.isEmpty() || s.length() == 1)).collect(Collectors.toList());
	}

	/**
	 * Attempts to create a Series object from an inputstream.
	 *
	 * @param xmlStream
	 * @return
	 */
	public static Series seriesFromXml(InputStream xmlStream) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlStream);
			NodeList seriesNodes = doc.getElementsByTagName("Series");
			Series series;
			if (seriesNodes.getLength() != 0) {
				Node seriesNode = seriesNodes.item(0);
				NodeList seriesChildren = seriesNode.getChildNodes();
				series = parseSeries(seriesChildren);
			} else {
				return null;
			}

			NodeList episodeNodes = doc.getElementsByTagName("Episode");
			List<Episode> episodes = new ArrayList<>();
			series.setEpisodes(episodes);
			for (int i = 0; i < episodeNodes.getLength(); i++) {
				Node episodeNode = episodeNodes.item(i);
				NodeList episodeChildren = episodeNode.getChildNodes();
				episodes.add(parseEpisode(episodeChildren));
			}
			return series;
		} catch (ParserConfigurationException e) {
			Logger.error("Serious configuration error:", e);
			return null;
		} catch (SAXException e) {
			Logger.error("SAX Problem:", e);
			return null;
		} catch (IOException e) {
			Logger.error("IOException while parsing:", e);
			return null;
		} catch (NumberFormatException e) {
			Logger.error("Tried to parse invalid number", e);
			return null;
		}
	}

	private static Episode parseEpisode(NodeList episodeChildren) {
		Episode episode = new Episode();
		for (int i = 0; i < episodeChildren.getLength(); i++) {
			Node episodeChild = episodeChildren.item(i);
			String childValue = episodeChild.getTextContent().replaceAll("\\s+", " ");
			switch (episodeChild.getNodeName()) {
				case "id":
					episode.setTvdbId(Long.parseLong(childValue));
					break;
				case "Director":
					episode.setDirectors(namesToDirectors(unPipeStringList(childValue)));
					break;
				case "GuestStars":
					episode.setGuestStars(namesToActors(unPipeStringList(childValue)));
					break;
				case "IMDB_ID":
					episode.setImdbId(childValue);
					break;
				case "Language":
					episode.setLanguage(childValue);
					break;
				case "Overview":
					episode.setOverview(childValue);
					break;
				case "Writer":
					episode.setWriters(namesToWriters(unPipeStringList(childValue)));
					break;
				case "filename":
					episode.setThumbFilename(childValue);
					break;
				case "lastupdated":
					episode.setLastUpdated(new Date(Long.parseLong(childValue) * 1000));
					break;
				case "thumb_height":
					episode.setThumbnailHeight(Integer.parseInt(childValue));
					break;
				case "thumb_width":
					episode.setThumbnailWidth(Integer.parseInt(childValue));
					break;
			}
		}
		return episode;
	}

	private static Series parseSeries(NodeList seriesChildren) {
		Series series = new Series();
		for (int i = 0; i < seriesChildren.getLength(); i++) {
			Node seriesChild = seriesChildren.item(i);
			String childValue = seriesChild.getTextContent().replaceAll("\\s+", " ");
			switch (seriesChild.getNodeName()) {
				case "id":
					series.setTvdbId(Long.parseLong(childValue));
					break;
				case "Actors":
					series.setActors(namesToActors(unPipeStringList(childValue)));
					break;
				case "Genre":
					series.setGenres(namesToGenres(unPipeStringList(childValue)));
					break;
				case "IMDB_ID":
					series.setImdbId(childValue);
					break;
				case "Overview":
					series.setOverview(childValue);
					break;
				case "SeriesName":
					series.setName(childValue);
					break;
				case "banner":
					series.setBannerFilename(childValue);
					break;
				case "Language":
					series.setLanguage(childValue);
					break;
				case "lastupdated":
					series.setLastUpdated(new Date(Long.parseLong(childValue) * 1000));
					break;
			}
		}
		return series;
	}

	private static List<Actor> namesToActors(List<String> names) {
		return names.parallelStream().map(Actor::new).collect(Collectors.toList());
	}

	private static List<Writer> namesToWriters(List<String> names) {
		return names.parallelStream().map(Writer::new).collect(Collectors.toList());
	}

	private static List<Director> namesToDirectors(List<String> names) {
		return names.parallelStream().map(Director::new).collect(Collectors.toList());
	}


	private static List<Genre> namesToGenres(List<String> names) {
		return names.parallelStream().map(Genre::new).collect(Collectors.toList());
	}

}
