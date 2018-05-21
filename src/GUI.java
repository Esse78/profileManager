import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class GUI {

	private String OS = System.getProperty("os.name").toLowerCase();
	private String home = System.getProperty("user.home");
	private String separetor = File.separator;

	public GUI() {
		Profile pr = new Profile();
		JFrame frame = new JFrame("Profile Manager");
		BorderLayout defaultLayout = new BorderLayout();
		ImageIcon icon = new ImageIcon(this.getClass().getResource("atomIcon.png"));
		frame.setIconImage(icon.getImage());
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setLayout(defaultLayout);

		JPanel panelProfiles = new JPanel();
		panelProfiles.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), null));
		ArrayList<File> profiles = pr.getProfiles();
		int numColumn = 5;
		if (profiles.size() < 5) {
			numColumn = profiles.size();
		}
		if (profiles.size() == 0) {
			numColumn = 1;
		}
		GridLayout layout = new GridLayout(0, numColumn, 10, 10);
		panelProfiles.setLayout(layout);

		for (int i = 0; i < profiles.size(); i++) {
			JPanel c = new JPanel();
			BorderLayout bLayout = new BorderLayout();
			c.setLayout(bLayout);

			String name = null;
			FileReader fr;
			try {
				fr = new FileReader(profiles.get(i).getAbsolutePath() + separetor + "profileInfo.pi");
				BufferedReader br = new BufferedReader(fr);
				name = br.readLine();
				br.close();
				fr.close();
			} catch (IOException e) {
				try {
					File profileData = new File(home + separetor + ".atom" + separetor + "profileInfo.pi");
					profileData.createNewFile();
					PrintWriter writer = new PrintWriter(profileData, "UTF-8");
					writer.println("Default");
					writer.close();
					File src = new File(this.getClass().getResource("atomIcon.png").getPath().replaceAll("%20", " "));
					File dest = new File(home + separetor + ".atom" + separetor + "icon.png");
					name = "default";
					try {
						Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} catch (IOException es) {
					e.printStackTrace();
				}
			}
			JLabel label = new JLabel(name);
			JButton button = new JButton();
			ImageIcon iIcon = null;
			try {
				File temp = null;
				for (File sub : profiles.get(i).listFiles()) {
					if (sub.isFile() && sub.getName().contains("icon")) {
						temp = new File(sub.getAbsolutePath());
					}
				}
				if (temp == null) {
					File src = new File(this.getClass().getResource("atomIcon.png").getPath().replaceAll("%20", " "));
					File dest = new File(home + separetor + ".atom" + separetor + "icon.png");
					try {
						Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
						temp = new File(dest.getAbsolutePath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				Image img = ImageIO.read(temp);
				Dimension dim = getScaledDimension(
						new Dimension(ImageIO.read(temp).getWidth(), ImageIO.read(temp).getHeight()),
						new Dimension(125, 125));
				Image newImage = img.getScaledInstance(dim.width, dim.height, Image.SCALE_DEFAULT);
				iIcon = new ImageIcon(newImage);
				button.setIcon(iIcon);
				button.setOpaque(false);
				button.setContentAreaFilled(false);
				button.setBorderPainted(false);
				button.setVisible(true);
			} catch (Exception ex) {
				button.setText(name);
				System.out.println(ex);
			}
			final File f = profiles.get(i);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pr.setCurrentProfile(f);
					try {
						if (isWindows()) {
							Runtime.getRuntime().exec("cmd /c atom");
						} else if (isMac()) {
							String[] cmd = { separetor + "bin" + separetor + "sh", "-c", "atom" };
							Runtime.getRuntime().exec(cmd);
						} else if (isUnix()) {
							String[] cmd = { separetor + "bin" + separetor + "sh", "-c", "atom" };
							Runtime.getRuntime().exec(cmd);
						} else {
							System.out.println("Your OS is not support!!");
						}
						frame.dispose();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			c.add(button, BorderLayout.CENTER);
			label.setHorizontalAlignment(JLabel.CENTER);
			c.add(label, BorderLayout.PAGE_END);
			panelProfiles.add(c);
		}

		frame.add(panelProfiles, BorderLayout.CENTER);

		JButton delite = new JButton("Delite");
		delite.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(home));
				chooser.setDialogTitle("Select atom profile");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				frame.setVisible(false);

				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					pr.delite(chooser.getSelectedFile().getAbsolutePath());
				} else {
					System.out.println("No Selection");
				}
				frame.dispose();
				new GUI();
			}
		});
		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				pr.createProfile();
				frame.dispose();
				new GUI();
			}
		});

		JPanel c = new JPanel();
		FlowLayout fLayout = new FlowLayout();
		c.setLayout(fLayout);
		c.add(create);
		c.add(delite);

		frame.add(c, BorderLayout.PAGE_END);
		frame.setVisible(true);
		frame.repaint();
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		if (original_width > bound_width) {
			new_width = bound_width;
			new_height = (new_width * original_height) / original_width;
		}

		if (new_height > bound_height) {
			new_height = bound_height;
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}

	private boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	private boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	private boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >= 0 || OS.indexOf("bsd") >= 0);
	}

}
