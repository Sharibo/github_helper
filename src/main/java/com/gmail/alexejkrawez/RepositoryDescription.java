package com.gmail.alexejkrawez;

import java.util.List;

import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;


public class RepositoryDescription {
    private String name;
    private GHRepository repository;
    private List<GHPullRequest> prs;

    public RepositoryDescription(String name, GHRepository repository, List<GHPullRequest> prs) {
        this.name = name;
        this.repository = repository;
        this.prs = prs;
    }

    public String getName() {
        return name;
    }

    public GHRepository getRepository() {
        return repository;
    }

    public List<GHPullRequest> getPrs() {
        return prs;
    }

}
