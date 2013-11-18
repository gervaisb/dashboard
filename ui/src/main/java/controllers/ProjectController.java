/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import static ninja.Result.SC_404_NOT_FOUND;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import ninja.Context;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.params.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.gervaisb.oss.dashboard.Application;
import be.gervaisb.oss.dashboard.HasModules;
import be.gervaisb.oss.dashboard.Module;
import be.gervaisb.oss.dashboard.Project;
import be.gervaisb.oss.dashboard.Version;
import be.gervaisb.oss.dashboard.repos.build.BuildsRepository;
import be.gervaisb.oss.dashboard.repos.issues.IssuesRepository;
import be.gervaisb.oss.dashboard.repos.maven.ArtifactNotFoundException;
import be.gervaisb.oss.dashboard.repos.maven.MvnRepository;
import be.gervaisb.oss.dashboard.repos.quality.QualityRepository;
import be.gervaisb.oss.dashboard.repos.status.Environment;
import be.gervaisb.oss.dashboard.repos.status.StatusRepository;

import com.google.inject.Singleton;

import conf.NinjaDashboard;
import filters.LoggerFilter;
import filters.ThisInjectionFilter;

@Singleton @FilterWith({LoggerFilter.class, ThisInjectionFilter.class})
public class ProjectController extends AsyncController {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectController.class);

    private final StatusRepository status;
    private final QualityRepository quality;
    private final BuildsRepository builds;
    private final IssuesRepository issues;
    private final MvnRepository maven;
    private final NinjaDashboard dashboard;

    @Inject
    public ProjectController(final NinjaDashboard dashboard, final StatusRepository status, final QualityRepository quality, final BuildsRepository builds, final IssuesRepository issues, final MvnRepository maven) {
	this.status = status;
	this.dashboard = dashboard;
	this.quality = quality;
	this.builds = builds;
	this.issues = issues;
	this.maven = maven;
    }

    public Result index() {
	return Results.html()
		.template("views/project/index.ftl.html")
		.render("projects", maven.find(Project.class));
    }

    public Result show(final @PathParam("groupId")String groupId, final @PathParam("artifactId")String artifactId) {
	try {
	    Project project = maven.get(groupId, artifactId, Version.LATEST).as(Project.class);
	    return Results.html()
		    .template("views/project/show.ftl.html")
		    .render("environments", status.getEnvironments())
		    .render("applications", getApplications(project))
		    .render("project", project);
	} catch (final ArtifactNotFoundException notFound) {
	    LOG.error("Project [{}:{}] not found in maven repository. Cannot show it.", groupId, artifactId, notFound);
	    return Results.html().template("views/project/_notfound.ftl.html")
		    .render("error", notFound)
		    .status(SC_404_NOT_FOUND);
	}
    }

    private Collection<Application> getApplications(final HasModules parent) {
	final Set<Application> applications = new TreeSet<>();
	for (final Module module : parent.getModules()) {
	    if ( Application.EXPECTED_PACKAGING.equals(module.getPackaging()) ) {
		applications.add(maven.get(module).as(Application.class));
	    } else {
		applications.addAll(getApplications(module));
	    }
	}
	return applications;
    }

    public Result metrics(final Context ctx, final @PathParam("groupId")String groupId, final @PathParam("artifactId")String artifactId) throws Exception {
	return async(ctx, new Callable<Result>() {
	    @Override
	    public Result call() throws Exception {
		LOG.debug("Rendering metrics of project [{}:{}].", groupId, artifactId);
		try {
		    Project project = maven.get(groupId, artifactId, Version.LATEST).as(Project.class);
		    Result result = Results.html()
			    .template("views/project/_metrics.ftl.html")
			    .render("project", project);
		    if ( dashboard.hasContinuousBuilder() ) {
			result.render("build", builds.getStatus(project));
		    }
		    if ( dashboard.hasIssuesTracker() ) {
			result.render("issues", issues.getStatistics(project));
		    }
		    if ( dashboard.hasQualityManager() ) {
			result.render("quality", quality.getQuality(project));
		    }
		    return result.doNotCacheContent();
		} catch (final ArtifactNotFoundException notFound) {
		    LOG.error("Project [{}:{}] not found in maven repository. Cannot get metrics for it.", groupId, artifactId);
		    return Results.html().template("views/project/_notfound.ftl.html")
			    .render("error", notFound)
			    .status(SC_404_NOT_FOUND);
		}
	    }
	});
    }

    public Result status(final Context ctx, final @PathParam("groupId")String groupId, final @PathParam("artifactId")String artifactId, final @PathParam("envId")String envId) throws Exception {
	return async(ctx, new Callable<Result>() {
	    @Override
	    public Result call() throws Exception {
		LOG.debug("Rendering status for application [{}:{}] on [{}].", groupId, artifactId, envId);
		try {
		    Application application = maven.get(groupId, artifactId, Version.LATEST).as(Application.class);
		    Environment environment = status.getEnvironment(envId);
		    return Results.html()
			    .template("views/project/_status.ftl.html")
			    .render("status", status.getStatus(application, environment))
			    .render("application", application)
			    .doNotCacheContent();
		} catch (final ArtifactNotFoundException notFound) {
		    LOG.error("Application [{}:{}] not found in maven repository. Cannot get status for it.", groupId, artifactId);
		    return Results.html().template("views/project/_notfound.ftl.html")
			    .render("error", notFound)
			    .status(SC_404_NOT_FOUND);
		} catch (final IllegalArgumentException illegal) {
		    LOG.error("Unknow environment [{}]. Cannot get application status on it.", envId);
		    return Results.html().template("views/environment/_notfound.ftl.html")
			    .render("environment", envId)
			    .status(SC_404_NOT_FOUND);

		}
	    }
	});
    }


}
