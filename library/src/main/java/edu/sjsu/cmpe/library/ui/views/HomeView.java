package edu.sjsu.cmpe.library.ui.views;

import java.util.List;

import com.yammer.dropwizard.views.View;

import edu.sjsu.cmpe.library.domain.Book;

public class HomeView extends View {
    private final List<Book> books;

    public HomeView(List<Book> books) {
	super("home.mustache");
	this.books = books;
    }

    public List<Book> getBooks() {
	return books;
    }
}
