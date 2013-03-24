package com.example.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ChatUI extends UI {

	public static final String USER_PARAM = "user";
	public static final String SESSION_PARAM = "sess";

	private static HashMap<Long, List<ChatEntry>> sessions = new HashMap<Long, List<ChatEntry>>();

	private String username;
	private long sessionId;
	private List<ChatEntry> session;

	private Panel mainPanel = new Panel();
	private Panel logPanel = new Panel();
	private Panel inputPanel = new Panel();
	private VerticalLayout layout = new VerticalLayout();
	private Refresher refresher = new Refresher(); 
	private HorizontalLayout footer = new HorizontalLayout();; 
	private TextField chatInput = new TextField();;
	private Date latestupdate = new Date();
	private VerticalLayout chatLayout  = new VerticalLayout();
	private ChatListener chatListener = new ChatListener();

	@Override
	public void init(VaadinRequest request) {
		Map<String, String[]> parameters = request.getParameterMap();
		username = parameters.get(USER_PARAM)[0];
		sessionId = Long.valueOf(parameters.get(SESSION_PARAM)[0]);
		session = sessions.get(sessionId);
		if(session == null){
			session = new LinkedList<ChatEntry>();
			sessions.put(sessionId, session);
		}

		addExtension(refresher);
		refresher.setRefreshInterval(500);
		refresher.addListener(new ChatRefreshListener());
		
		setContent(mainPanel);
		mainPanel.setContent(layout);
		mainPanel.setHeight(800,Unit.PIXELS);
		mainPanel.setWidth(500, Unit.PIXELS);

		layout.setSizeFull();
		logPanel.setContent(chatLayout);
		mainPanel.setHeight(600,Unit.PIXELS);
		mainPanel.setWidth(500, Unit.PIXELS);
		layout.addComponent(logPanel);

		footer.setWidth("100%");
		inputPanel.setContent(footer);
		layout.addComponent(inputPanel);

		chatInput.focus();
		chatInput.setWidth("100%");
		chatInput.setImmediate(true);
		chatInput.addValueChangeListener(chatListener);

		footer.addComponent(chatInput);
		footer.setExpandRatio(chatInput, 1);
		footer.addComponent(new Button("Send", chatListener));
	}

	private void updateChatLog() {
		Date newLatestUpdate = null;
		for(ChatEntry entry : session) {
			Date timestamp = entry.getTimestamp();
			if (timestamp.after(latestupdate)) {
				newLatestUpdate = timestamp;
				print(entry);
			}
		}
		if (newLatestUpdate != null) latestupdate = newLatestUpdate;
	}

	private void print(ChatEntry entry) {
		Date timestamp = entry.getTimestamp();
		String name = entry.getName();
		String message = entry.getMessage();

		HorizontalLayout chatLine = new HorizontalLayout();
		chatLine.setWidth(100, Unit.PERCENTAGE);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("[HH:mm]");
		Label timeLabel = new Label(dateFormat.format(timestamp));
		timeLabel.setWidth("50px");
		chatLine.addComponent(timeLabel);

		Label nameLabel = new Label(name + ": ");
		nameLabel.setWidth("100px");
		chatLine.addComponent(nameLabel);

		Label messageLabel = new Label(message);
		chatLine.addComponent(messageLabel);
		chatLine.setExpandRatio(messageLabel, 1);

		chatLayout.addComponent(chatLine);
	}

	private class ChatRefreshListener implements RefreshListener {
		public void refresh(Refresher source) {
			updateChatLog();
		}
	}

	private class ChatListener implements ClickListener, ValueChangeListener {
		public void valueChange(ValueChangeEvent event) {
			addNewChatLine();
		}

		public void buttonClick(ClickEvent event) {
			addNewChatLine();
		}

		private void addNewChatLine() {
			String message = chatInput.getValue().toString();
			Date timestamp = new Date();

			if (message != null && !message.isEmpty()) {
				session.add(new ChatEntry(username, timestamp, message));
				clearChat();
				updateChatLog();
			}
		}

		private void clearChat() {
			chatInput.setValue("");
			chatInput.focus();
		}
	}
}
