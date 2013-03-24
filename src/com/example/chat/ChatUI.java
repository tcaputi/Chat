package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.wolfie.refresher.Refresher;
import com.github.wolfie.refresher.Refresher.RefreshListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ChatUI extends UI {

	public class ChatRefreshListener implements RefreshListener {
		public void refresh(final Refresher source) {
			updateChatLog();
		}
	}

	private class ChatListener implements ClickListener, ValueChangeListener {

		public void valueChange(final ValueChangeEvent event) {
			addNewChatLine();
		}

		public void buttonClick(final ClickEvent event) {
			addNewChatLine();
		}

		private void addNewChatLine() {
			final String name = nameInput.getValue().toString();
			final String message = chatInput.getValue().toString();
			final Date timestamp = new Date();

			if (message != null && !message.isEmpty()) {
				entries.add(new ChatEntry(name, timestamp, message));
				clearChat();
				updateChatLog();
			}
		}

		private void clearChat() {
			chatInput.setValue("");
			chatInput.focus();
		}
	}

	private static final List<ChatEntry> entries = new ArrayList<ChatEntry>();
	private static int visitor = 1;

	private TextField nameInput;
	private TextField chatInput;
	private Date latestupdate = new Date();
	private VerticalLayout chatLayout;

	private final ChatListener chatListener = new ChatListener();

	@Override
	public void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		setContent(layout);
		
		chatLayout = new VerticalLayout();
		chatLayout.setSizeFull();
		layout.addComponent(chatLayout);

		final Refresher refresher = new Refresher();
		refresher.setRefreshInterval(500);
		refresher.addListener(new ChatRefreshListener());
		addExtension(refresher);
		
		final HorizontalLayout footer = new HorizontalLayout();
		footer.setWidth("100%");
		layout.addComponent(footer);

		nameInput = new TextField();
		nameInput.setValue("Anonymous " + visitor);
		visitor++;
		footer.addComponent(nameInput);

		chatInput = new TextField();
		chatInput.focus();
		chatInput.setWidth("100%");
		chatInput.setImmediate(true);
		chatInput.addListener(chatListener); //this may cause issues
		footer.addComponent(chatInput);
		footer.setExpandRatio(chatInput, 1);

		footer.addComponent(new Button("Send", chatListener));
	}

	private void updateChatLog() {
		Date newLatestUpdate = null;
		for (final ChatEntry entry : entries) {
			final Date timestamp = entry.getTimestamp();
			if (timestamp.after(latestupdate)) {
				newLatestUpdate = timestamp;
				print(entry);
			}
		}

		if (newLatestUpdate != null) {
			latestupdate = newLatestUpdate;
		}
	}

	private void print(final ChatEntry entry) {
		final Date timestamp = entry.getTimestamp();
		final String name = entry.getName();
		final String message = entry.getMessage();

		final HorizontalLayout chatLine = new HorizontalLayout();

		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final Label timeLabel = new Label(dateFormat.format(timestamp));
		timeLabel.setWidth("130px");
		chatLine.addComponent(timeLabel);

		final Label nameLabel = new Label(name + ": ");
		nameLabel.setWidth("150px");
		chatLine.addComponent(nameLabel);

		final Label messageLabel = new Label(message);
		chatLine.addComponent(messageLabel);
		chatLine.setExpandRatio(messageLabel, 1);
		
		chatLayout.addComponent(chatLine);
	}
}
