package be.gervaisb.oss.dashboard.extensions.deploy;

import ninja.Result;
import ninja.Results;
import be.gervaisb.oss.dashboard.extensions.Button;
import be.gervaisb.oss.dashboard.extensions.Dashboard;
import be.gervaisb.oss.dashboard.extensions.Extension;
import be.gervaisb.oss.dashboard.extensions.HttpVerb;
import be.gervaisb.oss.dashboard.extensions.Locations;
import be.gervaisb.oss.dashboard.extensions.deploy.mvc.DeploymentsController;

public class DeploymentsExtension implements Extension {

    private static final String URL_TO_FORM = "/deployments/new/";

    @Override
    public void extend(final Dashboard dashboard) {
	dashboard.include(new Button("Deploy", URL_TO_FORM)).into(Locations.Navigation);
	dashboard.include(new DeploymentButton(URL_TO_FORM)).into(Locations.ProjectActions);

	dashboard.route(HttpVerb.GET, URL_TO_FORM).to(DeploymentsController.class, "form");
    }

    public Result doSearch() {
	return Results.html().template("be/gervaisb/oss/dashboard/extensions/sample/view.ftl.html");
    }

}
