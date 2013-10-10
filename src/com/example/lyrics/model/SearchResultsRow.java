package com.example.lyrics.model;

public class SearchResultsRow {
	
	private String title;
	private String artist;
	private String titleLink;
	private String artistLink;
	
	public SearchResultsRow(String title, String artist, String titleLink, String artistLink){
		this.title = title;
		this.artist = artist;
		this.titleLink = titleLink;
		this.artistLink = artistLink;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitleLink() {
		return titleLink;
	}
	public void setTitleLink(String titleLink) {
		this.titleLink = titleLink;
	}
	public String getArtistLink() {
		return artistLink;
	}
	public void setArtistLink(String artistLink) {
		this.artistLink = artistLink;
	}
	

}
