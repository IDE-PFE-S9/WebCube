package fr.eseo.webcube.api.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jgit.api.Git;
import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.Details.TpDetails;
import fr.eseo.webcube.api.Response.TpResponse;
import fr.eseo.webcube.api.dao.TpRepository;
import fr.eseo.webcube.api.dao.UserRepository;
import fr.eseo.webcube.api.dao.UserTpRepository;
import fr.eseo.webcube.api.model.TP;
import fr.eseo.webcube.api.model.User;
import fr.eseo.webcube.api.model.UserTP;
import fr.eseo.webcube.api.model.UserTpKey;

@Service
public class TPService {

	@Autowired
	private TpRepository tpRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserTpRepository userTpRepository;

	public List<TP> getTpList() {
		return tpRepository.findAll();
	}

	public Optional<TP> getTp(Integer id) {
		return tpRepository.findById(id);
	}

	public Resource getArchive(Integer id, String uniqueName) throws Exception {
		Optional<TP> tpOptional = tpRepository.findById(id);

		TP tp = tpOptional.get();
		String gitLink = tp.getGitLink();
		String name = tp.getName();

		String username = uniqueName.split("@")[0].replace(".", "-");

		User user = userRepository.findByUniqueName(uniqueName);

		// TODO: fix the path to use the token.
		Path permDir = Paths.get(
				"/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/"
						+ username + "/" + name);

		// Check if the directory already exists
		if (!Files.exists(permDir)) {
			// If it does not exist, create it and clone the repo
			Files.createDirectories(permDir);
			Git.cloneRepository()
					.setURI(gitLink)
					.setDirectory(permDir.toFile())
					.call();

			// Store the TP in database
			// here i want to create an entry inside the userTP table with the id of the tp,
			// the uniqueName of the user and the completion set to 0
			// Create and store the UserTP in database
			UserTpKey userTPKey = new UserTpKey(uniqueName, tp.getId());
			UserTP userTP = new UserTP();
			userTP.setId(userTPKey);
			userTP.setUser(user);
			userTP.setTp(tp);
			userTP.setCompletion("0");
			userTpRepository.save(userTP);
		}

		
		Path zipPath = Files.createTempFile("archive", ".wc");
		zipFolder(permDir, zipPath);

		// Convert zip to Resource
		Resource resource = new UrlResource(zipPath.toUri());
		return resource;
	}

	private static void zipFolder(Path sourceFolderPath, Path zipPath) throws Exception {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath.toFile()));
		Files.walkFileTree(sourceFolderPath, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				zos.putNextEntry(new ZipEntry(sourceFolderPath.relativize(file).toString()));
				Files.copy(file, zos);
				zos.closeEntry();
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// Skip the .git and lib directories
				if (dir.endsWith(".git") || dir.endsWith("lib")) {
					return FileVisitResult.SKIP_SUBTREE;
				}
				return FileVisitResult.CONTINUE;
			}
		});
		zos.close();
	}

	public TpResponse getCompletionByUniqueName(String uniqueName) {
		List<TpDetails> tpDetails = userTpRepository.findDetailsByUniqueName(uniqueName);
		TpResponse tpResponse = new TpResponse(uniqueName, tpDetails);
        return tpResponse;
    }

	public TpResponse getCompletionByUniqueNameAndTpId(String uniqueName, Integer tpId) {
		List<TpDetails> tpDetails = userTpRepository.findDetailsByUniqueNameAndTpId(uniqueName, tpId);
		TpResponse tpResponse = new TpResponse(uniqueName, tpDetails);
		return tpResponse;
	}

	public List<TpResponse> getCompletionsByTpId(Integer tpId) {
        return userTpRepository.findTpResponseByTpId(tpId);
    }
}
