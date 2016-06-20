package io.jenkins.blueocean.service.embedded.rest;

import hudson.model.Job;
import io.jenkins.blueocean.rest.hal.Link;
import io.jenkins.blueocean.rest.model.Resource;
import jenkins.model.ParameterizedJobMixIn;
import jenkins.scm.api.SCMHead;
import jenkins.scm.api.actions.ChangeRequestAction;
import org.kohsuke.stapler.export.Exported;

/**
 * @author Vivek Pandey
 */
public class BranchImpl extends PipelineImpl {

    private static final String PULL_REQUEST = "pullRequest";

    private final Link parent;

    public BranchImpl(Job job, Link parent) {
        super(job, parent);
        this.parent = parent;
    }

    @Exported(name = PULL_REQUEST, inline = true)
    public PullRequest getPullRequest() {
        SCMHead head = SCMHead.HeadByItem.findHead(job);
        if(head != null) {
            ChangeRequestAction action = head.getAction(ChangeRequestAction.class);
            if(action != null){
                return new PullRequest(action.getId(), action.getURL().toExternalForm(), action.getTitle(), action.getAuthor());
            }
        }
        return null;
    }
    
    // TODO: remove when JENKINS-35797 is done
    // Note that when I (TF) tried using @POST, @TreeResponse etc I got a 500 error. The
    // invocation did arive at the function so the bining did happen, but on the 
    // response something went wrong.
    public void doStartRun() {
        ParameterizedJobMixIn.scheduleBuild2(job, 0);
    }

    @Override
    public Link getLink() {
        return parent.rel(getName());
    }

    public static class PullRequest extends Resource {
        private static final String PULL_REQUEST_NUMBER = "id";
        private static final String PULL_REQUEST_AUTHOR = "author";
        private static final String PULL_REQUEST_TITLE = "title";
        private static final String PULL_REQUEST_URL = "url";

        private final String id;

        private final String url;

        private final String title;

        private final String author;

        public PullRequest(String id, String url, String title, String author) {
            this.id = id;
            this.url = url;
            this.title = title;
            this.author = author;
        }

        @Exported(name = PULL_REQUEST_NUMBER)
        public String getId() {
            return id;
        }


        @Exported(name = PULL_REQUEST_URL)
        public String getUrl() {
            return url;
        }


        @Exported(name = PULL_REQUEST_TITLE)
        public String getTitle() {
            return title;
        }


        @Exported(name = PULL_REQUEST_AUTHOR)
        public String getAuthor() {
            return author;
        }

        @Override
        public Link getLink() {
            return null;
        }
    }

}
