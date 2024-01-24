package fr.eseo.webcube.api.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.transaction.Transactional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.storage.file.FileBasedConfig;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.FS;
import org.eclipse.jgit.util.SystemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${project.path}")
	private String projectPath;

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

		User user = userRepository.findByUniqueName(uniqueName).get();

		Path permDir = Paths.get(projectPath + "/" + username + "/" + name);

		// Check if the directory already exists
		if (!Files.exists(permDir)) {
			// If it does not exist, create it and clone the repo

			Files.createDirectories(permDir);

			URI gitServer = URI.create(gitLink);
			
			if (gitServer.getScheme().equals("https")) {
				FileBasedConfig config = SystemReader.getInstance().openUserConfig(null, FS.DETECTED);
				synchronized (config) {
					config.load();
					config.setBoolean(
							"http",
							"https://" + gitServer.getHost() + ':'
									+ (gitServer.getPort() == -1 ? 443 : gitServer.getPort()),
							"sslVerify", false);
					config.save();
				}
			}

			Git.cloneRepository()
					.setURI(gitLink)
					.setDirectory(permDir.toFile())
					.setCredentialsProvider(
							new UsernamePasswordCredentialsProvider("meyniear", "glpat-REWFnLwzzczXAstaQGNU"))
					.call();

			UserTpKey userTPKey = new UserTpKey(uniqueName, tp.getId());
			UserTP userTP = new UserTP();
			userTP.setId(userTPKey);
			userTP.setUser(user);
			userTP.setTp(tp);
			userTP.setCompletion(0);
			userTP.setTimeElapsed(0);
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
		List<Object[]> tpObjects = userTpRepository.findTpResponseByTpId(tpId);
		List<TpResponse> tpResponseList = new ArrayList<>();

		for (Object[] result : tpObjects) {
			String uniqueName = (String) result[0];
			String firstname = (String) result[1];
			String surname = (String) result[2];
			Integer completion = (Integer) result[3];
			Integer timeElapsed = (Integer) result[4];
			String rolesString = (String) result[5];

			// Diviser la chaîne des rôles en utilisant la virgule
			String[] rolesArray = rolesString.split(",");

			// Convertir le tableau de chaînes en un ensemble de rôles (de type String)
			Set<String> rolesSet = new HashSet<>(Arrays.asList(rolesArray));

			// Ajouter l'instance à la liste
			tpResponseList.add(new TpResponse(uniqueName, firstname, surname, completion, timeElapsed, rolesSet));
		}
		return tpResponseList;
	}

	@Transactional
	public void updateCompletion(String uniqueName, Integer tpId, Integer completion) {
		userTpRepository.updateCompletion(uniqueName, tpId, completion);
	}

	@Transactional
	public void updateTimeElapsed(String uniqueName, Integer tpId, Integer timeElapsed) {
		userTpRepository.updateTimeElapsed(uniqueName, tpId, timeElapsed);
	}
}
