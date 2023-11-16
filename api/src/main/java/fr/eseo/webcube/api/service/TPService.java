package fr.eseo.webcube.api.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import fr.eseo.webcube.api.dao.TPDAO;
import fr.eseo.webcube.api.model.TP;

@Service
public class TPService {

	@Autowired
	private TPDAO tpdao;

	// TODO regler probleme de type JpaRepository<TP,Integrer>
	protected TPDAO getDao() {
		return tpdao;
	}

	public List<TP> getTpList() {
		return tpdao.findAll();
	}

	public Optional<TP> getTp(Integer id) {
		return tpdao.findById(id);
	}

	public Resource getArchive(Integer id) throws Exception {
		Optional<TP> tpOptional = tpdao.findById(id);
	
		TP tp = tpOptional.get();
		String gitLink = tp.getGitLink();
		String name = tp.getName();
	
		// TODO: fix the path to use the token.
		Path permDir = Paths.get("/Users/arthur/Library/Mobile Documents/com~apple~CloudDocs/Documents/ESEO/Cours-i3/S9/PFE/WebCube/api/src/main/java/fr/eseo/webcube/api/workers/code/arthur/" + name);
	
		// Check if the directory already exists
		if (!Files.exists(permDir)) {
			// If it does not exist, create it and clone the repo
			Files.createDirectories(permDir);
			Git.cloneRepository()
					.setURI(gitLink)
					.setDirectory(permDir.toFile())
					.call();
		}
	
		// If the directory exists, create a zip of the directory
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
}
