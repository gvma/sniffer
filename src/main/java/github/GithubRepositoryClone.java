package github;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.kohsuke.github.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GithubRepositoryClone {

    public GithubRepositoryClone(String user, String password, String authToken, String stars, String outputPath) throws IOException, GitAPIException {
        GitHub gitHub = new GitHubBuilder().withPassword(user, password).withOAuthToken(authToken).build();
        GHRepositorySearchBuilder builder = gitHub.searchRepositories().language("Java").stars(">=" + stars);
        PagedSearchIterable<GHRepository> response = builder.list();

        for (GHRepository repository : response) {
            String url = "https://github.com/" + repository.getFullName();

            PagedIterable<GHRelease> releases = repository.listReleases();
            List<GHRelease> releaseList = releases.toList();
            String repoWithRelease = null;
            if (releaseList.size() > 0) {
                repoWithRelease = releaseList.get(0).getName();
            }

            try {
                System.out.println("Cloning url: " + url);
                cloneRepository(url, repoWithRelease == null ? repository.getName() : repoWithRelease + " " + repository.getName(), outputPath);
            } catch (JGitInternalException e) {
                System.out.println("Skipping project " + repository.getFullName());
            }

            System.out.println("Done!");
        }
    }

    private void cloneRepository(String url, String repositoryName, String outputPath) throws GitAPIException {
        Git.cloneRepository()
                .setURI(url)
                .setDirectory(new File(outputPath + repositoryName))
                .call();
    }
}
