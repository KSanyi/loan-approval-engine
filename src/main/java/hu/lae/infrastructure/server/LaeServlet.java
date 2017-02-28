package hu.lae.infrastructure.server;

import com.vaadin.server.VaadinServlet;

public class LaeServlet extends VaadinServlet {

	private static final long serialVersionUID = 1L;

	public final ApplicationService applicationService;
	
	public LaeServlet(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

}
