import java.io.*;
import java.nio.file.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Profile {

	private ArrayList<File> profiles;

	public Profile() {
		profiles = new ArrayList<File>();
		String path = System.getProperty("user.home");
		File defaultDirectory = new File(path);
		File listFile[] = defaultDirectory.listFiles();
		for (int i = 0; i < listFile.length; i++) {
			if (listFile[i].getName().contains(".atom") && listFile[i].isDirectory()) {
				profiles.add(listFile[i]);
			}
		}
	}

	public void setProfiles() {
		profiles = new ArrayList<File>();
		String path = System.getProperty("user.home");
		File defaultDirectory = new File(path);
		File listFile[] = defaultDirectory.listFiles();
		for (int i = 0; i < listFile.length; i++) {
			if (listFile[i].getName().contains(".atom") && listFile[i].isDirectory()) {
				profiles.add(listFile[i]);
			}
		}
		Collections.sort(profiles);
	}

	public ArrayList<File> getProfiles() {
		return profiles;
	}

	private void renameAllProfile() {
		for (File sub : new File(System.getProperty("user.home")).listFiles()) {
			if (sub.isDirectory() && sub.getAbsolutePath().contains(".atom")) {
				File profileData = new File(sub.getAbsolutePath() + "\\profileInfo.pi");
				if (profileData.exists()) {
					FileReader fr;
					try {
						fr = new FileReader(profileData.getAbsolutePath());
						BufferedReader br = new BufferedReader(fr);
						String name = br.readLine();
						br.close();
						fr.close();
						File file = new File(System.getProperty("user.home") + "\\.atom");
						file.renameTo(new File(System.getProperty("user.home") + "\\.atom-" + name));
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						File file = new File(System.getProperty("user.home") + "\\.atom");
						if (!file.exists()) {
							return;
						}
						profileData.createNewFile();
						PrintWriter writer = new PrintWriter(profileData, "UTF-8");
						writer.println("Default");
						writer.close();
						file.renameTo(new File(System.getProperty("user.home") + "\\.atom-default"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		setProfiles();
	}

	public void setCurrentProfile(int i) {
		renameAllProfile();
		setProfiles();

		String name = System.getProperty("user.home") + "\\.atom";
		profiles.get(i).renameTo(new File(name));

		setProfiles();
	}

	public void delite(String name) {
		File file = new File(name);
		deleteFile(file);
		setProfiles();
	}

	private void deleteFile(File element) {
		if (element.isDirectory()) {
			for (File sub : element.listFiles()) {
				deleteFile(sub);
			}
		}
		element.delete();
	}

	public void createProfile() {
		String input = JOptionPane.showInputDialog("Enter the name of the profile(cannot be empty):");
		while (input.equals("")) {

			input = JOptionPane.showInputDialog("Enter the name of the profile(cannot be empty):");
		}
		renameAllProfile();
		new File(System.getProperty("user.home") + "\\.atom").mkdir();
		File profileData = new File(System.getProperty("user.home") + "\\.atom\\profileInfo.pi");
		try {
			profileData.createNewFile();
			PrintWriter writer = new PrintWriter(profileData, "UTF-8");
			writer.println(input);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes()));
		chooser.setCurrentDirectory(new java.io.File(System.getProperty("user.home")));
		chooser.setDialogTitle("Select icon for atom profile");
		chooser.setAcceptAllFileFilterUsed(false);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			String name = chooser.getSelectedFile().getName();
			name = "icon." + name.substring(name.length() - 3, name.length());
			File src = chooser.getSelectedFile();
			File dest = new File(System.getProperty("user.home") + "\\.atom\\" + name);
			try {
				Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No Selection");
		}

		setProfiles();
	}
}
