package be.gervaisb.oss.dashboard.extensions.deploy;

import java.util.Map;

import be.gervaisb.oss.dashboard.Artifact;
import be.gervaisb.oss.dashboard.extensions.Button;

class DeploymentButton extends Button{

    private final String url;

    public DeploymentButton(final String url) {
	super("Deploy", url);
	this.url = url;
    }

    private void setArtifact(final Artifact artifact) {
	setTitle(artifact);
	setParameters(artifact);
    }
    private void setParameters(final Artifact artifact) {
	final ParametersBuilder parameters = new ParametersBuilder()
	.add("artifactId", artifact.getArtifactId())
	.add("groupId", artifact.getGroupId());
	attr("href", new StringBuilder(url).append(parameters).toString());
    }

    private void setTitle(final Artifact artifact) {
	final String title = new StringBuilder("Deploy a component of ")
	.append(artifact.getArtifactId()).toString();
	attr("title", title);
    }



    @Override
    public String render(final Map<String, Object> model) {
	setArtifact((Artifact) model.get("project"));
	return super.render(model);
    }

}
